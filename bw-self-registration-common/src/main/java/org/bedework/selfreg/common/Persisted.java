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

import org.bedework.base.exc.BedeworkException;
import org.bedework.database.db.DbSession;
import org.bedework.database.db.DbSessionFactoryProvider;
import org.bedework.database.db.DbSessionFactoryProviderImpl;
import org.bedework.selfreg.common.exception.SelfregException;
import org.bedework.util.config.OrmConfigI;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import com.fasterxml.jackson.databind.ObjectMapper;

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
  private final OrmConfigI config;

  /** */
  protected boolean open;

  /** Incremented we were created for debugging */
  private final static AtomicLong globalSessionCt = new AtomicLong();

  private long sessionCt;

  /* Factory used to obtain a session
   */
  private static DbSessionFactoryProvider factoryProvider;

  /** Current database session - exists only across one user interaction
   */
  protected DbSession sess;

  protected ObjectMapper mapper = new ObjectMapper(); // create once, reuse

  public Persisted(final OrmConfigI config) {
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
      } catch (final BedeworkException e) {
        throw new SelfregException(e);
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
          "select a from AccountInfo a " +
                  "where a.account=:account";

  public AccountInfo getAccount(final String account) {
    try {
      sess.createQuery(findByAccountQuery);
      sess.setString("account", account);

      return (AccountInfo)sess.getUnique();
    } catch (final BedeworkException e) {
      throw new SelfregException(e);
    }
  }

  private static final String findByConfidQuery =
          "select a from AccountInfo a " +
                  "where a.confid=:confid";

  public AccountInfo getAccountByConfid(final String confid) {
    try {
      sess.createQuery(findByConfidQuery);
      sess.setString("confid", confid);

      return (AccountInfo)sess.getUnique();
    } catch (final BedeworkException e) {
      throw new SelfregException(e);
    }
  }

  private static final String findByEmailQuery =
          "select a from AccountInfo a " +
                  "where a.email=:email";

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
    } catch (final BedeworkException e) {
      throw new SelfregException(e);
    }
  }

  public boolean emailPresent(final String val) {
    return getAccountByEmail(val) != null;
  }

  public void addAccount(final AccountInfo val) {
    validate(val);

    try {
      sess.add(val);
    } catch (final BedeworkException e) {
      throw new SelfregException(e);
    }
  }

  private static final String countQuery =
          "select count(*) from AccountInfo";

  public long numAccounts() {
    try {
      sess.createQuery(countQuery);
      @SuppressWarnings("unchecked")
      final Collection<Long> counts = (Collection<Long>)sess.getList();

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
    } catch (final BedeworkException e) {
      throw new SelfregException(e);
    }
  }

  public void addRole(final RoleInfo val) {
    try {
      sess.add(val);
    } catch (final BedeworkException e) {
      throw new SelfregException(e);
    }
  }

  public void updateAccount(final AccountInfo val) {
    validate(val);

    try {
      sess.update(val);
    } catch (final BedeworkException e) {
      throw new SelfregException(e);
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
          "select r from RoleInfo r " +
                  "where r.account=:account";

  public void removeAccount(final AccountInfo val) {
    try {
      sess.createQuery(findRoleByAccountQuery);
      sess.setString("account", val.getAccount());

      final RoleInfo ri = (RoleInfo)sess.getUnique();

      sess.delete(val);

      if (ri != null) {
        sess.delete(ri);
      }
    } catch (final BedeworkException e) {
      throw new SelfregException(e);
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
      if (factoryProvider == null) {
        factoryProvider =
                new DbSessionFactoryProviderImpl()
                        .init(config.getOrmProperties());
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
          debug("New orm session for " + sessionCt);
        }
        sess = factoryProvider.getNewSession();

        debug("Open session for " + sessionCt);
      }
    } catch (final BedeworkException e) {
      throw new SelfregException(e);
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
    } catch (final BedeworkException e) {
      throw new SelfregException(e);
    }
  }

  protected void rollbackTransaction() {
    try {
      checkOpen();
      sess.rollback();
    } catch (final BedeworkException e) {
      throw new SelfregException(e);
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
