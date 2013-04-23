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

import edu.rpi.cmt.config.ConfigBase;

import javax.xml.namespace.QName;

/** Properties for mailers.
 *
 * @author douglm
 *
 */
public class SelfregConfigProperties extends ConfigBase<SelfregConfigProperties> {
  /** */
  public final static QName confElement = new QName(ns, "bwselfreg");

  private static final QName ldapUrlProperty = new QName(ns, "ldapUrl");

  private static final QName baseDnProperty = new QName(ns, "baseDn");

  private static final QName accountsOuProperty = new QName(ns, "accountsOu");

  private static final QName accountsDnProperty = new QName(ns, "accountsDn");

  private static final QName accountsAttrProperty = new QName(ns, "accountsAttr");

  private static final QName groupsOuProperty = new QName(ns, "groupsOu");

  private static final QName groupsDnProperty = new QName(ns, "groupsDn");

  private static final QName groupsAttrProperty = new QName(ns, "groupsAttr");

  private static final QName adminIdProperty = new QName(ns, "adminId");

  private static final QName adminPwProperty = new QName(ns, "adminPw");

  private static final QName mailProtocolProperty = new QName(ns, "mailProtocol");

  private static final QName mailProtocolClassProperty = new QName(ns, "mailProtocolClass");

  private static final QName mailServerIpProperty = new QName(ns, "mailServerIp");

  private static final QName mailServerPortProperty = new QName(ns, "mailServerPort");

  private static final QName mailFromProperty = new QName(ns, "mailFrom");

  private static final QName mailSubjectProperty = new QName(ns, "mailSubject");

  private static final QName mailDisabledProperty = new QName(ns, "mailDisabled");

  @Override
  public QName getConfElement() {
    return confElement;
  }

  /**
  *
  * @param val
  */
  public void setLdapUrl(final String val)  {
    setProperty(ldapUrlProperty, val);
  }

  /**
   * @return String
   */
  public String getLdapUrl()  {
    return getPropertyValue(ldapUrlProperty);
  }

  /**
  *
  * @param val
  */
  public void setBaseDn(final String val)  {
    setProperty(baseDnProperty, val);
  }

  /**
   * @return String
   */
  public String getBaseDn()  {
    return getPropertyValue(baseDnProperty);
  }

  /**
   *
   * @param val
   */
  public void setAccountsOu(final String val)  {
    setProperty(accountsOuProperty, val);
  }

  /**
   * @return String
   */
  public String getAccountsOu()  {
    return getPropertyValue(accountsOuProperty);
  }

  /**
   *
   * @param val
   */
  public void setAccountsDn(final String val)  {
    setProperty(accountsDnProperty, val);
  }

  /**
   * @return String
   */
  public String getAccountsDn()  {
    return getPropertyValue(accountsDnProperty);
  }

  /**
   *
   * @param val
   */
  public void setAccountsAttr(final String val)  {
    setProperty(accountsAttrProperty, val);
  }

  /**
   * @return String
   */
  public String getAccountsAttr()  {
    return getPropertyValue(accountsAttrProperty);
  }

  /**
   *
   * @param val
   */
  public void setGroupsOu(final String val)  {
    setProperty(groupsOuProperty, val);
  }

  /**
   * @return String
   */
  public String getGroupsOu()  {
    return getPropertyValue(groupsOuProperty);
  }

  /**
   *
   * @param val
   */
  public void setGroupsDn(final String val)  {
    setProperty(groupsDnProperty, val);
  }

  /**
   * @return String
   */
  public String getGroupsDn()  {
    return getPropertyValue(groupsDnProperty);
  }

  /**
   *
   * @param val
   */
  public void setGroupsAttr(final String val)  {
    setProperty(groupsAttrProperty, val);
  }

  /**
   * @return String
   */
  public String getGroupsAttr()  {
    return getPropertyValue(groupsAttrProperty);
  }

  /**
   *
   * @param val
   */
  public void setAdminId(final String val)  {
    setProperty(adminIdProperty, val);
  }

  /**
   * @return String
   */
  public String getAdminId()  {
    return getPropertyValue(adminIdProperty);
  }

  /**
   *
   * @param val
   */
  public void setAdminPw(final String val)  {
    setProperty(adminPwProperty, val);
  }

  /**
   * @return String
   */
  public String getAdminPw()  {
    return getPropertyValue(adminPwProperty);
  }

  /**
   *
   * @param val
   */
  public void setMailProtocol(final String val)  {
    setProperty(mailProtocolProperty, val);
  }

  /**
   * @return String
   */
  public String getMailProtocol()  {
    return getPropertyValue(mailProtocolProperty);
  }

  /**
   *
   * @param val
   */
  public void setMailProtocolClass(final String val)  {
    setProperty(mailProtocolClassProperty, val);
  }

  /**
   * @return String
   */
  public String getMailProtocolClass()  {
    return getPropertyValue(mailProtocolClassProperty);
  }

  /**
   *
   * @param val
   */
  public void setMailServerIp(final String val)  {
    setProperty(mailServerIpProperty, val);
  }

  /**
   * @return String
   */
  public String getMailServerIp()  {
    return getPropertyValue(mailServerIpProperty);
  }

  /**
   *
   * @param val
   */
  public void setMailServerPort(final String val)  {
    setProperty(mailServerPortProperty, val);
  }

  /**
   * @return String
   */
  public String getMailServerPort()  {
    return getPropertyValue(mailServerPortProperty);
  }

  /**
   *
   * @param val
   */
  public void setMailFrom(final String val)  {
    setProperty(mailFromProperty, val);
  }

  /**
   * @return String
   */
  public String getMailFrom()  {
    return getPropertyValue(mailFromProperty);
  }

  /**
   *
   * @param val
   */
  public void setMailSubject(final String val)  {
    setProperty(mailSubjectProperty, val);
  }

  /**
   * @return String
   */
  public String getMailSubject()  {
    return getPropertyValue(mailSubjectProperty);
  }

  /**
   *
   * @param val
   */
  public void setMailDisabled(final boolean val)  {
    setBooleanProperty(mailDisabledProperty, val);
  }

  /**
   * @return boolean
   */
  public boolean getMailDisabled()  {
    return getBooleanPropertyValue(mailDisabledProperty);
  }
}
