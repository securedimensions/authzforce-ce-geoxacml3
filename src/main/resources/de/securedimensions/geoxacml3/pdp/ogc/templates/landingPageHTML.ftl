<!DOCTYPE html>
<html lang="en">
<head>
    <#include "_head.html">
    <link
            rel="license"
            type="text/html"
            href="https://creativecommons.org/licenses/by-nc-nd/4.0/"
            title="CC-BY-NC-ND 4.0" />
    <link
            rel="alternate"
            type="application/json"
            href="${ALT_URL}"
            title="This Document as JSON" />

</head>
<body>
<#include "_menue.html">
<div class="content">
    <div class="container" id="content">
        <div class="jumbotron">
            <h2>GeoXACML 3.0 Policy Decision Point Landing Page</h2>
            <p class="lead">This is the landing page for the OGC GeoXACML 3.0 implementation.</p>
            <h3>Conformance</h3>
            This implementation supports all <a href="http://www.opengis.net/spec/geoxacml/3.0">GeoXACML 3.0</a> conformance classes:
            <ul>
                <li>
                    <p><b><a href="http://www.opengis.net/spec/geoxacml/3.0/conf/core">Core</a></b> (mandatory): Defines the data type <code>Geometry</code>, support to use the WKT and WKB geometry encoding, and a set of topology test functions based on <a href="https://portal.opengeospatial.org/files/?artifact_id=25355">OGC Simple Features</a> Standard to support indexing of access conditions based on topology.</p>
                </li>
                <li>
                    <p><b><a href="http://www.opengis.net/spec/geoxacml/3.0/conf/spatial-analysis">Spatial Analysis</a></b> (optional): Defines an additional set of spatial analysis functions based on the <a href="https://portal.opengeospatial.org/files/?artifact_id=25355">OGC Simple Features</a> Standard.</p>
                </li>
                <li>
                    <p><b><a href="http://www.opengis.net/spec/geoxacml/3.0/conf/crs-transformation">CRS Transformation</a></b> (optional): Enables an implementation to apply an ad-hoc CRS transformation while deriving an authorization decision.</p>
                </li>
                <li>
                    <p><b><a href="http://www.opengis.net/spec/geoxacml/3.0/conf/ogc-api">API</a></b> (optional): Support OGC API compliance. An implementation provides an <a href="https://docs.ogc.org/is/19-072/19-072.html">OGC API - Common - Part 1: Core</a> compliant landing page, conformance class listing, OpenAPI document and supports requesting an Authorization Decision via HTTP POST.</p>
                </li>
            </ul>
            In addition, this implementation also supports the <a href="http://www.opengis.net/spec/geoxacml/3.0/json-profile/1.0">GeoXACML 3.0 JSON Profile 1.0</a>:
            <ul>
                <li>
                    <p><b><a href="http://www.opengis.net/spec/geoxacml/3.0/json-profile/1.0/conf/core">Core</a></b> (mandatory): This profile defines the encoding options for a <code>Geometry</code> instance as defined in GeoXACML 3.0 based on <code>Well-Known-Text</code>, <code>Well-Known-Binary</code> and <code>GeoJSON</code>.</p>
                </li>
            </ul>
            <h3>Implementation</h3>
            This GeoXACML 3.0 Policy Decision Point is developed as a plugin to the <a href="https://github.com/authzforce">Authzforce CE</a> software stack implemented by the Thales Group. This plugin implementation is open source and available on Github as <a href="https://github.com/securedimensions/authzforce-ce-geoxacml3">authzfoce-ce-geoxacml3</a>.
        </div>
    </div>
</div>
</body>
<footer>
    <#include "_footer.html">
</footer>
</html>