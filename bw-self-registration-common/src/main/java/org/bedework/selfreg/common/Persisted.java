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

import org.bedework.selfreg.common.exception.SelfregException;
import org.bedework.util.config.HibernateConfigI;
import org.bedework.util.hibernate.HibException;
import org.bedework.util.hibernate.HibSession;
import org.bedework.util.hibernate.HibSessionFactory;
import org.bedework.util.hibernate.HibSessionImpl;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.SessionFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User: mike Date: 8/31/15 Time: 17:04
 */
public class Persisted implements Logged {
  private final HibernateConfigI config;

  /** */
  protected boolean open;

  /** Incremented we were created for debugging */
  private final static AtomicLong globalSessionCt = new AtomicLong();

  private long sessionCt;

  /* Factory used to obtain a session
   */
  private static SessionFactory sessionFactory;

  /** Current hibernate session - exists only across one user interaction
   */
  protected HibSession sess;

  protected ObjectMapper mapper = new ObjectMapper(); // create once, reuse

  public Persisted(final HibernateConfigI config) {
    this.config = config;
  }

  public boolean startTransaction() {
    if (isOpen()) {
      return false;
    }

    openSession();
    open = true;
    return true;
  }

  public boolean isOpen() {
    return open;
  }

  public void endTransaction() {
    try {
      checkOpen();

      if (debug()) {
        debug("End transaction for " + sessionCt);
      }

      try {
        if (!sess.rolledback()) {
          sess.commit();
        }
      } catch (final HibException he) {
        throw new SelfregException(he);
      }
    } catch (final SelfregException ne) {
      try {
        rollbackTransaction();
      } catch (final SelfregException ignored) {}
      throw ne;
    } finally {
      try {
        closeSession();
      } catch (final SelfregException ignored) {}
      open = false;
    }
  }

  private static final String findByAccountQuery =
          "from " + AccountInfo.class.getName() +
                  " a where a.account=:account";

  public AccountInfo getAccount(final String account) {
    try {
      sess.createQuery(findByAccountQuery);
      sess.setString("account", account);

      return (AccountInfo)sess.getUnique();
    } catch (final HibException he) {
      throw new SelfregException(he);
    }
  }

  private static final String findByConfidQuery =
          "from " + AccountInfo.class.getName() +
                  " a where a.confid=:confid";

  public AccountInfo getAccountByConfid(final String confid) {
    try {
      sess.createQuery(findByConfidQuery);
      sess.setString("confid", confid);

      return (AccountInfo)sess.getUnique();
    } catch (final HibException he) {
      throw new SelfregException(he);
    }
  }

  private static final String findByEmailQuery =
          "from " + AccountInfo.class.getName() +
                  " a where a.email=:email";

  public AccountInfo getAccountByEmail(final String email) {
    try {
      sess.createQuery(findByEmailQuery);
      sess.setString("email", email);

      final List<?> l = sess.getList();

      if (l.isEmpty()) {
        return null;
      }

      if (l.size() > 1) {
        error("Bad data email has multiple occurences: " + email);
      }

      return (AccountInfo)l.get(0);
    } catch (final HibException he) {
      throw new SelfregException(he);
    }
  }

  public boolean emailPresent(final String val) {
    return getAccountByEmail(val) != null;
  }

  public void addAccount(final AccountInfo val) {
    validate(val);

    try {
      sess.save(val);
    } catch (final HibException he) {
      throw new SelfregException(he);
    }
  }

  private static final String countQuery =
          "select count(*) from " + AccountInfo.class.getName();

  public long numAccounts() {
    try {
      sess.createQuery(countQuery);
      @SuppressWarnings("unchecked")
      final Collection<Long> counts = sess.getList();

      long total = 0;

      if (debug()) {
        debug(" ----------- count = " + counts.size());
        if (!counts.isEmpty()) {
          debug(" ---------- first el class is " + counts.iterator()
                                                         .next()
                                                         .getClass()
                                                         .getName());
        }
      }

      for (final Long l: counts) {
        total += l;
      }

      return total;
    } catch (final HibException he) {
      throw new SelfregException(he);
    }
  }

