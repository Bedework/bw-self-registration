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

/** Properties for self reg.
 *
 * @author douglm
 *
 */
public interface SelfregConfigPropertiesI {

  /**
   *
   * @param val
   */
  void setLdapUrl(final String val);

  /**
   * @return String
   */
  String getLdapUrl();

  /** baseDn
   *
   * @param val
   */
  void setBaseDn(final String val);

  /**
   * @return String
   */
  String getBaseDn();

  /**
   *
   * @param val
   */
  void setAccountsOu(final String val);

  /**
   * @return String
   */
  String getAccountsOu();

  /**
   *
   * @param val
   */
  void setAccountsDn(final String val);

  /**
   * @return String
   */
  String getAccountsDn();

  /**
   *
   * @param val
   */
  void setAccountsAttr(final String val);

  /**
   * @return String
   */
  String getAccountsAttr();

  /**
   *
   * @param val
   */
  void setGroupsOu(final String val);

  /**
   * @return String
   */
  String getGroupsOu();

  /**
   *
   * @param val
   */
  void setGroupsDn(final String val);

  /**
   * @return String
   */
  String getGroupsDn();

  /**
   *
   * @param val
   */
  void setAdminId(final String val);

  /**
   * @return String
   */
  String getAdminId();

  /**
   *
   * @param val
   */
  void setAdminPw(final String val);

  /**
   * @return String
   */
  String getAdminPw();

  /** valid protocol for which an implementation exists, e.g "imap", "smtp"
   *
   * @param val
   */
  void setMailProtocol(final String val);

  /**
   * @return String
   */
  String getMailProtocol();

  /** Implementation for the selected protocol
   *
   * @param val
   */
  void setMailProtocolClass(final String val);

  /**
   * @return String
   */
  String getMailProtocolClass();

  /** Where we send it.
   *
   * @param val
   */
  void setMailServerIp(final String val);

  /**
   * @return String
   */
  String getMailServerIp();

  /**
   * @param val
   */
  void setMailServerPort(final String val);

  /**
   * @return String
   */
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
  String getMailFrom();

  /** Mailer: Subject we use when none supplied
   *
   * @param val
   */
  void setMailSubject(final String val);

  /**
   * @return String
   */
  String getMailSubject();

  /** Allow mailer to be disabled
   *
   * @param val
   */
  void setMailDisabled(final boolean val);

  /**
   * @return boolean
   */
  boolean getMailDisabled();
}
