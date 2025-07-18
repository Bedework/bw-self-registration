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

import org.bedework.selfreg.common.exception.SelfregException;
import org.bedework.selfreg.service.SelfregConfigProperties;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import jakarta.activation.CommandMap;
import jakarta.activation.MailcapCommandMap;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

/** A mailer which provides some minimal functionality for testing.
 * We do not consider many issues such as spam prevention, efficiency in
 * mailing to large lists, etc.
 *
 * @author  Mike Douglass douglm@rpi.edu
 */
public class Mailer implements Logged, MailerIntf {
  private SelfregConfigProperties config;

  private Session sess;

  @Override
  public void init(final SelfregConfigProperties config) {
    this.config = config;

    final Properties props = new Properties();

    /*
    props.put("mail." + config.getMailProtocol() + ".class", config.getMailProtocolClass());
    */
    props.put("mail.transport.protocol", config.getMailProtocol());
    props.put("mail." + config.getMailProtocol() + ".host", config.getMailServerHost());
    if (config.getMailServerPort() != null) {
      props.put("mail." + config.getMailProtocol() + ".port",
                config.getMailServerPort());
    }

    props.put("mail." + config.getMailProtocol() + ".starttls.enable",
              "true");   // String.valueOf(config.getStarttls()));

    //  add handlers for main MIME types
    final MailcapCommandMap mc = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
    mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
    mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
    mc.addMailcap(
            "text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
    mc.addMailcap(
            "text/calendar;; x-java-content-handler=com.sun.mail.handlers.text_html");
    mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
    mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
    CommandMap.setDefaultCommandMap(mc);

    final String username;
    final String pw;
    username = config.getMailServerAccount();
    pw = config.getMailServerPw();

    if (username != null) {
      // Authentication required.
      final MailerAuthenticator authenticator =
              new MailerAuthenticator(username, pw);
      props.put("mail." + config.getMailProtocol() + ".auth", "true");
      sess = Session.getInstance(props, authenticator);
    } else {
      sess = Session.getInstance(props);
    }

    sess.setDebug(debug());
  }

  @Override
  public Collection<String> listLists() {
    debug("listLists called");
    return new ArrayList<>();
  }

  @Override
  public void post(final Message val) {
    debug("Mailer called with:");
    debug(val.toString());

    if (config.getMailDisabled()) {
      return;
    }

    Transport tr = null;

    try {
      /* Create a message with the appropriate mime-type
       */
      final MimeMessage msg = new MimeMessage(sess);

      msg.setFrom(new InternetAddress(val.getFrom()));

      final InternetAddress[] tos = new InternetAddress[val.getMailTo().length];

      int i = 0;
      for (final String recip: val.getMailTo()) {
        tos[i] = new InternetAddress(recip);
        i++;
      }

      msg.setRecipients(jakarta.mail.Message.RecipientType.TO, tos);

      msg.setSubject(val.getSubject());
      msg.setSentDate(new Date());

      msg.setContent(val.getContent(), "text/plain");

      tr = sess.getTransport(config.getMailProtocol());

      tr.connect();
      tr.sendMessage(msg, tos);
    } catch (final Throwable t) {
      if (debug()) {
        t.printStackTrace();
      }

      throw new SelfregException(t);
    } finally {
      if (tr != null) {
        try {
          tr.close();
        } catch (final Throwable ignored) {}
      }
    }
  }

  private static class MailerAuthenticator
          extends Authenticator {
    private final PasswordAuthentication authentication;

    MailerAuthenticator(final String user, final String password) {
      authentication = new PasswordAuthentication(user, password);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
      return authentication;
    }
  }

  /* ==================================================
   *                   Logged methods
   * ================================================== */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}
