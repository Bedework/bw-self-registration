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
package org.bedework.selfreg.service;

import org.bedework.util.config.ConfInfo;
import org.bedework.util.config.OrmConfigI;
import org.bedework.util.jmx.MBeanInfo;

/** Properties for mailers.
 *
 * @author douglm
 *
 */
@ConfInfo(elementName = "selfreg")
public interface SelfregConfigProperties extends OrmConfigI {
  /**
   *
   * @param val True if accounts should be copied to ldap
   */
  void setUseLdap(boolean val);

  /**
   * @return boolean True if accounts should be copied to ldap
   */
  @MBeanInfo(" True if accounts should be copied to ldap")
  boolean getUseLdap();

  /**
   *
   * @param val the value
   */
  void setLdapUrl(String val);

  /**
   * @return String
   */
  @MBeanInfo("Url of ldap server")
  String getLdapUrl();

  /**
   *
   * @param val base dn
   */
  void setBaseDn(String val);

  /**
   * @return String
   */
  @MBeanInfo("Base dn for searches")
  String getBaseDn();

  /**
   *
   * @param val accounts ou
   */
  void setAccountsOu(String val);

  /**
   * @return String
   */
  @MBeanInfo("Ldap accounts ou")
  String getAccountsOu();

  /**
   *
   * @param val Ldap accounts dn
   */
  void setAccountsDn(String val);

  /**
   * @return String
   */
  @MBeanInfo("Ldap accounts dn")
  String getAccountsDn();

  /**
   *
   * @param val Ldap accounts attribute
   */
  void setAccountsAttr(String val);

  /**
   * @return String
   */
  @MBeanInfo("Ldap accounts attribute")
  String getAccountsAttr();

  /**
   *
   * @param val an ou
   */
  void setGroupsOu(String val);

  /**
   * @return String
   */
  @MBeanInfo("Ldap groups ou")
  String getGroupsOu();

  /**
   *
   * @param val dn
   */
  void setGroupsDn(String val);

  /**
   * @return String
   */
  @MBeanInfo("Ldap groups dn")
  String getGroupsDn();

  /**
   *
   * @param val ldap attr 
   */
  void setGroupsAttr(String val);

  /**
   * @return String
   */
  @MBeanInfo("Ldap groups attribute")
  String getGroupsAttr();

  /**
   *
   * @param val an id
   */
  void setAdminId(String val);

  /**
   * @return String
   */
  @MBeanInfo("Ldap admin id")
  String getAdminId();

  /**
   *
   * @param val a password
   */
  void setAdminPw(String val);

  /**
   * @return String
   */
  @MBeanInfo("Ldap admin pw")
  String getAdminPw();

  /**
   *
   * @param val valid protocol for which an implementation exists, e.g "imap", "smtp"
   */
  void setMailProtocol(String val);

  /**
   * @return String valid protocol for which an implementation exists, e.g "imap", "smtp"
   */
  @MBeanInfo("valid protocol for which an implementation exists, e.g \"imap\", \"smtp\"")
  String getMailProtocol();

  /**
   *
   * @param val Implementation for the selected protocol
   */
  void setMailProtocolClass(String val);

  /**
   * @return String
   */
  @MBeanInfo("Implementation class for the selected protocol")
  String getMailProtocolClass();

  /** Where we send it.
   *
   * @param val Mail server ip
   */
  void setMailServerHost(String val);

  /**
   * @return String
   */
  @MBeanInfo("Mail server ip")
  String getMailServerHost();

  /**
   * @param val Mail server port
   */
  void setMailServerPort(String val);

  /**
   * @return String
   */
  @MBeanInfo("Mail server port")
  String getMailServerPort();

  /**
   *
   * @param val mail server account
   */
  void setMailServerAccount(String val);

  /**
   * @return String
   */
  @MBeanInfo("mail server account")
  String getMailServerAccount();

  /**
   *
   * @param val mail server pw
   */
  void setMailServerPw(String val);

  /**
   * @return String
   */
  @MBeanInfo("mail server password")
  String getMailServerPw();

