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

import org.apache.http.client.utils.URIBuilder;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Handle POST for selfreg servlet.
 */
public class GetMethod extends MethodBase {
  @Override
  public void init() throws SelfregException {
  }
  
  private final static Set<String> validActions = new TreeSet<>();
  
  static {
    validActions.add("fid");
    validActions.add("fpw");
    validActions.add("setpw");
    validActions.add("confirmed");
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public void doMethod(final HttpServletRequest req,
                       final HttpServletResponse resp) throws SelfregException {
    try {
      final List<String> resourceUri = getResourceUri(req);

      final String action;
      if (Util.isEmpty(resourceUri)) {
        action = null;
      } else {
        action = resourceUri.get(0);
      }

      if ((config.getUnauthCanRegister() && (action == null)) ||
              "authinit".equals(action)) {
        // Initial action
        req.setAttribute("sitekey", config.getCaptchaPublicKey());
        req.setAttribute("canSpecifyAccount", config.getCanSpecifyAccount());
        final RequestDispatcher dispatcher = getContext()
                .getRequestDispatcher("/docs/index.jsp");
        dispatcher.forward(req, resp);
        return;
      }

      if (action == null) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
      
      if (action.equals("confirm")) {
        processConfirm(req, resp);
        return;
      }
      
      /* Actions fid, fpw, setpw all require the addiotn of attributes
         to the request then forwarding to the jsp which is at 
         "/docs/" + action " +".jsp"
       */
      
      if (validActions.contains(action)) {
        req.setAttribute("sitekey", config.getCaptchaPublicKey());
        final RequestDispatcher dispatcher = getContext()
                .getRequestDispatcher("/docs/" + action + ".jsp");
        dispatcher.forward(req, resp);
        return;
      }

      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } catch (final SelfregException se) {
      throw se;
    } catch(final Throwable t) {
      throw new SelfregException(t);
    }
  }

  private void processConfirm(final HttpServletRequest req,
                              final HttpServletResponse resp) throws SelfregException {
    final ReqUtil rutil = new ReqUtil(req, resp);

    final String confid = rutil.getReqPar("confid");

    if (confid == null) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    final String account = getDir().confirm(confid);

    if (account == null) {
      sendError(resp, "failed");
      return;
    }

    final String confirmRedirect = config.getConfirmForward();

    if (confirmRedirect != null) {
      try {
        final URIBuilder bldr = new URIBuilder(confirmRedirect);

        bldr.addParameter("status", "ok");
        bldr.addParameter("account", account);
        resp.setHeader("Location", bldr.toString());
        resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
      } catch (final Throwable t) {
        error(t);
      }
      return;
    }

    // Fall back or no redirect
    final String json = "{\"status\": \"ok\", \"account\": \"" + account + "\"}";
    sendOkJsonData(resp, json);
  }
}

