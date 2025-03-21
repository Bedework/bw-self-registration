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

import org.bedework.selfreg.common.AccountInfo;
import org.bedework.util.jmx.ConfBaseMBean;
import org.bedework.util.jmx.MBeanInfo;

import java.util.List;

/** Run the Bedework selfreg engine service
 *
 * @author douglm
 */
public interface SelfregMBean extends ConfBaseMBean,
        SelfregConfigProperties {
  /** Export schema to database?
   *
   * @param val Export schema to database?
   */
  void setExport(boolean val);

  /**
   * @return true for export schema
   */
  @MBeanInfo("Export (write) schema to database?")
  boolean getExport();

  /* ==============================================================
   * Operations
   * ============================================================== */

  /** Create or dump new schema. If export and drop set will try to drop tables.
   * Export and create will create a schema in the db and export, drop, create
   * will drop tables, and try to create  anew schema.
   * <p>
   * The export, create and drop flags will all be reset to false after this,
   * whatever the result. This avoids accidental damage to the db.
   *
   * @return Completion message
   */
  @MBeanInfo("Start build of the database schema. Set export flag to write to db.")
  String schema();

  /** Returns status of the schema build.
   *
   * @return Completion messages
   */
  @MBeanInfo("Status of the database schema build.")
  List<String> schemaStatus();

  /** Display request status
   *
   * @param confId supplied by system
   * @return status
   */
  @MBeanInfo("Display request status")
  String displayRequest(@MBeanInfo("Confid")String confId);

  /** Add a user
   *
   * @param account non-null if allowed to specify
   * @param first users first
   * @param last users last
   * @param email their email
   * @param password pw they want
   * @return status
   */
  @MBeanInfo("Add a user")
  String addUser(@MBeanInfo("Account")String account,
                 @MBeanInfo("Firstname")String first,
                 @MBeanInfo("Lastname")String last,
                 @MBeanInfo("Email")String email,
                 @MBeanInfo("Password")String password);

  /** Display a user
   *
   * @param account for user
   * @return status
   */
  @MBeanInfo("Display a user")
  String displayUser(@MBeanInfo("Account")String account);

  /** Set a user password
   *
   * @param account for user
   * @param password for user
   * @return status
   */
  @MBeanInfo("Set a user password")
  String setUserPassword(@MBeanInfo("Account")String account,
                         @MBeanInfo("Password")String password);

  /** Remove a user
   *
   * @param account for user
   * @return status
   */
  @MBeanInfo("Remove a user")
  String removeUser(@MBeanInfo("Account")String account);

  /** Add a group
   *
   * @param group name
   * @param account for user
   * @return status
   */
  @MBeanInfo("Add a group")
  String addGroup(@MBeanInfo("Group")String group,
                  @MBeanInfo("Account")String account);

  /** Add a group member
   *
   * @param group name
   * @param account for user
   * @return status
   */
  @MBeanInfo("Add a group member")
  String addGroupMember(@MBeanInfo("Group")String group,
                        @MBeanInfo("Account")String account);

  /** (Re)load the configuration
   *
   * @return status
   */
  @MBeanInfo("(Re)load the configuration")
  String loadConfig();

  @MBeanInfo("Get an account entry given the user account")
  AccountInfo getAccount(String account);
}
