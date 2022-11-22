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
import de.securedimensions.geoxacml3.function.BagSetFunctions;
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

@RunWith(Parameterized.class)
public class BagSetFunctionsTest extends GeometryFunctionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BagSetFunctionsTest.class);

    public BagSetFunctionsTest(final String functionName, final List<Value> inputs, final Value expectedResult) {
        super(functionName, null, inputs, expectedResult);
    }

    // precision is 4 decimal places
    private static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(PrecisionModel.FIXED));

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> params() {
        Geometry gWGS84 = GEOMETRY_FACTORY.createPoint(new Coordinate(-77.035278, 38.889444));
        gWGS84.setSRID(-4326);

        Geometry gSRS4326 = GEOMETRY_FACTORY.createPoint(new Coordinate(38.889444, -77.035278));
        gSRS4326.setSRID(4326);

        Geometry gSRID4326 = GEOMETRY_FACTORY.createPoint(new Coordinate(38.889444, -77.035278));
        gSRID4326.setSRID(4326);

        Geometry gSRID3857 = GEOMETRY_FACTORY.createPoint(new Coordinate(-8575527.92007827, 4705847.723791289));
        gSRID3857.setSRID(3857);

        Geometry p00 = GEOMETRY_FACTORY.createPoint(new Coordinate(0, 0));
        p00.setSRID(-4326);

        Geometry p50 = GEOMETRY_FACTORY.createPoint(new Coordinate(0, 50));
        p50.setSRID(-4326);

        Geometry p100 = GEOMETRY_FACTORY.createPoint(new Coordinate(0, 100));
        p100.setSRID(-4326);

        Geometry l00100 = GEOMETRY_FACTORY.createLineString(new Coordinate[]{new Coordinate(0, 0), new Coordinate(0, 100)});
        l00100.setSRID(-4326);

        Geometry gEmpty = GEOMETRY_FACTORY.createEmpty(0);
        gEmpty.setSRID(-4326);

        CoordinateSequence cps00100 = new CoordinateArraySequence(new Coordinate[]{new Coordinate(0, 0), new Coordinate(0, 100), new Coordinate(100, 100), new Coordinate(100, 0), new Coordinate(0, 0)});
        Geometry pg00100 = GEOMETRY_FACTORY.createPolygon(new LinearRing(cps00100, GEOMETRY_FACTORY));
        pg00100.setSRID(-4326);

        CoordinateSequence cps_5050 = new CoordinateArraySequence(new Coordinate[]{new Coordinate(-50, -50), new Coordinate(-50, 50), new Coordinate(50, 50), new Coordinate(50, -50), new Coordinate(-50, -50)});
        Geometry pg_5050 = GEOMETRY_FACTORY.createPolygon(new LinearRing(cps_5050, GEOMETRY_FACTORY));
        pg_5050.setSRID(-4326);

        Geometry mp00100 = GEOMETRY_FACTORY.createMultiPoint(new Point[]{(Point) p00, (Point) p100});
        mp00100.setSRID(-4326);

        Geometry pb00 = p00.buffer(10.);
        pb00.setSRID(-4326);

        Geometry gWGS84AllowTransform = gWGS84.copy();
        gWGS84AllowTransform.setUserData(Boolean.TRUE);

        Geometry gSRID4326AllowTransform = gSRID4326.copy();
        gSRID4326AllowTransform.setUserData(Boolean.TRUE);

        Geometry gSRID3857AllowTransform = gSRID3857.copy();
        gSRID3857AllowTransform.setUserData(Boolean.TRUE);

        Bag<GeometryValue> bag0 = Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(p00)));
        Bag<GeometryValue> bag1 = Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(p00), new GeometryValue(p50)));
        Bag<GeometryValue> bag2 = Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(p00), new GeometryValue(p50), new GeometryValue(p100)));
        Bag<GeometryValue> bag3 = Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(p50), new GeometryValue(p100)));
        // bag with EPSG:4326
        Bag<GeometryValue> bag4326 = Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gSRID4326)));
        // bag with WGS84
        Bag<GeometryValue> bagWGS84 = Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWGS84)));
        // bag with WPSG:4326 allowTransformation=true
        Bag<GeometryValue> bag4326Allow = Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gSRID4326AllowTransform)));
        // bag with WGS84 allowTransformation=true
        Bag<GeometryValue> bagDefaultAllow = Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWGS84AllowTransform)));
        // bag with EPSG:3857
        Bag<GeometryValue> bag3857 = Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gSRID3857)));
        // bag with EPSG:3857 allowTransformation=true
        Bag<GeometryValue> bag3857Allow = Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gSRID3857AllowTransform)));

        return Arrays
                .asList(
                        // urn:ogc:def:function:geoxacml:3.0:geometry-bag
                        new Object[]{BagSetFunctions.GeometryBag.ID, Arrays.asList(new GeometryValue(p00), new GeometryValue(p100)), bag3},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-one-and-only
                        new Object[]{BagSetFunctions.SingletonBagToPrimitive.ID, Arrays.asList(bag0), new GeometryValue(p00)},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-bag-size
                        new Object[]{BagSetFunctions.BagSize.ID, Arrays.asList(bag0), IntegerValue.valueOf(1)},
                        new Object[]{BagSetFunctions.BagSize.ID, Arrays.asList(bag1), IntegerValue.valueOf(2)},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-bag-contains
                        new Object[]{BagSetFunctions.BagContains.ID, Arrays.asList(new GeometryValue(p00), bag0), BooleanValue.TRUE},
                        new Object[]{BagSetFunctions.BagContains.ID, Arrays.asList(new GeometryValue(p50), bag0), BooleanValue.FALSE},

                        // primitive geometry and bag have 'allowTransformation=false' -> Indeterminate
                        new Object[]{BagSetFunctions.BagContains.ID, Arrays.asList(new GeometryValue(gSRS4326), bag3857), null},
                        // swap axis for primitive geometry regardless of allowTransformation
                        new Object[]{BagSetFunctions.BagContains.ID, Arrays.asList(new GeometryValue(gWGS84), bag4326), BooleanValue.TRUE},
                        // swap axis for bag geometry regardless of allowTransformation
                        new Object[]{BagSetFunctions.BagContains.ID, Arrays.asList(new GeometryValue(gSRS4326), bagWGS84), BooleanValue.TRUE},
                        // CRS transformation for primitive geometry - bag geometry must have axis order corrected as CRS transformation from 3857 results in LAT/LON
                        new Object[]{BagSetFunctions.BagContains.ID, Arrays.asList(new GeometryValue(gSRID3857AllowTransform), bag4326), BooleanValue.FALSE}, // FALSE because of precision error
                        // CRS transformation for primitive geometry - bag geometry must NOT have axis order corrected as CRS transformation from 3857 results in LAT/LON
                        new Object[]{BagSetFunctions.BagContains.ID, Arrays.asList(new GeometryValue(gSRID3857AllowTransform), bagWGS84), BooleanValue.FALSE}, // FALSE because of precision error
                        // CRS transformation for bag geometry - primitive geometry must have axis order corrected as CRS transformation from 3857 results in LAT/LON
                        new Object[]{BagSetFunctions.BagContains.ID, Arrays.asList(new GeometryValue(gWGS84), bag3857Allow), BooleanValue.FALSE}, // FALSE because of precision error
                        // CRS transformation for bag geometry - primitive geometry must have axis order corrected as CRS transformation from 3857 results in LAT/LON
                        new Object[]{BagSetFunctions.BagContains.ID, Arrays.asList(new GeometryValue(gSRS4326), bag3857Allow), BooleanValue.FALSE}, // FALSE because of precision error

                        // primitive geometry can be transformed
                        new Object[]{BagSetFunctions.BagContains.ID, Arrays.asList(new GeometryValue(gWGS84AllowTransform), bag3857), BooleanValue.TRUE},
                        // bag can be transformed
                        new Object[]{BagSetFunctions.BagContains.ID, Arrays.asList(new GeometryValue(gSRID3857), bag4326Allow), BooleanValue.TRUE},


                        // urn:ogc:def:function:geoxacml:3.0:geometry-at-least-one-member-of
                        new Object[]{BagSetFunctions.AtLeastOneMemberOf.ID, Arrays.asList(bag0, bag1), BooleanValue.TRUE},
                        new Object[]{BagSetFunctions.AtLeastOneMemberOf.ID, Arrays.asList(bag2, bag0), BooleanValue.TRUE},
                        new Object[]{BagSetFunctions.AtLeastOneMemberOf.ID, Arrays.asList(bag0, bag3), BooleanValue.FALSE},
                        new Object[]{BagSetFunctions.AtLeastOneMemberOf.ID, Arrays.asList(bag0, bag4326), null},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-intersection
                        new Object[]{BagSetFunctions.Intersection.ID, Arrays.asList(bag0, bag1), bag0},
                        // CRS Transformation not allowed in both bags-> empty result because of 'allowTransformation'=false
                        new Object[]{BagSetFunctions.Intersection.ID, Arrays.asList(bagWGS84, bag3857), Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList())},
                        // CRS Transformation not allowed in first bag-> empty result because of 'allowTransformation'=false
                        new Object[]{BagSetFunctions.Intersection.ID, Arrays.asList(bagWGS84, bag3857Allow), Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList())},
                        // CRS Transformation not allowed in second bag-> empty result because of 'allowTransformation'=false
                        new Object[]{BagSetFunctions.Intersection.ID, Arrays.asList(bag4326Allow, bag3857), Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList())},
                        // CRS Transformation allowed but because of transformation precision error equals is FALSE -> empty result
                        new Object[]{BagSetFunctions.Intersection.ID, Arrays.asList(bag4326Allow, bag3857Allow), Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList())},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-union
                        new Object[]{BagSetFunctions.Union.ID, Arrays.asList(bag0, bag1), bag1},
                        // CRS Transformation not allowed in both bags-> empty result because of 'allowTransformation'=false
                        new Object[]{BagSetFunctions.Union.ID, Arrays.asList(bagWGS84, bag3857), Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList())},
                        // CRS Transformation not allowed in first bag-> empty result because of 'allowTransformation'=false
                        new Object[]{BagSetFunctions.Union.ID, Arrays.asList(bagWGS84, bag3857Allow), Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList())},
                        // CRS Transformation not allowed in second bag-> empty result because of 'allowTransformation'=false
                        new Object[]{BagSetFunctions.Union.ID, Arrays.asList(bag4326Allow, bag3857), Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList())},
                        // CRS Transformation allowed but because of transformation precision error equals is FALSE -> empty result
                        new Object[]{BagSetFunctions.Union.ID, Arrays.asList(bag4326Allow, bag3857Allow), Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList())},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-subset
                        new Object[]{BagSetFunctions.Subset.ID, Arrays.asList(bag1, bag2), BooleanValue.TRUE},
                        new Object[]{BagSetFunctions.Subset.ID, Arrays.asList(bag2, bag1), BooleanValue.FALSE},

                        // urn:ogc:def:function:geoxacml:3.0:geometry-set-equals
                        new Object[]{BagSetFunctions.SetEquals.ID, Arrays.asList(bag0, bag0), BooleanValue.TRUE},
                        new Object[]{BagSetFunctions.SetEquals.ID, Arrays.asList(bag1, bag1), BooleanValue.TRUE},
                        new Object[]{BagSetFunctions.SetEquals.ID, Arrays.asList(bag0, bag1), BooleanValue.FALSE}

                );
    }

}
