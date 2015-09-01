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

/**
 * User: mike Date: 8/31/15 Time: 17:04
 */
public class AccountInfo {
  private String account;

  private String dtstamp;

  private String firstName;
  private String lastName;
  private String email;
  private String pw;

  public String getAccount() {
    return account;
  }

  public void setAccount(final String val) {
    account = val;
  }

  public String getDtstamp() {
    return dtstamp;
  }

  public void setDtstamp(final String val) {
    dtstamp = val;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(final String val) {
    firstName = val;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(final String val) {
    lastName = val;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String val) {
    email = val;
  }

  public String getPw() {
    return pw;
  }

  public void setPw(final String val) {
    pw = val;
  }
}
