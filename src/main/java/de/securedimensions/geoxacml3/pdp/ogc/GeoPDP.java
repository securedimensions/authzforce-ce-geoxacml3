/**
 * Copyright 2023 Secure Dimensions GmbH.
 * <p>
 * This file is part of GeoXACML 3 Community Version.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.securedimensions.geoxacml3.pdp.ogc;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class GeoPDP implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeoPDP.class);

    private static Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);

    private Template landingPageJSON;
    private Template landingPageHTML;
    private Template openapiPageJSON;
    private Template openapiPageHTML;
    private Template conformancePageJSON;
    private Template conformancePageHTML;

    private Template termsPageHTML, cookiesPageHTML, privacyPageHTML;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            cfg.setClassForTemplateLoading(GeoPDP.class, "templates");
            landingPageJSON = cfg.getTemplate("landingPageJSON.ftl");
            landingPageHTML = cfg.getTemplate("landingPageHTML.ftl");
            openapiPageJSON = cfg.getTemplate("openapiPageJSON.ftl");
            openapiPageHTML = cfg.getTemplate("openapiPageHTML.ftl");
            conformancePageJSON = cfg.getTemplate("conformancePageJSON.ftl");
            conformancePageHTML = cfg.getTemplate("conformancePageHTML.ftl");
            termsPageHTML = cfg.getTemplate("terms.ftl");
            cookiesPageHTML = cfg.getTemplate("cookies.ftl");
            privacyPageHTML = cfg.getTemplate("privacy.ftl");
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getPathInfo();

        // The OGC GeoXACML 3.0 API conformance class requires to support the path .../decision
        if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
            if (path.endsWith("/decision")) {
                request.getRequestDispatcher(path.replace("/decision", "")).forward(request, response);
            } else if (path.endsWith("/decision/")) {
                request.getRequestDispatcher(path.replace("/decision/", "")).forward(request, response);
            } else {
                chain.doFilter(request, response);
            }
        } else if ("GET".equalsIgnoreCase(httpRequest.getMethod())) {
            // The OGC GeoXACML 3.0 API conformance class requires to support the path for /api and /conformance
            if (!path.matches("/domains/.*/pdp(.*)")) {
                chain.doFilter(request, response);
            } else {
                String xpath = path.replaceAll("/domains/.*/pdp", "");
                if (xpath.equalsIgnoreCase("") || xpath.equalsIgnoreCase("/")) {
                    doLandingPage(httpRequest, httpResponse);
                } else if (xpath.startsWith("/api")) {
                    doApi(httpRequest, httpResponse);
                } else if (xpath.startsWith("/conformance")) {
                    doConformance(httpRequest, httpResponse);
                } else if (xpath.startsWith("/terms.html")) {
                    doTerms(httpRequest, httpResponse);
                } else if (xpath.startsWith("/cookies.html")) {
                    doCookies(httpRequest, httpResponse);
                } else if (xpath.startsWith("/privacy.html")) {
                    doPrivacy(httpRequest, httpResponse);
                } else {
                    InputStream resource = GeoPDP.class.getResourceAsStream("." + xpath);
                    if (resource != null) {
                        resource.transferTo(response.getOutputStream());
                        resource.close();
                    } else {
                        chain.doFilter(request, response);
                    }
                }
            }
        } else {
            // Forward any non OGC specific request to Authzforce
            chain.doFilter(request, response);
        }

    }

    void doLandingPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseURI = request.getRequestURI().replaceAll("/pdp.*", "/pdp");
        Query query = new Query(request.getQueryString());

        try {
            Map<String, Object> input = new HashMap<>();

            input.put("HOME_URL", baseURI);

            query.replace("f", "json");
            input.put("API_JSON_URL", baseURI + "/api" + query.toQueryString());
            input.put("CONF_JSON_URL", baseURI + "/conformance" + query.toQueryString());

            query.replace("f", "html");
            input.put("API_HTML_URL", baseURI + "/api" + query.toQueryString());
            input.put("CONF_HTML_URL", baseURI + "/conformance" + query.toQueryString());

            if (f(request).equalsIgnoreCase("json")) {
                query.replace("f", "html");
                input.put("ALT_URL", baseURI + query.toQueryString());

                response.setContentType("application/json");
                this.landingPageJSON.process(input, response.getWriter());
            } else {
                query.replace("f", "json");
                input.put("ALT_URL", baseURI + query.toQueryString());

                response.setContentType("text/html");
                this.landingPageHTML.process(input, response.getWriter());
            }
        } catch (TemplateException e) {
            response.sendError(500, "Template error");
        }
    }

    void doApi(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseURL = request.getRequestURL().toString().replaceAll("/pdp.*", "/pdp");
        String baseURI = request.getRequestURI().replaceAll("/pdp.*", "/pdp");

        Query query = new Query(request.getQueryString());

        try {
            Map<String, Object> input = new HashMap<>();
            input.put("HOME_URL", baseURI);

            query.replace("f", "json");
            input.put("API_JSON_URL", baseURL + "/api" + query.toQueryString());
            input.put("CONF_JSON_URL", baseURI + "/conformance" + query.toQueryString());
            input.put("PDP_URL", baseURL);

            query.replace("f", "html");
            input.put("API_HTML_URL", baseURI + "/api" + query.toQueryString());
            input.put("CONF_HTML_URL", baseURI + "/conformance" + query.toQueryString());

            if (f(request).equalsIgnoreCase("json")) {
                query.replace("f", "html");
                input.put("ALT_URL", baseURI + "/api" + query.toQueryString());
                response.setContentType("application/json");
                this.openapiPageJSON.process(input, response.getWriter());
            } else {
                response.setContentType("text/html");
                query.replace("f", "json");
                input.put("ALT_URL", baseURI + "/api" + query.toQueryString());
                this.openapiPageHTML.process(input, response.getWriter());
            }
        } catch (TemplateException e) {
            response.sendError(500, "Template error");
        }

    }

    void doConformance(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseURI = request.getRequestURI().replaceAll("/pdp.*", "/pdp");
        Query query = new Query(request.getQueryString());

        try {
            Map<String, Object> input = new HashMap<>();

            input.put("HOME_URL", baseURI);

            query.replace("f", "json");
            input.put("API_JSON_URL", baseURI + "/api" + query.toQueryString());
            input.put("CONF_JSON_URL", baseURI + "/conformance" + query.toQueryString());

            query.replace("f", "html");
            input.put("API_HTML_URL", baseURI + "/api" + query.toQueryString());
            input.put("CONF_HTML_URL", baseURI + "/conformance" + query.toQueryString());

            if (f(request).equalsIgnoreCase("json")) {
                query.replace("f", "html");
                input.put("ALT_URL", baseURI + "/conformance" + query.toQueryString());

                response.setContentType("application/json");
                this.conformancePageJSON.process(input, response.getWriter());
            } else {
                query.replace("f", "json");
                input.put("ALT_URL", baseURI + "/conformance" + query.toQueryString());

                response.setContentType("text/html");
                this.conformancePageHTML.process(input, response.getWriter());
            }
        } catch (TemplateException e) {
            response.sendError(500, "Template error");
        }
    }

    void doTerms(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseURI = request.getRequestURI().replaceAll("/pdp.*", "/pdp");
        Query query = new Query(request.getQueryString());

        try {
            Map<String, Object> input = new HashMap<>();

            input.put("HOME_URL", baseURI);
            input.put("ALT_URL", null);

            query.replace("f", "json");
            input.put("API_JSON_URL", baseURI + "/api" + query.toQueryString());
            input.put("CONF_JSON_URL", baseURI + "/conformance" + query.toQueryString());

            query.replace("f", "html");
            input.put("API_HTML_URL", baseURI + "/api" + query.toQueryString());
            input.put("CONF_HTML_URL", baseURI + "/conformance" + query.toQueryString());

            response.setContentType("text/html");
            this.termsPageHTML.process(input, response.getWriter());
        } catch (TemplateException e) {
            response.sendError(500, "Template error");
        }
    }

    void doCookies(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseURI = request.getRequestURI().replaceAll("/pdp.*", "/pdp");
        Query query = new Query(request.getQueryString());

        try {
            Map<String, Object> input = new HashMap<>();

            input.put("HOME_URL", baseURI);
            input.put("ALT_URL", null);

            query.replace("f", "json");
            input.put("API_JSON_URL", baseURI + "/api" + query.toQueryString());
            input.put("CONF_JSON_URL", baseURI + "/conformance" + query.toQueryString());

            query.replace("f", "html");
            input.put("API_HTML_URL", baseURI + "/api" + query.toQueryString());
            input.put("CONF_HTML_URL", baseURI + "/conformance" + query.toQueryString());

            response.setContentType("text/html");
            this.cookiesPageHTML.process(input, response.getWriter());
        } catch (TemplateException e) {
            response.sendError(500, "Template error");
        }
    }

    void doPrivacy(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseURI = request.getRequestURI().replaceAll("/pdp.*", "/pdp");
        Query query = new Query(request.getQueryString());

        try {
            Map<String, Object> input = new HashMap<>();

            input.put("HOME_URL", baseURI);
            input.put("ALT_URL", null);

            query.replace("f", "json");
            input.put("API_JSON_URL", baseURI + "/api" + query.toQueryString());
            input.put("CONF_JSON_URL", baseURI + "/conformance" + query.toQueryString());

            query.replace("f", "html");
            input.put("API_HTML_URL", baseURI + "/api" + query.toQueryString());
            input.put("CONF_HTML_URL", baseURI + "/conformance" + query.toQueryString());

            response.setContentType("text/html");
            this.privacyPageHTML.process(input, response.getWriter());
        } catch (TemplateException e) {
            response.sendError(500, "Template error");
        }
    }

    private String f(HttpServletRequest request) {
        String f = request.getParameter("f");

        // html is default
        if (f == null)
            if (request.getHeader("Accept").endsWith("json"))
                return "json";
            else
                return "html";
        else if (f.equalsIgnoreCase("json"))
            return "json";
        else
            return "html";
    }

    private class Query {

        private Map<String, String> map;

        private Query() {

        }

        public Query(String query) {
            map = queryMap(query);
        }

        private Map<String, String> queryMap(String query) {
            Map<String, String> map = new HashMap<String, String>();
            if (query == null)
                return map;

            String[] params = query.split("&");
            for (String param : params) {
                try {
                    String name = param.split("=")[0];
                    String value = param.split("=")[1];
                    map.put(name, value);
                } catch (Exception e) {
                    // malformed url params, just ignore
                }

            }
            return map;
        }

        public String toString() {
            try {
                if (map.size() > 0) {
                    StringBuffer rc = new StringBuffer();
                    boolean first = true;
                    for (String key : map.keySet()) {
                        if (first) {
                            first = false;
                        } else {
                            rc.append("&");
                        }
                        rc.append(URLEncoder.encode(key, "UTF-8"));
                        rc.append("=");
                        rc.append(URLEncoder.encode(map.get(key), "UTF-8"));
                    }
                    return rc.toString();
                }
            } catch (UnsupportedEncodingException e) {
                // ignore
            }
            return "";
        }

        public String toQueryString() {
            if (map.isEmpty()) {
                return "";
            }

            return "?" + this.toString();
        }

        public void replace(String key, String value) {
            if (map.replace(key, value) == null)
                map.put(key, value);
        }

        public boolean isEmpty() {
            return map.isEmpty();
        }
    }
}
