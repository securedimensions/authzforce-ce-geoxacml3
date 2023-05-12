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
import org.ow2.authzforce.core.pdp.api.value.Bags;
import org.ow2.authzforce.core.pdp.api.value.BooleanValue;
import org.ow2.authzforce.core.pdp.api.value.IntegerValue;
import org.ow2.authzforce.core.pdp.api.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static de.securedimensions.geoxacml3.test.datatype.GeometryValueTest.*;

@RunWith(Parameterized.class)
public class BagSetFunctionsTest extends GeometryFunctionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BagSetFunctionsTest.class);

    public BagSetFunctionsTest(final String functionName, final List<Value> inputs, final Value expectedResult) {
        super(functionName, null, inputs, expectedResult);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> params() {

        return Arrays.asList(
                // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-bag
                new Object[]{BagSetFunctions.GeometryBag.ID, Arrays.asList(new GeometryValue(pWashingtonDCCRS84), new GeometryValue(pMunichCRS84)), Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(pWashingtonDCCRS84), new GeometryValue(pMunichCRS84)))},
                new Object[]{BagSetFunctions.GeometryBag.ID, List.of(), Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of())},

                // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-one-and-only
                new Object[]{BagSetFunctions.SingletonBagToPrimitive.ID, List.of(Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84)))), new GeometryValue(gWMCRS84)},
                new Object[]{BagSetFunctions.SingletonBagToPrimitive.ID, List.of(), null},

                // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-bag-size
                new Object[]{BagSetFunctions.BagSize.ID, List.of(Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of())), IntegerValue.valueOf(0)},
                new Object[]{BagSetFunctions.BagSize.ID, List.of(Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84)))), IntegerValue.valueOf(1)},
                new Object[]{BagSetFunctions.BagSize.ID, List.of(Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)))), IntegerValue.valueOf(2)},
                new Object[]{BagSetFunctions.BagSize.ID, List.of(Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84), new GeometryValue(pUSACRS84)))), IntegerValue.valueOf(3)},

                // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-is-in
                new Object[]{BagSetFunctions.BagContains.ID, Arrays.asList(
                        new GeometryValue(gEmpty),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of())),
                        BooleanValue.FALSE},
                new Object[]{BagSetFunctions.BagContains.ID, Arrays.asList(
                        new GeometryValue(pWashingtonDCCRS84),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of())),
                        BooleanValue.FALSE},
                new Object[]{BagSetFunctions.BagContains.ID, Arrays.asList(
                        new GeometryValue(pWashingtonDCCRS84),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(pWashingtonDCCRS84), new GeometryValue(pMunichCRS84)))),
                        BooleanValue.TRUE},
                new Object[]{BagSetFunctions.BagContains.ID, Arrays.asList(
                        new GeometryValue(gWMCRS84),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(pWashingtonDCCRS84), new GeometryValue(pMunichCRS84)))),
                        BooleanValue.FALSE},

                // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-at-least-one-member-of
                new Object[]{BagSetFunctions.AtLeastOneMemberOf.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of()),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)))),
                        BooleanValue.FALSE},
                new Object[]{BagSetFunctions.AtLeastOneMemberOf.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of())),
                        BooleanValue.FALSE},
                new Object[]{BagSetFunctions.AtLeastOneMemberOf.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)))),
                        BooleanValue.TRUE},
                new Object[]{BagSetFunctions.AtLeastOneMemberOf.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(pUSACRS84)))), BooleanValue.TRUE},
                new Object[]{BagSetFunctions.AtLeastOneMemberOf.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(pUSACRS84), new GeometryValue(pMunichCRS84)))), BooleanValue.FALSE},

                // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-intersection
                new Object[]{BagSetFunctions.Intersection.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of()),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of())),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of())},
                new Object[]{BagSetFunctions.Intersection.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of()),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84)))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of())},
                new Object[]{BagSetFunctions.Intersection.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of())),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of())},
                new Object[]{BagSetFunctions.Intersection.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84)))},
                new Object[]{BagSetFunctions.Intersection.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84)))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84)))},
                new Object[]{BagSetFunctions.Intersection.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84), new GeometryValue(pUSACRS84), new GeometryValue(pMunichCRS84)))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)))},
                new Object[]{BagSetFunctions.Intersection.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84), new GeometryValue(pUSACRS84), new GeometryValue(pMunichCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)))},

                // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-union
                new Object[]{BagSetFunctions.Union.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of()),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of())),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of())},
                new Object[]{BagSetFunctions.Union.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of()),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84)))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84)))},
                new Object[]{BagSetFunctions.Union.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of())),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84)))},
                new Object[]{BagSetFunctions.Union.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)))},
                new Object[]{BagSetFunctions.Union.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84)))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)))},
                new Object[]{BagSetFunctions.Union.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84), new GeometryValue(pUSACRS84), new GeometryValue(pMunichCRS84)))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84), new GeometryValue(pUSACRS84), new GeometryValue(pMunichCRS84)))},
                new Object[]{BagSetFunctions.Union.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84), new GeometryValue(pUSACRS84), new GeometryValue(pMunichCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84), new GeometryValue(pUSACRS84), new GeometryValue(pMunichCRS84)))},

                // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-subset
                new Object[]{BagSetFunctions.Subset.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of()),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of())),
                        BooleanValue.TRUE},
                new Object[]{BagSetFunctions.Subset.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of()),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84)))),
                        BooleanValue.TRUE},
                new Object[]{BagSetFunctions.Subset.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of())),
                        BooleanValue.FALSE},
                new Object[]{BagSetFunctions.Subset.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)))),
                        BooleanValue.TRUE},
                new Object[]{BagSetFunctions.Subset.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84)))),
                        BooleanValue.FALSE},

                // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-set-equals
                new Object[]{BagSetFunctions.SetEquals.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of()),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of())),
                        BooleanValue.TRUE},
                new Object[]{BagSetFunctions.SetEquals.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of()),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84)))),
                        BooleanValue.FALSE},
                new Object[]{BagSetFunctions.SetEquals.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of())),
                        BooleanValue.FALSE},
                new Object[]{BagSetFunctions.SetEquals.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84)))),
                        BooleanValue.TRUE},
                new Object[]{BagSetFunctions.SetEquals.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)))),
                        BooleanValue.TRUE},
                new Object[]{BagSetFunctions.SetEquals.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)))),
                        BooleanValue.FALSE},
                new Object[]{BagSetFunctions.SetEquals.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), List.of(new GeometryValue(gWMCRS84)))),
                        BooleanValue.FALSE},
                new Object[]{BagSetFunctions.SetEquals.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84), new GeometryValue(pUSACRS84)))),
                        BooleanValue.FALSE},
                new Object[]{BagSetFunctions.SetEquals.ID, Arrays.asList(
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84), new GeometryValue(pUSACRS84))),
                        Bags.newBag(GeometryValue.FACTORY.getDatatype(), Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gMCRS84)))),
                        BooleanValue.FALSE}
        );
    }

}
