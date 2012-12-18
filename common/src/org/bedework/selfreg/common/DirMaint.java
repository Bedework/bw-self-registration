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

/** Define methods for directory maintenance. Most methods here corrspond to
 * web or service actions.
 *
 * @author douglm
 */
public interface DirMaint {
  /**
   * @throws SelfregException
   */
  void init() throws SelfregException;

	/** First step in obtaining a new account. The response is an encoded UUID
	 * which identifies this request. It should be provided as a parameter to
	 * confirm which will create the account or reject the UUID
	 *
   * @param accountName - supplied by user
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param pw
	 * @return confirmation id
	 * @throws SelfregException
	 */
	String requestId(String accountName,
	                 String firstName,
                   String lastName,
	                 String email,
	                 String pw) throws SelfregException;

	/** Create (or enable) the account represented by the confid.
	 *
	 * @param confId
	 * @return true for OK - false if confId is bad
	 * @throws SelfregException
	 */
	boolean confirm(String confId) throws SelfregException;

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
	                      String pw) throws SelfregException;

	/** Called to send a message providing the recipient with their id
	 *
	 * @param email
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
}
