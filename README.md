# GeoXACML 3.0 Policy Decision Point
This implementation is an open source implementation of the following OGC Draft Standards
* [OGC Geospatial eXtensible Access Control Markup Language (GeoXACML) 3.0](docs.ogc.org/DRAFTS/22-049.html)
* [OGC Geospatial eXtensible Access Control Markup Language (GeoXACML) 3.0 JSON Profile 1.0](docs.ogc.org/DRAFTS/22-050.html)

This GeoXACML 3.0 implementation is a plugin to the [Authzforce CE](https://github.com/authzforce) software stack and tested with Authzforce CE Server version 11.0.1.

This implementation is available under the Apache 2.0 license.

## Installation
The following installation instructions were created based on Ubuntu 20.0.4 TLS.

### Java 11 SDK
Please install the JAVA 11 SDK plus tools.

```shell
$ sudo apt install -y openjdk-11-jre tomcat9 git curl maven
```

### Install Authzforce CE
Follow the [instructions](https://github.com/authzforce/server) how to deploy the Authzforce CE Server version 11.0.1.

For Ubuntu, the .deb from the maven repository can be used. Please select the option to create the default domain! The further installation will assume that the default domain identifier is `A0bdIbmGEeWhFwcKrC9gSQ/`.

```shell
$ cd /opt
$ sudo wget https://repo1.maven.org/maven2/org/ow2/authzforce/authzforce-ce-server-dist/11.0.1/authzforce-ce-server-dist-11.0.1.deb
$ sudo dpkg -i authzforce-ce-server-dist-11.0.1.deb
```

The above installs the Authzforce CE Server into `/opt/authzforce-ce-server`. Please make the `data` directory writable to Tomcat:

```shell
$ cd /opt/authzforce-ce-server
$ sudo chown -R tomcat:tomcat data
```

**NOTE:** In case the .deb distribution did not create a default domain, or you have choosen `no`, you need to create a domain yourself. Please execute the following command to create the `default` domain:

```shell
$ echo '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><domainProperties xmlns="http://authzforce.github.io/rest-api-model/xmlns/authz/5" externalId="default"><description>GeoXACML default domain</description></domainProperties>'|curl -X POST -H 'Content-type: application/xml' -d @- http://localhost:8080/authzforce-ce/domains
```

The response contains the domain identifier. Please use this identifier as the default domain.

### Update Authzforce CE rest-api-model
Please follow the [instructions](https://github.com/securedimensions/authzforce-ce-geoxacml3-rest-api-model) how to update the Rest-API-Model JAR file.


### Installation of the GeoXACML 3.0 Policy Decision Point
Simply clone this repository and execute maven to build the JAR.

```shell
$ cd /opt
$ sudo git clone https://github.com/securedimensions/authzforce-ce-geoxacml3
$ cd authzforce-ce-geoxacml3
$ sudo mvn install
```

To make the GeoXACML plugin work, copy the following files:

```shell
$ sudo cp target/authzforce-ce-geoxacml3-1.0.jar /opt/authzforce-ce-server/webapp/WEB-INF/lib
$ sudo cp target/lib/json-simple-*.jar /opt/authzforce-ce-server/webapp/WEB-INF/lib
$ sudo cp target/lib/jts-core-*.jar /opt/authzforce-ce-server/webapp/WEB-INF/lib
$ sudo cp target/lib/jts-io-common-*.jar /opt/authzforce-ce-server/webapp/WEB-INF/lib
$ sudo cp target/lib/jul-to-slf4j-2.0.5.jar /opt/authzforce-ce-server/webapp/WEB-INF/lib
$ sudo cp target/lib/proj4j-1.1.5.jar /opt/authzforce-ce-server/webapp/WEB-INF/lib
$ sudo cp target/lib/freemarker-2.3.32.jar /opt/authzforce-ce-server/webapp/WEB-INF/lib
```

In addition, the following files must be upgraded:

```shell
$ sudo cp target/lib/authzforce-ce-core-pdp-engine-20.2.0.jar /opt/authzforce-ce-server/webapp/WEB-INF/lib
$ sudo rm /opt/authzforce-ce-server/webapp/WEB-INF/lib/authzforce-ce-core-pdp-engine-20.1.0.jar
$ sudo cp target/lib/authzforce-ce-core-pdp-io-xacml-json-20.2.0.jar /opt/authzforce-ce-server/webapp/WEB-INF/lib
$ sudo rm /opt/authzforce-ce-server/webapp/WEB-INF/lib/authzforce-ce-core-pdp-io-xacml-json-20.1.0.jar
$ sudo cp target/lib/authzforce-ce-core-pdp-api-21.3.0.jar /opt/authzforce-ce-server/webapp/WEB-INF/lib
$ sudo rm /opt/authzforce-ce-server/webapp/WEB-INF/lib/authzforce-ce-core-pdp-api-21.2.0.jar
```

## Configuration
For enabling the `authzforce-ce-geoxacml3` plugin with the Authzforce CE Server deployment, a few configuration steps are required.

### Enable the OGC API Common conformance class
The GeoXACML 3.0 Policy Decision Point implements the OGC API Common conformance class via a Tomcat Filter. This filter needs to be activated.

In `/opt/authzforce-ce-server/webapp/WEB-INF/web.xml` insert the GeoPDP Filter as the last filter. It is also required to add the `default` Servlet allowing access to the static files required for the HTML page rendering.

Please add the following filter definition after the `exceptionFilter` filter definition in `/opt/authzforce-ce-server/webapp/WEB-INF/web.xml`:

```xml
<filter>
    <description>The OGC GeoXACML 3.0 Landing Page</description>
    <filter-name>GeoPDP</filter-name>
    <filter-class>de.securedimensions.geoxacml3.pdp.ogc.GeoPDP</filter-class>
</filter>
```

Please add the following filter mapping after the `exceptionFilter` filter mapping in `/opt/authzforce-ce-server/webapp/WEB-INF/web.xml`:

```xml
<filter-mapping>
    <filter-name>GeoPDP</filter-name>
    <servlet-name>CXFServlet</servlet-name>
    <url-pattern>/</url-pattern>
    <url-pattern>/api</url-pattern>
    <url-pattern>/conformance</url-pattern>
    <url-pattern>/decision</url-pattern>
    <url-pattern>/cookies.html</url-pattern>
    <url-pattern>/privacy.html</url-pattern>
    <url-pattern>/terms.html</url-pattern>
</filter-mapping>
<servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>/static/*</url-pattern>
</servlet-mapping>
```

Once you have applied the configuration steps above, open the PDP URL in your Web Browser: [http://localhost:8080/authzforce-ce/domains/A0bdIbmGEeWhFwcKrC9gSQ//pdp](http://localhost:8080/authzforce-ce/domains/A0bdIbmGEeWhFwcKrC9gSQ//pdp).

Now, you should see the OGC GeoXACML 3.0 Policy Decision Point Landing Page.
![GeoPGP Landing Page](GeoPDP.png)


### Enable GeoXACML extension for decision making
The PDP configuration must be updated to contain the GeoXACML 3.0 `geometry` data-type and functions. Please replace the following files with the XML from below:
* `/opt/authzforce-ce-server/data/domains/A0bdIbmGEeWhFwcKrC9gSQ/pdp.xml` ensures that the default domain supports GeoXACML 3.0
* `/opt/authzforce-ce-server/conf/domain.tmpl/pdp.xml` ensures that each newly created domain supports GeoXACML 3.0

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<pdp xmlns="http://authzforce.github.io/core/xmlns/pdp/8" xmlns:ns2="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17" version="8.0" standardDatatypesEnabled="true" standardFunctionsEnabled="true" standardCombiningAlgorithmsEnabled="true" standardAttributeProvidersEnabled="true" xPathEnabled="false" strictAttributeIssuerMatch="false" maxIntegerValue="2147483647" maxVariableRefDepth="10" maxPolicyRefDepth="10" clientRequestErrorVerbosityLevel="0">
    <!-- GeoXACML 3.0 Geometry data-type -->
    <attributeDatatype>urn:ogc:def:geoxacml:3.0:data-type:geometry</attributeDatatype>
    <!-- GeoXACML 3.0 Core Geometry Functions -->
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-dimension</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-type</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-srid</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-is-empty</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-is-simple</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-srid-equals</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-ensure-srid</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-precision</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-ensure-precision</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-has-precision</function>
    <!-- GeoXACML 3.0 Core Spatial Relations Functions -->
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-equals</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-disjoint</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-intersects</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-touches</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-crosses</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-within</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-contains</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-overlaps</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-relate</function>
    <!-- GeoXACML 3.0 Core Analysis Functions -->
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-length</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-area</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-distance</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-distance-equals</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-is-within-distance</function>
    <!-- GeoXACML 3.0 Bag/Set Functions  -->
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-bag-one-and-only</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-bag-size</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-is-in-bag</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-bag</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-bag-to-collection</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-bag-from-collection</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-bag-at-least-one-member-of</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-bag-intersection</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-bag-union</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-bag-subset</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-set-equals</function>
    <!-- GeoXACML 3.0 Spatial Analysis Functions -->
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-envelope</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-boundary</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-buffer</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-convex-hull</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-intersection</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-union</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-difference</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-sym-difference</function>
    <function>urn:ogc:def:geoxacml:3.0:function:geometry-centroid</function>

    <policyProvider xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:ns4="http://authzforce.github.io/pap-dao-flat-file/xmlns/pdp-ext/4" xsi:type="ns4:StaticFlatFileDaoPolicyProviderDescriptor" policyLocationPattern="${PARENT_DIR}/policies/*.xml" id="rootPolicyProvider"/>
    <rootPolicyRef policySet="true">root</rootPolicyRef>
    <ioProcChain>
        <!-- GeoXACML 3.0 application/geoxacml+xml pre-processor -->
        <requestPreproc>urn:de:securedimensions:feature:pdp:request-preproc:geoxacml-xml:default-lax</requestPreproc>
    </ioProcChain>
    <ioProcChain>
        <!-- GeoXACML 3.0 application/geoxacml+json pre-processor -->
        <requestPreproc>urn:de:securedimensions:feature:pdp:request-preproc:geoxacml-json:default-lax</requestPreproc>
        <!-- GeoXACML 3.0 application/geoxacml+json post-processor -->
        <resultPostproc>urn:de:securedimensions:feature:pdp:response-postproc:geoxacml-json:default-lax</resultPostproc>
    </ioProcChain>
</pdp>
```

### Enable GeoXACML Media Types

In file `/opt/authzforce-ce-server/webapp/WEB-INF/beans.xml`

* Update `<beans profile="-fastinfoset">` to include the media types `application/geoxacml+json` and `application/geoxacml+xml`. The final edit should look like this:

```xml
<util:list id="defaultJsonMediaTypes">
    <value>application/json</value>
</util:list>
<util:list id="xacmlJsonMediaTypes">
    <!-- OASIS JSON Profile of XACML 3.0 -->
    <value>application/xacml+json</value>
    <!-- OGC JSON Profile of GeoXACML 3.0 -->
    <value>application/geoxacml+json</value>
</util:list>
```

* Update `<bean class="org.ow2.authzforce.jaxrs.util.AcceptMediaTypeCheckingRequestFilter">` to include the media types `application/geoxacml+json` and `application/geoxacml+xml`. The final edit should look like this:

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

### Configure loading GeoXACML JSON schema
The GeoXACML request and response uses an extended JSON schema. It is therefore required to copy the following files from the `conf` directory into the `/opt/authzforce-ce-server/conf` directory.

```shell
$ /opt/authzforce-ce-geoxacml3
$ sudo cp conf/*.json /opt/authzforce-ce-server/conf
```

Next, update the `/etc/tomcat9/Catalina/localhost/authzforce-ce.xml` file and update the JSON schema location. Find the environment variable `org.ow2.authzforce.domains.xacmlJsonSchemaRelativePath` and set the value to `Request.schema.json`. The final edit should look like this:

```xml
<Environment 
        name="org.ow2.authzforce.domains.xacmlJsonSchemaRelativePath" 
        value="Request.schema.json" 
        type="java.lang.String" 
        override="false"
        description="Path to JSON schema file for XACML JSON Profile's Request validation, relative to ${org.ow2.authzforce.config.dir} (if undefined/empty value, the Request.schema.json file from authzforce-ce-xacml-json-model project is used by default)" />
```

Finally, restart Tomcat:

```shell
$ sudo service tomcat9 restart
```

## Test
The OGC Landing Page can be used to test the basic functionality to ensure that the media types `application/geoxacml+xml` and `application/geoxacml+json` are accepted. These tests are basic because the default policy always returns `Permit`.

Please open the [OGC Landing Page](http://localhost:8080/authzforce-ce/domains/A0bdIbmGEeWhFwcKrC9gSQ/pdp) in the Web Browser and select `openAPI/as HTML` from the top menu. Then open the tab `POST /decision` and click `Try it out`.

### Test Media Type application/geoxacml+xml
Please select the `application/geoxacml+xml` media type for input **and** output! Then paste the following as the request body and click `Execute`:

```xml
<Request xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17" xmlns:geoxacml3="http://www.opengis.net/spec/geoxacml/3.0"
         ReturnPolicyIdList="true"
         CombinedDecision="false"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17
 http://docs.oasis-open.org/xacml/3.0/xacml-core-v3-schema-wd-17.xsd">
    <Attributes Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject">
        <Attribute AttributeId="subject-location" IncludeInResult="false">
            <AttributeValue geoxacml3:srid="4326" geoxacml3:encoding="WKT" DataType="urn:ogc:def:geoxacml:3.0:data-type:geometry">Point (29.963745015416 -90.029951432619)</AttributeValue>
        </Attribute>
    </Attributes>
</Request>
```

Alternatively, you could also use CURL:

```shell
$ echo '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><Request xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17" xmlns:geoxacml3="http://www.opengis.net/spec/geoxacml/3.0" ReturnPolicyIdList="true" CombinedDecision="false" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17 http://docs.oasis-open.org/xacml/3.0/xacml-core-v3-schema-wd-17.xsd"><Attributes Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"><Attribute AttributeId="subject-location" IncludeInResult="false"><AttributeValue geoxacml3:srid="4326" geoxacml3:encoding="WKT" DataType="urn:ogc:def:geoxacml:3.0:data-type:geometry">Point (29.963745015416 -90.029951432619)</AttributeValue></Attribute></Attributes></Request>'|curl -X POST -H 'Content-type: application/geoxacml+xml' -d @- http://localhost:8080/authzforce-ce/domains/A0bdIbmGEeWhFwcKrC9gSQ/pdp
```

Both options should return the following `Permit` response:

```xml
<?xml version='1.0' encoding='UTF-8'?>
<ns2:Response xmlns:ns6="http://authzforce.github.io/pap-dao-flat-file/xmlns/properties/3.6" xmlns:ns5="http://authzforce.github.io/core/xmlns/pdp/8" xmlns:ns4="http://www.w3.org/2005/Atom" xmlns:ns3="http://authzforce.github.io/rest-api-model/xmlns/authz/5" xmlns:ns2="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17">
  <ns2:Result>
    <ns2:Decision>Permit</ns2:Decision>
    <ns2:PolicyIdentifierList>
      <ns2:PolicyIdReference Version="0.1.0">permit-all</ns2:PolicyIdReference>
      <ns2:PolicySetIdReference Version="0.1.0">root</ns2:PolicySetIdReference>
    </ns2:PolicyIdentifierList>
  </ns2:Result>
</ns2:Response>
```

### Test Media Type application/geoxacml+json
Please select the `application/geoxacml+json` media type for input **and** output! Then paste the following as the request body and click `Execute`:

```json
{
  "Request": {
    "Category": [
      {
        "CategoryId": "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject",
        "Attribute": [
          {
            "AttributeId": "subject-location",
            "DataType": "urn:ogc:def:geoxacml:3.0:data-type:geometry",
	    "SRID": -4711,
            "Value": {
                "type": "Point",
                "coordinates": [11, 47]
            }
          }
        ]
      }
    ]
  }
}
```

Alternatively, you could also use CURL:

```shell
$ echo '{"Request": {"Category": [{"CategoryId": "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject","Attribute": [{"AttributeId": "subject-location","DataType": "urn:ogc:def:geoxacml:3.0:data-type:geometry","SRID": -4711,"Value": {"type": "Point","coordinates": [11, 47]}}]}]}}'|curl -X POST -H 'Content-type: application/geoxacml+json' -d @- http://localhost:8080/authzforce-ce/domains/A0bdIbmGEeWhFwcKrC9gSQ/pdp
```

Both options should return the following `Permit` response:

```json
{"Response":[{"Decision":"Permit"}]}
```