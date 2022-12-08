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
import de.securedimensions.geoxacml3.function.BagSetFunctions;
import de.securedimensions.geoxacml3.function.TopologicalFunctions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.ow2.authzforce.core.pdp.api.value.Bags;
import org.ow2.authzforce.core.pdp.api.value.BooleanValue;
import org.ow2.authzforce.core.pdp.api.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.*;

import static de.securedimensions.geoxacml3.datatype.GeometryValue.SOURCE_ATTR_DESIGNATOR;
import static de.securedimensions.geoxacml3.datatype.GeometryValue.SOURCE_POLICY;
import static de.securedimensions.geoxacml3.test.datatype.GeometryValueTest.*;
import static de.securedimensions.geoxacml3.test.datatype.GeometryValueTest.gWMSRID3857;

@RunWith(Parameterized.class)
public class PrecisionTest extends GeometryFunctionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrecisionTest.class);

    public PrecisionTest(final String functionName, final List<Value> inputs, final Value expectedResult) {
        super(functionName, null, inputs, expectedResult);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> params() {

        Map<QName, String> xmlPrecision1 = new HashMap<QName, String>();
        xmlPrecision1.put(GeometryValue.xmlPrecision, "1.0");
        xmlPrecision1.put(GeometryValue.SOURCE, SOURCE_POLICY);

        Map<QName, String> xmlPrecision9 = new HashMap<QName, String>();
        xmlPrecision9.put(GeometryValue.xmlPrecision, "9.0");
        xmlPrecision9.put(GeometryValue.SOURCE, SOURCE_POLICY);

        Map<QName, String> xmlPrecision1ADR = new HashMap<QName, String>();
        xmlPrecision1ADR.put(GeometryValue.xmlPrecision, "1.0");
        xmlPrecision1ADR.put(GeometryValue.SOURCE, SOURCE_ATTR_DESIGNATOR);
        xmlPrecision1ADR.put(GeometryValue.xmlAttributeId, "Washington Monument at precision 1.0");
        xmlPrecision1ADR.put(GeometryValue.xmlCategoryId, "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");

        Map<QName, String> xmlPrecision9ADR = new HashMap<QName, String>();
        xmlPrecision9ADR.put(GeometryValue.xmlPrecision, "9.0");
        xmlPrecision9ADR.put(GeometryValue.SOURCE, SOURCE_ATTR_DESIGNATOR);
        xmlPrecision9ADR.put(GeometryValue.xmlAttributeId, "Washington Monument at precision 9.0");
        xmlPrecision9ADR.put(GeometryValue.xmlCategoryId, "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");

        Geometry gWMCRS84Precision1Policy = gWMCRS84.copy();
        gWMCRS84Precision1Policy.setUserData(xmlPrecision1);

        Geometry gWMCRS84Precision1ADR = gWMCRS84.copy();
        gWMCRS84Precision1ADR.setUserData(xmlPrecision1ADR);

        Geometry gWMCRS84Precision9Policy = gWMCRS84.copy();
        gWMCRS84Precision9Policy.setUserData(xmlPrecision9);

        Geometry gWMCRS84Precision9ADR = gWMCRS84.copy();
        gWMCRS84Precision9ADR.setUserData(xmlPrecision9ADR);

        return Arrays.asList(
                // precision 1.0 in policy - precision 1.0 in policy -> Exception
                new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy), new GeometryValue(gWMCRS84Precision1Policy)), null},

                // precision 1.0 in policy - precision 9.0 in ADR -> Exception
                new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy), new GeometryValue(gWMCRS84Precision9ADR)), null},

                // precision 9.0 in policy - precision 1.0 in ADR -> TRUE
                new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision9Policy), new GeometryValue(gWMCRS84Precision1ADR)), BooleanValue.TRUE},

                // precision 1.0 in ADR - precision 1.0 in ADR -> TRUE
                new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision1ADR), new GeometryValue(gWMCRS84Precision1ADR)), BooleanValue.TRUE},

                // precision 9.0 in ADR - precision 1.0 in ADR -> Exception
                new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision9ADR), new GeometryValue(gWMCRS84Precision1ADR)), null},

                // precision 1.0 in policy - precision 1.0 in policy -> Exception
                new Object[]{AnalysisFunctions.GeometryIntersection.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy), new GeometryValue(gWMCRS84Precision1Policy)), null},

                // precision 1.0 in policy - precision 9.0 in ADR -> Exception
                new Object[]{AnalysisFunctions.GeometryIntersection.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy), new GeometryValue(gWMCRS84Precision9ADR)), null},

                // precision 9.0 in policy - precision 1.0 in ADR -> TRUE
                new Object[]{AnalysisFunctions.GeometryIntersection.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision9Policy), new GeometryValue(gWMCRS84Precision1ADR)), new GeometryValue(gWMCRS84Precision1ADR)},

                // precision 1.0 in ADR - precision 1.0 in ADR -> TRUE
                new Object[]{AnalysisFunctions.GeometryIntersection.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision1ADR), new GeometryValue(gWMCRS84Precision1ADR)), new GeometryValue(gWMCRS84Precision1ADR)},

                // precision 9.0 in ADR - precision 1.0 in ADR -> Exception
                new Object[]{AnalysisFunctions.GeometryUnion.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision9ADR), new GeometryValue(gWMCRS84Precision1ADR)), null},

                // precision 1.0 in policy - precision 1.0 in policy -> Exception
                new Object[]{AnalysisFunctions.GeometryUnion.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy), new GeometryValue(gWMCRS84Precision1Policy)), null},

                // precision 1.0 in policy - precision 9.0 in ADR -> Exception
                new Object[]{AnalysisFunctions.GeometryUnion.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy), new GeometryValue(gWMCRS84Precision9ADR)), null},

                // precision 9.0 in policy - precision 1.0 in ADR -> TRUE
                new Object[]{AnalysisFunctions.GeometryUnion.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision9Policy), new GeometryValue(gWMCRS84Precision1ADR)), new GeometryValue(gWMCRS84Precision1ADR)},

                // precision 1.0 in ADR - precision 1.0 in ADR -> TRUE
                new Object[]{AnalysisFunctions.GeometryUnion.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision1ADR), new GeometryValue(gWMCRS84Precision1ADR)), new GeometryValue(gWMCRS84Precision1ADR)},

                // precision 9.0 in ADR - precision 1.0 in ADR -> Exception
                new Object[]{AnalysisFunctions.GeometryUnion.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision9ADR), new GeometryValue(gWMCRS84Precision1ADR)), null},

                // precision 9.0 in ADR - precision 1.0 in ADR -> Exception
                new Object[]{AnalysisFunctions.GeometryDifference.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision9ADR), new GeometryValue(gWMCRS84Precision1ADR)), null},

                // precision 1.0 in policy - precision 1.0 in policy -> Exception
                new Object[]{AnalysisFunctions.GeometryDifference.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy), new GeometryValue(gWMCRS84Precision1Policy)), null},

                // precision 1.0 in policy - precision 9.0 in ADR -> Exception
                new Object[]{AnalysisFunctions.GeometryDifference.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy), new GeometryValue(gWMCRS84Precision9ADR)), null},

                // precision 9.0 in policy - precision 1.0 in ADR -> TRUE
                new Object[]{AnalysisFunctions.GeometryDifference.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision9Policy), new GeometryValue(gWMCRS84Precision1ADR)), new GeometryValue(gEmpty)},

                // precision 1.0 in ADR - precision 1.0 in ADR -> TRUE
                new Object[]{AnalysisFunctions.GeometryDifference.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision1ADR), new GeometryValue(gWMCRS84Precision1ADR)), new GeometryValue(gEmpty)},

                // precision 9.0 in ADR - precision 1.0 in ADR -> Exception
                new Object[]{AnalysisFunctions.GeometryDifference.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision9ADR), new GeometryValue(gWMCRS84Precision1ADR)), null},


                // precision 9.0 in ADR - precision 1.0 in ADR -> Exception
                new Object[]{AnalysisFunctions.GeometrySymDifference.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision9ADR), new GeometryValue(gWMCRS84Precision1ADR)), null},

                // precision 1.0 in policy - precision 1.0 in policy -> Exception
                new Object[]{AnalysisFunctions.GeometrySymDifference.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy), new GeometryValue(gWMCRS84Precision1Policy)), null},

                // precision 1.0 in policy - precision 9.0 in ADR -> Exception
                new Object[]{AnalysisFunctions.GeometrySymDifference.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy), new GeometryValue(gWMCRS84Precision9ADR)), null},

                // precision 9.0 in policy - precision 1.0 in ADR -> TRUE
                new Object[]{AnalysisFunctions.GeometrySymDifference.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision9Policy), new GeometryValue(gWMCRS84Precision1ADR)), new GeometryValue(gEmpty)},

                // precision 1.0 in ADR - precision 1.0 in ADR -> TRUE
                new Object[]{AnalysisFunctions.GeometrySymDifference.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision1ADR), new GeometryValue(gWMCRS84Precision1ADR)), new GeometryValue(gEmpty)},

                // precision 9.0 in ADR - precision 1.0 in ADR -> Exception
                new Object[]{AnalysisFunctions.GeometrySymDifference.ID, Arrays.asList(new GeometryValue(gWMCRS84Precision9ADR), new GeometryValue(gWMCRS84Precision1ADR)), null},

                new Object[]{BagSetFunctions.BagContains.ID, Arrays.asList(
                        new GeometryValue(gWMCRS84Precision1ADR),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy)))),
                        BooleanValue.TRUE},
                new Object[]{BagSetFunctions.BagContains.ID, Arrays.asList(
                        new GeometryValue(gWMCRS84Precision9ADR),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy)))),
                        null},

                new Object[]{BagSetFunctions.AtLeastOneMemberOf.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1ADR))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy), new GeometryValue(gWMCRS84Precision9Policy)))),
                        BooleanValue.TRUE},
                new Object[]{BagSetFunctions.AtLeastOneMemberOf.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision9ADR))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy), new GeometryValue(gWMCRS84Precision9Policy)))),
                        null},

                new Object[]{BagSetFunctions.Intersection.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1ADR))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy), new GeometryValue(gWMCRS84Precision9Policy)))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1ADR)))},
                new Object[]{BagSetFunctions.Intersection.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision9ADR))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy), new GeometryValue(gWMCRS84Precision9Policy)))),
                        null},

                new Object[]{BagSetFunctions.Union.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1ADR))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy)))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1ADR)))},
                new Object[]{BagSetFunctions.Union.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision9ADR))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy)))),
                        null},

                new Object[]{BagSetFunctions.Subset.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1ADR))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy)))),
                        BooleanValue.TRUE},
                new Object[]{BagSetFunctions.Subset.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision9ADR))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy)))),
                        null},

                new Object[]{BagSetFunctions.SetEquals.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1ADR))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy)))),
                        BooleanValue.TRUE},
                new Object[]{BagSetFunctions.SetEquals.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision9ADR))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84Precision1Policy)))),
                        null}

        );
    }

}