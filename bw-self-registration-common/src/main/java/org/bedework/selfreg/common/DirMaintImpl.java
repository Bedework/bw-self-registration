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

import org.bedework.base.response.Response;
import org.bedework.selfreg.common.dir.BasicDirRecord;
import org.bedework.selfreg.common.dir.DirRecord;
import org.bedework.selfreg.common.dir.Directory;
import org.bedework.selfreg.common.dir.Directory.DirSearchResult;
import org.bedework.selfreg.common.dir.LdapDirectory;
import org.bedework.selfreg.common.exception.SelfregException;
import org.bedework.selfreg.common.mail.Mailer;
import org.bedework.selfreg.common.mail.MailerIntf;
import org.bedework.selfreg.common.mail.Message;
import org.bedework.selfreg.service.SelfregConfigProperties;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;
import org.bedework.util.security.PasswordGenerator;

import org.apache.http.client.utils.URIBuilder;
import org.jasypt.util.password.rfc2307.RFC2307MD5PasswordEncryptor;
import org.jasypt.util.password.rfc2307.RFC2307SHAPasswordEncryptor;
import org.jasypt.util.password.rfc2307.RFC2307SSHAPasswordEncryptor;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.UUID;

import javax.naming.Context;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import static java.nio.file.StandardOpenOption.CREATE;

/** Handle accounts.
 *
 */
public class DirMaintImpl implements Logged, DirMaint {
  private SelfregConfigProperties config;