  /** Mailer:
   *
   * @param val Mail 'from'
   */
  void setMailFrom(String val);

  /** Mailer:
   /**
   * @return String
   */
  @MBeanInfo("Mail 'from'")
  String getMailFrom();

  /** Mailer: Subject we use when none supplied
   *
   * @param val Mailer: Subject we use when none supplied
   */
  void setMailSubject(String val);

  /**
   * @return String
   */
  @MBeanInfo("Mailer: Subject we use when none supplied")
  String getMailSubject();

  /** Allow mailer to be disabled
   *
   * @param val Allow mailer to be disabled
   */
  void setMailDisabled(boolean val);

  /**
   * @return boolean
   */
  @MBeanInfo("Allow mailer to be disabled")
  boolean getMailDisabled();

  /**
   *
   * @param val the confirmation url
   */
  void setConfirmUrl(String val);

  /**
   * @return String
   */
  @MBeanInfo("The url of the web service entry point for confirmations embedded in email")
  String getConfirmUrl();

  /**
   *
   * @param val the newpw url
   */
  void setNewpwUrl(String val);

  /**
   * @return String
   */
  @MBeanInfo("The url of the web service entry point for new pw changes embedded in email")
  String getNewpwUrl();

  /**
   *
   * @param val the confirmation url
   */
  void setConfirmForward(String val);

  /**
   * @return String
   */
  @MBeanInfo("The url we redirect to after new id confirmation")
  String getConfirmForward();

  /**
   *
   * @param val the newpw url
   */
  void setNewpwForward(String val);

  /**
   * @return String
   */
  @MBeanInfo("The url we redirect to after new pw changes")
  String getNewpwForward();

  /**
   *
   * @param val path to a file to write the confirm url into
   */
  void setTestConfirmFile(String val);

  /**
   * @return String
   */
  @MBeanInfo("The path to a file to write the confirm url into")
  String getTestConfirmFile();

  /**
   *
   * @param val the message digest (SHA, MD5 etc)
   */
  void setMessageDigest(String val);

  /**
   * @return String
   */
  @MBeanInfo("The MessageDigest")
  String getMessageDigest();

  /**
   *
   * @param val new account prefix
   */
  void setAccountPrefix(String val);

  /**
   * @return String
   */
  @MBeanInfo("The account prefix")
  String getAccountPrefix();

  /**
   *
   * @param val true if unauthenticated users can start the process
   */
  void setUnauthCanRegister(boolean val);

  /**
   *
   * @return true if unauthenticated users can start the process
   */
  @MBeanInfo("True if unauthenticated users can start the process")
  boolean getUnauthCanRegister();

  /**
   * 
   * @param val true if can specify account
   */
  void setCanSpecifyAccount(boolean val);

  /**
   * 
   * @return true if can specify account
   */
  @MBeanInfo("True if can specify account")
  boolean getCanSpecifyAccount();

  /**
   *
   * @param val true if account derived from email
   */
  void setAccountFromEmail(boolean val);

  /**
   *
   * @return true if account derived from email
   */
  @MBeanInfo("True if account derived from email")
  boolean getAccountFromEmail();

  /**
   *
   * @param val true if pw is token we provide
   */
  void setPwIsToken(boolean val);

  /**
   *
   * @return true if pw is token we provide
   */
  @MBeanInfo("True if account derived from email")
  boolean getPwIsToken();

  /**
   *
   * @param val public key
   */
  void setCaptchaPublicKey(String val);

  /**
   * @return String
   */
  @MBeanInfo("The captcha public key - obtained from captcha")
  String getCaptchaPublicKey();

  /**
   *
   * @param val private key
   */
  void setCaptchaPrivateKey(String val);

  /**
   * @return String
   */
  @MBeanInfo("The captcha private key - obtained from captcha")
  String getCaptchaPrivateKey();

  /* ========================================================================
   * Schema
   * ======================================================================== */

  /** Output file name - full path
   *
   * @param val path
   */
  void setSchemaOutFile(String val);

  /**
   * @return Output file name - full path
   */
  @MBeanInfo("Full path of schema output file")
  String getSchemaOutFile();
}
