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

import org.bedework.selfreg.common.DirMaint;
import org.bedework.selfreg.common.DirMaintImpl;
import org.bedework.util.jmx.ConfBase;

/**
 * @author douglm
 *
 */
public class Selfreg extends ConfBase<SelfregConfigPropertiesImpl>
        implements SelfregMBean {
  /* Name of the property holding the location of the config data */
  private static final String datauriPname = "org.bedework.selfreg.datauri";

  /**
   */
  public Selfreg() {
    super("org.bedework.selfreg:service=Selfreg");

    setConfigPname(datauriPname);
  }

  /* ========================================================================
   * Attributes
   * ======================================================================== */

  @Override
  public void setLdapUrl(final String val)  {
    getConfig().setLdapUrl(val);
  }

  @Override
  public String getLdapUrl()  {
    return getConfig().getLdapUrl();
  }

  @Override
  public void setBaseDn(final String val)  {
    getConfig().setBaseDn(val);
  }

  @Override
  public String getBaseDn()  {
    return getConfig().getBaseDn();
  }

  @Override
  public void setAccountsOu(final String val)  {
    getConfig().setAccountsOu(val);
  }

  @Override
  public String getAccountsOu()  {
    return getConfig().getAccountsOu();
  }

  @Override
  public void setAccountsDn(final String val)  {
    getConfig().setAccountsDn(val);
  }

  @Override
  public String getAccountsDn()  {
    return getConfig().getAccountsDn();
  }

  @Override
  public void setAccountsAttr(final String val)  {
    getConfig().setAccountsAttr(val);
  }

  @Override
  public String getAccountsAttr()  {
    return getConfig().getAccountsAttr();
  }

  @Override
  public void setGroupsOu(final String val)  {
    getConfig().setGroupsOu(val);
  }

  @Override
  public String getGroupsOu()  {
    return getConfig().getGroupsOu();
  }

  @Override
  public void setGroupsDn(final String val)  {
    getConfig().setGroupsDn(val);
  }

  @Override
  public String getGroupsDn()  {
    return getConfig().getGroupsDn();
  }

  @Override
  public void setGroupsAttr(final String val)  {
    getConfig().setGroupsAttr(val);
  }

  @Override
  public String getGroupsAttr()  {
    return getConfig().getGroupsAttr();
  }

  @Override
  public void setAdminId(final String val)  {
    getConfig().setAdminId(val);
  }

  @Override
  public String getAdminId()  {
    return getConfig().getAdminId();
  }

  @Override
  public void setAdminPw(final String val)  {
    getConfig().setAdminPw(val);
  }

  @Override
  public String getAdminPw()  {
    return getConfig().getAdminPw();
  }

  @Override
  public void setMailProtocol(final String val)  {
    getConfig().setMailProtocol(val);
  }

  @Override
  public String getMailProtocol()  {
    return getConfig().getMailProtocol();
  }

  @Override
  public void setMailProtocolClass(final String val)  {
    getConfig().setMailProtocolClass(val);
  }

  @Override
  public String getMailProtocolClass()  {
    return getConfig().getMailProtocolClass();
  }

  @Override
  public void setMailServerIp(final String val)  {
    getConfig().setMailServerIp(val);
  }

  @Override
  public String getMailServerIp()  {
    return getConfig().getMailServerIp();
  }

  @Override
  public void setMailServerPort(final String val)  {
    getConfig().setMailServerPort(val);
  }

  @Override
  public String getMailServerPort()  {
    return getConfig().getMailServerPort();
  }

  @Override
  public void setMailFrom(final String val)  {
    getConfig().setMailFrom(val);
  }

  @Override
  public String getMailFrom()  {
    return getConfig().getMailFrom();
  }

  @Override
  public void setMailSubject(final String val)  {
    getConfig().setMailSubject(val);
  }

  @Override
  public String getMailSubject()  {
    return getConfig().getMailSubject();
  }

  @Override
  public void setMailDisabled(final boolean val)  {
    getConfig().setMailDisabled(val);
  }

  @Override
  public boolean getMailDisabled()  {
    return getConfig().getMailDisabled();
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

  @Override
  public String loadConfig() {
    return loadConfig(SelfregConfigPropertiesImpl.class);
  }

  /* ========================================================================
   * Lifecycle
   * ======================================================================== */

  /* ====================================================================
   *                   Private methods
   * ==================================================================== */

  private DirMaint getDir() throws Throwable {
    DirMaint dir = new DirMaintImpl();

    dir.init(getConfig());

    return dir;
  }
}
