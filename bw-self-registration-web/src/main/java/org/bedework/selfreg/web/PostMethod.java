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
package org.bedework.selfreg.web;

import org.bedework.selfreg.common.exception.SelfregException;
import org.bedework.util.misc.Util;
import org.bedework.util.servlet.ReqUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/** Handle POST for selfreg servlet.
 */
public class PostMethod extends MethodBase {
  @Override
  public void init() throws SelfregException {
  }

  @Override
  public void doMethod(final HttpServletRequest req,
                       final HttpServletResponse resp) throws SelfregException {
    try {
      final List<String> resourceUri = getResourceUri(req);

      if (Util.isEmpty(resourceUri)) {
        sendError(resp,
                  "Bad resource url - no path specified");
        return;
      }

      final String action = resourceUri.getFirst();

      switch (action) {
        case "newid" -> {
          processNewid(req, resp);
          return;
        }
        case "fid" -> {
          processForgotid(req, resp);
          return;
        }
        case "fpw" -> {
          processForgotpw(req, resp);
          return;
        }
        case "setpw" -> {
          processSetpw(req, resp);
          return;
        }
      }

      if (debug()) {
        debug("Illegal action " + action);
      }

      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } catch (final SelfregException se) {
      throw se;
    } catch(final Throwable t) {
      throw new SelfregException(t);
    }
  }

  private void processNewid(final HttpServletRequest req,
                            final HttpServletResponse resp) throws SelfregException {
    if (debug()) {
      debug("Process new id request");
    }

    if (!verifyCaptcha(req)) {
      debug("failed captcha");
      sendError(resp,
                "Incorrect captcha response");
      return;
    }

    final ReqUtil rutil = new ReqUtil(req, resp);

    final String firstName = rutil.getReqPar("fname");
    final String lastName = rutil.getReqPar("lname");
    final String email = rutil.getReqPar("email");
    final String pw = rutil.getReqPar("pw");

    if ((firstName == null) ||
            (lastName == null) ||
            (email == null) ||
            (pw == null)) {
      if (debug()) {
        debug("Failed to create new id: missing fields");
      }

      sendError(resp, "Missing fields");
      return;
    }

    final var dresp = getDir().requestId(firstName,
                                        lastName,
                                        email,
                                        rutil.getReqPar("account"),
                                        pw);

    if (dresp.isOk()) {
      sendOkJsonData(resp);
      return;
    }

    if (debug()) {
      debug("Failed to create new id with reason " + dresp);
    }

    sendError(resp, dresp.getMessage());
  }

  private void processForgotid(final HttpServletRequest req,
                               final HttpServletResponse resp) throws SelfregException {
    if (debug()) {
      debug("Process forgot id request");
    }

    if (!verifyCaptcha(req)) {
      debug("failed captcha");
      sendError(resp,
                "Incorrect captcha response");
      return;
    }

    final ReqUtil rutil = new ReqUtil(req, resp);

    final String email = rutil.getReqPar("email");

    getDir().sendAccount(email);

    sendOkJsonData(resp);
  }

  private void processForgotpw(final HttpServletRequest req,
                               final HttpServletResponse resp) throws SelfregException {
    if (debug()) {
      debug("Process forgot pw request");
    }

    if (!verifyCaptcha(req)) {
      debug("failed captcha");
      sendError(resp,
                "Incorrect captcha response");
      return;
    }

    final ReqUtil rutil = new ReqUtil(req, resp);

    final String account = rutil.getReqPar("account");

    getDir().sendForgotpw(account);

    sendOkJsonData(resp);
  }

  private void processSetpw(final HttpServletRequest req,
                            final HttpServletResponse resp) throws SelfregException {
    if (debug()) {
      debug("Process set pw request");
    }

    final ReqUtil rutil = new ReqUtil(req, resp);

    final String pw = rutil.getReqPar("pw");
    final String confid = rutil.getReqPar("confid");

    if ((confid == null) ||
            (pw == null)) {
      if (debug()) {
        debug("Failed to set pw: missing fields");
      }

      sendError(resp,
                "Missing fields");
      return;
    }

    final String response = getDir().setpw(confid, pw);

    if (response == null) {
      sendOkJsonData(resp);
    }

    sendError(resp, response);
  }
}

