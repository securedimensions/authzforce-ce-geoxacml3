/**
 * Copyright 2022 Secure Dimensions GmbH.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class GeoPDP implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeoPDP.class);

    private static String LANDING_PAGE_HTML;
    private static String LANDING_PAGE_JSON;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            LANDING_PAGE_HTML = new String(GeoPDP.class.getResourceAsStream("landingpage.html").readAllBytes());
            LANDING_PAGE_JSON = new String(GeoPDP.class.getResourceAsStream("landingpage.json").readAllBytes());
        }
        catch (IOException e)
        {
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
        	if (path.endsWith("/decision"))
        	{
        		request.getRequestDispatcher(path.replace("/decision", "")).forward(request, response);
        	}
        	else if (path.endsWith("/decision/"))
        	{
        		request.getRequestDispatcher(path.replace("/decision/", "")).forward(request, response);
        	}
            else {
                chain.doFilter(request, response);
            }
        }
        else if ("GET".equalsIgnoreCase(httpRequest.getMethod())){
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
        }
        else
        {
        	// Forward any non OGC specific request to Authzforce
        	chain.doFilter(request, response);
        }

    }

    void doLandingPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (f(request).equalsIgnoreCase("json")) {
            response.setContentType("application/json");
            response.getWriter().write(LANDING_PAGE_JSON.replaceAll("BASE_URL", request.getRequestURI()));
        } else {
            response.setContentType("text/html");
            response.getWriter().write(LANDING_PAGE_HTML.replaceAll("BASE_URL", request.getRequestURI()));
        }
    }

    void doApi(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (f(request).equalsIgnoreCase("json")) {
            response.sendRedirect(request.getRequestURI().replace("/api", "/static") + "/openapi.json");
        } else {
            response.sendRedirect(request.getRequestURI().replace("/api", "/static") + "/openapi.html");
        }

    }

    void doConformance(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (f(request).equalsIgnoreCase("json")) {
            response.sendRedirect(request.getRequestURI().replace("/conformance", "/static") + "/conformance.json");
        } else {
            response.sendRedirect(request.getRequestURI().replace("/conformance", "/static") + "/conformance.html");
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
}
