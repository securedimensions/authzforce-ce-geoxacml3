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
import org.ow2.authzforce.core.pdp.api.value.BooleanValue;
import org.ow2.authzforce.core.pdp.api.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static de.securedimensions.geoxacml3.test.datatype.GeometryValueTest.*;

@RunWith(Parameterized.class)
public class TopologicalFunctionsTest extends GeometryFunctionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopologicalFunctionsTest.class);

    public TopologicalFunctionsTest(final String functionName, final List<Value> inputs, final Value expectedResult) {
        super(functionName, null, inputs, expectedResult);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> params() {


        return Arrays
                .asList(
                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-equal
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gWMCRS84)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)), BooleanValue.FALSE},

                        // Axis order test EPSG:4326 vs. WGS84
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gWMSRS4326)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gWMSRID4326), new GeometryValue(gWMSRS4326)), BooleanValue.TRUE},

                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-equals
                        new Object[]{TopologicalFunctions.Equals.ID, Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gWMCRS84)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Equals.ID, Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)), BooleanValue.FALSE},

                        // Axis order test EPSG:4326 vs. WGS84
                        new Object[]{TopologicalFunctions.Equals.ID, Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gWMSRS4326)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Equals.ID, Arrays.asList(new GeometryValue(gWMSRID4326), new GeometryValue(gWMSRS4326)), BooleanValue.TRUE},

                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-disjoint
                        new Object[]{TopologicalFunctions.Disjoint.ID, Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(pMunichCRS84)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Disjoint.ID, Arrays.asList(new GeometryValue(gMCRS84), new GeometryValue(pMunichCRS84)), BooleanValue.FALSE},

                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-touches
                        new Object[]{TopologicalFunctions.Touches.ID, Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(lDefault)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Touches.ID, Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(pMunichCRS84)), BooleanValue.FALSE},

                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-crosses
                        new Object[]{TopologicalFunctions.Crosses.ID, Arrays.asList(new GeometryValue(lDefault), new GeometryValue(pUSACRS84)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Crosses.ID, Arrays.asList(new GeometryValue(lEquator), new GeometryValue(pUSACRS84)), BooleanValue.FALSE},

                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-within
                        new Object[]{TopologicalFunctions.Within.ID, Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(pUSACRS84)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Within.ID, Arrays.asList(new GeometryValue(pWashingtonDCCRS84), new GeometryValue(pUSACRS84)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Within.ID, Arrays.asList(new GeometryValue(pMunichCRS84), new GeometryValue(pUSACRS84)), BooleanValue.FALSE},

                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-contains
                        new Object[]{TopologicalFunctions.Contains.ID, Arrays.asList(new GeometryValue(pUSACRS84), new GeometryValue(gWMCRS84)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Contains.ID, Arrays.asList(new GeometryValue(pUSACRS84), new GeometryValue(pMunichCRS84)), BooleanValue.FALSE},

                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-overlaps
                        new Object[]{TopologicalFunctions.Overlaps.ID, Arrays.asList(new GeometryValue(pNevadaCRS84), new GeometryValue(pCaliforniaCRS84)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Overlaps.ID, Arrays.asList(new GeometryValue(pUSACRS84), new GeometryValue(pMunichCRS84)), BooleanValue.FALSE},

                        // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-intersects
                        new Object[]{TopologicalFunctions.Intersects.ID, Arrays.asList(new GeometryValue(lEquator), new GeometryValue(lMeridian)), BooleanValue.TRUE},
                        new Object[]{TopologicalFunctions.Intersects.ID, Arrays.asList(new GeometryValue(lEquator), new GeometryValue(lDefault)), BooleanValue.FALSE}

                );
    }

}