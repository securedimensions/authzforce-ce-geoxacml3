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
import org.locationtech.jts.io.WKTReader;
import org.ow2.authzforce.core.pdp.api.value.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static de.securedimensions.geoxacml3.test.datatype.GeometryValueTest.*;

@RunWith(Parameterized.class)
public class CoreFunctionsTest extends GeometryFunctionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreFunctionsTest.class);

    public CoreFunctionsTest(final String functionName, final List<Value> inputs, final Value expectedResult) {
        super(functionName, null, inputs, expectedResult);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> params() {

        GeometryCollection homogeneousCollection = GeometryValue.Factory.GEOMETRY_FACTORY.createGeometryCollection(new Geometry[]{gWMCRS84, gMCRS84});
        GeometryCollection heterogeneousCollection = GeometryValue.Factory.GEOMETRY_FACTORY.createGeometryCollection(new Geometry[]{gMCRS84, pMunichCRS84});

        WKTReader wktReader = new WKTReader(new GeometryFactory(new PrecisionModel(4)));
        Geometry gWMSRS4326Precision4 = null;
        try {
            gWMSRS4326Precision4 = wktReader.read(gWMSRS4326.toText());
        }
        catch (Exception e) {}
        return Arrays.asList(

                // urn:ogc:def:function:geoxacml:3.0:geometry-dimension
                new Object[]{CoreFunctions.Dimension.ID, Arrays.asList(new GeometryValue(gMCRS84)), IntegerValue.valueOf(0)},
                new Object[]{CoreFunctions.Dimension.ID, Arrays.asList(new GeometryValue(lDefault)), IntegerValue.valueOf(1)},
                new Object[]{CoreFunctions.Dimension.ID, Arrays.asList(new GeometryValue(pMunichCRS84)), IntegerValue.valueOf(2)},

                // urn:ogc:def:function:geoxacml:3.0:geometry-type
                new Object[]{CoreFunctions.GeometryType.ID, Arrays.asList(new GeometryValue(gMCRS84)), StringValue.parse("Point")},
                new Object[]{CoreFunctions.GeometryType.ID, Arrays.asList(new GeometryValue(lDefault)), StringValue.parse("LineString")},
                new Object[]{CoreFunctions.GeometryType.ID, Arrays.asList(new GeometryValue(pMunichCRS84)), StringValue.parse("Polygon")},

                // urn:ogc:def:function:geoxacml:3.0:geometry-srid
                new Object[]{CoreFunctions.SRID.ID, Arrays.asList(new GeometryValue(gWMCRS84)), IntegerValue.valueOf(-4326)},
                new Object[]{CoreFunctions.SRID.ID, Arrays.asList(new GeometryValue(gWMSRID4326)), IntegerValue.valueOf(4326)},
                new Object[]{CoreFunctions.SRID.ID, Arrays.asList(new GeometryValue(gWMSRS4326)), IntegerValue.valueOf(4326)},

                // urn:ogc:def:function:geoxacml:3.0:geometry-srid-equals
                new Object[]{CoreFunctions.SRIDEquals.ID, Arrays.asList(IntegerValue.valueOf(-4326), new GeometryValue(gWMCRS84)), BooleanValue.TRUE},
                new Object[]{CoreFunctions.SRIDEquals.ID, Arrays.asList(IntegerValue.valueOf(-4326), new GeometryValue(gWMSRS4326)), BooleanValue.FALSE},
                new Object[]{CoreFunctions.SRIDEquals.ID, Arrays.asList(IntegerValue.valueOf(4326), new GeometryValue(gWMSRS4326)), BooleanValue.TRUE},
                new Object[]{CoreFunctions.SRIDEquals.ID, Arrays.asList(IntegerValue.valueOf(3857), new GeometryValue(gWMSRID3857)), BooleanValue.TRUE},

                // urn:ogc:def:function:geoxacml:3.0:geometry-is-simple
                new Object[]{CoreFunctions.IsSimple.ID, Arrays.asList(new GeometryValue(lEquator)), BooleanValue.TRUE},
                new Object[]{CoreFunctions.IsSimple.ID, Arrays.asList(new GeometryValue(lMeridian)), BooleanValue.TRUE},
                new Object[]{CoreFunctions.IsSimple.ID, Arrays.asList(new GeometryValue(lNotSimple)), BooleanValue.FALSE},

                // urn:ogc:def:function:geoxacml:3.0:geometry-is-empty
                new Object[]{CoreFunctions.IsEmpty.ID, Arrays.asList(new GeometryValue(gEmpty)), BooleanValue.TRUE},
                new Object[]{CoreFunctions.IsEmpty.ID, Arrays.asList(new GeometryValue(gWMCRS84)), BooleanValue.FALSE},

                // urn:ogc:def:function:geoxacml:3.0:geometry-length
                new Object[]{CoreFunctions.Length.ID, Arrays.asList(new GeometryValue(lEquator)), new DoubleValue(360.)},
                new Object[]{CoreFunctions.Length.ID, Arrays.asList(new GeometryValue(lMeridian)), new DoubleValue(180.)},
                new Object[]{CoreFunctions.Length.ID, Arrays.asList(new GeometryValue(gWMCRS84)), new DoubleValue(0.)},

                // urn:ogc:def:function:geoxacml:3.0:geometry-area
                new Object[]{CoreFunctions.Area.ID, Arrays.asList(new GeometryValue(gWMCRS84)), new DoubleValue(0.)},
                new Object[]{CoreFunctions.Area.ID, Arrays.asList(new GeometryValue(lEquator)), new DoubleValue(0.)},
                new Object[]{CoreFunctions.Area.ID, Arrays.asList(new GeometryValue(pWorldCRS84)), new DoubleValue(360. * 180.)},

                // urn:ogc:def:function:geoxacml:3.0:geometry-distance
                new Object[]{CoreFunctions.Distance.ID, Arrays.asList(
                        new GeometryValue(GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(-180,0))),
                        new GeometryValue(GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(180,0)))),
                        new DoubleValue(360.)},

                // urn:ogc:def:function:geoxacml:3.0:geometry-is-within-distance
                new Object[]{CoreFunctions.IsWithinDistance.ID, Arrays.asList(
                        new DoubleValue(180.),
                        new GeometryValue(GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(-180,0))),
                        new GeometryValue(GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(-0,0)))),
                        BooleanValue.TRUE},
                new Object[]{CoreFunctions.IsWithinDistance.ID, Arrays.asList(
                        new DoubleValue(90.),
                        new GeometryValue(GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(-180,0))),
                        new GeometryValue(GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(-0,0)))),
                        BooleanValue.FALSE},

                // urn:ogc:def:function:geoxacml:3.0:geometry-has-distance
                new Object[]{CoreFunctions.DistanceEquals.ID, Arrays.asList(
                        new DoubleValue(180.),
                        new GeometryValue(GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(-180,0))),
                        new GeometryValue(GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(-0,0)))),
                        BooleanValue.TRUE},
                new Object[]{CoreFunctions.DistanceEquals.ID, Arrays.asList(
                        new DoubleValue(90.),
                        new GeometryValue(GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(-180,0))),
                        new GeometryValue(GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(-0,0)))),
                        BooleanValue.FALSE},

                // urn:ogc:def:function:geoxacml:3.0:geometry-relate
                new Object[]{CoreFunctions.Relate.ID, Arrays.asList(StringValue.parse("FF0FFF0F2"), new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)), BooleanValue.TRUE},
                new Object[]{CoreFunctions.Relate.ID, Arrays.asList(StringValue.parse("FF0FFF102"), new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)), BooleanValue.FALSE},
                new Object[]{CoreFunctions.Relate.ID, Arrays.asList(StringValue.parse("FF0FFF102"), new GeometryValue(gWMCRS84), new GeometryValue(lEquator)), BooleanValue.TRUE},
                new Object[]{CoreFunctions.Relate.ID, Arrays.asList(StringValue.parse("FF0FFF102"), new GeometryValue(lEquator), new GeometryValue(gWMCRS84)), BooleanValue.FALSE},
                new Object[]{CoreFunctions.Relate.ID, Arrays.asList(StringValue.parse("FF1FF00F2"), new GeometryValue(lEquator), new GeometryValue(gWMCRS84)), BooleanValue.TRUE},

                // urn:ogc:def:function:geoxacml:3.0:geometry-bag-from-collection
                new Object[]{CoreFunctions.GeometryBagFromCollection.ID, Arrays.asList(new GeometryValue(homogeneousCollection)), Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)))},
                new Object[]{CoreFunctions.GeometryBagFromCollection.ID, Arrays.asList(new GeometryValue(heterogeneousCollection)), Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gMCRS84), new GeometryValue(pMunichCRS84)))},

                // urn:ogc:def:function:geoxacml:3.0:geometry-bag-to-collection
                new Object[]{CoreFunctions.GeometryBagToHomogeneousCollection.ID, Arrays.asList(Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)))), new GeometryValue(homogeneousCollection)},
                new Object[]{CoreFunctions.GeometryBagToHomogeneousCollection.ID, Arrays.asList(Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gMCRS84), new GeometryValue(pMunichCRS84)))), null}

        );
    }

}
