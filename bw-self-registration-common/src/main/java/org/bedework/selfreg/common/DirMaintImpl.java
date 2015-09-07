/* ********************************************************************
    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.
*/
package org.bedework.selfreg.common;

import org.bedework.selfreg.common.dir.BasicDirRecord;
import org.bedework.selfreg.common.dir.DirRecord;
import org.bedework.selfreg.common.dir.Directory;
import org.bedework.selfreg.common.dir.Directory.DirSearchResult;
import org.bedework.selfreg.common.dir.LdapDirectory;
import org.bedework.selfreg.common.exception.SelfregException;
import org.bedework.selfreg.common.mail.Mailer;
import org.bedework.selfreg.common.mail.MailerIntf;
import org.bedework.selfreg.common.mail.Message;
import org.bedework.util.misc.Logged;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.rfc2307.RFC2307MD5PasswordEncryptor;
import org.jasypt.util.password.rfc2307.RFC2307SHAPasswordEncryptor;
import org.jasypt.util.password.rfc2307.RFC2307SSHAPasswordEncryptor;

import java.sql.Timestamp;
import java.util.Properties;
import java.util.UUID;

import javax.naming.Context;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

/** Handle accounts.
 *
 */
public class DirMaintImpl extends Logged implements DirMaint {
  private final boolean debug = true;

  private SelfregConfigProperties config;

  /* We'll use leveldb to store teh outstanding requests with the
     confid as the key. The value will be a json object maintaining
     all the values we got from the first page.
   */
  protected Persisted db;

  /*
  private String ldapUrl = "ldap://localhost:10389";   // <providerUrl>
  private String baseDn = "dc=bedework, dc=org";

  private String accountsOu = "accounts";
  private String accountsDn = "ou=" + accountsOu + ", " + baseDn; // <userDnSuffix>
  private String accountsAttr = "uid";      // <userDnPrefix>

  private String groupsOu = "groups";
  private String groupsDn = "ou=" + groupsOu + ", " + baseDn; // <groupContextDn>

  private String adminId = "uid=admin,ou=system";  // <authDn>

  private String adminPw = "secret";               // <authPw>
  */

  private static final String pwEncryption = "SHA";

  private LdapDirectory ldir;

  private MailerIntf mailer;

  @Override
  public void init(final SelfregConfigProperties config) {
    this.config = config;
    db = new Persisted(config);
  }

  @Override
  public String requestId(final String firstName,
                          final String lastName,
                          final String email,
                          final String pw) throws SelfregException {
    final AccountInfo ainfo = new AccountInfo();

    ainfo.setConfid(UUID.randomUUID().toString());

    try {
      db.startTransaction();

      if (db.emailPresent(email)) {
        return "Account with that email already exists";
      }

      String id = config.getAccountPrefix();
      if (id == null) {
        id = "";
      }

      if ((firstName == null) || (firstName.length() == 0)) {
        return "Missing fields";
      }

      id += firstName.substring(0, 1).toLowerCase();

      if ((lastName == null) || (lastName.length() == 0)) {
        id += "x";
      } else {
        id += lastName.substring(0, 1).toLowerCase();
      }

      id += String.valueOf(db.numAccounts() + 1001);

      ainfo.setAccount(id);
      ainfo.setDtstamp(new Timestamp(System.currentTimeMillis()).toString());
      ainfo.setFirstName(firstName);
      ainfo.setLastName(lastName);
      ainfo.setEmail(email);
      ainfo.setPw(encodedPassword(pw));

      db.addAccount(ainfo);
    } finally {
      db.endTransaction();
    }

    final Message msg = new Message();

    msg.setFrom(config.getMailFrom());

    final String[] to = { email };
    msg.setMailTo(to);
    msg.setSubject(config.getMailSubject());

    // Should be built from a template
    msg.setContent("We have a request for a new account for this email address\n" +
                           "\n" +
                           "If you did not request an account, ignore this message\n" +
                           "\n" +
                           "Otherwise, click on, or copy and paste into your browser, " +
                           "the confirmation link below.\n" +
                           "\n" +
                           config.getConfirmUrl() + "/confirm?confid=" + ainfo.getConfid() + "\n");

    getMailer().post(msg);
    return null;
  }

  @Override
  public AccountInfo getAccount(final String confId)
          throws SelfregException {
    try {
      db.startTransaction();
      return db.getAccount(confId);
    } finally {
      db.endTransaction();
    }
  }

