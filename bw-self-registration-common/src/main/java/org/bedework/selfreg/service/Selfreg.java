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

import org.bedework.selfreg.common.AccountInfo;
import org.bedework.selfreg.common.DirMaint;
import org.bedework.selfreg.common.DirMaintImpl;
import org.bedework.util.hibernate.HibConfig;
import org.bedework.util.hibernate.SchemaThread;
import org.bedework.util.jmx.ConfBase;
import org.bedework.util.jmx.InfoLines;
import org.bedework.util.jmx.MBeanInfo;

import java.util.List;

/**
 * @author douglm
 *
 */
public class Selfreg extends ConfBase<SelfregConfigPropertiesImpl>
        implements SelfregMBean {
  /* Name of the directory holding the config data */
  private static final String confDirName = "selfreg";

  private final static String nm = "config";

  /* Be safe - default to false */
  private boolean export;

  /* ==============================================================
   * Dump/restore
   * ============================================================== */

  private class SchemaBuilder extends SchemaThread {

    SchemaBuilder(final String outFile,
                  final boolean export) {
      super(outFile, export, new HibConfig(getConfig(),
                                           Selfreg.class.getClassLoader()));
      setContextClassLoader(Selfreg.class.getClassLoader());
    }

    @Override
    public void completed(final String status) {
      if (status.equals(SchemaThread.statusDone)) {
        Selfreg.this.setStatus(ConfBase.statusDone);
      } else {
        Selfreg.this.setStatus(ConfBase.statusFailed);
      }
      setExport(false);
      info("Schema build completed with status " + status);
    }
  }

  private SchemaBuilder buildSchema;

  /**
   */
  public Selfreg() {
    super(getServiceName(nm), confDirName, nm);
  }

  /**
   * @param name of service
   * @return object name value for the mbean with this name
   */
  public static String getServiceName(final String name) {
    return "org.bedework.selfreg:service=" + name;
  }

  /* ========================================================================
   * Attributes
   * ======================================================================== */

  @Override
  public void setUseLdap(final boolean val) {
    getConfig().setUseLdap(val);
  }

  @Override
  public boolean getUseLdap() {
    return getConfig().getUseLdap();
  }

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
  public void setMailServerHost(final String val)  {
    getConfig().setMailServerHost(val);
  }

  @Override
  public String getMailServerHost()  {
    return getConfig().getMailServerHost();
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
  public void setMailServerAccount(final String val) {
    getConfig().setMailServerAccount(val);
  }

  @Override
  public String getMailServerAccount() {
    return getConfig().getMailServerAccount();
  }

  @Override
  public void setMailServerPw(final String val) {
    getConfig().setMailServerPw(val);
  }

  @Override
  public String getMailServerPw() {
    return getConfig().getMailServerPw();
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

  @Override
  public void setConfirmUrl(final String val) {
    getConfig().setConfirmUrl(val);
  }

  @Override
  public String getConfirmUrl() {
    return getConfig().getConfirmUrl();
  }

  @Override
  public void setNewpwUrl(final String val) {
    getConfig().setNewpwUrl(val);
  }

  @Override
  public String getNewpwUrl() {
    return getConfig().getNewpwUrl();
  }

  @Override
  public void setConfirmForward(final String val) {
    getConfig().setConfirmForward(val);
  }

  @Override
  public String getConfirmForward() {
    return getConfig().getConfirmForward();
  }

  @Override
  public void setNewpwForward(final String val) {
    getConfig().setNewpwForward(val);
  }

  @Override
  public String getNewpwForward() {
    return getConfig().getNewpwForward();
  }

  @Override
  public void setMessageDigest(final String val) {
    getConfig().setMessageDigest(val);
  }

  @Override
  public String getMessageDigest() {
    return getConfig().getMessageDigest();
  }

  @Override
  public void setAccountPrefix(final String val) {
    getConfig().setAccountPrefix(val);
  }

  @Override
  public String getAccountPrefix() {
    return getConfig().getAccountPrefix();
  }

  @Override
  public void setUnauthCanRegister(final boolean val) {
    getConfig().setUnauthCanRegister(val);
  }

  @Override
  public boolean getUnauthCanRegister() {
    return getConfig().getUnauthCanRegister();
  }

  @Override
  public void setCanSpecifyAccount(final boolean val) {
    getConfig().setCanSpecifyAccount(val);
  }

  @Override
  public boolean getCanSpecifyAccount() {
    return getConfig().getCanSpecifyAccount();
  }

  @Override
  public void setAccountFromEmail(final boolean val) {
    getConfig().setAccountFromEmail(val);
  }

  @Override
  public boolean getAccountFromEmail() {
    return getConfig().getAccountFromEmail();
  }

  @Override
  public void setPwIsToken(final boolean val) {
    getConfig().setPwIsToken(val);
  }

  @Override
  public boolean getPwIsToken() {
    return getConfig().getPwIsToken();
  }

  @Override
  public void setCaptchaPublicKey(final String val) {
    getConfig().setCaptchaPublicKey(val);
  }

  @Override
  public String getCaptchaPublicKey() {
    return getConfig().getCaptchaPublicKey();
  }

  @Override
  public void setCaptchaPrivateKey(final String val) {
    getConfig().setCaptchaPrivateKey(val);
  }

  @Override
  public String getCaptchaPrivateKey() {
    return getConfig().getCaptchaPrivateKey();
  }

  @Override
  public void setSchemaOutFile(final String val) {
    getConfig().setSchemaOutFile(val);
  }

  @Override
  public String getSchemaOutFile() {
    return getConfig().getSchemaOutFile();
  }

  /* ========================================================================
   * Hibernate properties
   * ======================================================================== */

  @Override
  public void setHibernateProperties(final List<String> val) {
    getConfig().setHibernateProperties(val);
  }

  @Override
  public List<String> getHibernateProperties() {
    return getConfig().getHibernateProperties();
  }

  @Override
  public void setHibernateDialect(final String value) {
    getConfig().setHibernateDialect(value);
  }

  @Override
  public String getHibernateDialect() {
    return getConfig().getHibernateDialect();
  }

  @Override
  public void removeHibernateProperty(final String name) {
    getConfig().removeHibernateProperty(name);
  }

  @Override
  public void addHibernateProperty(final String name,
                                   final String value) {
    getConfig().addHibernateProperty(name, value);
  }

  @Override
  public String getHibernateProperty(final String name) {
    return getConfig().getHibernateProperty(name);
  }

  @Override
  public void setHibernateProperty(final String name,
                                   final String value) {
    getConfig().setHibernateProperty(name, value);
  }

  /* ========================================================================
   * Mbean attributes
   * ======================================================================== */

  @Override
  public void setExport(final boolean val) {
    export = val;
  }

  @Override
  @MBeanInfo("Export (write) schema to database?")
  public boolean getExport() {
    return export;
  }

  /* ========================================================================
   * Operations
   * ======================================================================== */

  @Override
  public String schema() {
    try {
      buildSchema = new SchemaBuilder(
              getSchemaOutFile(),
              getExport());

      setStatus(statusRunning);

      buildSchema.start();

      return "OK";
    } catch (final Throwable t) {
      error(t);

      return "Exception: " + t.getLocalizedMessage();
    }
  }

  @Override
  public synchronized List<String> schemaStatus() {
    if (buildSchema == null) {
      final InfoLines infoLines = new InfoLines();

      infoLines.addLn("Schema build has not been started");

      return infoLines;
    }

    return buildSchema.infoLines;
  }

  @Override
  public String displayRequest(final String confid) {
    try {
      final AccountInfo ainfo = getDir().getAccountByConfid(confid);

      if (ainfo == null) {
        return "No account";
      }
      return ainfo.toString();
    } catch (final Throwable t) {
      error(t);
      return t.getLocalizedMessage();
    }
  }

  @Override
  public String addUser(final String account,
                        final String first,
                        final String last,
                        final String email,
                        final String password) {
    try {
      if (!getDir().createAccount(account,
                                  first,
                                  last,
                                  email,
                                  password,
                                  null)) {
        return "Account " + account + " exists already";
      }

      return "Created";
    } catch (final Throwable t) {
      error(t);
      return t.getLocalizedMessage();
    }
  }

  @Override
  public String displayUser(final String account) {
    try {
      return getDir().displayAccount(account);
    } catch (final Throwable t) {
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
    } catch (final Throwable t) {
      error(t);
      return t.getLocalizedMessage();
    }
  }

  @Override
  public String removeUser(final String val) {
    try {
      AccountInfo ainfo = null;
      if (val.contains("@")) {
        ainfo = getDir().getAccountByEmail(val);
      }

      if (ainfo == null) {
        return "Not found";
      }

      if (!getDir().deleteAccount(ainfo.getConfid())) {
        return "Unable to delete";
      }

      return "Ok";
    } catch (final Throwable t) {
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
    } catch (final Throwable t) {
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
    } catch (final Throwable t) {
      error(t);
      return t.getLocalizedMessage();
    }
  }

  @Override
  public String loadConfig() {
    return loadConfig(SelfregConfigPropertiesImpl.class);
  }

  public AccountInfo getAccount(final String account) {
    try {
      return getDir().getAccount(account);
    } catch (final Throwable t) {
      error(t);
      return null;
    }
  }

  /* ========================================================================
   * Lifecycle
   * ======================================================================== */

  /* ====================================================================
   *                   Private methods
   * ==================================================================== */

  private DirMaint getDir() throws Throwable {
    final DirMaint dir = new DirMaintImpl();

    dir.init(getConfig());

    return dir;
  }
}
