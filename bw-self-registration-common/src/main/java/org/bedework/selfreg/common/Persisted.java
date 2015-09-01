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
import org.bedework.util.misc.Logged;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: mike Date: 8/31/15 Time: 17:04
 */
public class Persisted extends Logged {
  private DB db;

  private String dataPath;

  protected ObjectMapper mapper = new ObjectMapper(); // create once, reuse

  public Persisted(final String dataPath) {
    this.dataPath = dataPath;
  }

  public void open() throws SelfregException {
    getDb();
  }

  public void close() {
    closeDb();
  }

  public AccountInfo getAccount(final String confid) throws SelfregException {
    final byte[] bytes = db.get(Iq80DBFactory.bytes(confid));

    if (bytes == null) {
      return null;
    }

    return getJson(bytes, AccountInfo.class);
  }

  public void putAccount(final String confid,
                         final AccountInfo val) throws SelfregException {
    if (val.getEmail() == null) {
      throw new SelfregException("No email");
    }

    byte[] accountBytes = bytesJson(val);

    db.put(Iq80DBFactory.bytes(confid), accountBytes);
    db.put(Iq80DBFactory.bytes(val.getEmail()), accountBytes);
  }

  public void removeAccount(final String confid) throws SelfregException {
    db.delete(Iq80DBFactory.bytes(confid));
  }

  private DB getDb() throws SelfregException {
    if (db != null) {
      return db;
    }

    try {
      if (debug) {
        debug("Try to open leveldb at " + dataPath);
      }

      final File f = new File(dataPath);

      if (!f.isAbsolute()) {
        throw new SelfregException("levelDbPath must be absolute - found " +
                                      dataPath);
      }

      final Options options = new Options();
      options.createIfMissing(true);
      db = Iq80DBFactory.factory.open(new File(dataPath), options);
    } catch (final Throwable t) {
      // Always bad.
      error(t);
      throw new SelfregException(t);
    }

    return db;
  }

  private void closeDb() {
    if (db == null) {
      return;
    }

    try {
      db.close();
      db = null;
    } catch (final Throwable t) {
      warn("Error closing db: " + t.getMessage());
      error(t);
    }
  }

  /** ===================================================================
   *                   Json methods
   *  =================================================================== */

  protected void writeJson(final OutputStream out,
                           final Object val) throws SelfregException {
    try {
      mapper.writeValue(out, val);
    } catch (final Throwable t) {
      throw new SelfregException(t);
    }
  }

  protected byte[] bytesJson(final Object val) throws SelfregException {
    try {
      final ByteArrayOutputStream os = new ByteArrayOutputStream();

      mapper.writeValue(os, val);

      return os.toByteArray();
    } catch (final Throwable t) {
      throw new SelfregException(t);
    }
  }

  protected <T> T getJson(final byte[] value,
                          final Class<T> valueType) throws SelfregException {
    InputStream is = null;
    try {
      is = new ByteArrayInputStream(value);

      return mapper.readValue(is, valueType);
    } catch (final Throwable t) {
      throw new SelfregException(t);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (final Throwable ignored) {}
      }
    }
  }
}
