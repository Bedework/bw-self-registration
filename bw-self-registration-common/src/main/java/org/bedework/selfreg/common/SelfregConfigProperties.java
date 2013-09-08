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

import org.bedework.util.config.ConfInfo;
import org.bedework.util.jmx.MBeanInfo;

import java.io.Serializable;

/** Properties for mailers.
 *
 * @author douglm
 *
 */
@ConfInfo(elementName = "selfreg")
public interface SelfregConfigProperties extends Serializable {
  /**
   *
   * @param val the value
   */
  void setLdapUrl(final String val);

  /**
   * @return String
   */
  @MBeanInfo("Url of ldap server")
  String getLdapUrl();

  /**
   *
   * @param val
   */
  void setBaseDn(final String val);

  /**
   * @return String
   */
  @MBeanInfo("Base dn for searches")
  String getBaseDn();

  /**
   *
   * @param val
   */
  void setAccountsOu(final String val);

  /**
   * @return String
   */
  @MBeanInfo("Ldap accounts ou")
  String getAccountsOu();

  /**
   *
   * @param val
   */
  void setAccountsDn(final String val);

  /**
   * @return String
   */
  @MBeanInfo("Ldap accounts dn")
  String getAccountsDn();

  /**
   *
   * @param val
   */
  void setAccountsAttr(final String val);

  /**
   * @return String
   */
  @MBeanInfo("Ldap accounts attribute")
  String getAccountsAttr();

  /**
   *
   * @param val
   */
  void setGroupsOu(final String val);

  /**
   * @return String
   */
  @MBeanInfo("Ldap groups ou")
  String getGroupsOu();

  /**
   *
   * @param val
   */
  void setGroupsDn(final String val);

  /**
   * @return String
   */
  @MBeanInfo("Ldap groups dn")
  String getGroupsDn();

  /**
   *
   * @param val
   */
  void setGroupsAttr(final String val);

  /**
   * @return String
   */
  @MBeanInfo("Ldap groups attribute")
  String getGroupsAttr();

  /**
   *
   * @param val
   */
  void setAdminId(final String val);

  /**
   * @return String
   */
  @MBeanInfo("Ldap admin id")
  String getAdminId();

  /**
   *
   * @param val
   */
  void setAdminPw(final String val);

  /**
   * @return String
   */
  @MBeanInfo("Ldap admin pw")
  String getAdminPw();

  /** valid protocol for which an implementation exists, e.g "imap", "smtp"
   *
   * @param val
   */
  void setMailProtocol(final String val);

  /**
   * @return String
   */
  @MBeanInfo("valid protocol for which an implementation exists, e.g \"imap\", \"smtp\"")
  String getMailProtocol();

  /** Implementation for the selected protocol
   *
   * @param val
   */
  void setMailProtocolClass(final String val);

  /**
   * @return String
   */
  @MBeanInfo("Implementation class for the selected protocol")
  String getMailProtocolClass();

  /** Where we send it.
   *
   * @param val
   */
  void setMailServerIp(final String val);

  /**
   * @return String
   */
  @MBeanInfo("Mail server ip")
  String getMailServerIp();

  /**
   * @param val
   */
  void setMailServerPort(final String val);

  /**
   * @return String
   */
  @MBeanInfo("Mail server port")
  String getMailServerPort();

  /** Mailer:
   *
   * @param val
   */
  void setMailFrom(final String val);

  /** Mailer:
   /**
   * @return String
   */
  @MBeanInfo("Mail 'from'")
  String getMailFrom();

  /** Mailer: Subject we use when none supplied
   *
   * @param val
   */
  void setMailSubject(final String val);

  /**
   * @return String
   */
  @MBeanInfo("Mailer: Subject we use when none supplied")
  String getMailSubject();

  /** Allow mailer to be disabled
   *
   * @param val
   */
  void setMailDisabled(final boolean val);

  /**
   * @return boolean
   */
  @MBeanInfo("Allow mailer to be disabled")
  boolean getMailDisabled();
}