  @Override
  public boolean confirm(final String confId) throws SelfregException {
    try {
      db.startTransaction();
      final AccountInfo ainfo = db.getAccount(confId);

      if (ainfo == null) {
        return false;
      }

      final Message msg = new Message();

      msg.setFrom(config.getMailFrom());

      final String[] to = { ainfo.getEmail() };
      msg.setMailTo(to);

      if (config.getUseLdap()) {
        /* Create an account on ldap directory */

        if (!createLdapAccount(ainfo.getAccount(),
                               ainfo.getFirstName(),
                               ainfo.getLastName(),
                               ainfo.getEmail(),
                               null, // plaintext pw
                               ainfo.getPw())) {
          msg.setSubject(config.getMailSubject() + ": failed");
          msg.setContent("Unable to create an account.");

          getMailer().post(msg);
          return false;
        }
      } else {
        ainfo.setEnabled(true);
        db.updateAccount(ainfo);

        /* have to create one of these to satisfy jboss */
        final RoleInfo ri = new RoleInfo();
        ri.setAccount(ainfo.getAccount());
        ri.setRole("user");

        db.addRole(ri);
      }

      msg.setSubject(config.getMailSubject() + ": success");
      msg.setContent("Your account " +
                             ainfo.getAccount() +
                             "has been created. ");

      getMailer().post(msg);
      return true;
    } finally {
      db.endTransaction();
    }
  }

  @Override
  public boolean lostId(final String email) throws SelfregException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean lostPw(final String id) throws SelfregException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean confirmPwChange(final String confid) throws SelfregException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean confirmPwChange(final String confid,
                                 final String newPw) throws SelfregException {
    // TODO Auto-generated method stub
    return false;
  }

  /* ========================================================================
   * Service interface methods
   * ======================================================================== */


  @Override
  public boolean createAccount(final String accountName,
                               final String firstName,
                               final String lastName,
                               final String email,
                               final String pw,
                               final String encodedPw) throws SelfregException {
    if (config.getUseLdap()) {
      return createLdapAccount(accountName,
                               firstName,
                               lastName,
                               email,
                               pw,
                               encodedPw);
    }

    /* create an enabled db account */

    final AccountInfo ainfo = new AccountInfo();

    ainfo.setConfid(UUID.randomUUID().toString());
    ainfo.setAccount(accountName);
    ainfo.setFirstName(firstName);
    ainfo.setLastName(lastName);
    ainfo.setEmail(email);

    if ((pw != null) && (encodedPw == null)) {
      ainfo.setPw(encodedPassword(pw));
    } else if (encodedPw == null) {
      throw new SelfregException("No password supplied");
    } else {
      ainfo.setPw(encodedPw);
    }

    try {
      db.startTransaction();
      db.addAccount(ainfo);

      /* have to create one of these to satisfy jboss */
      final RoleInfo ri = new RoleInfo();
      ri.setAccount(ainfo.getAccount());
      ri.setRole("user");

      db.addRole(ri);
    } finally {
      db.endTransaction();
    }

    return true;
  }

  private boolean createLdapAccount(final String accountName,
                                    final String firstName,
                                    final String lastName,
                                    final String email,
                                    final String pw,
                                    final String encodedPw) throws SelfregException {
    try {
      /** Build a directory record and add the attributes
       */
      final DirRecord dirRec = new BasicDirRecord();

      final String userDn = accountDn(accountName);
      dirRec.setDn(userDn);
      dirRec.setAttr(config.getAccountsAttr(), accountName);
      dirRec.setAttr("objectclass", "top");
      dirRec.setAttr("objectclass", "person");
      dirRec.setAttr("objectclass", "organizationalPerson");
      dirRec.setAttr("objectclass", "inetOrgPerson");

      final String cn = lastName + ", " + firstName;
      dirRec.setAttr("cn", cn);
      //dirRec.setAttr("gecos", cn);
      dirRec.setAttr("sn", lastName);
      dirRec.setAttr("mail", email);

      if (pw != null) {
        dirRec.setAttr("userPassword", encodedPassword(pw));
      } else if (encodedPw != null) {
        dirRec.setAttr("userPassword", encodedPw.toCharArray());
      }

      /* Posix account requires these but we just set them to dummy values
       * /
      dirRec.setAttr("homeDirectory", "dummy");
      dirRec.setAttr("loginShell", "dummy");
      dirRec.setAttr("uidNumber", "999");
      dirRec.setAttr("gidNumber", "999");
      */

      LdapDirectory dir = getLdir();

      if (dir == null) {
        // TODO need failure response
        return false;
      }

      return dir.create(dirRec);
    } catch (final SelfregException se) {
      throw se;
    } catch (final Throwable t) {
      throw new SelfregException(t);
    }
  }

