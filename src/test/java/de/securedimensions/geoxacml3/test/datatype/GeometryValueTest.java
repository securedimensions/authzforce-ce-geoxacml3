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
package de.securedimensions.geoxacml3.test.datatype;

import de.securedimensions.geoxacml3.datatype.GeometryValue;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XPathCompiler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.ow2.authzforce.core.pdp.io.xacml.json.SerializableJSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.*;

/**
 *
 * GeoXACML3 GeometryAttribute validation test. 
 */
@RunWith(value = Parameterized.class)
public class GeometryValueTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeometryValueTest.class);
    private final Object value;
    private final String comment;
    private final Geometry result;
    private final Boolean isValid;
    private final Map<QName, String> otherXmlAttributes;
    private final XPathCompiler xPathCompiler;
    public GeometryValueTest(Object geometry, Map<QName, String> otherXmlAttributes, XPathCompiler xPathCompiler, String comment, Geometry result, Boolean isValid) {
        this.value = geometry;
        this.otherXmlAttributes = otherXmlAttributes;
        this.xPathCompiler = xPathCompiler;
        this.comment = comment;
        this.result = result;
        this.isValid = isValid;
    }

    @Parameters
    public static Collection<Object[]> data() {
        Map<QName, String> xmlAttributeSRS4326 = new HashMap<QName, String>();
        xmlAttributeSRS4326.put(GeometryValue.xmlSRS, "EPSG:4326");
        Map<QName, String> xmlAttributeSRID4326 = new HashMap<QName, String>();
        xmlAttributeSRID4326.put(GeometryValue.xmlSRID, "4326");

        Processor processor = new Processor(false);
        XPathCompiler xPathCompiler = processor.newXPathCompiler();

        Geometry gDefault = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(-77.035278, 38.889444));
        gDefault.setSRID(-4326);

        Geometry gSRS4326 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(38.889444, -77.035278));
        gSRS4326.setSRID(4326);

        Geometry gSRID4326 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(38.889444, -77.035278));
        gSRS4326.setSRID(4326);

        JSONObject geojson = new JSONObject();
        geojson.putOpt("type", gDefault.getGeometryType());
        double[] coords = {gDefault.getCoordinate().getX(), gDefault.getCoordinate().getY()};
        geojson.putOpt("coordinates", new JSONArray(coords));

        final Object[][] data = new Object[][]{

                // WKT encoding with default CRS
                {gDefault.toString(), null, xPathCompiler, "WKT with using default CRS", gDefault, true},

                // WKT encoding with CRS in otherXMLAttributes: SRS
                {gSRS4326.toString(), xmlAttributeSRS4326, xPathCompiler, "WKT using CRS as attribute 'srs' in AttributeValue", gSRS4326, true},

                // WKT encoding with CRS in otherXMLAttributes: SRID
                {gSRID4326.toString(), xmlAttributeSRID4326, xPathCompiler, "WKT using CRS as attribute 'srid' in AttributeValue", gSRID4326, true},

                // GeoJSON encoding with default CRS
                {new SerializableJSONObject(geojson), null, xPathCompiler, "GeoJSON using CRS as attribute 'srid' in AttributeValue", gDefault, true},

        };
        return Arrays.asList(data);
    }

    @Test
    public void test() {
        LOGGER.info("Test Begin: " + comment);
        Boolean isValidResult = false;
        GeometryValue gv;

        if (this.value instanceof String)
            gv = GeometryValue.FACTORY.getInstance((String) this.value, null, Optional.empty());
        else
            gv = GeometryValue.FACTORY.getInstance((SerializableJSONObject) this.value, null, Optional.empty());

        LOGGER.debug("GeometryValue: " + gv);
        LOGGER.debug("Expected result: " + result);
        Geometry g = gv.getGeometry();
        isValidResult = ((g.getSRID() == result.getSRID()) && (g.equals(result)));
        try {
            Assert.assertEquals("Test failed on: '" + this.value + "' (" + this.comment + ")", isValid, isValidResult);
            LOGGER.info("Test Success\n");
        } catch (AssertionError e) {
            LOGGER.error(e.getLocalizedMessage() + '\n');
        } finally {

        }
    }

}