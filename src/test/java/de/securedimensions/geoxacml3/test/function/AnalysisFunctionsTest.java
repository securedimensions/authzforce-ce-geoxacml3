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
import de.securedimensions.geoxacml3.function.AnalysisFunctions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.ow2.authzforce.core.pdp.api.value.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static de.securedimensions.geoxacml3.test.datatype.GeometryValueTest.gWMCRS84;
import static de.securedimensions.geoxacml3.test.datatype.GeometryValueTest.gWMSRS4326;

@RunWith(Parameterized.class)
public class AnalysisFunctionsTest extends GeometryFunctionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreFunctionsTest.class);

    public AnalysisFunctionsTest(final String functionName, final List<Value> inputs, final Value expectedResult) {
        super(functionName, null, inputs, expectedResult);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> params() {

        Geometry p00 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(0, 0));
        p00.setSRID(-4326);

        Geometry p100 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(0, 100));
        p100.setSRID(-4326);

        Geometry l00100 = GeometryValue.Factory.GEOMETRY_FACTORY.createLineString(new Coordinate[]{new Coordinate(0, 0), new Coordinate(0, 100)});
        l00100.setSRID(-4326);

        Geometry gEmpty = GeometryValue.Factory.GEOMETRY_FACTORY.createEmpty(0);
        gEmpty.setSRID(-4326);

        CoordinateSequence cps00100 = new CoordinateArraySequence(new Coordinate[]{new Coordinate(0, 0), new Coordinate(0, 100), new Coordinate(100, 100), new Coordinate(100, 0), new Coordinate(0, 0)});
        Geometry pg00100 = GeometryValue.Factory.GEOMETRY_FACTORY.createPolygon(new LinearRing(cps00100, GeometryValue.Factory.GEOMETRY_FACTORY));
        pg00100.setSRID(-4326);

        CoordinateSequence cps_5050 = new CoordinateArraySequence(new Coordinate[]{new Coordinate(-50, -50), new Coordinate(-50, 50), new Coordinate(50, 50), new Coordinate(50, -50), new Coordinate(-50, -50)});
        Geometry pg_5050 = GeometryValue.Factory.GEOMETRY_FACTORY.createPolygon(new LinearRing(cps_5050, GeometryValue.Factory.GEOMETRY_FACTORY));
        pg_5050.setSRID(-4326);

        Geometry mp00100 = GeometryValue.Factory.GEOMETRY_FACTORY.createMultiPoint(new Point[]{(Point) p00, (Point) p100});
        mp00100.setSRID(-4326);

        Geometry pb00 = p00.buffer(10.);
        pb00.setSRID(-4326);



        return Arrays.asList(

                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-envelope
                        new Object[]{AnalysisFunctions.Envelope.ID, List.of(new GeometryValue(pg00100)), GeometryValue.FACTORY.getInstance("POLYGON ((0 0, 0 100, 100 100, 100 0, 0 0))", null, Optional.empty())},
                        new Object[]{AnalysisFunctions.Envelope.ID, List.of(new GeometryValue(l00100)), GeometryValue.FACTORY.getInstance("LINESTRING (0 0, 0 100)", null, Optional.empty())},
                        new Object[]{AnalysisFunctions.Envelope.ID, List.of(new GeometryValue(p00)), GeometryValue.FACTORY.getInstance("POINT (0 0)", null, Optional.empty())},

                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-boundary
                        new Object[]{AnalysisFunctions.Boundary.ID, List.of(new GeometryValue(pg00100)), GeometryValue.FACTORY.getInstance("LINEARRING (0 0, 0 100, 100 100, 100 0, 0 0)", null, Optional.empty())},
                        new Object[]{AnalysisFunctions.Boundary.ID, List.of(new GeometryValue(l00100)), GeometryValue.FACTORY.getInstance("MULTIPOINT ((0 0), (0 100))", null, Optional.empty())},
                        new Object[]{AnalysisFunctions.Boundary.ID, List.of(new GeometryValue(p00)), null},

                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-buffer
                        new Object[]{AnalysisFunctions.Buffer.ID, Arrays.asList(new GeometryValue(p00), new DoubleValue(10.0)), new GeometryValue(pb00)},

                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-convex-hull
                        new Object[]{AnalysisFunctions.ConvexHull.ID, List.of(new GeometryValue(mp00100)), GeometryValue.FACTORY.getInstance("LINESTRING (0 0, 0 100)", null, Optional.empty())},

                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-centroid
                        new Object[]{AnalysisFunctions.Centroid.ID, List.of(new GeometryValue(pg00100)), GeometryValue.FACTORY.getInstance("POINT (50 50)", null, Optional.empty())},

                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-geometry-intersection
                        new Object[]{AnalysisFunctions.GeometryIntersection.ID, Arrays.asList(new GeometryValue(pg00100), new GeometryValue(pg_5050)), GeometryValue.FACTORY.getInstance("POLYGON ((0 0, 0 50, 50 50, 50 0, 0 0))", null, Optional.empty())},

                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-geometry-union
                        new Object[]{AnalysisFunctions.GeometryUnion.ID, Arrays.asList(new GeometryValue(pg00100), new GeometryValue(pg_5050)), GeometryValue.FACTORY.getInstance("POLYGON ((0 50, 0 100, 100 100, 100 0, 50 0, 50 -50, -50 -50, -50 50, 0 50))", null, Optional.empty())},

                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-difference
                        new Object[]{AnalysisFunctions.GeometryDifference.ID, Arrays.asList(new GeometryValue(pg00100), new GeometryValue(pg_5050)), GeometryValue.FACTORY.getInstance("POLYGON ((0 50, 0 100, 100 100, 100 0, 50 0, 50 50, 0 50))", null, Optional.empty())},

                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-sym-difference
                        new Object[]{AnalysisFunctions.GeometrySymDifference.ID, Arrays.asList(new GeometryValue(pg00100), new GeometryValue(pg_5050)), GeometryValue.FACTORY.getInstance("MULTIPOLYGON (((0 50, 0 0, 50 0, 50 -50, -50 -50, -50 50, 0 50)), ((0 50, 0 100, 100 100, 100 0, 50 0, 50 50, 0 50)))", null, Optional.empty())}


                );
    }

}
