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
import de.securedimensions.geoxacml3.function.CoreFunctions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.locationtech.jts.io.WKBWriter;
import org.locationtech.jts.io.WKTWriter;
import org.ow2.authzforce.core.pdp.api.value.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class CoreFunctionsTest extends GeometryFunctionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreFunctionsTest.class);

    public CoreFunctionsTest(final String functionName, final List<Value> inputs, final Value expectedResult) {
        super(functionName, null, inputs, expectedResult);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> params() {

        Geometry gDefault = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(-77.035278, 38.889444));
        gDefault.setSRID(-4326);
        WKTWriter wktWriter = new WKTWriter();
        String wkt = wktWriter.write(gDefault);

        WKBWriter wkbWriter = new WKBWriter();
        String wkb = WKBWriter.toHex(wkbWriter.write(gDefault));

        Geometry gSRS4326 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(38.889444, -77.035278));
        gSRS4326.setSRID(4326);

        Geometry gSRID4326 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(38.889444, -77.035278));
        gSRID4326.setSRID(4326);

        Geometry p00 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(0, 0));
        p00.setSRID(-4326);

        Geometry p100 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(0, 100));
        p100.setSRID(-4326);

        Geometry l00100 = GeometryValue.Factory.GEOMETRY_FACTORY.createLineString(new Coordinate[]{new Coordinate(0, 0), new Coordinate(0, 100)});
        l00100.setSRID(-4326);

        Geometry gEmpty = GeometryValue.Factory.GEOMETRY_FACTORY.createEmpty(0);
        gEmpty.setSRID(-4326);

        CoordinateSequence cs00100 = new CoordinateArraySequence(new Coordinate[]{new Coordinate(0, 0), new Coordinate(0, 100), new Coordinate(100, 100), new Coordinate(100, 0), new Coordinate(0, 0)});
        Geometry pg00100 = GeometryValue.Factory.GEOMETRY_FACTORY.createPolygon(new LinearRing(cs00100, GeometryValue.Factory.GEOMETRY_FACTORY));
        pg00100.setSRID(-4326);

        Bag<GeometryValue> bag = Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(p00), new GeometryValue(p100)));

        GeometryCollection gc = GeometryValue.Factory.GEOMETRY_FACTORY.createGeometryCollection(new Geometry[]{p00, p100});

        return Arrays
                .asList(

                        // urn:ogc:def:function:geoxacml:3.0:geometry-dimension
                        new Object[]{CoreFunctions.Dimension.ID, Arrays.asList(new GeometryValue(p00)), IntegerValue.valueOf(0)},
                        new Object[]{CoreFunctions.Dimension.ID, Arrays.asList(new GeometryValue(l00100)), IntegerValue.valueOf(1)},
                        new Object[]{CoreFunctions.Dimension.ID, Arrays.asList(new GeometryValue(pg00100)), IntegerValue.valueOf(2)},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-type
                        new Object[]{CoreFunctions.GeometryType.ID, Arrays.asList(new GeometryValue(p00)), StringValue.parse("Point")},
                        new Object[]{CoreFunctions.GeometryType.ID, Arrays.asList(new GeometryValue(l00100)), StringValue.parse("LineString")},
                        new Object[]{CoreFunctions.GeometryType.ID, Arrays.asList(new GeometryValue(pg00100)), StringValue.parse("Polygon")},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-srid
                        new Object[]{CoreFunctions.SRID.ID, Arrays.asList(new GeometryValue(gDefault)), IntegerValue.valueOf(-4326)},
                        new Object[]{CoreFunctions.SRID.ID, Arrays.asList(new GeometryValue(gSRID4326)), IntegerValue.valueOf(4326)},
                        new Object[]{CoreFunctions.SRID.ID, Arrays.asList(new GeometryValue(gSRS4326)), IntegerValue.valueOf(4326)},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-srid-equals
                        new Object[]{CoreFunctions.SRIDEquals.ID, Arrays.asList(new GeometryValue(gDefault), IntegerValue.valueOf(-4326)), BooleanValue.TRUE},
                        new Object[]{CoreFunctions.SRIDEquals.ID, Arrays.asList(new GeometryValue(gDefault), IntegerValue.valueOf(4326)), BooleanValue.FALSE},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-srs-equals
                        new Object[]{CoreFunctions.SRSEquals.ID, Arrays.asList(new GeometryValue(gDefault), new StringValue("WGS84")), BooleanValue.TRUE},
                        new Object[]{CoreFunctions.SRSEquals.ID, Arrays.asList(new GeometryValue(gDefault), new StringValue("urn:ogc:def:crs:OGC::CRS84")), BooleanValue.TRUE},
                        new Object[]{CoreFunctions.SRSEquals.ID, Arrays.asList(new GeometryValue(gDefault), new StringValue("EPSG:4326")), BooleanValue.FALSE},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-is-simple
                        new Object[]{CoreFunctions.IsSimple.ID, Arrays.asList(new GeometryValue(pg00100)), BooleanValue.TRUE},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-is-empty
                        new Object[]{CoreFunctions.IsEmpty.ID, Arrays.asList(new GeometryValue(gEmpty)), BooleanValue.TRUE},
                        new Object[]{CoreFunctions.IsEmpty.ID, Arrays.asList(new GeometryValue(gDefault)), BooleanValue.FALSE},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-length
                        new Object[]{CoreFunctions.Length.ID, Arrays.asList(new GeometryValue(p00)), new DoubleValue(0.)},
                        new Object[]{CoreFunctions.Length.ID, Arrays.asList(new GeometryValue(l00100)), new DoubleValue(100.)},
                        new Object[]{CoreFunctions.Length.ID, Arrays.asList(new GeometryValue(pg00100)), new DoubleValue(400.)},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-area
                        new Object[]{CoreFunctions.Area.ID, Arrays.asList(new GeometryValue(p00)), new DoubleValue(0.)},
                        new Object[]{CoreFunctions.Area.ID, Arrays.asList(new GeometryValue(l00100)), new DoubleValue(0.)},
                        new Object[]{CoreFunctions.Area.ID, Arrays.asList(new GeometryValue(pg00100)), new DoubleValue(100. * 100.)},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-distance
                        new Object[]{CoreFunctions.Distance.ID, Arrays.asList(new GeometryValue(p00), new GeometryValue(p100)), new DoubleValue(100.)},
                        new Object[]{CoreFunctions.Distance.ID, Arrays.asList(new GeometryValue(gDefault), new GeometryValue(gSRID4326)), null},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-is-within-distance
                        new Object[]{CoreFunctions.IsWithinDistance.ID, Arrays.asList(new GeometryValue(p00), new GeometryValue(p100), new DoubleValue(100.)), BooleanValue.TRUE},
                        new Object[]{CoreFunctions.IsWithinDistance.ID, Arrays.asList(new GeometryValue(p00), new GeometryValue(p100), new DoubleValue(10.)), BooleanValue.FALSE},
                        new Object[]{CoreFunctions.IsWithinDistance.ID, Arrays.asList(new GeometryValue(gDefault), new GeometryValue(gSRID4326), new DoubleValue(0.)), null},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-distance-equals
                        new Object[]{CoreFunctions.EqualsDistance.ID, Arrays.asList(new GeometryValue(p00), new GeometryValue(p100), new DoubleValue(100.)), BooleanValue.TRUE},
                        new Object[]{CoreFunctions.EqualsDistance.ID, Arrays.asList(new GeometryValue(p00), new GeometryValue(p100), new DoubleValue(10.)), BooleanValue.FALSE},
                        new Object[]{CoreFunctions.EqualsDistance.ID, Arrays.asList(new GeometryValue(gDefault), new GeometryValue(gSRID4326), new DoubleValue(0.)), null},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-relate
                        new Object[]{CoreFunctions.Relate.ID, Arrays.asList(new GeometryValue(p00), new GeometryValue(p100), StringValue.parse("FF0FFF0F2")), BooleanValue.TRUE},
                        new Object[]{CoreFunctions.Relate.ID, Arrays.asList(new GeometryValue(p00), new GeometryValue(pg00100), StringValue.parse("FF0FFF0F2")), BooleanValue.FALSE},
                        new Object[]{CoreFunctions.Relate.ID, Arrays.asList(new GeometryValue(gDefault), new GeometryValue(gSRID4326), StringValue.parse("FFFFFFFFF")), null}
                );
    }

}
