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

import org.bedework.util.misc.ToString;

/**
 * User: mike Date: 8/31/15 Time: 17:04
 */
public class AccountInfo {
  private Long id;

  private int seq;

  private String confid;

  private String account;

  private boolean enabled;

  private String dtstamp;

  private String firstName;
  private String lastName;
  private String email;
  private String pw;

  private String properties;

  public Long getId() {
    return id;
  }

  public void setId(final Long val) {
    id = val;
  }

  public int getSeq() {
    return seq;
  }

  public void setSeq(final int val) {
    seq = val;
  }

  public String getConfid() {
    return confid;
  }

  public void setConfid(final String val) {
    confid = val;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(final String val) {
    account = val;
  }

  public boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(final boolean val) {
    enabled = val;
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

  public String getProperties() {
    return properties;
  }

  public void setProperties(final String val) {
    properties = val;
  }

  public String toString() {
    final ToString ts = new ToString(this);

    ts.append("id", getId());
    ts.append("seq", getSeq());

    ts.append("confid", getConfid());
    ts.append("account", getAccount());
    ts.append("enabled", getEnabled());

    ts.append("dtstamp", getDtstamp());

    ts.append("firstName", getFirstName());
    ts.append("lastName", getLastName());
    ts.append("email", getEmail());
    ts.append("pw", getPw());
    ts.append("properties", getProperties());

    return ts.toString();
  }
}