  /* We'll store the outstanding requests with the
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
  public Response<?> requestId(final String firstName,
                               final String lastName,
                               final String email,
                               final String account,
                               final String pw) {
    final var ainfo = new AccountInfo();
    final var resp = new Response<>();

    setConfid(ainfo);

    try {
      db.startTransaction();

      if (db.emailPresent(email)) {
        return resp.error(
                "Account with that email already exists");
      }

      String id;
      
      if (account != null) {
        id = account;
      } else if (config.getAccountFromEmail()) {
        final int pos = email.indexOf("@");
        
        if (pos < 0) {
          return resp.error("Invalid email");
        }
        
        id = email.substring(0, pos);
      } else {
        id = config.getAccountPrefix();
        if (id == null) {
          id = "";
        }

        if ((firstName == null) || (firstName.isEmpty())) {
          return resp.error("Missing fields");
        }

        id += firstName.substring(0, 1).toLowerCase();

        if ((lastName == null) || (lastName.isEmpty())) {
          id += "x";
        } else {
          id += lastName.substring(0, 1).toLowerCase();
        }

        id += String.valueOf(db.numAccounts() + 1001);
      }

      ainfo.setAccount(id);
      ainfo.setDtstamp(new Timestamp(System.currentTimeMillis()).toString());
      ainfo.setFirstName(firstName);
      ainfo.setLastName(lastName);
      ainfo.setEmail(email);

      final String thePw;
      if (config.getPwIsToken()) {
        thePw = ainfo.getConfid();
      } else {
        thePw = pw;
      }

      ainfo.setPw(encodedPassword(thePw, config.getUseLdap()));

      db.addAccount(ainfo);
    } finally {
      db.endTransaction();
    }

    final Message msg = new Message();

    msg.setFrom(config.getMailFrom());

    final String[] to = { email };
    msg.setMailTo(to);
    msg.setSubject(config.getMailSubject());

    try {
      final URIBuilder builder = new URIBuilder(config.getConfirmUrl());

      builder.addParameter("confid", ainfo.getConfid());

      if (config.getTestConfirmFile() != null) {
        Files.writeString(
                Paths.get(config.getTestConfirmFile()),
                builder.toString(),
                CREATE
        );
      }

      // Should be built from a template
      msg.setContent(
              "We have a request for a new account for this email address\n" +
                      "\n" +
                      "If you did not request an account, ignore this message\n" +
                      "\n" +
                      "Otherwise, click on, or copy and paste into your browser, " +
                      "the confirmation link below.\n" +
                      "\n" +
                      builder + "\n");
    } catch (final Throwable t) {
      error(t);
      return resp.error(t);
    }

    getMailer().post(msg);
    return resp.ok();
  }

  @Override
  public String sendAccount(final String email) {
    final AccountInfo ainfo = getAccountByEmail(email);

    if (ainfo == null) {
      // Ignore it
      return null;
    }

    final Message msg = new Message();

    msg.setFrom(config.getMailFrom());

    final String[] to = { email };
    msg.setMailTo(to);
    msg.setSubject(config.getMailSubject());

    try {
      // Should be built from a template
      msg.setContent(
              "You requested your account for this email address\n" +
                      "\n" +
                      "Your account is " + ainfo.getAccount() + "\n" +
                      "\n" +
                      "If you did not make this request, please ignore this message\n" +
                      "\n");
    } catch (final Throwable t) {
      error(t);
      return t.getLocalizedMessage();
    }

    getMailer().post(msg);
    return null;
  }

  @Override
  public String sendForgotpw(final String account) {
    try {
      db.startTransaction();

      final AccountInfo ainfo = db.getAccount(account);

      if (ainfo == null) {
        // Ignore it
        return null;
      }

      setConfid(ainfo);

      db.updateAccount(ainfo);

      final Message msg = new Message();

      msg.setFrom(config.getMailFrom());

      final String[] to = { ainfo.getEmail() };
      msg.setMailTo(to);
      msg.setSubject(config.getMailSubject());

      try {
        final URIBuilder builder = new URIBuilder(config.getNewpwUrl());

        builder.addParameter("confid", ainfo.getConfid());

        // Should be built from a template
        msg.setContent(
                "We have a request to change your password for this email address\n" +
                        "\n" +
                        "If you did not make this request, please ignore this message\n" +
                        "\n" +
                        "Otherwise, click on, or copy and paste into your browser, " +
                        "the confirmation link below.\n" +
                        "\n" +
                        builder + "\n");
      } catch (final Throwable t) {
        error(t);
        return t.getLocalizedMessage();
      }

      getMailer().post(msg);
      return null;
    } finally {
      db.endTransaction();
    }
  }

  @Override
  public String setpw(final String confid,
                      final String pw)  {
    try {
      db.startTransaction();

      final AccountInfo ainfo = db.getAccountByConfid(confid);

      if (ainfo == null) {
        // Ignore it
        return "The confirmation id is invalid";
      }

      // Prevent reuse
      setConfid(ainfo);

      final String thePw;

      if (config.getPwIsToken()) {
        thePw = ainfo.getConfid();
      } else {
        thePw = pw;
      }

      ainfo.setPw(encodedPassword(thePw, false));

      db.updateAccount(ainfo);

      final Message msg = new Message();

      msg.setFrom(config.getMailFrom());

      final String[] to = { ainfo.getEmail() };
      msg.setMailTo(to);

      String content = "Your password for account " +
              ainfo.getAccount() +
              " has been ";

      if (config.getPwIsToken()) {
        content += "set to " + ainfo.getConfid();
      } else {
        content += "updated.";
      }

      msg.setSubject(config.getMailSubject() + ": password changed");
      msg.setContent(content);

      getMailer().post(msg);
      return null;
    } finally {
      db.endTransaction();
    }
  }

  @Override
  public AccountInfo getAccount(final String account)
          {
    try {
      db.startTransaction();
      return db.getAccount(account);
    } finally {
      db.endTransaction();
    }
  }

  @Override
  public AccountInfo getAccountByConfid(final String confId)
          {
    try {
      db.startTransaction();
      return db.getAccountByConfid(confId);
    } finally {
      db.endTransaction();
    }
  }

  @Override
  public AccountInfo getAccountByEmail(final String email) {
    try {
      db.startTransaction();
      return db.getAccountByEmail(email);
    } finally {
      db.endTransaction();
    }
  }

  @Override
  public boolean deleteAccount(final String confId)
          {
    try {
      db.startTransaction();
      final AccountInfo ainfo = db.getAccountByConfid(confId);

      if (ainfo == null) {
        return false;
      }

      db.removeAccount(ainfo);

      return true;
    } finally {
      db.endTransaction();
    }
  }

  @Override
  public String confirm(final String confId) {
    try {
      db.startTransaction();
      final AccountInfo ainfo = db.getAccountByConfid(confId);

      if (ainfo == null) {
        return null;
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
          return null;
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

      String content = "Your account " +
              ainfo.getAccount();

      if (config.getPwIsToken()) {
        content += " with password " + ainfo.getConfid();
      }

      content += " has been created.";

      msg.setSubject(config.getMailSubject() + ": success");
      msg.setContent(content);

      getMailer().post(msg);
      return ainfo.getAccount();
    } finally {
      db.endTransaction();
    }
  }

  @Override
  public boolean lostId(final String email) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean lostPw(final String id) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean confirmPwChange(final String confid) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean confirmPwChange(final String confid,
                                 final String newPw) {
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
                               final String encodedPw) {
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

    setConfid(ainfo);
    ainfo.setAccount(accountName);
    ainfo.setFirstName(firstName);
    ainfo.setLastName(lastName);
    ainfo.setEmail(email);
    ainfo.setDtstamp(new Timestamp(System.currentTimeMillis()).toString());
    ainfo.setEnabled(true);

    if ((pw != null) && (encodedPw == null)) {
      ainfo.setPw(encodedPassword(pw, false));
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

  private void setConfid(final AccountInfo ainfo) {
    if (config.getPwIsToken()) {
      ainfo.setConfid(PasswordGenerator.generate(10));
    } else {
      ainfo.setConfid(UUID.randomUUID().toString());
    }
  }

  private boolean createLdapAccount(final String accountName,
                                    final String firstName,
                                    final String lastName,
                                    final String email,
                                    final String pw,
                                    final String encodedPw) {
    try {
      /* Build a directory record and add the attributes
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
        dirRec.setAttr("userPassword", encodedPassword(pw, true));
      } else if (encodedPw != null) {
        dirRec.setAttr("userPassword", encodedPw);
      }

      /* Posix account requires these but we just set them to dummy values
       * /
      dirRec.setAttr("homeDirectory", "dummy");
      dirRec.setAttr("loginShell", "dummy");
      dirRec.setAttr("uidNumber", "999");
      dirRec.setAttr("gidNumber", "999");
      */

