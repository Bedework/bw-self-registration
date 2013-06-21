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

import org.bedework.selfreg.common.SelfregConfigProperties;
import edu.rpi.cmt.jmx.ConfBaseMBean;
import edu.rpi.cmt.jmx.MBeanInfo;


/** Run the Bedework selfreg engine service
 *
 * @author douglm
 */
public interface SelfregMBean extends ConfBaseMBean,
        SelfregConfigProperties {
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
  @MBeanInfo("Add a user")
  public String addUser(String account,
                        String first,
                        String last,
                        String email,
                        String password);

  /** Display a user
   *
   * @param account
   * @return status
   */
  @MBeanInfo("Display a user")
  public String displayUser(String account);

  /** Set a user password
   *
   * @param account
   * @param password
   * @return status
   */
  @MBeanInfo("Set a user password")
  public String setUserPassword(String account,
                                String password);

  /** Remove a user
   *
   * @param account
   * @return status
   */
  @MBeanInfo("Remove a user")
  public String removeUser(String account);

  /** Add a group
   *
   * @param group
   * @param account
   * @return status
   */
  @MBeanInfo("Add a group")
  public String addGroup(String group,
                         String account);

  /** Add a group member
   *
   * @param group
   * @param account
   * @return status
   */
  @MBeanInfo("Add a group member")
  public String addGroupMember(String group,
                               String account);

  /** (Re)load the configuration
   *
   * @return status
   */
  @MBeanInfo("(Re)load the configuration")
  String loadConfig();
}
