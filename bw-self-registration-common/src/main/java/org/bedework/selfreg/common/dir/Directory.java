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
package org.bedework.selfreg.common.dir;

import org.bedework.selfreg.common.exception.SelfregException;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import java.util.Properties;

import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

/** **********************************************************************
 Provide access to directory services.
 If the object is provided with a Properties object it will be queried for
 a number of properties. If they are absent or no Properties object is
 provided they will default to some value. Properties are defined in
 javax.naming.Context and are:

 java.naming.factory.initial
 java.naming.provider.url   Service provider, e.g. ldap://ldap.bedework.org:389
 java.naming.security.authentication    e.g. "simple"
 java.naming.security.principal         e.g. cn=dirManager
 java.naming.security.credentials       Usually the password

 The intention is that this class should be able to represent various forms of
 directory, even a sequential input stream of records.
 *************************************************************************/

public abstract class Directory implements Logged {
  /** pr is the properties provided at init
   */
  protected Properties pr;

  /** */
  public abstract static class DirSearchResult {
    /**
     * @return DirRecord
     * @throws SelfregException on fatal error
     */
    public abstract DirRecord nextRecord() throws SelfregException;
  }

  /** Constructor required so we can instantiate object dynamically
   */
  public Directory() {
  }

  /**
   * @param pr properties
   * @param mngrDN management dn
   * @param pw password
   */
  public Directory(final Properties pr,
                   final String mngrDN,
                   final String pw) {
    init(pr, mngrDN, pw);
  }

  /**
   * @param pr properties
   * @param mngrDN management dn
   * @param pw password
   */
  public void init(final Properties pr,
                   final String mngrDN,
                   final String pw) {
    if (pr == null) {
      this.pr = new Properties();
    } else {
      this.pr = pr;
    }
  }

  /** If possible, reInit should allow reuse after a close
   *
   * @throws SelfregException on fatal error
   */
  public abstract void reInit() throws SelfregException;

  /**
   * @param dn distinguished name
   */
  public abstract void destroy(String dn);

  /* These define the values used for scope parameters
   */

  /** */
  public static final int scopeBase = SearchControls.OBJECT_SCOPE;
  /** */
  public static final int scopeOne  = SearchControls.ONELEVEL_SCOPE;
  /** */
  public static final int scopeSub  = SearchControls.SUBTREE_SCOPE;

  /** Carry out a subtree search
   *
   * @param base search base
   * @param filter for search
   * @return DirSearchResult
   */
  public DirSearchResult search(final String base,
                                final String filter) {
    return search(base, filter, scopeSub);
  }

  /** Carry out a base level search. This should be the default if the scope
   *  is not specified.
   *
   * @param base search base
   * @param filter for search
   * @return DirSearchResult or null
   */
  public DirSearchResult searchBase(final String base,
                                    final String filter) {
    return search(base, filter, scopeBase);
  }

  /** Carry out a one level search
   *
   * @param base search base
   * @param filter for search
   * @return DirSearchResult
   */
  public DirSearchResult searchOne(final String base, final String filter) {
    return search(base, filter, scopeOne);
  }

  /** Carry out a search with specified scope.
   *
   * @param base search base
   * @param filter for search
   * @param scope for search
   * @return DirSearchResult null means no record(s) found.
   */
  public abstract DirSearchResult search(String base, String filter, int scope);

  /** newRecord - Return a record which can have attribute values added.
   *  create should be called to create the directory entry.
   *
   * @param entryDn new dn
   * @return DirRecord
   */
  public DirRecord newRecord(final String entryDn) {
    final DirRecord rec = new BasicDirRecord();
    rec.setDn(entryDn);
    return rec;
  }

  /**
   * @param rec to create
   * @return boolean true if created, false if already exists
   */
  public abstract boolean create(DirRecord rec);

  /* The replace methods modify a directory record in the directory.
   */

  /** Replace an entire attribute with one containing only the given value
   *
   * @param dn a dn
   * @param attrName for attribute
   * @param val new single value
   */
  public abstract void replace(String dn,
                               String attrName,
                               Object val);

  /** Replace an entire attribute with one containing only the given values
   *
   * @param dn a dn
   * @param attrName for attribute
   * @param val new multiple values
   */
  public abstract void replace(String dn,
                               String attrName,
                               Object[] val);

  /** Replace a single given attribute value with the given value
   *
   * @param dn a dn
   * @param attrName for attribute
   * @param oldval to replace
   * @param newval with this
   */
  public abstract void replace(String dn,
                               String attrName,
                               Object oldval,
                               Object newval);

  /**
   * @param dn a dn
   * @param mods modifications
   */
  public abstract void modify(String dn, ModificationItem[] mods);

  /**
   * @return Properties
   */
  public abstract Properties getEnvironment();

  /**
   *
   */
  public abstract void close();

  /* ==============================================================
   *                   Logged methods
   * ============================================================== */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}