      final LdapDirectory dir = getLdir();

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
   */
  @Override
  public String displayAccount(final String account) {
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
                              final String password) {
    final BasicAttribute attr =
            new BasicAttribute("userPassword",
                               encodedPassword(password, true));
    final ModificationItem mi = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                                               attr);

    final ModificationItem[] mods = {mi};
    getLdir().modify(accountDn(account), mods);
  }

  @Override
  public boolean createGroup(final String group,
                             final String account) {
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
                               final String account) {
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

  private LdapDirectory getLdir() {
    try {
      if (ldir != null) {
        return ldir;
      }

      final Properties pr = new Properties();

      pr.put(Context.PROVIDER_URL, config.getLdapUrl());

      ldir = new LdapDirectory(pr, config.getAdminId(),
                               config.getAdminPw());

      return ldir;
    } catch (final Throwable t) {
      throw new SelfregException(t);
    }
  }

  private String encodedPassword(final String pw,
                                 final boolean ldap) {
    try {
      /*
      final MessageDigest md = MessageDigest.getInstance(pwEncryption);

      md.update(pw.getBytes());

      final byte[] b64s = new Base64().encode(md.digest());

      return "{" + pwEncryption + "}" + new String(b64s);
      */
      final var encryptor =
              switch (config.getMessageDigest()) {
                case "SSHA" ->
                        new RFC2307SSHAPasswordEncryptor();
                case "SHA" ->
                        new RFC2307SHAPasswordEncryptor();
                case "MD5" ->
                        new RFC2307MD5PasswordEncryptor();
                default -> throw new SelfregException(
                        "Unsupported message digest");
              };

      final String encpw = encryptor.encryptPassword(pw);
      final String prefix = "{" + config.getMessageDigest() + "}";

      if (!encpw.startsWith(prefix)) {
        throw new SelfregException("Doesn't start with " + prefix);
      }

      if (ldap) {
        return encpw;
      }

      return encpw.substring(prefix.length());
    } catch (final Throwable t) {
      throw new SelfregException(t);
    }
  }

  private boolean sendConfirm(final String text,
                              final String subject,
                              final String email) {
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

  private MailerIntf getMailer() {
    if (mailer == null) {
      mailer = new Mailer();

      mailer.init(config);
    }

    return mailer;
  }

  /* ==============================================================
   *                   Logged methods
   * ============================================================== */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}
