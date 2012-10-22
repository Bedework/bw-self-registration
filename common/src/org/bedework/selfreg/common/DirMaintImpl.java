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

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.bedework.selfreg.common.mail.Mailer;
import org.bedework.selfreg.common.mail.MailerIntf;
import org.bedework.selfreg.common.mail.Message;

public class DirMaintImpl implements DirMaint {
  private boolean debug = true;

  private transient Logger log;

  private String fromUri = "bedework@bedework.org";

  private String ldapUrl = "ldap://localhost:10389";
  private String baseDn = "dc=bedework, dc=org";

  private String accountsOu = "accounts";
  private String accountsDn = "ou=" + accountsOu + ", " + baseDn;
  private String accountsAttr = "uid";

  private String groupsOu = "groups";
  private String groupsDn = "ou=" + groupsOu + ", " + baseDn;

  private LdapDirectory ldir;

  private String adminId = "uid=admin,ou=system";

  private String adminPw = "secret";

  private static final String pwEncryption = "SHA";

  private MailerIntf mailer;

  @Override
  public String requestId(final String accountName,
                          final String firstName,
                          final String lastName,
                          final String email,
                          final String pw) throws Throwable {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean confirm(final String confId) throws Throwable {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean createAccount(final String accountName,
                               final String firstName,
                               final String lastName,
                               final String email,
                               final String pw) throws Throwable {
    /** Build a directory record and add the attributes
     */
    DirRecord dirRec = new BasicDirRecord();

    String userDn = accountsAttr + "=" + accountName + ", " + accountsDn;
    dirRec.setDn(userDn);
    dirRec.setAttr(accountsAttr, accountName);
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

    getLdir().create(dirRec);

    return false;
  }

  @Override
  public boolean lostId(final String email) throws Throwable {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean lostPw(final String id) throws Throwable {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean confirmPwChange(final String confid) throws Throwable {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean confirmPwChange(final String confid,
                                 final String newPw) throws Throwable {
    // TODO Auto-generated method stub
    return false;
  }

  private LdapDirectory getLdir() throws Throwable {
    if (ldir != null) {
      return ldir;
    }

    Properties pr = new Properties();

    pr.put(Context.PROVIDER_URL, ldapUrl);

    ldir = new LdapDirectory(pr, adminId, adminPw, debug);

    return ldir;
  }

  private String encodedPassword(final char[] pw) throws Exception {
    MessageDigest md = MessageDigest.getInstance(pwEncryption);

    md.update(new String(pw).getBytes());

    byte[] b64s = new Base64().encode(md.digest());

    return "{" + pwEncryption + "}" + new String(b64s);
  }

  private boolean sendConfirm(final String text,
                              final String subject,
                              final String email) throws Throwable {
    if (email == null) {
      return false;
    }

    Message emsg = new Message();

    String[] to = new String[]{email};
    emsg.setMailTo(to);

    emsg.setFrom(fromUri);
    emsg.setSubject(subject);
    emsg.setContent(text);

    getMailer().post(emsg);

    return true;
  }

  private MailerIntf getMailer() throws Throwable {
    if (mailer == null) {
      mailer = new Mailer();

      mailer.init();
    }

    return mailer;
  }
}
