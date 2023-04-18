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
import de.securedimensions.geoxacml3.function.TopologicalFunctions;
import de.securedimensions.geoxacml3.identifiers.Definitions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.locationtech.jts.geom.Geometry;
import org.ow2.authzforce.core.pdp.api.value.BooleanValue;
import org.ow2.authzforce.core.pdp.api.value.IntegerValue;
import org.ow2.authzforce.core.pdp.api.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.*;

import static de.securedimensions.geoxacml3.pdp.io.GeoXACMLRequestPreprocessor.XACML_ATTRIBUTE_ID_QNAME;
import static de.securedimensions.geoxacml3.pdp.io.GeoXACMLRequestPreprocessor.XACML_CATEGORY_ID_QNAME;
import static de.securedimensions.geoxacml3.test.datatype.GeometryValueTest.*;

@RunWith(Parameterized.class)
public class CRSTransformationTest extends GeometryFunctionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CRSTransformationTest.class);

    public CRSTransformationTest(final String functionName, final List<Value> inputs, final Value expectedResult) {
        super(functionName, null, inputs, expectedResult);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> params() {

        Map<QName, String> xmlAllowTransformation = new HashMap<QName, String>();
        xmlAllowTransformation.put(Definitions.ATTR_ALLOW_TRANSFORMATION, "true");

        Map<QName, String> xmlAttributes = new HashMap<QName, String>();
        xmlAttributes.put(XACML_ATTRIBUTE_ID_QNAME, "subject-location");
        xmlAttributes.put(XACML_CATEGORY_ID_QNAME, "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");

        Geometry gWMCRS84DisallowTransformation = gWMCRS84.copy();
        gWMCRS84DisallowTransformation.setUserData(xmlAttributes);

        Geometry gWMCRS84AllowTransformation = gWMCRS84.copy();
        gWMCRS84AllowTransformation.setUserData(xmlAllowTransformation);

        Geometry gWMSRS4326AllowTransformation = gWMSRS4326.copy();
        gWMSRS4326AllowTransformation.setUserData(xmlAllowTransformation);

        Geometry gWMSRID3857AllowTransformation = gWMSRID3857.copy();
        gWMSRID3857AllowTransformation.setUserData(xmlAllowTransformation);

        return Arrays.asList(

            // It is sufficient to test the CRS transformation with EQUAL only, because all other topological test functions use the same CRS transformation class
            // Axis order test EPSG:4326 vs. WGS84 - no CRS transformation - only axis swapping
            new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gWMSRS4326)), BooleanValue.TRUE},
            new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gWMSRS4326), new GeometryValue(gWMCRS84)), BooleanValue.TRUE},

            // CRS transformation required -> Core conformance would throw an error
            // transformation of gWMCRS84AllowTransformation
            new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gWMCRS84AllowTransformation), new GeometryValue(gWMSRID3857)), BooleanValue.TRUE},
            // transformation of gWMSRS4326AllowTransformation
            new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gWMSRS4326AllowTransformation), new GeometryValue(gWMSRID3857)), BooleanValue.TRUE},

            // CRS transformation required -> Core conformance would throw an error
            // transformation of gWMCRS84AllowTransformation
            new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gWMSRID3857), new GeometryValue(gWMCRS84AllowTransformation)), BooleanValue.TRUE},
            // transformation of gWMSRS4326AllowTransformation
            new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gWMSRID3857), new GeometryValue(gWMSRS4326AllowTransformation)), BooleanValue.TRUE},

            // CRS transformation not allowed per 'allowTransformation' attribute in AttributeValue -> throw Indeterminate
            new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gWMCRS84), new GeometryValue(gWMSRID3857)), null},
            new Object[]{TopologicalFunctions.Equal.ID, Arrays.asList(new GeometryValue(gWMSRS4326), new GeometryValue(gWMSRID3857)), null},

            // urn:ogc:def:geoxacml:3.0:function:geometry:geometry-ensure-srid
            new Object[]{CoreFunctions.EnsureSRID.ID, Arrays.asList(IntegerValue.valueOf(-4326), new GeometryValue(gWMCRS84)), new GeometryValue(gWMCRS84)},
            new Object[]{CoreFunctions.EnsureSRID.ID, Arrays.asList(IntegerValue.valueOf(4326), new GeometryValue(gWMCRS84)), new GeometryValue(gWMSRS4326)},
            new Object[]{CoreFunctions.EnsureSRID.ID, Arrays.asList(IntegerValue.valueOf(3857), new GeometryValue(gWMSRS4326AllowTransformation)), new GeometryValue(gWMSRID3857)},
            new Object[]{CoreFunctions.EnsureSRID.ID, Arrays.asList(IntegerValue.valueOf(3857), new GeometryValue(gWMCRS84DisallowTransformation)), null}

        );
    }

}