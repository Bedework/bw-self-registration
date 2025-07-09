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

import org.bedework.base.response.Response;
import org.bedework.selfreg.service.SelfregConfigProperties;

/** Define methods for directory maintenance. Most methods here corrspond to
 * web or service actions.
 *
 * @author douglm
 */
public interface DirMaint {
  /**
   * @param config our properties
   */
  void init(SelfregConfigProperties config);

	/** First step in obtaining a new account. The response is an encoded UUID
	 * which identifies this request. It should be provided as a parameter to
	 * confirm which will create the account or reject the UUID
	 *
	 * @param firstName users first
	 * @param lastName users last
	 * @param email their email
   * @param account non-null if allowed to specify
	 * @param pw pw they want
	 * @return Response: OK or error message
	 */
  Response<?> requestId(String firstName,
                        String lastName,
                        String email,
                        String account,
                        String pw);

  /** Send the user their account.
   *
   * @param email their email
   * @return null for OK, otherwise error message
   */
  String sendAccount(String email) ;

  /** Send the user a message to allow pw reset.
   *
   * @param account of user
   * @return null for OK, otherwise error message
   */
  String sendForgotpw(String account) ;

  /** Set pw.
   *
   * @param confid th econfirmation id
   * @param pw new pw
   * @return null for OK, otherwise error message
   */
  String setpw(String confid, String pw) ;

  /** Return account info for the account
   *
   * @param account of user
   * @return null if confId is bad
   */
  AccountInfo getAccount(String account);

  /** Return account info for the account represented by the confid.
   *
   * @param confId supplied by system
   * @return null if confId is bad
   */
  AccountInfo getAccountByConfid(String confId);

  /** Return account info for the account represented by the email.
   *
   * @param email for user
   * @return null if confId is bad
   */
  AccountInfo getAccountByEmail(String email);

  /** Delete the account given a confirmation id
   *
   * @param confId supplied by system
   * @return true for ok
   */
  boolean deleteAccount(String confId);

	/** Create (or enable) the account represented by the confid.
	 *
   * @param confId supplied by system
	 * @return account for OK - null if confId is bad
	 */
	String confirm(String confId);

	/** Called to send a message providing the recipient with their id
	 *
   * @param email of recipient
	 * @return true if email known - false otherwise
	 */
	boolean lostId(String email);

  /** Called to send a message providing a way for the recipient
   * to change their password
   *
   * @param id for user
   * @return true if id known - false otherwise
   */
  boolean lostPw(String id);

  /** Called with the confId sent by lostPw
   * @param confId supplied by system
   * @return true for valid confId else false
   */
  boolean confirmPwChange(String confId);

  /** Called with the confId sent by lostPw
   *
   * @param confId supplied by system
   * @param newPw password
   * @return true for valid confId else false
   */
  boolean confirmPwChange(String confId,
                          String newPw);

  /* ==============================================================
   * Service interface methods
   * ============================================================== */

  /** Create an account. Used by the service interface
   *
   * @param accountName non-null if allowed to specify
   * @param firstName users first
   * @param lastName users last
   * @param email their email
   * @param pw pw they want
   *
   * @return true if created OK - false if exists
   */
  boolean createAccount(String accountName,
                        String firstName,
                        String lastName,
                        String email,
                        String pw,
                        String encodedPw);

  /** Display an account
   *
   * @param account for user
   * @return message or displayable information
   */
  String displayAccount(String account);

  /** Set account password
   *
   * @param account for user
   * @param password for user
   */
  void setUserPassword(String account,
                       String password);

  /** Add a group
   *
   * @param group name
   * @param account for user
   * @return true if created OK - false if exists
   */
  boolean createGroup(String group,
                      String account);

  /** Add a group member
   *
   * @param group name
   * @param account for user
   */
  void addGroupMember(String group,
                      String account);
}
