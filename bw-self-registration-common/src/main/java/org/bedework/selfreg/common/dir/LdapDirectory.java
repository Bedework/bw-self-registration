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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

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

    @Override
    public DirRecord nextRecord() throws SelfregException {
      try {
        SearchResult s = null;

        if (recs == null) {
          throw new SelfregException("null search result");
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
      } catch (Throwable t) {
        throw new SelfregException(t);
      }
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
   * @throws SelfregException
   */
  public LdapDirectory(final Properties pr, final String mngrDN,
                       final String pw,
                       final boolean debug) throws SelfregException {
    super(pr, mngrDN, pw, debug);
  }

  /* (non-Javadoc)
   * @see org.bedework.tools.directory.Directory#init(java.util.Properties, java.lang.String, java.lang.String, boolean)
   */
  @Override
  public void init(final Properties pr, final String mngrDN, final String pw,
                   final boolean debug) throws SelfregException {
    if (pr == null) {
      throw new SelfregException("No properties supplied");
    }
    super.init(pr, mngrDN, pw, debug);
    this.mngrDN = mngrDN;
    this.pw = pw;
    reInit();
  }

  @Override
  public void reInit() throws SelfregException {
    try {
      /** If we weren't given a url try to get one.
       */

      if (pr == null) {
        throw new Exception("No properties supplied (again)");
      }
      checkProp(pr, Context.PROVIDER_URL, defaultURL);
      checkProp(pr, Context.INITIAL_CONTEXT_FACTORY, defaultCTX);

      if ((mngrDN != null) && (pw != null)) {
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
      if (debug) {
        debugMsg("Directory: init OK " + pr.get(Context.PROVIDER_URL));
      }
    } catch (Throwable t) {
      throw new SelfregException(t);
    }
  }

  @Override
  public void destroy(final String dn) throws SelfregException {
    try {
      ctx.destroySubcontext(dn);
    } catch (Throwable t) {
      throw new SelfregException(t);
    }
  }

  @Override
  public DirSearchResult search(final String base, String filter,
                                final int scope) throws SelfregException {
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
    } catch (Throwable t) {
      throw new SelfregException(t);
    }

    return sres;
  }

  @Override
  public boolean create(final DirRecord rec) throws SelfregException {
    try {
      ctx.createSubcontext(rec.getDn(), rec.getAttributes());
      return true;
    } catch (NameAlreadyBoundException nabe) {
      return false;
    } catch (Throwable t) {
      throw new SelfregException(t);
    }
  }

  @Override
  public void replace(final String dn,
                      final String attrName,
                      final Object val) throws SelfregException {
    try {
      BasicAttributes attrs = new BasicAttributes(attrName, val);
      ctx.modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, attrs);
    } catch (Throwable t) {
      throw new SelfregException(t);
    }
  }

  @Override
  public void replace(final String dn,
                      final String attrName,
                      final Object[] val) throws SelfregException {
    try {
      BasicAttributes attrs = new BasicAttributes();
      BasicAttribute attr = new BasicAttribute(attrName);

      for (Object o: val) {
        attr.add(o);
      }
      ctx.modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, attrs);
    } catch (Throwable t) {
      throw new SelfregException(t);
    }
  }

  @Override
  public void replace(final String dn,
                      final String attrName,
                      final Object oldval,
                      final Object newval) throws SelfregException {
    throw new SelfregException("ldap replace(old, new) not implemented");
  }

  @Override
  public void modify(final String dn, final ModificationItem[] mods) throws SelfregException {
    try {
      ctx.modifyAttributes(dn, mods);
    } catch (Throwable t) {
      throw new SelfregException(t);
    }
  }

  @Override
  public Properties getEnvironment() throws SelfregException {
    try {
      Properties pr = new Properties();

      Hashtable<?, ?> tbl = ctx.getEnvironment();
      Enumeration e = tbl.keys();
      while (e.hasMoreElements()) {
        String name = (String)e.nextElement();
        String val = (String)tbl.get(name);

        pr.put(name, val);
      }
      return pr;
    } catch (Throwable t) {
      throw new SelfregException(t);
    }
  }

  @Override
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
  public String checkProp(final Properties pr, final String name, final String defaultVal) {
    String val = pr.getProperty(name);

    if (val == null) {
      pr.put(name, defaultVal);
      val = defaultVal;
    }

    return val;
  }
}
