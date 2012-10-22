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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;

/** **********************************************************************
 Provide access to ldap directory services.
 If the object is provided with a Properties object it will be queried for
 a number of properties. If they are absent or no Properties object is
 provided they will default to some value. Properties are defined in
 javax.naming.Context and are:

 java.naming.factory.initial
 java.naming.provider.url   Service provider, e.g. ldap://ldap.myhost.edu:389
 java.naming.security.authentication    e.g. "simple"
 java.naming.security.principal         e.g. cn=dirManager
 java.naming.security.credentials       Usually the password
 *************************************************************************/

public class LdapDirectory extends Directory {
  /** The default values
   */
  private static final String defaultCTX = "com.sun.jndi.ldap.LdapCtxFactory";
  private static final String defaultLdapURL = "ldap://localhost";
  private static final String defaultURL = defaultLdapURL;

  private String mngrDN;
  private String pw;

  DirContext ctx;

  SearchControls constraints;

  /** */
  public static class LdapSearchResult extends DirSearchResult {
    NamingEnumeration recs;
    String base;

    /* (non-Javadoc)
     * @see org.bedework.tools.directory.Directory#nextRecord()
     */
    public DirRecord nextRecord() throws Throwable {
      SearchResult s = null;

      if (recs == null) {
        throw new Exception("null search result");
      }

      if (!recs.hasMore()) {
        recs = null;
        return null;
      }

      try {
        s = (SearchResult)recs.next();
      } finally {
        if (s == null) {
          try {
            recs.close();
          } catch (Exception e) {};

          recs = null;
        }
      }

      if (s == null) {
        return null;
      }

      DirRecord rec = new BasicDirRecord(s.getAttributes());

      rec.setName(s.getName());

      return rec;
    }
  }

  /** Constructor required so we can instantiate object dynamically
   */
  public LdapDirectory() {
  }

  /**
   * @param pr
   * @param mngrDN
   * @param pw
   * @param debug
   * @throws Throwable
   */
  public LdapDirectory(Properties pr, String mngrDN,
                       String pw,
                       boolean debug) throws Throwable {
    super(pr, mngrDN, pw, debug);
  }

  /* (non-Javadoc)
   * @see org.bedework.tools.directory.Directory#init(java.util.Properties, java.lang.String, java.lang.String, boolean)
   */
  public void init(Properties pr, String mngrDN, String pw,
                   boolean debug) throws Throwable {
    if (pr == null) {
      throw new Exception("No properties supplied");
    }
    super.init(pr, mngrDN, pw, debug);
    this.mngrDN = mngrDN;
    this.pw = pw;
    reInit();
  }

  /* (non-Javadoc)
   * @see org.bedework.tools.directory.Directory#reInit()
   */
  public void reInit() throws Throwable {
    /** If we weren't given a url try to get one.
     */

    if (pr == null) throw new Exception("No properties supplied (again)");
    checkProp(pr, Context.PROVIDER_URL, defaultURL);
    checkProp(pr, Context.INITIAL_CONTEXT_FACTORY, defaultCTX);

    if (mngrDN != null && pw != null) {
      checkProp(pr, Context.SECURITY_AUTHENTICATION, "simple");
      pr.put(Context.SECURITY_PRINCIPAL, mngrDN);
      pr.put(Context.SECURITY_CREDENTIALS, pw);
    }

    // Make simple authentication the default
    checkProp(pr, Context.SECURITY_AUTHENTICATION, "simple");

    if (debug) {
      debugMsg("Directory: get new context for " +
            pr.get(Context.PROVIDER_URL));
    }
    ctx = new InitialDirContext(pr);
    constraints = new SearchControls();
    if (debug) debugMsg("Directory: init OK " + pr.get(Context.PROVIDER_URL));
  }

  /* (non-Javadoc)
   * @see org.bedework.tools.directory.Directory#destroy(java.lang.String)
   */
  public void destroy(String dn) throws Throwable {
    ctx.destroySubcontext(dn);
  }

  public DirSearchResult search(String base, String filter,
                                int scope) throws Throwable {
    if (debug) {
      debugMsg("About to search: base=" + base + " filter=" + filter +
               " scope=" + scope);
    }

    LdapSearchResult sres = new LdapSearchResult();
    sres.base = base;

    constraints.setSearchScope(scope);
    constraints.setCountLimit(1000);

    try {
      if (filter == null) {
        filter = "(objectClass=*)";
      }

      sres.recs = ctx.search(base, filter, constraints);

      if ((sres.recs == null) || !sres.recs.hasMore()) {
        sres = null;
      }
    } catch (NameNotFoundException e) {
      // Allow that one.
      if (debug) {
        debugMsg("NameNotFoundException: return with null");
      }
      sres = null;
    }

    return sres;
  }

  /* (non-Javadoc)
   * @see org.bedework.tools.directory.Directory#create(org.bedework.tools.directory.DirRecord)
   */
  public boolean create(DirRecord rec) throws Throwable {
    try {
      ctx.createSubcontext(rec.getDn(), rec.getAttributes());
      return true;
    } catch (NameAlreadyBoundException nabe) {
      return false;
    }
  }

  /* (non-Javadoc)
   * @see org.bedework.tools.directory.Directory#replace(java.lang.String, java.lang.String, java.lang.Object)
   */
  public void replace(String dn, String attrName,
                      Object val) throws Throwable {
    BasicAttributes attrs = new BasicAttributes(attrName, val);
    ctx.modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, attrs);
  }

  public void replace(String dn, String attrName, Object[] val)
      throws Exception {
    BasicAttributes attrs = new BasicAttributes();
    BasicAttribute attr = new BasicAttribute(attrName);
    for (int i = 0; i < val.length; i++) {
      attr.add(val[i]);
    }
    ctx.modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, attrs);
  }

  public void replace(String dn, String attrName, Object oldval, Object newval)
      throws Exception {
    throw new Exception("ldap replace(old, new) not implemented");
  }

  /* (non-Javadoc)
   * @see org.bedework.tools.directory.Directory#modify(java.lang.String, javax.naming.directory.ModificationItem[])
   */
  public void modify(String dn, ModificationItem[] mods) throws Throwable {
    ctx.modifyAttributes(dn, mods);
  }

  /* (non-Javadoc)
   * @see org.bedework.tools.directory.Directory#getEnvironment()
   */
  public Properties getEnvironment() throws Throwable {
    Properties pr = new Properties();

    Hashtable<?, ?> tbl = ctx.getEnvironment();
    Enumeration e = tbl.keys();
    while (e.hasMoreElements()) {
      String name = (String)e.nextElement();
      String val = (String)tbl.get(name);

      pr.put(name, val);
    }
    return pr;
  }

  /* (non-Javadoc)
   * @see org.bedework.tools.directory.Directory#close()
   */
  public void close() {
    if (ctx != null) {
      try {
        ctx.close();
      } catch (Exception e) {};

      ctx = null;
    }
  }

  /** If the named property is present and has a value use that.
   *  Otherwise, set the value to the given default and use that.
   *
   * @param pr
   * @param name
   * @param defaultVal
   * @return String
   */
  public String checkProp(Properties pr, String name, String defaultVal) {
    String val = pr.getProperty(name);

    if (val == null) {
      pr.put(name, defaultVal);
      val = defaultVal;
    }

    return val;
  }
}
