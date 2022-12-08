# GeoXACML 3.0 Implementation

## Installation
git clone <repo>

mvn pacakge

* cp target/authzforce-geoxacml-<version>.jar <authzforce-server>/webapp/WEB-INF/lib
* cp target/lib/jts-core-1.19.0.jar <authzforce-server>/webapp/WEB-INF/lib
* cp target/lib/jts-io-common-1.19.0.jar <authzforce-server>/webapp/WEB-INF/lib
* cp target/lib/jul-to-slf4j-2.0.5.jar <authzforce-server>/webapp/WEB-INF/lib
* cp target/lib/proj4j-1.1.5.jar <authzforce-server>/webapp/WEB-INF/lib

## pdp.xml
modify domain.tmpl/pdp.xml and domains/<default>/pdp.xml to include
```xml
<ioProcChain>
    <requestPreproc>urn:de:securedimensions:feature:pdp:request-preproc:geoxacml-xml:default-lax</requestPreproc>
</ioProcChain>
<ioProcChain>
    <requestPreproc>urn:de:securedimensions:feature:pdp:request-preproc:geoxacml-json:default-lax</requestPreproc>
</ioProcChain>
```

## Enable GeoXACML extension
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
    >urn:ogc:def:function:geoxacml:3.0:geometry-equals-distance</ns3:feature>
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
    >urn:ogc:def:function:geoxacml:3.0:geometry-ensure-srs</ns3:feature>
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

## Enable GeoXACML Media Types

File <authzforce>/webapp/WEB-INF/beans.xml

Update `<beans profile="-fastinfoset">`
```xml
<util:list id="xacmlJsonMediaTypes">
         <!-- OASIS JSON Profile of XACML 3.0 -->
         <value>application/xacml+json</value>
         <!-- OGC JSON Profile of GeoXACML 3.0 -->
         <value>application/geoxacml+json</value>
      </util:list>
      <util:list id="xacmlXmlMediaTypes">
         <!-- OASIS XACML 3.0 -->
         <value>application/xacml+xml</value>
         <!-- OGC GeoXACML 3.0 -->
         <value>application/geoxacml+xml</value>
      </util:list>
```

Update `<bean class="org.ow2.authzforce.jaxrs.util.AcceptMediaTypeCheckingRequestFilter">`
```xml
<constructor-arg>
                  <util:list>
                     <value>application/xml</value>
                     <!-- IETF RFC 7061 -->
                     <value>application/xacml+xml</value>
                     <value>application/json</value>
                     <!-- OASIS JSON Profile of XACML 3.0 -->
                     <value>application/xacml+json</value>
                     <!-- GeoXACML 3.0 -->
                     <value>application/geoxacml+xml</value>
                     <value>application/geoxacml+json</value>
                  </util:list>
               </constructor-arg>
```

Update `<bean class="org.ow2.authzforce.webapp.NamespaceCollectingCxfJAXBElementProvider">`
```xml
<property name="produceMediaTypes" ref="xacmlXmlMediaTypes" />
<property name="consumeMediaTypes" ref="xacmlXmlMediaTypes" />
```

Update `<bean class="org.ow2.authzforce.webapp.JsonRiCxfJaxrsProvider">`
```xml
 <property name="produceMediaTypes" ref="xacmlJsonMediaTypes" />
<property name="consumeMediaTypes" ref="xacmlJsonMediaTypes" />
```

Update `<bean class="org.ow2.authzforce.webapp.org.apache.cxf.jaxrs.provider.json.JSONProvider">`
```xml
 <property name="produceMediaTypes" ref="defaultJsonMediaTypes" />
<property name="consumeMediaTypes" ref="xacmlJsonMediaTypes" />
```