  /** See if account exists
   *
   * @param account the account
   * @return boolean
   * @throws SelfregException
   */
  @Override
  public String displayAccount(final String account) throws SelfregException {
    final String search = "(&(" +
            config.getAccountsAttr() +
            "=" +
            account +
            "))(objectClass=inetOrgPerson)";

    final DirSearchResult dsr = getLdir().search(config.getAccountsDn(),
                                                 search,
                                                 Directory.scopeOne);

    if (dsr == null) {
      return "No entry found";
    }

    final DirRecord dr = dsr.nextRecord();
    if (dr == null) {
      return "No entry found";
    }

    return dr.toString();
  }

  @Override
  public void setUserPassword(final String account,
                              final String password) throws SelfregException {
    final BasicAttribute attr = new BasicAttribute("userPassword",
                                             encodedPassword(password));
    final ModificationItem mi = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                                               attr);

    final ModificationItem[] mods = {mi};
    getLdir().modify(accountDn(account), mods);
  }

  @Override
  public boolean createGroup(final String group,
                             final String account) throws SelfregException {
    try {
      final DirRecord dirRec = new BasicDirRecord();

      dirRec.setDn(groupDn(group));
      dirRec.setAttr("cn", group);
      dirRec.setAttr("objectclass", "top");
      dirRec.setAttr("objectclass", "groupOfNames");

      dirRec.setAttr("member", accountDn(account));

      return getLdir().create(dirRec);
    } catch (final SelfregException se) {
      throw se;
    } catch (final Throwable t) {
      throw new SelfregException(t);
    }
  }

  @Override
  public void addGroupMember(final String group,
                               final String account) throws SelfregException {
    //if (!accountExists(account)) {
    //  error("Account " + account + " does not exist");
    //}

    final BasicAttribute attr = new BasicAttribute("member",
                                             accountDn(account));
    final ModificationItem mi = new ModificationItem(DirContext.ADD_ATTRIBUTE,
                                               attr);

    final ModificationItem[] mods = {mi};
    getLdir().modify(groupDn(group), mods);
  }

  /* ========================================================================
   * Private methods
   * ======================================================================== */

  private String accountDn(final String account) {
    return config.getAccountsAttr() + "=" + account + ", " +
        config.getAccountsDn();
  }

  private String groupDn(final String group) {
    return config.getGroupsAttr() + "=" + group + ", " +
        config.getGroupsDn();
  }

  private LdapDirectory getLdir() throws SelfregException {
    try {
      if (ldir != null) {
        return ldir;
      }

      final Properties pr = new Properties();

      pr.put(Context.PROVIDER_URL, config.getLdapUrl());

      ldir = new LdapDirectory(pr, config.getAdminId(),
                               config.getAdminPw(), debug);

      return ldir;
    } catch (final Throwable t) {
      error(t);
      return null;
    }
  }

  private String encodedPassword(final String pw) throws SelfregException {
    try {
      /*
      final MessageDigest md = MessageDigest.getInstance(pwEncryption);

      md.update(pw.getBytes());

      final byte[] b64s = new Base64().encode(md.digest());

      return "{" + pwEncryption + "}" + new String(b64s);
      */
      PasswordEncryptor encryptor;
      if ("SSHA".equals(config.getMessageDigest())) {
        encryptor = new RFC2307SSHAPasswordEncryptor();
      } else if ("SHA".equals(config.getMessageDigest())) {
        encryptor = new RFC2307SHAPasswordEncryptor();
      } else if ("MD5".equals(config.getMessageDigest())) {
        encryptor = new RFC2307MD5PasswordEncryptor();
      } else {
        throw new SelfregException("Unsupported message digest");
      }

      final String encpw = encryptor.encryptPassword(pw);
      final String prefix = "{" + config.getMessageDigest() + "}";

      if (!encpw.startsWith(prefix)) {
        throw new SelfregException("Doesn't start with " + prefix);
      }

      return encpw.substring(prefix.length());
    } catch (final Throwable t) {
      throw new SelfregException(t);
    }
  }

  private boolean sendConfirm(final String text,
                              final String subject,
                              final String email) throws SelfregException {
    if (email == null) {
      return false;
    }

    final Message emsg = new Message();

    final String[] to = new String[]{email};
    emsg.setMailTo(to);

    emsg.setFrom(config.getMailFrom());
    emsg.setSubject(subject);
    emsg.setContent(text);

    getMailer().post(emsg);

    return true;
  }

  private MailerIntf getMailer() throws SelfregException {
    if (mailer == null) {
      mailer = new Mailer();

      mailer.init(config);
    }

    return mailer;
  }
}
