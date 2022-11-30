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
package de.securedimensions.geoxacml3.test.function;

import de.securedimensions.geoxacml3.datatype.GeometryValue;
import de.securedimensions.geoxacml3.function.TopologicalFunctions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.ow2.authzforce.core.pdp.api.value.BooleanValue;
import org.ow2.authzforce.core.pdp.api.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.*;

import static de.securedimensions.geoxacml3.datatype.GeometryValue.SOURCE_ATTR_DESIGNATOR;
import static de.securedimensions.geoxacml3.datatype.GeometryValue.SOURCE_POLICY;

@RunWith(Parameterized.class)
public class TopologicalFunctionsTest extends GeometryFunctionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopologicalFunctionsTest.class);

    public TopologicalFunctionsTest(final String functionName, final List<Value> inputs, final Value expectedResult) {
        super(functionName, null, inputs, expectedResult);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> params() {

        Geometry gDefault = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(-77.035278, 38.889444));
        gDefault.setSRID(-4326);

        Geometry gDefaultADRPrecision1 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(-77, 38));
        gDefaultADRPrecision1.setSRID(-4326);

        Geometry gDefaultADRPrecision9 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(-77.035278, 38.889444));
        gDefaultADRPrecision9.setSRID(-4326);

        Geometry gSRS4326 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(38.889444, -77.035278));
        gSRS4326.setSRID(4326);

        Geometry gSRID4326 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(38.889444, -77.035278));
        gSRID4326.setSRID(4326);

        Geometry gSRID3857 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(-8575527.92007827, 4705847.723791289));
        gSRID3857.setSRID(3857);

        Geometry p00 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(0, 0));
        p00.setSRID(-4326);

        Geometry p100 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(0, 100));
        p100.setSRID(-4326);

        Geometry p1010 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(10, 10));
        p1010.setSRID(-4326);

        Geometry l00100 = GeometryValue.Factory.GEOMETRY_FACTORY.createLineString(new Coordinate[]{new Coordinate(0, 0), new Coordinate(0, 100)});
        l00100.setSRID(-4326);

        Geometry l50100 = GeometryValue.Factory.GEOMETRY_FACTORY.createLineString(new Coordinate[]{new Coordinate(50, 50), new Coordinate(0, 100)});
        l50100.setSRID(-4326);

        Geometry l0005 = GeometryValue.Factory.GEOMETRY_FACTORY.createLineString(new Coordinate[]{new Coordinate(0, 0), new Coordinate(0, 5)});
        l0005.setSRID(-4326);

        CoordinateSequence cs1010 = new CoordinateArraySequence(new Coordinate[]{new Coordinate(-10, -10), new Coordinate(-10, 10), new Coordinate(10, 10), new Coordinate(-10, 10), new Coordinate(-10, -10)});
        Geometry pg1010 = GeometryValue.Factory.GEOMETRY_FACTORY.createPolygon(new LinearRing(cs1010, GeometryValue.Factory.GEOMETRY_FACTORY));
        pg1010.setSRID(-4326);

        CoordinateSequence cs00100 = new CoordinateArraySequence(new Coordinate[]{new Coordinate(0, 0), new Coordinate(0, 100), new Coordinate(100, 100), new Coordinate(100, 0), new Coordinate(0, 0)});
        Geometry pg00100 = GeometryValue.Factory.GEOMETRY_FACTORY.createPolygon(new LinearRing(cs00100, GeometryValue.Factory.GEOMETRY_FACTORY));
        pg00100.setSRID(-4326);

        CoordinateSequence cs00_100 = new CoordinateArraySequence(new Coordinate[]{new Coordinate(0, 0), new Coordinate(0, -100), new Coordinate(-100, -100), new Coordinate(-100, 0), new Coordinate(0, 0)});
        Geometry pg00_100 = GeometryValue.Factory.GEOMETRY_FACTORY.createPolygon(new LinearRing(cs00_100, GeometryValue.Factory.GEOMETRY_FACTORY));
        pg00_100.setSRID(-4326);

        Map<QName, String> xmlPrecision1SourceADR = new HashMap<QName, String>();
        xmlPrecision1SourceADR.put(GeometryValue.xmlPrecision, "1.0");
        xmlPrecision1SourceADR.put(GeometryValue.SOURCE, SOURCE_ATTR_DESIGNATOR);
        gDefaultADRPrecision1.setUserData(xmlPrecision1SourceADR);

        Map<QName, String> xmlPrecision1SourcePolicy = new HashMap<QName, String>();
        xmlPrecision1SourcePolicy.put(GeometryValue.xmlPrecision, "1.0");
        xmlPrecision1SourcePolicy.put(GeometryValue.SOURCE, SOURCE_POLICY);
        Geometry gDefaultPolicyPrecision1 = gDefaultADRPrecision1.copy();
        gDefaultPolicyPrecision1.setUserData(xmlPrecision1SourcePolicy);

        Map<QName, String> xmlPrecision9SourceADR = new HashMap<QName, String>();
        xmlPrecision9SourceADR.put(GeometryValue.xmlPrecision, "9.0");
        xmlPrecision9SourceADR.put(GeometryValue.SOURCE, SOURCE_ATTR_DESIGNATOR);
        gDefaultADRPrecision9.setUserData(xmlPrecision9SourceADR);

        Map<QName, String> xmlAllowTransformation = new HashMap<QName, String>();
        xmlAllowTransformation.put(GeometryValue.xmlAllowTransformation, "true");

        Geometry gDefaultAllowTransform = gDefault.copy();
        gDefaultAllowTransform.setUserData(xmlAllowTransformation);

        Geometry gSRID4326AllowTransform = gSRID4326.copy();
        gSRID4326AllowTransform.setUserData(xmlAllowTransformation);


        return Arrays
                .asList(
                        // urn:ogc:def:function:geoxacml:3.0:geometry-equal test precision
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gDefault), new GeometryValue(gDefaultADRPrecision1)), BooleanValue.FALSE},
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gDefaultPolicyPrecision1), new GeometryValue(gDefaultADRPrecision1)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gDefaultPolicyPrecision1), new GeometryValue(gDefaultADRPrecision9)), null},
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gDefaultADRPrecision1), new GeometryValue(gDefaultADRPrecision1)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gDefaultADRPrecision1), new GeometryValue(gDefaultADRPrecision9)), null},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-equal
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gDefault), new GeometryValue(gDefault)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(p00), new GeometryValue(p100)), BooleanValue.FALSE},

                        // Axis order test EPSG:4326 vs. WGS84
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gDefault), new GeometryValue(gSRS4326)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gDefault), new GeometryValue(gSRID4326)), BooleanValue.TRUE},

                        // CRS transformation required -> Core conformance throws an error
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gDefaultAllowTransform), new GeometryValue(gSRID3857)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gSRID4326AllowTransform), new GeometryValue(gSRID3857)), BooleanValue.TRUE},

                        // CRS transformation required -> Core conformance throws an error
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gSRID3857), new GeometryValue(gDefaultAllowTransform)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gSRID3857), new GeometryValue(gSRID4326AllowTransform)), BooleanValue.TRUE},

                        // CRS transformation not allowed per 'allowTransformation' attribute in AttributeValue -> throw Indeterminate
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gDefault), new GeometryValue(gSRID3857)), null},
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gSRID4326), new GeometryValue(gSRID3857)), null},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-equals
                        new Object[]{TopologicalFunctions.Equals.ID, Arrays.asList(new GeometryValue(gDefault), new GeometryValue(gDefault)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Equals.ID, Arrays.asList(new GeometryValue(p00), new GeometryValue(p100)), BooleanValue.FALSE},
                        new Object[]{TopologicalFunctions.Equals.ID, Arrays.asList(new GeometryValue(gDefault), new GeometryValue(gSRS4326)), null},
                        new Object[]{TopologicalFunctions.Equals.ID, Arrays.asList(new GeometryValue(gDefault), new GeometryValue(gSRID4326)), null},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-disjoint
                        new Object[]{TopologicalFunctions.Disjoint.ID, Arrays.asList(new GeometryValue(pg1010), new GeometryValue(p100)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Disjoint.ID, Arrays.asList(new GeometryValue(pg1010), new GeometryValue(p00)), BooleanValue.FALSE},
                        new Object[]{TopologicalFunctions.Disjoint.ID, Arrays.asList(new GeometryValue(pg1010), new GeometryValue(gDefault)), null},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-touches
                        new Object[]{TopologicalFunctions.Touches.ID, Arrays.asList(new GeometryValue(pg1010), new GeometryValue(p1010)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Touches.ID, Arrays.asList(new GeometryValue(pg1010), new GeometryValue(p00)), BooleanValue.FALSE},
                        new Object[]{TopologicalFunctions.Touches.ID, Arrays.asList(new GeometryValue(pg1010), new GeometryValue(gDefault)), null},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-crosses
                        new Object[]{TopologicalFunctions.Crosses.ID, Arrays.asList(new GeometryValue(pg1010), new GeometryValue(l00100)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Crosses.ID, Arrays.asList(new GeometryValue(pg1010), new GeometryValue(l0005)), BooleanValue.FALSE},
                        new Object[]{TopologicalFunctions.Crosses.ID, Arrays.asList(new GeometryValue(pg1010), new GeometryValue(gDefault)), null},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-within
                        new Object[]{TopologicalFunctions.Within.ID, Arrays.asList(new GeometryValue(p00), new GeometryValue(pg1010)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Within.ID, Arrays.asList(new GeometryValue(p100), new GeometryValue(pg1010)), BooleanValue.FALSE},
                        new Object[]{TopologicalFunctions.Within.ID, Arrays.asList(new GeometryValue(pg1010), new GeometryValue(gDefault)), null},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-contains
                        new Object[]{TopologicalFunctions.Contains.ID, Arrays.asList(new GeometryValue(pg1010), new GeometryValue(p00)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Contains.ID, Arrays.asList(new GeometryValue(pg1010), new GeometryValue(p100)), BooleanValue.FALSE},
                        new Object[]{TopologicalFunctions.Contains.ID, Arrays.asList(new GeometryValue(pg1010), new GeometryValue(gDefault)), null},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-overlaps
                        new Object[]{TopologicalFunctions.Overlaps.ID, Arrays.asList(new GeometryValue(pg1010), new GeometryValue(pg00100)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Overlaps.ID, Arrays.asList(new GeometryValue(pg00_100), new GeometryValue(pg00100)), BooleanValue.FALSE},
                        new Object[]{TopologicalFunctions.Overlaps.ID, Arrays.asList(new GeometryValue(pg1010), new GeometryValue(gDefault)), null},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-intersects
                        new Object[]{TopologicalFunctions.Intersects.ID, Arrays.asList(new GeometryValue(pg1010), new GeometryValue(l00100)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Intersects.ID, Arrays.asList(new GeometryValue(pg00_100), new GeometryValue(l50100)), BooleanValue.FALSE},
                        new Object[]{TopologicalFunctions.Intersects.ID, Arrays.asList(new GeometryValue(pg1010), new GeometryValue(gDefault)), null}

                );
    }

}