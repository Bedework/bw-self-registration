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

package org.bedework.selfreg.common.mail;

import java.io.Serializable;
import java.util.Collection;

import org.bedework.selfreg.service.SelfregConfigProperties;
import org.bedework.selfreg.common.exception.SelfregException;

/**
 * @author Mike Douglass douglm  rpi.edu
 */
public interface MailerIntf extends Serializable {
  /**
   * @param config
   * @throws SelfregException
   */
  public void init(SelfregConfigProperties config) throws SelfregException;

  /** Return a collection of mail list ids
   *
   * @return collection of mail list ids
   * @throws SelfregException
   */
  public Collection<String> listLists() throws SelfregException;

  /**
   * @param val
   * @throws SelfregException
   */
  public void post(Message val) throws SelfregException;
}

