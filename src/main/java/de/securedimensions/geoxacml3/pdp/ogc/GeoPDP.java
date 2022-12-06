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

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class GeoPDP implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (!"GET".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
        } else if (!httpRequest.getPathInfo().matches("/domains/.*/pdp(.*)")) {
            chain.doFilter(request, response);
        } else {
            String path = httpRequest.getPathInfo().replaceAll("/domains/.*/pdp", "");
            if (path.equalsIgnoreCase("") || path.equalsIgnoreCase("/")) {
                doLandingPage(httpRequest, httpResponse);
            } else if (path.startsWith("/api")) {
                doApi(httpRequest, httpResponse);
            } else if (path.startsWith("/conformance")) {
                doConformance(httpRequest, httpResponse);
            } else {
                InputStream resource = GeoPDP.class.getResourceAsStream("." + path);
                if (resource != null) {
                    resource.transferTo(response.getOutputStream());
                    resource.close();
                } else {
                    chain.doFilter(request, response);
                }
            }
        }
    }

    void doLandingPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (f(request).equalsIgnoreCase("json")) {
            response.sendRedirect(request.getRequestURI() + "/static/landingpage.json");
        } else {
            response.sendRedirect(request.getRequestURI() + "/static/landingpage.html");
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
