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

/** Properties for mailers.
 *
 * @author douglm
 *
 */
public class SelfregConfigProperties implements SelfregConfigPropertiesI {
  private String ldapUrl;

  private String baseDn;

  private String accountsOu;

  private String accountsDn;

  private String accountsAttr;

  private String groupsOu;

  private String groupsDn;

  private String adminId;

  private String adminPw;

  private String mailProtocol;

  private String mailProtocolClass;

  private String mailServerIp;

  private String mailServerPort;

  private String mailFrom;

  private String mailSubject;

  private boolean mailDisabled;

  @Override
  public void setLdapUrl(final String val)  {
    ldapUrl  = val;
  }

  @Override
  public String getLdapUrl()  {
    return ldapUrl;
  }

  @Override
  public void setBaseDn(final String val)  {
    baseDn = val;
  }

  @Override
  public String getBaseDn()  {
    return baseDn;
  }

  @Override
  public void setAccountsOu(final String val)  {
    accountsOu  = val;
  }

  @Override
  public String getAccountsOu()  {
    return accountsOu;
  }

  @Override
  public void setAccountsDn(final String val)  {
    accountsDn  = val;
  }

  @Override
  public String getAccountsDn()  {
    return accountsDn;
  }

  @Override
  public void setAccountsAttr(final String val)  {
    accountsAttr  = val;
  }

  @Override
  public String getAccountsAttr()  {
    return accountsAttr;
  }

  @Override
  public void setGroupsOu(final String val)  {
    groupsOu  = val;
  }

  @Override
  public String getGroupsOu()  {
    return groupsOu;
  }

  @Override
  public void setGroupsDn(final String val)  {
    groupsDn  = val;
  }

  @Override
  public String getGroupsDn()  {
    return groupsDn;
  }

  @Override
  public void setAdminId(final String val)  {
    adminId  = val;
  }

  @Override
  public String getAdminId()  {
    return adminId;
  }

  @Override
  public void setAdminPw(final String val)  {
    adminPw  = val;
  }

  @Override
  public String getAdminPw()  {
    return adminPw;
  }

  @Override
  public void setMailProtocol(final String val)  {
    mailProtocol  = val;
  }

  @Override
  public String getMailProtocol()  {
    return mailProtocol;
  }

  @Override
  public void setMailProtocolClass(final String val)  {
    mailProtocolClass  = val;
  }

  @Override
  public String getMailProtocolClass()  {
    return mailProtocolClass;
  }

  @Override
  public void setMailServerIp(final String val)  {
    mailServerIp  = val;
  }

  @Override
  public String getMailServerIp()  {
    return mailServerIp;
  }

  @Override
  public void setMailServerPort(final String val)  {
    mailServerPort  = val;
  }

  @Override
  public String getMailServerPort()  {
    return mailServerPort;
  }

  @Override
  public void setMailFrom(final String val)  {
    mailFrom  = val;
  }

  @Override
  public String getMailFrom()  {
    return mailFrom;
  }

  @Override
  public void setMailSubject(final String val)  {
    mailSubject = val;
  }

  @Override
  public String getMailSubject()  {
    return mailSubject;
  }

  @Override
  public void setMailDisabled(final boolean val)  {
    mailDisabled = val;
  }

  @Override
  public boolean getMailDisabled()  {
    return mailDisabled;
  }
}
