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

import org.bedework.selfreg.common.SelfregConfigProperties;
import org.bedework.util.config.ConfInfo;
import org.bedework.util.config.HibernateConfigBase;
import org.bedework.util.jmx.MBeanInfo;

/** Properties for mailers.
 *
 * @author douglm
 *
 */
@ConfInfo(elementName = "selfreg")
public class SelfregConfigPropertiesImpl
        extends HibernateConfigBase<SelfregConfigPropertiesImpl>
        implements SelfregConfigProperties {
  private boolean useLdap;

  private String ldapUrl;

  private String baseDn;

  private String accountsOu;

  private String accountsDn;

  private String accountsAttr;

  private String groupsOu;

  private String groupsDn;

  private String groupsAttr;

  private String adminId;

  private String adminPw;

  private String mailProtocol;

  private String mailProtocolClass;

  private String mailServerHost;

  private String mailServerPort;

  private String mailServerAccount;

  private String mailServerPw;

  private String mailFrom;

  private String mailSubject;

  private boolean mailDisabled;

  private String confirmUrl;

  private String schemaOutFile;

  private String messageDigest;

  private String accountPrefix;

  @Override
  public void setUseLdap(final boolean val) {
    useLdap = val;
  }

  @Override
  public boolean getUseLdap() {
    return useLdap;
  }

  @Override
  public void setLdapUrl(final String val)  {
    ldapUrl = val;
  }

  @Override
  @MBeanInfo("Ldap server url")
  public String getLdapUrl()  {
    return ldapUrl;
  }

  @Override
  public void setBaseDn(final String val)  {
    baseDn = val;
  }

  @Override
  @MBeanInfo("Ldap server base dn")
  public String getBaseDn()  {
    return baseDn;
  }

  @Override
  public void setAccountsOu(final String val)  {
    accountsOu = val;
  }

  @Override
  @MBeanInfo("Ldap server accounts ou")
  public String getAccountsOu()  {
    return accountsOu;
  }

  @Override
  public void setAccountsDn(final String val)  {
    accountsDn = val;
  }

  @Override
  @MBeanInfo("Ldap server accounts dn")
  public String getAccountsDn()  {
    return accountsDn;
  }

  @Override
  public void setAccountsAttr(final String val)  {
    accountsAttr = val;
  }

  @Override
  @MBeanInfo("Ldap server accounts attribute: e.g. uid")
  public String getAccountsAttr()  {
    return accountsAttr;
  }

  @Override
  public void setGroupsOu(final String val)  {
    groupsOu = val;
  }

  @Override
  @MBeanInfo("Ldap server groups ou")
  public String getGroupsOu()  {
    return groupsOu;
  }

  @Override
  public void setGroupsDn(final String val)  {
    groupsDn = val;
  }

  @Override
  @MBeanInfo("Ldap server groups dn")
  public String getGroupsDn()  {
    return groupsDn;
  }

  @Override
  public void setGroupsAttr(final String val)  {
    groupsAttr = val;
  }

  @Override
  @MBeanInfo("Ldap server groups attribute: e.g. cn")
  public String getGroupsAttr()  {
    return groupsAttr;
  }

  @Override
  public void setAdminId(final String val)  {
    adminId = val;
  }

  @Override
  @MBeanInfo("Ldap server admin id")
  public String getAdminId()  {
    return adminId;
  }

  @Override
  public void setAdminPw(final String val)  {
    adminPw = val;
  }

  @Override
  @MBeanInfo("Ldap server admin pw")
  public String getAdminPw()  {
    return adminPw;
  }

  @Override
  public void setMailProtocol(final String val)  {
    mailProtocol = val;
  }

  @Override
  @MBeanInfo("mail protocol for confirmation: e.g. smtp")
  public String getMailProtocol()  {
    return mailProtocol;
  }

  @Override
  public void setMailProtocolClass(final String val)  {
    mailProtocolClass = val;
  }

  @Override
  public String getMailProtocolClass()  {
    return mailProtocolClass;
  }

  @Override
  public void setMailServerHost(final String val)  {
    mailServerHost = val;
  }

  @Override
  @MBeanInfo("mail server host")
  public String getMailServerHost()  {
    return mailServerHost;
  }

  @Override
  public void setMailServerPort(final String val)  {
    mailServerPort = val;
  }

  @Override
  @MBeanInfo("mail server port")
  public String getMailServerPort()  {
    return mailServerPort;
  }

  @Override
  public void setMailServerAccount(final String val)  {
    mailServerAccount = val;
  }

  @Override
  @MBeanInfo("mail server account")
  public String getMailServerAccount()  {
    return mailServerAccount;
  }

  @Override
  public void setMailServerPw(final String val)  {
    mailServerPw = val;
  }

  @Override
  @MBeanInfo("mail server password")
  public String getMailServerPw()  {
    return mailServerPw;
  }

  @Override
  public void setMailFrom(final String val)  {
    mailFrom = val;
  }

  @Override
  @MBeanInfo("mail from")
  public String getMailFrom()  {
    return mailFrom;
  }

  @Override
  public void setMailSubject(final String val)  {
    mailSubject = val;
  }

  @Override
  @MBeanInfo("mail subject")
  public String getMailSubject()  {
    return mailSubject;
  }

  @Override
  public void setMailDisabled(final boolean val)  {
    mailDisabled = val;
  }

  @Override
  @MBeanInfo("mail disabled")
  public boolean getMailDisabled()  {
    return mailDisabled;
  }

  @Override
  public void setConfirmUrl(final String val) {
    confirmUrl = val;
  }

  @Override
  public String getConfirmUrl() {
    return confirmUrl;
  }

  @Override
  public void setMessageDigest(final String val) {
    messageDigest = val;
  }

  @Override
  public String getMessageDigest() {
    return messageDigest;
  }

  @Override
  public void setAccountPrefix(final String val) {
    accountPrefix = val;
  }

  @Override
  public String getAccountPrefix() {
    return accountPrefix;
  }

  @Override
  public void setSchemaOutFile(final String val) {
    schemaOutFile = val;
  }

  @Override
  public String getSchemaOutFile() {
    return schemaOutFile;
  }
}