## Load GeoXACML JSON schema
Copy the following JSON into `<authzforce>/conf/Request.schema.json`
```json
{
	"$schema": "http://json-schema.org/draft-06/schema",
	"$id": "Request.schema.json",
	"title": "JSON schema of Request object defined in JSON profile of GeoXACML 3.0 v1.0",
	"description": "",
	"definitions": {
		"AttributeValueType": {
			"description": "Security warning: this definition allows any JSON object as value. TODO: find a way to validate it somehow. Possible solutions: 1) Modify this schema in production to restrict possible values as much as possible. 2) Any equivalent of XML processContents='strict'. 3) Any JSON processor that enforces a max text length, max number of keys, max object depth.",
			"anyOf": [
				{"type": "boolean"},
				{"type": "number"},
				{"type": "string"},
				{"type": "object"},
				{
					"type": "array",
					"items": {"type": "boolean"},
					"minItems": 0
				},
				{
					"type": "array",
					"items": {
						"type": [
							"string",
							"number"
						]
					},
					"minItems": 0
				},
				{
					"type": "array",
					"items": {"type": "object"},
					"minItems": 0
				}
			]
		},
		"AttributeType": {
			"type": "object",
			"properties": {
				"AttributeId": {
					"type": "string",
					"format": "uri-reference"
				},
				"SRS": {"type": "string"},
				"Issuer": {"type": "string"},
				"IncludeInResult": {"type": "boolean"},
				"DataType": {
					"type": "string",
					"format": "uri-reference"
				},
				"Value": {"$ref": "#/definitions/AttributeValueType"}
			}
		},
		"AttributeCategoryType": {
			"type": "object",
			"properties": {
				"CategoryId": {
					"type": "string",
					"format": "uri-reference"
				},
				"Id": {"type": "string"},
				"Content": {"type": "string"},
				"Attribute": {
					"type": "array",
					"items": {"$ref": "#/definitions/AttributeType"},
					"minItems": 0
				}
			},
			"required": ["CategoryId"],
			"additionalProperties": true
		},
		"RequestReferenceType": {
			"type": "object",
			"properties": {
				"ReferenceId": {
					"type": "array",
					"items": {
						"description": "Each item is a Category/Id",
						"type": "string"
					},
					"minItems": 1
				}
			},
			"required": ["ReferenceId"],
			"additionalProperties": false
		},
		"MultiRequestsType": {
			"type": "object",
			"properties": {
				"RequestReference": {
					"type": "array",
					"items": {"$ref": "#/definitions/RequestReferenceType"},
					"minItems": 1
				}
			},
			"required": ["RequestReference"],
			"additionalProperties": false
		},
		"RequestType": {
			"type": "object",
			"properties": {
				"ReturnPolicyIdList": {"type": "boolean"},
				"CombinedDecision": {"type": "boolean"},
				"XPathVersion": {"type": "string"},
				"Category": {
					"type": "array",
					"items": {"$ref": "#/definitions/AttributeCategoryType"},
					"minItems": 1
				},
				"MultiRequests": {"$ref": "#/definitions/MultiRequestsType"}
			},
			"required": ["Category"],
			"additionalProperties": false
		}
	},
	"type": "object",
	"properties": {
		"Request": {"$ref": "#/definitions/RequestType"}
	},
	"required": ["Request"],
	"additionalProperties": false
}
```

Update `authzforce-ce.xml`

```xml
<Environment name="org.ow2.authzforce.domains.xacmlJsonSchemaRelativePath" value="Request.schema.json" type="java.lang.String" override="false"
                                 description="Path to JSON schema file for XACML JSON Profile's Request validation, relative to ${org.ow2.authzforce.config.dir} (if undefined/empty value, the Request.schema.json file from authzforce-ce-xacml-json-model project is used by default)" />
```

## Enable PDP
In <authzforce>/webapp/WEB-INF/web.xml insert the following

```xml
<filter>
      <description></description>
      <filter-name>GeoPDP</filter-name>
      <filter-class>de.securedimensions.geoxacml3.pdp.ogc.GeoPDP</filter-class>
   </filter>
   <filter-mapping>
      <filter-name>GeoPDP</filter-name>
       <servlet-name>CXFServlet</servlet-name>
   </filter-mapping>
<servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>/static/*</url-pattern>
</servlet-mapping>
```