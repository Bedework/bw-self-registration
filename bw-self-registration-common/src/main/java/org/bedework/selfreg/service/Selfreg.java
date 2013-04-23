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
import org.bedework.selfreg.common.SelfregConfigProperties;

import edu.rpi.cmt.config.ConfigurationStore;
import edu.rpi.cmt.config.ConfigurationType;
import edu.rpi.cmt.jmx.ConfBase;

import java.util.Collection;

/**
 * @author douglm
 *
 */
public class Selfreg extends ConfBase<SelfregConfigProperties> implements SelfregMBean {
  private SelfregConfigProperties cfg;

  /* Name of the property holding the location of the config data */
  private static final String datauriPname = "org.bedework.selfreg.datauri";

  /**
   */
  public Selfreg() {
    super("org.bedework.selfreg:service=Selfreg");

    setConfigPname(datauriPname);
  }

  @Override
  public ConfigurationType getConfigObject() {
    return getCfg().getConfig();
  }

  /* ========================================================================
   * Attributes
   * ======================================================================== */

  @Override
  public void setLdapUrl(final String val)  {
    getCfg().setLdapUrl(val);
  }

  @Override
  public String getLdapUrl()  {
    return getCfg().getLdapUrl();
  }

  @Override
  public void setBaseDn(final String val)  {
    getCfg().setBaseDn(val);
  }

  @Override
  public String getBaseDn()  {
    return getCfg().getBaseDn();
  }

  @Override
  public void setAccountsOu(final String val)  {
    getCfg().setAccountsOu(val);
  }

  @Override
  public String getAccountsOu()  {
    return getCfg().getAccountsOu();
  }

  @Override
  public void setAccountsDn(final String val)  {
    getCfg().setAccountsDn(val);
  }

  @Override
  public String getAccountsDn()  {
    return getCfg().getAccountsDn();
  }

  @Override
  public void setAccountsAttr(final String val)  {
    getCfg().setAccountsAttr(val);
  }

  @Override
  public String getAccountsAttr()  {
    return getCfg().getAccountsAttr();
  }

  @Override
  public void setGroupsOu(final String val)  {
    getCfg().setGroupsOu(val);
  }

  @Override
  public String getGroupsOu()  {
    return getCfg().getGroupsOu();
  }

  @Override
  public void setGroupsDn(final String val)  {
    getCfg().setGroupsDn(val);
  }

  @Override
  public String getGroupsDn()  {
    return getCfg().getGroupsDn();
  }

  @Override
  public void setAdminId(final String val)  {
    getCfg().setAdminId(val);
  }

  @Override
  public String getAdminId()  {
    return getCfg().getAdminId();
  }

  @Override
  public void setAdminPw(final String val)  {
    getCfg().setAdminPw(val);
  }

  @Override
  public String getAdminPw()  {
    return getCfg().getAdminPw();
  }

  @Override
  public void setMailProtocol(final String val)  {
    getCfg().setMailProtocol(val);
  }

  @Override
  public String getMailProtocol()  {
    return getCfg().getMailProtocol();
  }

  @Override
  public void setMailProtocolClass(final String val)  {
    getCfg().setMailProtocolClass(val);
  }

  @Override
  public String getMailProtocolClass()  {
    return getCfg().getMailProtocolClass();
  }

  @Override
  public void setMailServerIp(final String val)  {
    getCfg().setMailServerIp(val);
  }

  @Override
  public String getMailServerIp()  {
    return getCfg().getMailServerIp();
  }

  @Override
  public void setMailServerPort(final String val)  {
    getCfg().setMailServerPort(val);
  }

  @Override
  public String getMailServerPort()  {
    return getCfg().getMailServerPort();
  }

  @Override
  public void setMailFrom(final String val)  {
    getCfg().setMailFrom(val);
  }

  @Override
  public String getMailFrom()  {
    return getCfg().getMailFrom();
  }

  @Override
  public void setMailSubject(final String val)  {
    getCfg().setMailSubject(val);
  }

  @Override
  public String getMailSubject()  {
    return getCfg().getMailSubject();
  }

  @Override
  public void setMailDisabled(final boolean val)  {
    getCfg().setMailDisabled(val);
  }

  @Override
  public boolean getMailDisabled()  {
    return getCfg().getMailDisabled();
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
    try {
      /* Load up the config */

      ConfigurationStore cs = getStore();

      Collection<String> configNames = cs.getConfigs();

      if (configNames.isEmpty()) {
        return "No configuration";
      }

      if (configNames.size() != 1) {
        return "1 and only 1 configuration allowed";
      }

      String configName = configNames.iterator().next();

      cfg = getConfigInfo(configName, SelfregConfigProperties.class);

      if (cfg == null) {
        return "Unable to read configuration";
      }

      setConfigName(configName);

      saveConfig(); // Just to ensure we have it for next time

      return "OK";
    } catch (Throwable t) {
      error("Failed to start management context");
      error(t);
      return "failed";
    }
  }

  /* ====================================================================
   *                   Non-mbean methods
   * ==================================================================== */

  /**
   * @return current state of config
   */
  public SelfregConfigProperties getCfg() {
    return cfg;
  }

  /* ========================================================================
   * Lifecycle
   * ======================================================================== */

  /* ====================================================================
   *                   Private methods
   * ==================================================================== */

  private DirMaint getDir() throws Throwable {
    DirMaint dir = new DirMaintImpl();

    dir.init(getCfg());

    return dir;
  }
}
