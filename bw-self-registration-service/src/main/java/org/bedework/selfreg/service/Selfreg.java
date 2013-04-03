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

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.bedework.selfreg.common.DirMaint;
import org.bedework.selfreg.common.DirMaintImpl;
import org.bedework.selfreg.common.SelfregConfigProperties;
import org.jboss.system.ServiceMBeanSupport;

/**
 * @author douglm
 *
 */
public class Selfreg extends ServiceMBeanSupport implements SelfregMBean {
  private transient Logger log;

  private SelfregConfigProperties conf = new SelfregConfigProperties();

  /* ========================================================================
   * Attributes
   * ======================================================================== */

  @Override
  public void setLdapUrl(final String val)  {
    conf.setLdapUrl(val);
  }

  @Override
  public String getLdapUrl()  {
    return conf.getLdapUrl();
  }

  @Override
  public void setBaseDn(final String val)  {
    conf.setBaseDn(val);
  }

  @Override
  public String getBaseDn()  {
    return getBaseDn();
  }

  @Override
  public void setAccountsOu(final String val)  {
    conf.setAccountsOu(val);
  }

  @Override
  public String getAccountsOu()  {
    return conf.getAccountsOu();
  }

  @Override
  public void setAccountsDn(final String val)  {
    conf.setAccountsDn(val);
  }

  @Override
  public String getAccountsDn()  {
    return conf.getAccountsDn();
  }

  @Override
  public void setAccountsAttr(final String val)  {
    conf.setAccountsAttr(val);
  }

  @Override
  public String getAccountsAttr()  {
    return conf.getAccountsAttr();
  }

  @Override
  public void setGroupsOu(final String val)  {
    conf.setGroupsOu(val);
  }

  @Override
  public String getGroupsOu()  {
    return conf.getGroupsOu();
  }

  @Override
  public void setGroupsDn(final String val)  {
    conf.setGroupsDn(val);
  }

  @Override
  public String getGroupsDn()  {
    return conf.getGroupsDn();
  }

  @Override
  public void setAdminId(final String val)  {
    conf.setAdminId(val);
  }

  @Override
  public String getAdminId()  {
    return conf.getAdminId();
  }

  @Override
  public void setAdminPw(final String val)  {
    conf.setAdminPw(val);
  }

  @Override
  public String getAdminPw()  {
    return conf.getAdminPw();
  }

  @Override
  public void setMailProtocol(final String val)  {
    conf.setMailProtocol(val);
  }

  @Override
  public String getMailProtocol()  {
    return conf.getMailProtocol();
  }

  @Override
  public void setMailProtocolClass(final String val)  {
    conf.setMailProtocolClass(val);
  }

  @Override
  public String getMailProtocolClass()  {
    return conf.getMailProtocolClass();
  }

  @Override
  public void setMailServerIp(final String val)  {
    conf.setMailServerIp(val);
  }

  @Override
  public String getMailServerIp()  {
    return conf.getMailServerIp();
  }

  @Override
  public void setMailServerPort(final String val)  {
    conf.setMailServerPort(val);
  }

  @Override
  public String getMailServerPort()  {
    return conf.getMailServerPort();
  }

  @Override
  public void setMailFrom(final String val)  {
    conf.setMailFrom(val);
  }

  @Override
  public String getMailFrom()  {
    return conf.getMailFrom();
  }

  @Override
  public void setMailSubject(final String val)  {
    conf.setMailSubject(val);
  }

  @Override
  public String getMailSubject()  {
    return conf.getMailSubject();
  }

  @Override
  public void setMailDisabled(final boolean val)  {
    conf.setMailDisabled(val);
  }

  @Override
  public boolean getMailDisabled()  {
    return conf.getMailDisabled();
  }

  /* ========================================================================
   * Operations
   * ======================================================================== */

  @Override
  public String addUser(final String account,
                        final String first,
                        final String last,
                        final String email,
                        final String password) {
    try {
      if (!getDir().createAccount(account, first, last, email, password)) {
        return "Account " + account + " exists already";
      }

      return "Created";
    } catch (Throwable t) {
      error(t);
      return t.getLocalizedMessage();
    }
  }

  @Override
  public String displayUser(final String account) {
    try {
      return getDir().displayAccount(account);
    } catch (Throwable t) {
      error(t);
      return t.getLocalizedMessage();
    }
  }

  @Override
  public String setUserPassword(final String account,
                                final String password) {
    try {
      getDir().setUserPassword(account, password);

      return "Ok";
    } catch (Throwable t) {
      error(t);
      return t.getLocalizedMessage();
    }
  }

  @Override
  public String removeUser(final String account) {
    try {

      return "Ok";
    } catch (Throwable t) {
      error(t);
      return t.getLocalizedMessage();
    }
  }

  @Override
  public String addGroup(final String group,
                         final String account) {
    try {
      if (!getDir().createGroup(group, account)) {
        return "Group " + group + " exists already";
      }


      return "Ok";
    } catch (Throwable t) {
      error(t);
      return t.getLocalizedMessage();
    }
  }

  @Override
  public String addGroupMember(final String group,
                               final String account) {
    try {
      getDir().addGroupMember(group, account);

      return "Ok";
    } catch (Throwable t) {
      error(t);
      return t.getLocalizedMessage();
    }
  }

  /* ========================================================================
   * Lifecycle
   * ======================================================================== */

  @Override
  protected ObjectName getObjectName(final MBeanServer server,
                                     final ObjectName name)
      throws MalformedObjectNameException {
    if (name == null) {
      return OBJECT_NAME;
    }

    return name;
   }

  @Override
  public void startService() throws Exception {
  }

  @Override
  public void stopService() throws Exception {
  }

  /* ====================================================================
   *                   Private methods
   * ==================================================================== */

  private DirMaint getDir() throws Throwable {
    DirMaint dir = new DirMaintImpl();

    dir.init(conf);

    return dir;
  }

  /* ====================================================================
   *                   Protected methods
   * ==================================================================== */

  protected void info(final String msg) {
    getLogger().info(msg);
  }

  protected void trace(final String msg) {
    getLogger().debug(msg);
  }

  protected void error(final Throwable t) {
    getLogger().error(this, t);
  }

  protected void error(final String msg) {
    getLogger().error(msg);
  }

  /* Get a logger for messages
   */
  protected Logger getLogger() {
    if (log == null) {
      log = Logger.getLogger(this.getClass());
    }

    return log;
  }
}
