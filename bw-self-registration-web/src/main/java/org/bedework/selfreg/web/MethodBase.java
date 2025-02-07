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

import org.bedework.selfreg.common.DirMaint;
import org.bedework.selfreg.common.DirMaintImpl;
import org.bedework.selfreg.common.exception.SelfregException;
import org.bedework.selfreg.service.SelfregConfigProperties;
import org.bedework.util.http.HttpUtil;
import org.bedework.util.http.PooledHttpClient;
import org.bedework.util.http.PooledHttpClient.ResponseHolder;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.apache.http.HttpStatus.SC_OK;

/** Base class for all webdav servlet methods.
 */
public abstract class MethodBase implements Logged {
  protected boolean dumpContent;

  protected SelfregConfigProperties config;

  private DirMaint dm;

  private final ObjectMapper om = new ObjectMapper();
  
  private ServletContext context;

  /** Called at each request
   *
   */
  public abstract void init();

  /** Called at each request
   *
   * @param dumpContent true for dump of content - requires a wrapper
   */
  public void init(final SelfregConfigProperties config,
                   final ServletContext context,
                   final boolean dumpContent) {
    this.config = config;
    this.dumpContent = dumpContent;
    this.context = context;

    init();
  }
  
  protected ServletContext getContext() {
    return context;
  }

  protected boolean verifyCaptcha(final HttpServletRequest req)
    {
    final PooledHttpClient cl;
    try {
      cl = new PooledHttpClient(
              new URI("https://www.google.com/recaptcha/api/siteverify"));

      final List <NameValuePair> nvps = new ArrayList<>();
      nvps.add(new BasicNameValuePair("secret",
                                      config.getCaptchaPrivateKey()));
      nvps.add(new BasicNameValuePair("response",
                                      req.getParameter(
                                              "g-recaptcha-response")));

      final StringEntity content =
              new UrlEncodedFormEntity(nvps, Consts.UTF_8);

      final ResponseHolder<Boolean> resp = cl.post("",
                                     content,
                                     this::processResponse);

      if (resp.failed) {
        return false;
      }

      return resp.response;
    } catch (final Throwable t) {
      throw new SelfregException(t);
    }
  }

  final ResponseHolder<?> processResponse(final String path,
                                          final CloseableHttpResponse resp) {
    try {
      final int status = HttpUtil.getStatus(resp);

      if (status != SC_OK) {
        return new ResponseHolder(status,
                                  "Failed response from server");
      }

      if (resp.getEntity() == null) {
        return new ResponseHolder(status,
                                  "No content in response from server");
      }

      final InputStream is = resp.getEntity().getContent();

      final Map<?, ?> vals = (Map<?, ?>)om.readValue(is,
                                                     Object.class);

      final Object o = vals.get("success");

      final Boolean sb = (o instanceof Boolean) &&
              (Boolean)o;
      return new ResponseHolder(sb);
    } catch (final Throwable t) {
      return new ResponseHolder(t);
    }
  }

  private final SimpleDateFormat httpDateFormatter =
      new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss ");

  /**
   * @param req http request
   * @param resp http response
   */
  public abstract void doMethod(HttpServletRequest req,
                                HttpServletResponse resp)
       ;

  /** Allow servlet to create method.
   */
  public static class MethodInfo {
    private final Class<? extends MethodBase> methodClass;

    private final boolean requiresAuth;

    /**
     * @param methodClass class of th emethod
     * @param requiresAuth true for auth needed
     */
    public MethodInfo(final Class<? extends MethodBase> methodClass,
                      final boolean requiresAuth) {
      this.methodClass = methodClass;
      this.requiresAuth = requiresAuth;
    }

    /**
     * @return Class for this method
     */
    public Class<? extends MethodBase> getMethodClass() {
      return methodClass;
    }

    /** Called when servicing a request to determine if this method requires
     * authentication. Allows the servlet to reject attempts to change state
     * while unauthenticated.
     *
     * @return boolean true if authentication required.
     */
    public boolean getRequiresAuth() {
      return requiresAuth;
    }
  }

  /** Get the decoded and fixed resource URI. This calls getServletPath() to
   * obtain the path information. The description of that method is a little
   * obscure in it's meaning. In a request of this form:<br/><br/>
   * "GET /ucaldav/user/douglm/calendar/1302064354993-g.ics HTTP/1.1[\r][\n]"<br/><br/>
   * getServletPath() will return <br/><br/>
   * /user/douglm/calendar/1302064354993-g.ics<br/><br/>
   * that is the context has been removed. In addition this method will URL
   * decode the path. getRequestUrl() does neither.
   *
   * @param req      Servlet request object
   * @return List    Path elements of fixed up uri
   */
  public List<String> getResourceUri(final HttpServletRequest req)
      {
    String uri = req.getServletPath();

    if ((uri == null) || (uri.isEmpty())) {
      /* No path specified - set it to root. */
      uri = "/";
    }

    return fixPath(uri);
  }

