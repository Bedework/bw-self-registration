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

import org.bedework.selfreg.common.SelfregConfigPropertiesI;


/** Run the Bedework synch engine service
 *
 * @author douglm
 */
public interface SelfregMBean extends SelfregConfigPropertiesI {
  /* ========================================================================
   * Attributes
   * ======================================================================== */

  /** Name apparently must be the same as the name attribute in the
   * jboss service definition
   *
   * @return Name
   */
  public String getName();

  /* ========================================================================
   * Variables
   * ======================================================================== */

  /** Statement delimiter
   *
   * @param val
   * /
  public void setDelimiter(String val);

  /**
   * @return Statement delimiter
   * /
  public String getDelimiter();
  */

  /* ========================================================================
   * Operations
   * ======================================================================== */

  /** Add a user
   *
   * @param account
   * @param first
   * @param last
   * @param email
   * @param password
   * @return status
   */
  public String adduser(String account,
                        String first,
                        String last,
                        String email,
                        String password);

  /** Set a user password
   *
   * @param account
   * @param password
   * @return status
   */
  public String setUserPassword(String account,
                                String password);

  /** Remove a user
   *
   * @param account
   * @return status
   */
  public String removeUser(String account);

  /* ========================================================================
   * Lifecycle
   * ======================================================================== */

  /** Lifecycle
   *
   */
  public void create();

  /** Lifecycle
   *
   */
  public void start();

  /** Lifecycle
   *
   */
  public void stop();

  /** Lifecycle
   *
   * @return true if started
   */
  public boolean isStarted();

  /** Lifecycle
   *
   */
  public void destroy();
}
