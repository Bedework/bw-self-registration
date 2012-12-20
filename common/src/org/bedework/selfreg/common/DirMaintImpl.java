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

import java.security.MessageDigest;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.bedework.selfreg.common.dir.BasicDirRecord;
import org.bedework.selfreg.common.dir.DirRecord;
import org.bedework.selfreg.common.dir.Directory;
import org.bedework.selfreg.common.dir.Directory.DirSearchResult;
import org.bedework.selfreg.common.dir.LdapDirectory;
import org.bedework.selfreg.common.exception.SelfregException;
import org.bedework.selfreg.common.mail.Mailer;
import org.bedework.selfreg.common.mail.MailerIntf;
import org.bedework.selfreg.common.mail.Message;

/** Handle accounts.
 *
 */
public class DirMaintImpl implements DirMaint {
  private boolean debug = true;

  private transient Logger log;

  private SelfregConfigProperties config;

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
  }

  @Override
  public String requestId(final String accountName,
                          final String firstName,
                          final String lastName,
                          final String email,
                          final String pw) throws SelfregException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean confirm(final String confId) throws SelfregException {
    // TODO Auto-generated method stub
    return false;
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
                               final String pw) throws SelfregException {
    try {
      /** Build a directory record and add the attributes
       */
      DirRecord dirRec = new BasicDirRecord();

      String userDn = accountDn(accountName);
      dirRec.setDn(userDn);
      dirRec.setAttr(config.getAccountsAttr(), accountName);
      dirRec.setAttr("objectclass", "top");
      dirRec.setAttr("objectclass", "person");
      dirRec.setAttr("objectclass", "organizationalPerson");
      dirRec.setAttr("objectclass", "inetOrgPerson");

      String cn = lastName + ", " + firstName;
      dirRec.setAttr("cn", cn);
      //dirRec.setAttr("gecos", cn);
      dirRec.setAttr("sn", lastName);
      dirRec.setAttr("mail", email);

      if (pw != null) {
        dirRec.setAttr("userPassword", encodedPassword(pw.toCharArray()));
      }

      /* Posix account requires these but we just set them to dummy values
       * /
      dirRec.setAttr("homeDirectory", "dummy");
      dirRec.setAttr("loginShell", "dummy");
      dirRec.setAttr("uidNumber", "999");
      dirRec.setAttr("gidNumber", "999");
      */

      return getLdir().create(dirRec);
    } catch (SelfregException se) {
      throw se;
    } catch (Throwable t) {
      throw new SelfregException(t);
    }
  }

  /** See if account exists
   *
   * @param account
   * @return boolean
   * @throws SelfregException
   */
  @Override
  public String displayAccount(final String account) throws SelfregException {
    StringBuilder sb = new StringBuilder();

    sb.append("(&(");
    sb.append(config.getAccountsAttr());
    sb.append("=");
    sb.append(account);
    sb.append("))(objectClass=inetOrgPerson)");

    DirSearchResult dsr = getLdir().search(config.getAccountsDn(),
                                           sb.toString(),
                                           Directory.scopeOne);

    if (dsr == null) {
      return "No entry found";
    }

    DirRecord dr = dsr.nextRecord();
    if (dr == null) {
      return "No entry found";
    }

    return dr.toString();
  }

  @Override
  public void setUserPassword(final String account,
                              final String password) throws SelfregException {
    BasicAttribute attr = new BasicAttribute("userPassword",
                                             encodedPassword(password.toCharArray()));
    ModificationItem mi = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                                               attr);

    ModificationItem[] mods = {mi};
    getLdir().modify(accountDn(account), mods);
  }

  @Override
  public boolean createGroup(final String group,
                             final String account) throws SelfregException {
    try {
      DirRecord dirRec = new BasicDirRecord();

      dirRec.setDn(groupDn(group));
      dirRec.setAttr("cn", group);
      dirRec.setAttr("objectclass", "top");
      dirRec.setAttr("objectclass", "groupOfNames");

      dirRec.setAttr("member", accountDn(account));

      return getLdir().create(dirRec);
    } catch (SelfregException se) {
      throw se;
    } catch (Throwable t) {
      throw new SelfregException(t);
    }
  }

  @Override
  public void addGroupMember(final String group,
                               final String account) throws SelfregException {
    //if (!accountExists(account)) {
    //  error("Account " + account + " does not exist");
    //}

    BasicAttribute attr = new BasicAttribute("member",
                                             accountDn(account));
    ModificationItem mi = new ModificationItem(DirContext.ADD_ATTRIBUTE,
                                               attr);

    ModificationItem[] mods = {mi};
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
    if (ldir != null) {
      return ldir;
    }

    Properties pr = new Properties();

    pr.put(Context.PROVIDER_URL, config.getLdapUrl());

    ldir = new LdapDirectory(pr, config.getAdminId(), config.getAdminPw(), debug);

    return ldir;
  }

  private String encodedPassword(final char[] pw) throws SelfregException {
    try {
      MessageDigest md = MessageDigest.getInstance(pwEncryption);

      md.update(new String(pw).getBytes());

      byte[] b64s = new Base64().encode(md.digest());

      return "{" + pwEncryption + "}" + new String(b64s);
    } catch (Throwable t) {
      throw new SelfregException(t);
    }
  }

  private boolean sendConfirm(final String text,
                              final String subject,
                              final String email) throws SelfregException {
    if (email == null) {
      return false;
    }

    Message emsg = new Message();

    String[] to = new String[]{email};
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
