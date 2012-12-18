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
public class SelfregConfigProperties {
  private String ldapUrl;

  private String protocol;

  private String protocolClass;

  private String serverIp;

  private String serverPort;

  private String from;

  private String subject;

  private boolean disabled;

  /** Used by configuration tools
   *
   * @param val
   */
  public void setLdapUrl(final String val)  {
    ldapUrl  = val;
  }

  /**
   * @return String
   */
  public String getLdapUrl()  {
    return ldapUrl;
  }

  /** valid protocol for which an implementation exists, e.g "imap", "smtp"
   *
   * @param val
   */
  public void setProtocol(final String val)  {
    protocol  = val;
  }

  /**
   * @return String
   */
  public String getProtocol()  {
    return protocol;
  }

  /** Implementation for the selected protocol
   *
   * @param val
   */
  public void setProtocolClass(final String val)  {
    protocolClass  = val;
  }

  /**
   * @return String
   */
  public String getProtocolClass()  {
    return protocolClass;
  }

  /** Where we send it.
   *
   * @param val
   */
  public void setServerIp(final String val)  {
    serverIp  = val;
  }

  /**
   * @return String
   */
  public String getServerIp()  {
    return serverIp;
  }

  /**
   * @param val
   */
  public void setServerPort(final String val)  {
    serverPort  = val;
  }

  /**
   * @return String
   */
  public String getServerPort()  {
    return serverPort;
  }

  /** Address we use when none supplied
   *
   * @param val
   */
  public void setFrom(final String val)  {
    from = val;
  }

  /**
   * @return String
   */
  public String getFrom()  {
    return from;
  }

  /** Subject we use when none supplied
   *
   * @param val
   */
  public void setSubject(final String val)  {
    subject = val;
  }

  /**
   * @return String
   */
  public String getSubject()  {
    return subject;
  }

  /** Allow mailer to be disabled
   *
   * @param val
   */
  public void setDisabled(final boolean val)  {
    disabled = val;
  }

  /**
   * @return boolean
   */
  public boolean getDisabled()  {
    return disabled;
  }
}
