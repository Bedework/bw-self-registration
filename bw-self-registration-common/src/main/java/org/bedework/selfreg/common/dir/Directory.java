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
import org.bedework.util.logging.Logged;

import java.util.Properties;

import javax.naming.NamingException;
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
     * @throws SelfregException
     */
    public abstract DirRecord nextRecord() throws SelfregException;
  }

  /** Constructor required so we can instantiate object dynamically
   */
  public Directory() {
  }

  /**
   * @param pr
   * @param mngrDN
   * @param pw
   * @throws SelfregException
   */
  public Directory(final Properties pr, final String mngrDN,
                   final String pw) throws SelfregException {
    init(pr, mngrDN, pw);
  }

  /**
   * @param pr
   * @param mngrDN
   * @param pw
   * @throws SelfregException
   */
  public void init(final Properties pr,
                   final String mngrDN,
                   final String pw) throws SelfregException {
    if (pr == null) {
      this.pr = new Properties();
    } else {
      this.pr = pr;
    }
  }

  /** If possible, reInit should allow reuse after a close
   *
   * @throws SelfregException
   */
  public abstract void reInit() throws SelfregException;

  /**
   * @param dn
   * @throws SelfregException
   */
  public abstract void destroy(String dn) throws SelfregException;

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
   * @throws SelfregException
   */
  public DirSearchResult search(final String base, final String filter) throws SelfregException {
    return search(base, filter, scopeSub);
  }

  /** Carry out a base level search. This should be the default if the scope
   *  is not specified.
   *
   * @param base
   * @param filter
   * @return DirSearchResult or null
   * @throws SelfregException
   */
  public DirSearchResult searchBase(final String base, final String filter) throws SelfregException {
    return search(base, filter, scopeBase);
  }

  /** Carry out a one level search
   *
   * @param base
   * @param filter
   * @return DirSearchResult
   * @throws SelfregException
   */
  public DirSearchResult searchOne(final String base, final String filter) throws SelfregException {
    return search(base, filter, scopeOne);
  }

  /** Carry out a search with specified scope.
   *
   * @param base
   * @param filter
   * @param scope
   * @return DirSearchResult null means no record(s) found.
   * @throws SelfregException
   */
  public abstract DirSearchResult search(String base, String filter, int scope)
      throws SelfregException;

  /** newRecord - Return a record which can have attribute values added.
   *  create should be called to create the directory entry.
   *
   * @param entryDn
   * @return DirRecord
   * @throws NamingException
   */
  public DirRecord newRecord(final String entryDn) throws NamingException {
    DirRecord rec = new BasicDirRecord();
    rec.setDn(entryDn);
    return rec;
  }

  /**
   * @param rec
   * @return boolean true if created, false if already exists
   * @throws SelfregException
   */
  public abstract boolean create(DirRecord rec) throws SelfregException;

  /* The replace methods modify a directory record in the directory.
   */

  /** Replace an entire attribute with one containing only the given value
   *
   * @param dn
   * @param attrName
   * @param val
   * @throws SelfregException
   */
  public abstract void replace(String dn, String attrName, Object val) throws SelfregException;

  /** Replace an entire attribute with one containing only the given values
   *
   * @param dn
   * @param attrName
   * @param val
   * @throws SelfregException
   */
  public abstract void replace(String dn, String attrName, Object[] val) throws SelfregException;

  /** Replace a single given attribute value with the given value
   *
   * @param dn
   * @param attrName
   * @param oldval
   * @param newval
   * @throws SelfregException
   */
  public abstract void replace(String dn, String attrName, Object oldval,
                               Object newval) throws SelfregException;

  /**
   * @param dn
   * @param mods
   * @throws SelfregException
   */
  public abstract void modify(String dn, ModificationItem[] mods) throws SelfregException;

  /**
   * @return Properties
   * @throws SelfregException
   */
  public abstract Properties getEnvironment() throws SelfregException;

  /**
   *
   */
  public abstract void close();

}