  /** Return a path, broken into its elements, after "." and ".." are removed.
   * If the parameter path attempts to go above the root we return null.
   * <p>
   * Other than the backslash thing why not use URI?
   *
   * @param path      String path to be fixed
   * @return String[]   fixed path broken into elements
   */
  public static List<String> fixPath(final String path) {
    if (path == null) {
      return null;
    }

    String decoded;
    try {
      decoded = URLDecoder.decode(path, StandardCharsets.UTF_8);
    } catch (final Throwable t) {
      throw new SelfregException("bad path: " + path);
    }

    if (decoded == null) {
      return (null);
    }

    /* Make any backslashes into forward slashes.
     */
    if (decoded.indexOf('\\') >= 0) {
      decoded = decoded.replace('\\', '/');
    }

    /* Ensure a leading '/'
     */
    if (!decoded.startsWith("/")) {
      decoded = "/" + decoded;
    }

    /* Remove all instances of '//'.
     */
    while (decoded.contains("//")) {
      decoded = decoded.replaceAll("//", "/");
    }

    /* Somewhere we may have /./ or /../
     */

    final StringTokenizer st = new StringTokenizer(decoded, "/");

    final ArrayList<String> al = new ArrayList<>();
    while (st.hasMoreTokens()) {
      final String s = st.nextToken();

      if (s.equals(".")) {
        continue;
      }

      if (s.equals("..")) {
        // Back up 1
        if (al.isEmpty()) {
          // back too far
          return null;
        }

        al.remove(al.size() - 1);
      } else {
        al.add(s);
      }
    }

    return al;
  }

  protected void addHeaders(final HttpServletResponse resp) {
    // This probably needs changes
/*
    StringBuilder methods = new StringBuilder();
    for (String name: getSyncher().getMethodNames()) {
      if (methods.length() > 0) {
        methods.append(", ");
      }

      methods.append(name);
    }

    resp.addHeader("Allow", methods.toString());
    */
    resp.addHeader("Allow", "POST, GET");
  }

  /** Parse the request body, and return the DOM representation.
   *
   * @param req        Servlet request object
   * @param resp       Servlet response object for bad status
   * @return Document  Parsed body or null for no body
   */
  protected Document parseContent(final HttpServletRequest req,
                                  final HttpServletResponse resp) {
    final int len = req.getContentLength();
    if (len == 0) {
      return null;
    }

    try {
      final DocumentBuilderFactory factory =
              DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setFeature(
              "http://javax.xml.XMLConstants/feature/secure-processing",
              true);
      factory.setFeature(
              "http://xml.org/sax/features/external-general-entities",
              false);
      factory.setFeature(
              "http://xml.org/sax/features/external-parameter-entities",
              false);
      factory.setAttribute(
              "http://apache.org/xml/features/nonvalidating/load-external-dtd",
              false);

      //DocumentBuilder builder = factory.newDocumentBuilder();
/*
      Reader rdr = getNsIntf().getReader(req);

      if (rdr == null) {
        // No content?
        return null;
      }

      return builder.parse(new InputSource(rdr));*/
      return null;
//    } catch (SAXException e) {
  //    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    //  throw new SynchException(HttpServletResponse.SC_BAD_REQUEST);
    } catch (final Throwable t) {
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      throw new SelfregException(t);
    }
  }

  protected String formatHTTPDate(final Timestamp val) {
    if (val == null) {
      return null;
    }

    synchronized (httpDateFormatter) {
      return httpDateFormatter.format(val) + "GMT";
    }
  }

  protected void sendError(final HttpServletResponse resp,
                           final String msg) {
    try {
      resp.setStatus(HttpServletResponse.SC_OK);
      resp.setContentType("application/json; charset=UTF-8");

      final String json = "{\"status\": \"failed\", \"msg\": \"" + msg + "\"}";

      resp.setContentType("application/json; charset=UTF-8");

      final OutputStream os = resp.getOutputStream();

      final byte[] bytes = json.getBytes();

      resp.setContentLength(bytes.length);
      os.write(bytes);
      os.close();
    } catch (final Throwable ignored) {
      // Pretty much screwed if we get here
      if (debug()) {
        debug("Unable to send error: " + msg);
      }
    }
  }

  protected void sendOkJsonData(final HttpServletResponse resp) {
    final String json = "{\"status\": \"ok\"}";

    sendOkJsonData(resp, json);
  }

  protected void sendOkJsonData(final HttpServletResponse resp,
                            final String data) {
    try {
      resp.setStatus(HttpServletResponse.SC_OK);
      resp.setContentType("application/json; charset=UTF-8");

      final OutputStream os = resp.getOutputStream();

      final byte[] bytes = data.getBytes();

      resp.setContentLength(bytes.length);
      os.write(bytes);
      os.close();
    } catch (final Throwable ignored) {
      // Pretty much screwed if we get here
    }
  }

  protected DirMaint getDir() {
    if (dm != null) {
      return dm;
    }

    dm = new DirMaintImpl();

    dm.init(config);

    return dm;
  }

  /* ==============================================================
   *                   Logged methods
   * ============================================================== */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}

