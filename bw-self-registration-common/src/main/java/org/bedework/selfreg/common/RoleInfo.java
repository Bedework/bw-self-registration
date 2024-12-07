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

/** This isn't used other than to create a table so that the jboss
 * module works without errors.
 * <br/>
 * User: mike Date: 8/31/15 Time: 17:04
 */
public class RoleInfo {
  private Long id;

  private int seq;

  private String account;

  private String role;

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

  public String getAccount() {
    return account;
  }

  public void setAccount(final String val) {
    account = val;
  }

  public String getRole() {
    return role;
  }

  public void setRole(final String val) {
    role = val;
  }


  public String toString() {
    final ToString ts = new ToString(this);

    ts.append("id", getId());
    ts.append("seq", getSeq());

    ts.append("account", getAccount());
    ts.append("role", getRole());

    return ts.toString();
  }
}
