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
import org.bedework.selfreg.shared.AccountInfo;
import org.bedework.selfreg.shared.SelfregConfigProperties;

/** Define methods for directory maintenance. Most methods here corrspond to
 * web or service actions.
 *
 * @author douglm
 */
public interface DirMaint {
  /**
   * @param config
   * @throws SelfregException
   */
  void init(SelfregConfigProperties config) throws SelfregException;

	/** First step in obtaining a new account. The response is an encoded UUID
	 * which identifies this request. It should be provided as a parameter to
	 * confirm which will create the account or reject the UUID
	 *
	 * @param firstName users first
	 * @param lastName users last
	 * @param email their email
	 * @param pw pw they want
	 * @return null for OK or error message
	 * @throws SelfregException
	 */
	String requestId(String firstName,
                   String lastName,
	                 String email,
	                 String pw) throws SelfregException;

  /** Send the user their account.
   *
   * @param email
   * @return null for OK, otherwise error message
   * @throws SelfregException
   */
  String sendAccount(String email)  throws SelfregException;

  /** Send the user a message to allow pw reset.
   *
   * @param account -
   * @return null for OK, otherwise error message
   * @throws SelfregException
   */
  String sendForgotpw(String account)  throws SelfregException;

  /** Set pw.
   *
   * @param confid -
   * @param pw -
   * @return null for OK, otherwise error message
   * @throws SelfregException
   */
  String setpw(String confid, String pw)  throws SelfregException;

  /** Return account info for the account
   *
   * @param account of user
   * @return null if confId is bad
   * @throws SelfregException
   */
  AccountInfo getAccount(String account) throws SelfregException;

  /** Return account info for the account represented by the confid.
   *
   * @param confId supplied by system
   * @return null if confId is bad
   * @throws SelfregException
   */
  AccountInfo getAccountByConfid(String confId) throws SelfregException;

  /** Return account info for the account represented by the email.
   *
   * @param email for user
   * @return null if confId is bad
   * @throws SelfregException
   */
  AccountInfo getAccountByEmail(String email) throws SelfregException;

  /** Delete the account given a confirmation id
   *
   * @param confId
   * @return
   * @throws SelfregException
   */
  boolean deleteAccount(final String confId) throws SelfregException;

	/** Create (or enable) the account represented by the confid.
	 *
   * @param confId supplied by system
	 * @return account for OK - null if confId is bad
	 * @throws SelfregException
	 */
	String confirm(String confId) throws SelfregException;

	/** Called to send a message providing the recipient with their id
	 *
   * @param email of recipient
	 * @return true if email known - false otherwise
	 * @throws SelfregException
	 */
	boolean lostId(String email) throws SelfregException;

  /** Called to send a message providing a way for the recipient their password
   *
   * @param id
   * @return true if id known - false otherwise
   * @throws SelfregException
   */
  boolean lostPw(String id) throws SelfregException;

  /** Called with the confId sent by lostPw
   * @param confid
   * @return true for valid confId else false
   * @throws SelfregException
   */
  boolean confirmPwChange(String confid) throws SelfregException;

  /** Called with the confId sent by lostPw
   *
   * @param confid
   * @param newPw
   * @return true for valid confId else false
   * @throws SelfregException
   */
  boolean confirmPwChange(String confid,
                          String newPw) throws SelfregException;

  /* ========================================================================
   * Service interface methods
   * ======================================================================== */

  /** Create an account. Used by the service interface
   * @param accountName
   * @param firstName
   * @param lastName
   * @param email
   * @param pw
   * @return true if created OK - false if exists
   * @throws SelfregException
   */
  boolean createAccount(String accountName,
                        String firstName,
                        String lastName,
                        String email,
                        String pw,
                        String encodedPw) throws SelfregException;

  /** Display an account
   *
   * @param account
   * @return message or displayable information
   * @throws SelfregException
   */
  public String displayAccount(String account) throws SelfregException;

  /** Set account password
   *
   * @param account
   * @param password
   * @throws SelfregException
   */
  public void setUserPassword(String account,
                                String password) throws SelfregException;

  /** Add a group
   *
   * @param group
   * @param account
   * @return true if created OK - false if exists
   * @throws SelfregException
   */
  public boolean createGroup(String group,
                             String account) throws SelfregException;

  /** Add a group member
   *
   * @param group
   * @param account
   * @throws SelfregException
   */
  public void addGroupMember(String group,
                             String account) throws SelfregException;
}