  public void addRole(final RoleInfo val) {
    try {
      sess.save(val);
    } catch (final HibException he) {
      throw new SelfregException(he);
    }
  }

  public void updateAccount(final AccountInfo val) {
    validate(val);

    try {
      sess.update(val);
    } catch (final HibException he) {
      throw new SelfregException(he);
    }
  }

  private void validate(final AccountInfo val) {
    if (val.getEmail() == null) {
      throw new SelfregException("No email");
    }

    if (val.getConfid() == null) {
      throw new SelfregException("No confid");
    }

    if (val.getAccount() == null) {
      throw new SelfregException("No account");
    }
  }

  private static final String findRoleByAccountQuery =
          "from " + RoleInfo.class.getName() +
                  " r where r.account=:account";

  public void removeAccount(final AccountInfo val) {
    try {
      sess.createQuery(findRoleByAccountQuery);
      sess.setString("account", val.getAccount());

      final RoleInfo ri = (RoleInfo)sess.getUnique();

      sess.delete(val);

      if (ri != null) {
        sess.delete(ri);
      }
    } catch (final HibException he) {
      throw new SelfregException(he);
    }
  }

  /* ====================================================================
   *                   Session methods
   * ==================================================================== */

  protected void checkOpen() {
    if (!isOpen()) {
      throw new SelfregException("Session call when closed");
    }
  }

  protected synchronized void openSession() {
    if (isOpen()) {
      throw new SelfregException("Already open");
    }

    try {
      if (sessionFactory == null) {
        sessionFactory = HibSessionFactory.
                getSessionFactory(config.getHibernateProperties());
      }

      open = true;

      if (sess != null) {
        warn("Session is not null. Will close");
        try {
          endTransaction();
        } catch (final Throwable ignored) {
        }
      }

      sessionCt = globalSessionCt.incrementAndGet();

      if (sess == null) {
        if (debug()) {
          debug("New hibernate session for " + sessionCt);
        }
        sess = new HibSessionImpl();
        sess.init(sessionFactory);
        debug("Open session for " + sessionCt);
      }
    } catch (final HibException he) {
      throw new SelfregException(he);
    }

    beginTransaction();
  }

  protected synchronized void closeSession() {
    if (!isOpen()) {
      if (debug()) {
        debug("Close for " + sessionCt + " closed session");
      }
      return;
    }

    if (debug()) {
      debug("Close for " + sessionCt);
    }

    try {
      if (sess != null) {
        if (sess.rolledback()) {
          sess = null;
          return;
        }

        if (sess.transactionStarted()) {
          sess.rollback();
        }
//        sess.disconnect();
        sess.close();
        sess = null;
      }
    } catch (final Throwable t) {
      try {
        sess.close();
      } catch (final Throwable ignored) {}
      sess = null; // Discard on error
    } finally {
      open = false;
    }
  }

  private void beginTransaction() {
    checkOpen();

    if (debug()) {
      debug("Begin transaction for " + sessionCt);
    }
    try {
      sess.beginTransaction();
    } catch (final HibException he) {
      throw new SelfregException(he);
    }
  }

  protected void rollbackTransaction() {
    try {
      checkOpen();
      sess.rollback();
    } catch (final HibException he) {
      throw new SelfregException(he);
    }
  }

  /** =============================================================
   *                   Json methods
   *  ============================================================= */

  protected void writeJson(final OutputStream out,
                           final Object val) {
    try {
      mapper.writeValue(out, val);
    } catch (final Throwable t) {
      throw new SelfregException(t);
    }
  }

  protected byte[] bytesJson(final Object val) {
    try {
      final ByteArrayOutputStream os = new ByteArrayOutputStream();

      mapper.writeValue(os, val);

      return os.toByteArray();
    } catch (final Throwable t) {
      throw new SelfregException(t);
    }
  }

  protected <T> T getJson(final byte[] value,
                          final Class<T> valueType) {
    try (final InputStream is = new ByteArrayInputStream(value)) {
      return mapper.readValue(is, valueType);
    } catch (final Throwable t) {
      throw new SelfregException(t);
    }
  }

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
