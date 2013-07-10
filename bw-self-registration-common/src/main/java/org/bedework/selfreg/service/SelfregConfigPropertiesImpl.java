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
import edu.rpi.cmt.config.ConfigBase;

/** Properties for mailers.
 *
 * @author douglm
 *
 */
public class SelfregConfigPropertiesImpl
        extends ConfigBase<SelfregConfigPropertiesImpl>
        implements SelfregConfigProperties {
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

  private String mailServerIp;

  private String mailServerPort;

  private String mailFrom;

  private String mailSubject;

  private boolean mailDisabled;

  /**
   *
   * @param val
   */
  @Override
  public void setLdapUrl(final String val)  {
    ldapUrl = val;
  }

  /**
   * @return String
   */
  public String getLdapUrl()  {
    return ldapUrl;
  }

  /**
  *
  * @param val
  */
  public void setBaseDn(final String val)  {
    baseDn = val;
  }

  /**
   * @return String
   */
  public String getBaseDn()  {
    return baseDn;
  }

  /**
   *
   * @param val
   */
  public void setAccountsOu(final String val)  {
    accountsOu = val;
  }

  /**
   * @return String
   */
  public String getAccountsOu()  {
    return accountsOu;
  }

  /**
   *
   * @param val
   */
  public void setAccountsDn(final String val)  {
    accountsDn = val;
  }

  /**
   * @return String
   */
  public String getAccountsDn()  {
    return accountsDn;
  }

  /**
   *
   * @param val
   */
  public void setAccountsAttr(final String val)  {
    accountsAttr = val;
  }

  /**
   * @return String
   */
  public String getAccountsAttr()  {
    return accountsAttr;
  }

  /**
   *
   * @param val
   */
  public void setGroupsOu(final String val)  {
    groupsOu = val;
  }

  /**
   * @return String
   */
  public String getGroupsOu()  {
    return groupsOu;
  }

  /**
   *
   * @param val
   */
  public void setGroupsDn(final String val)  {
    groupsDn = val;
  }

  /**
   * @return String
   */
  public String getGroupsDn()  {
    return groupsDn;
  }

  /**
   *
   * @param val
   */
  public void setGroupsAttr(final String val)  {
    groupsAttr = val;
  }

  /**
   * @return String
   */
  public String getGroupsAttr()  {
    return groupsAttr;
  }

  /**
   *
   * @param val
   */
  public void setAdminId(final String val)  {
    adminId = val;
  }

  /**
   * @return String
   */
  public String getAdminId()  {
    return adminId;
  }

  /**
   *
   * @param val
   */
  public void setAdminPw(final String val)  {
    adminPw = val;
  }

  /**
   * @return String
   */
  public String getAdminPw()  {
    return adminPw;
  }

  /**
   *
   * @param val
   */
  public void setMailProtocol(final String val)  {
    mailProtocol = val;
  }

  /**
   * @return String
   */
  public String getMailProtocol()  {
    return mailProtocol;
  }

  /**
   *
   * @param val
   */
  public void setMailProtocolClass(final String val)  {
    mailProtocolClass = val;
  }

  /**
   * @return String
   */
  public String getMailProtocolClass()  {
    return mailProtocolClass;
  }

  /**
   *
   * @param val
   */
  public void setMailServerIp(final String val)  {
    mailServerIp = val;
  }

  /**
   * @return String
   */
  public String getMailServerIp()  {
    return mailServerIp;
  }

  /**
   *
   * @param val
   */
  public void setMailServerPort(final String val)  {
    mailServerPort = val;
  }

  /**
   * @return String
   */
  public String getMailServerPort()  {
    return mailServerPort;
  }

  /**
   *
   * @param val
   */
  public void setMailFrom(final String val)  {
    mailFrom = val;
  }

  /**
   * @return String
   */
  public String getMailFrom()  {
    return mailFrom;
  }

  /**
   *
   * @param val
   */
  public void setMailSubject(final String val)  {
    mailSubject = val;
  }

  /**
   * @return String
   */
  public String getMailSubject()  {
    return mailSubject;
  }

  /**
   *
   * @param val
   */
  public void setMailDisabled(final boolean val)  {
    mailDisabled = val;
  }

  /**
   * @return boolean
   */
  public boolean getMailDisabled()  {
    return mailDisabled;
  }
}
