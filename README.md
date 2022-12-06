# GeoXACML 3.0 Implementation

## Installation
git clone <repo>

mvn pacakge

* cp target/authzforce-geoxacml-<version>.jar <authzforce-server>/webapp/WEB-INF/lib
* cp target/lib/jts-core-1.19.0.jar <authzforce-server>/webapp/WEB-INF/lib
* cp target/lib/jts-io-common-1.19.0.jar <authzforce-server>/webapp/WEB-INF/lib
* cp target/lib/log4j-over-slf4j-1.7.32.jar <authzforce-server>/webapp/WEB-INF/lib
* cp target/lib/proj4j-1.1.5.jar <authzforce-server>/webapp/WEB-INF/lib


## Enable 
PATCH <authzforce-server>/domains/<id>/pap/pdp.properties

```xml
<?xml version='1.0' encoding='UTF-8'?>
<ns3:pdpPropertiesUpdate
        xmlns:ns6="http://authzforce.github.io/pap-dao-flat-file/xmlns/properties/3.6"
        xmlns:ns5="http://authzforce.github.io/core/xmlns/pdp/8" xmlns:ns4="http://www.w3.org/2005/Atom"
        xmlns:ns3="http://authzforce.github.io/rest-api-model/xmlns/authz/5"
        xmlns:ns2="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17">
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:core" enabled="false"
    >urn:ow2:authzforce:feature:pdp:core:strict-attribute-issuer-match</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:core" enabled="false"
    >urn:ow2:authzforce:feature:pdp:core:xpath-eval</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:data-type" enabled="true"
    >urn:ogc:def:dataType:geoxacml:3.0:geometry</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-equals</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-is-in</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-at-least-one-member-of</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-touches</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-srid-equals</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-subset</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-set-equals</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-disjoint</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-is-empty</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-bag-from-collection</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-crosses</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-geometry-intersection</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-geometry-sym-difference</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-distance-equals</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-bag</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-relate</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-srs-equals</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-is-within-distance</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-geometry-difference</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-intersects</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-contains</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-length</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-one-and-only</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-boundary</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-bag-to-collection</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-within</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-union</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-equal</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-distance</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-bag-size</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-buffer</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-overlaps</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-geometry-union</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-centroid</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-dimension</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-type</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-srid</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-is-simple</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-area</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-ensure-crs</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-intersection</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-envelope</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:function" enabled="true"
    >urn:ogc:def:function:geoxacml:3.0:geometry-convex-hull</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:request-preproc" enabled="true"
    >urn:de:securedimensions:feature:pdp:request-preproc:geoxacml-xml:default-lax</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:request-preproc" enabled="false"
    >urn:ow2:authzforce:feature:pdp:request-preproc:xacml-xml:default-lax</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:request-preproc" enabled="true"
    >urn:de:securedimensions:feature:pdp:request-preproc:geoxacml-json:default-lax</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:request-preproc" enabled="false"
    >urn:ow2:authzforce:feature:pdp:request-preproc:xacml-json:default-lax</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:request-preproc" enabled="false"
    >urn:ow2:authzforce:feature:pdp:request-preproc:xacml-xml:multiple:repeated-attribute-categories-strict</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:request-preproc" enabled="false"
    >urn:ow2:authzforce:feature:pdp:request-preproc:xacml-json:multiple:repeated-attribute-categories-strict</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:request-preproc" enabled="false"
    >urn:ow2:authzforce:feature:pdp:request-preproc:xacml-xml:multiple:repeated-attribute-categories-lax</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:request-preproc" enabled="false"
    >urn:de:securedimensions:feature:pdp:request-preproc:geoxacml-json:default-strict</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:request-preproc" enabled="false"
    >urn:ow2:authzforce:feature:pdp:request-preproc:xacml-json:default-strict</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:request-preproc" enabled="false"
    >urn:ow2:authzforce:feature:pdp:request-preproc:xacml-json:multiple:repeated-attribute-categories-lax</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:request-preproc" enabled="false"
    >urn:ow2:authzforce:feature:pdp:request-preproc:xacml-xml:default-strict</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:request-preproc" enabled="false"
    >urn:de:securedimensions:feature:pdp:request-preproc:geoxacml-xml:default-strict</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:request-preproc" enabled="false"
    >urn:de:securedimensions:feature:pdp:request-preproc:xacml-xml:default-strict</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:result-postproc" enabled="false"
    >urn:ow2:authzforce:feature:pdp:result-postproc:xacml-xml:default</ns3:feature>
    <ns3:feature type="urn:ow2:authzforce:feature-type:pdp:result-postproc" enabled="false"
    >urn:ow2:authzforce:feature:pdp:result-postproc:xacml-json:default</ns3:feature>
    <ns3:rootPolicyRefExpression>root</ns3:rootPolicyRefExpression>
</ns3:pdpPropertiesUpdate>
```