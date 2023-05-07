/**
 * Copyright 2019-2022 Secure Dimensions GmbH.
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
package de.securedimensions.geoxacml3.datatype;

import de.securedimensions.geoxacml3.identifiers.Definitions;
import de.securedimensions.io.geojson.GeoJsonReader;
import net.sf.saxon.s9api.ItemType;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmItem;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.ow2.authzforce.core.pdp.api.expression.XPathCompilerProxy;
import org.ow2.authzforce.core.pdp.api.value.AttributeDatatype;
import org.ow2.authzforce.core.pdp.api.value.SimpleValue;
import org.ow2.authzforce.core.pdp.io.xacml.json.SerializableJSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the Geometry datatype <i>GeoXACML 3.0 Data Type<i>.
 * <p>
 * The XACML AttributeValue value is interpreted as a Geometry.
 * <p>
 * Encoding as Well Known Text or Well Known Binary and GeoJSON are supported.
 * <p>
 * Used here for a geographic datatype extension mechanism to Authzforce PDP engine.
 * With the combination of the GeoXACML Geometry functions, this data type allows deriving
 * authorization decisions based on geographic conditions.
 *
 * @author Andreas Matheus, Secure Dimensions GmbH.
 */
public final class GeometryValue extends SimpleValue<Geometry> {

    public static final AttributeDatatype<GeometryValue> DATATYPE =
            new AttributeDatatype<GeometryValue>(
                    GeometryValue.class,
                    Definitions.GEOMETRY,
                    Definitions.FUNCTION_PREFIX,
                    ItemType.STRING);
    public static final Factory FACTORY = new Factory();

    private static final Logger LOGGER = LoggerFactory.getLogger(GeometryValue.class);
    private transient volatile XdmItem xdmItem = null;

    private transient volatile int hashCode = 0;

    public GeometryValue(Geometry g) {

        super(g.copy());
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = value.hashCode();
        }

        return hashCode;
    }
    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     *
     * We override the equals because for geometry, we have to use the JTS topological test function g1.equals(g2)
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof GeometryValue)) {
            return false;
        }

        Geometry g1 = this.getUnderlyingValue();
        Geometry g2 = ((GeometryValue) obj).getUnderlyingValue();

        if (g1.getSRID() != g2.getSRID())
            return false;

        // Test for exact equal - NOT for topological equals. That is done via the geometry-equals function.
        // This function is the basic primitive that is used e.g. with Bag/Set functions
        return g1.equalsExact(g2);
    }

    public Geometry getGeometry() {
        return value;
    }

    @Override
    public String printXML() {
        WKTWriter wktWriter = new WKTWriter();
        return wktWriter.write(value);
    }

    @Override
    public XdmItem getXdmItem() {
        if (xdmItem == null) {
            xdmItem = new XdmAtomicValue(value.toText());
        }
        return null;
    }

    public String toString() {

        return value.toText();
    }

    @Override
    public Map<QName, String> getXmlAttributes() {
        return (Map<QName, String>) value.getUserData();
    }

    public static final class Factory extends BaseFactory<GeometryValue> {

        public static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel());

        public Factory() {
            super(DATATYPE);
        }

        @Override
        public Set<Class<? extends Serializable>> getSupportedInputTypes() {
            return Set.of(String.class);
        }

        @Override
        public GeometryValue getInstance(final Serializable content, final Map<QName, String> otherXmlAttributes, final Optional<XPathCompilerProxy> xPathCompiler) throws IllegalArgumentException {
            // XML encoded as AttributeValue with String value
                /*
                // WKT in default SRID
                <Attribute IncludeInResult="false" AttributeId="subject-location">
                  <AttributeValue DataType="urn:ogc:def:geoxacml:3.0:data-type:geometry">POINT(1 0)</AttributeValue>
                </Attribute>
                // WKT with 'srid'
                <Attribute IncludeInResult="false" AttributeId="subject-location">
                  <AttributeValue DataType="urn:ogc:def:geoxacml:3.0:data-type:geometry" xmlns:geoxacml="urn:ogc:def:dataType:geoxacml:3.0" geoxacml:srid="4711">POINT(1 0)</AttributeValue>
                </Attribute>
                // WKB in default SRID
                <Attribute IncludeInResult="false" AttributeId="subject-location">
                  <AttributeValue DataType="urn:ogc:def:geoxacml:3.0:data-type:geometry">010100000000000000000000400000000000001040</AttributeValue>
                </Attribute>
                 */
            // Or JSON
                /*
                // in default SRS
                {
                    "AttributeId": "subject-location",
                    "DataType": "urn:ogc:def:geoxacml:3.0:data-type:geometry",
                    "Value": "POINT(1 0)"
                }
                // with 'srid'
                {
                    "AttributeId": "subject-location",
                    "DataType": "urn:ogc:def:geoxacml:3.0:data-type:geometry",
                    "SRID": 4326,
                    "Value": "POINT(1 0)"
                }
                */
            // Or GeoJSON
                /*
                {
                    "AttributeId": "subject-location",
                    "DataType": "urn:ogc:def:geoxacml:3.0:data-type:geometry",
                    "Value": [
                        {
                            "type": "Point",
                            "coordinates": [
                                125.6,
                                10.1
                            ]
                        }
                    ]
                }
                */

            try {
                int srid = -4326;
                String encoding = Definitions.DEFAULT_ENCODING;
                if (otherXmlAttributes != null && !otherXmlAttributes.isEmpty()) {
                    if (otherXmlAttributes.containsKey(Definitions.xmlSRID)) {
                        srid = Integer.parseInt(otherXmlAttributes.get(Definitions.xmlSRID));
                    }
                    if (otherXmlAttributes.containsKey(Definitions.xmlENCODING)) {
                        encoding = otherXmlAttributes.get(Definitions.xmlENCODING);
                    }
                }
                Geometry g = null;
                if (content instanceof String) {
                    String val = (String) content;
                    if (encoding.equalsIgnoreCase(Definitions.DEFAULT_ENCODING)) {
                        WKTReader wktReader = new WKTReader(GEOMETRY_FACTORY);
                        g = wktReader.read(val);
                    } else {
                        WKBReader wkbReader = new WKBReader(GEOMETRY_FACTORY);
                        g = wkbReader.read(WKBReader.hexToBytes(val));
                    }
                    // store SRID with the geometry -- WKTReader and WKBReader don't do that
                    g.setSRID(srid);

                } else if (content instanceof SerializableJSONObject) {
                    GeoJsonReader geojsonReader = new GeoJsonReader();
                    g = geojsonReader.create(((SerializableJSONObject) content).get().toMap(), GEOMETRY_FACTORY);
                    g.setSRID(srid);

                } else {
                    throw new IllegalArgumentException("Geometry encoding not supported");
                }
                // Ensure heterogeneous GeometryCollection
                if (g.getGeometryType().equalsIgnoreCase("GEOMETRYCOLLECTION")) {
                    String geometryType = null;
                    GeometryCollection gc = (GeometryCollection) g;
                    int numGeometries = gc.getNumGeometries();
                    for (int ix = 0; ix < numGeometries; ix++) {
                        if (geometryType == null)
                            geometryType = gc.getGeometryN(ix).getGeometryType();
                        else if (geometryType != gc.getGeometryN(ix).getGeometryType())
                            throw new IllegalArgumentException("GeometryCollection must be homogeneous");
                    }
                }

                g.setUserData(otherXmlAttributes);
                return new GeometryValue(g);
            } catch (ParseException e) {
                throw new IllegalArgumentException(Definitions.GEOMETRY_ERROR, e);
            }


        }
    }

}

