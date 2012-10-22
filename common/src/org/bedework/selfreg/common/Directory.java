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

import java.util.Properties;

import javax.naming.NamingException;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

import org.apache.log4j.Logger;

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

public abstract class Directory {
  protected boolean debug = false;

  private transient Logger log;

  /** pr is the properties provided at init
   */
  protected Properties pr;

  /** */
  public abstract static class DirSearchResult {
    /**
     * @return DirRecord
     * @throws Throwable
     */
    public abstract DirRecord nextRecord() throws Throwable;
  }

  /** Constructor required so we can instantiate object dynamically
   */
  public Directory() {
  }

  /**
   * @param pr
   * @param mngrDN
   * @param pw
   * @param debug
   * @throws Throwable
   */
  public Directory(Properties pr, String mngrDN,
                   String pw,
                   boolean debug) throws Throwable {
    init(pr, mngrDN, pw, debug);
  }

  /**
   * @param pr
   * @param mngrDN
   * @param pw
   * @param debug
   * @throws Throwable
   */
  public void init(Properties pr, String mngrDN, String pw,
                   boolean debug) throws Throwable {
    if (pr == null) {
      this.pr = new Properties();
    } else {
      this.pr = pr;
    }

    this.debug = debug;
  }

  /** If possible, reInit should allow reuse after a close
   *
   * @throws Throwable
   */
  public abstract void reInit() throws Throwable;

  /**
   * @param dn
   * @throws Throwable
   */
  public abstract void destroy(String dn) throws Throwable;

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
   * @param base
   * @param filter
   * @return DirSearchResult
   * @throws Throwable
   */
  public DirSearchResult search(String base, String filter) throws Throwable {
    return search(base, filter, scopeSub);
  }

  /** Carry out a base level search. This should be the default if the scope
   *  is not specified.
   *
   * @param base
   * @param filter
   * @return DirSearchResult or null
   * @throws Throwable
   */
  public DirSearchResult searchBase(String base, String filter) throws Throwable {
    return search(base, filter, scopeBase);
  }

  /** Carry out a one level search
   *
   * @param base
   * @param filter
   * @return DirSearchResult
   * @throws Throwable
   */
  public DirSearchResult searchOne(String base, String filter) throws Throwable {
    return search(base, filter, scopeOne);
  }

  /** Carry out a search with specified scope.
   *
   * @param base
   * @param filter
   * @param scope
   * @return DirSearchResult null means no record(s) found.
   * @throws Throwable
   */
  public abstract DirSearchResult search(String base, String filter, int scope)
      throws Throwable;

  /** newRecord - Return a record which can have attribute values added.
   *  create should be called to create the directory entry.
   *
   * @param entryDn
   * @return DirRecord
   * @throws NamingException
   */
  public DirRecord newRecord(String entryDn) throws NamingException {
    DirRecord rec = new BasicDirRecord();
    rec.setDn(entryDn);
    return rec;
  }

  /**
   * @param rec
   * @return boolean true if created, false if already exists
   * @throws Throwable
   */
  public abstract boolean create(DirRecord rec) throws Throwable;

  /* The replace methods modify a directory record in the directory.
   */

  /** Replace an entire attribute with one containing only the given value
   *
   * @param dn
   * @param attrName
   * @param val
   * @throws Throwable
   */
  public abstract void replace(String dn, String attrName, Object val) throws Throwable;

  /** Replace an entire attribute with one containing only the given values
   *
   * @param dn
   * @param attrName
   * @param val
   * @throws Throwable
   */
  public abstract void replace(String dn, String attrName, Object[] val) throws Throwable;

  /** Replace a single given attribute value with the given value
   *
   * @param dn
   * @param attrName
   * @param oldval
   * @param newval
   * @throws Throwable
   */
  public abstract void replace(String dn, String attrName, Object oldval,
                               Object newval) throws Throwable;

  /**
   * @param dn
   * @param mods
   * @throws Throwable
   */
  public abstract void modify(String dn, ModificationItem[] mods) throws Throwable;

  /**
   * @return Properties
   * @throws Throwable
   */
  public abstract Properties getEnvironment() throws Throwable;

  /**
   *
   */
  public abstract void close();

  protected Logger getLog() {
    if (log == null) {
      log = Logger.getLogger(this.getClass());
    }

    return log;
  }

  protected void error(String msg) {
    getLog().error(msg);
  }

  protected void debugMsg(String msg) {
    getLog().debug(msg);
  }

}
