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

import org.bedework.util.misc.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/** Bean to represent a mail message. A serializable simplification and
 * restatement of the javax.mail.Message class. Serializability is
 * important if we want to queue the message..
 *
 * @author Mike Douglass douglm@rpi.edu
 */
public class Message implements Serializable {
  /** Who is it from
   */
  private String from;

  /** Who we send to
   */
  private String[] mailTo;

  /** Who we cc to
   */
  private String[] ccTo;

  /** Who we bcc to
   */
  private String[] bccTo;

  /** Subject
   */
  private String subject;

  /** When the message was queued
   */
  private long genDate;

  /** Content
   */
  private String content;

  private Collection<Attachment> attachments;

  /**
   * @param val
   */
  public void setFrom(String val) {
    from = val;
  }

  /**
   * @return value
   */
  public String getFrom() {
    return from;
  }

  /**
   * @param val
   */
  public void setMailTo(String[] val) {
    mailTo = val;
  }

  /**
   * @return value
   */
  public String[] getMailTo() {
    return mailTo;
  }

  /**
   * @param val
   */
  public void setCcTo(String[] val) {
    ccTo = val;
  }

  /**
   * @return value
   */
  public String[] getCcTo() {
    return ccTo;
  }

  /**
   * @param val
   */
  public void setBccTo(String[] val) {
    bccTo = val;
  }

  /**
   * @return value
   */
  public String[] getBccTo() {
    return bccTo;
  }

  /**
   * @param val
   */
  public void setSubject(String val) {
    subject = val;
  }

  /**
   * @return value
   */
  public String getSubject() {
    return subject;
  }

  /**
   * @param val
   */
  public void setGenDate(long val) {
    genDate = val;
  }

  /**
   * @return value
   */
  public long getGenDate() {
    return genDate;
  }

  /**
   * @param val
   */
  public void setContent(String val) {
    content = val;
  }

  /**
   * @return value
   */
  public String getContent() {
    return content;
  }

  /**
   * @param val
   */
  public void setAttachments(Collection<Attachment> val) {
    attachments = val;
  }

  /**
   * @return value
   */
  public Collection<Attachment> getAttachments() {
    if (attachments == null) {
      attachments = new ArrayList<Attachment>();
    }

    return attachments;
  }

  /**
   * @param val
   */
  public void addAttachment(Attachment val) {
    getAttachments().add(val);
  }

  public String toString() {
    final ToString ts = new ToString(this);

    ts.append("from", getFrom());
    ts.append("to", getMailTo());
    ts.append("cc", getCcTo());
    ts.append("bcc", getBccTo());

    ts.append("subject", getSubject());
    ts.append("content", getContent());

    ts.append("attachments", getAttachments());

    return ts.toString();
  }
}
