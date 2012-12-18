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

import org.apache.log4j.Logger;

/**
 * @author douglm
 *
 */
public class Selfreg implements SelfregMBean {
  private transient Logger log;

  private boolean running;

  /* ========================================================================
   * Attributes
   * ======================================================================== */

  /* (non-Javadoc)
   * @see org.bedework.dumprestore.BwDumpRestoreMBean#getName()
   */
  @Override
  public String getName() {
    /* This apparently must be the same as the name attribute in the
     * jboss service definition
     */
    return "org.bedework:service=Selfreg";
  }

  /* ========================================================================
   * Fields
   * ======================================================================== */

  /* ========================================================================
   * Operations
   * ======================================================================== */

  @Override
  public void adduser(final String account,
                      final String first,
                      final String last,
                      final String email,
                      final String password) {

  }

  /** Set a user password
   *
   * @param account
   * @param password
   */
  @Override
  public void setUserPassword(final String account,
                              final String password) {

  }

  /** Remove a user
   *
   * @param account
   */
  @Override
  public void removeUser(final String account) {

  }

  /* ========================================================================
   * Lifecycle
   * ======================================================================== */

  /* (non-Javadoc)
   * @see org.bedework.dumprestore.BwDumpRestoreMBean#create()
   */
  @Override
  public void create() {
    // An opportunity to initialise
  }

  /* (non-Javadoc)
   * @see org.bedework.indexer.BwIndexerMBean#start()
   */
  @Override
  public void start() {
  }

  /* (non-Javadoc)
   * @see org.bedework.indexer.BwIndexerMBean#stop()
   */
  @Override
  public void stop() {
  }

  /* (non-Javadoc)
   * @see org.bedework.indexer.BwIndexerMBean#isStarted()
   */
  @Override
  public boolean isStarted() {
    return true;
  }

  /* (non-Javadoc)
   * @see org.bedework.dumprestore.BwDumpRestoreMBean#destroy()
   */
  @Override
  public void destroy() {
  }

  /* ====================================================================
   *                   Protected methods
   * ==================================================================== */

  protected void info(final String msg) {
    getLogger().info(msg);
  }

  protected void trace(final String msg) {
    getLogger().debug(msg);
  }

  protected void error(final Throwable t) {
    getLogger().error(this, t);
  }

  protected void error(final String msg) {
    getLogger().error(msg);
  }

  /* Get a logger for messages
   */
  protected Logger getLogger() {
    if (log == null) {
      log = Logger.getLogger(this.getClass());
    }

    return log;
  }
}
