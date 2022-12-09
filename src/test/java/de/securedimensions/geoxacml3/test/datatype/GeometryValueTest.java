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
import de.securedimensions.geoxacml3.identifiers.Definitions;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XPathCompiler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.locationtech.jts.geom.*;
import org.ow2.authzforce.core.pdp.io.xacml.json.SerializableJSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.*;

/**
 * GeoXACML3 GeometryAttribute validation test.
 */
@RunWith(value = Parameterized.class)
public class GeometryValueTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeometryValueTest.class);
    private final Object value;
    private final String comment;
    private final Object result;
    private final Boolean isValid;
    private final Map<QName, String> otherXmlAttributes;
    private final XPathCompiler xPathCompiler;

    /**
    Test Geometries
     */
    public static Map<QName, String> xmlAttributeSRS4326 = new HashMap<QName, String>();
    public static Map<QName, String> xmlAttributeWGS84 = new HashMap<QName, String>();

    public static Map<QName, String> xmlAttributeSRID4326 = new HashMap<QName, String>();

    public static Map<QName, String> xmlAttributeCRS84 = new HashMap<QName, String>();


    public static Map<QName, String> xmlAttributePrecision1 = new HashMap<QName, String>();
    public static Map<QName, String> xmlAttributePrecision9 = new HashMap<QName, String>();

    public static Geometry gWMCRS84, gMCRS84, gWMSRS4326, gWMSRID4326, gWMSRID3857, lDefault, lEquator, lMeridian, lNotSimple, gEmpty;

    public static Geometry pWashingtonDCCRS84, pUSACRS84, pCaliforniaCRS84, pNevadaCRS84, pMunichCRS84, pWorldCRS84;
    public static GeometryCollection gHomogeneousCollection, gHeterogeneousCollection;

    static {
        xmlAttributeSRS4326.put(Definitions.xmlCRS, "EPSG:4326");
        xmlAttributeWGS84.put(Definitions.xmlCRS, "WGS84");
        xmlAttributeCRS84.put(Definitions.xmlCRS, "urn:ogc:def:crs:OGC::CRS84");
        xmlAttributeSRID4326.put(Definitions.xmlSRID, "4326");

        xmlAttributePrecision1.put(Definitions.xmlPrecision, "1.0");
        xmlAttributePrecision9.put(Definitions.xmlPrecision, "9.0");

        // Washington Monument in CRS84 (lon/lat)
        gWMCRS84 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(-77.035278, 38.889444));
        gWMCRS84.setSRID(-4326);

        // Munich in CRS84
        gMCRS84 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(11.576124, 48.137154));
        gMCRS84.setSRID(-4326);

        // Washington Monument in EPSG:4326 (lat/lon)
        gWMSRS4326 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(38.889444, -77.035278));
        gWMSRS4326.setSRID(4326);

        // Washington Monument in CRS84 (lon/lat)
        gWMSRID4326 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(38.889444, -77.035278));
        gWMSRID4326.setSRID(4326);

        pWashingtonDCCRS84 = PolygonGenerator.fromBBox(-77.119759,38.791645,-76.909395,38.99511, GeometryValue.Factory.GEOMETRY_FACTORY);
        pWashingtonDCCRS84.setSRID(-4326);

        pUSACRS84 = PolygonGenerator.fromBBox(-124.763068,24.523096,	-66.949895,49.002494, GeometryValue.Factory.GEOMETRY_FACTORY);
        pUSACRS84.setSRID(-4326);

        pCaliforniaCRS84 = PolygonGenerator.fromBBox(-124.409591,	32.534156,	-114.131211,	42.009518, GeometryValue.Factory.GEOMETRY_FACTORY);
        pCaliforniaCRS84.setSRID(-4326);

        pNevadaCRS84 = PolygonGenerator.fromBBox(-120.005746,	35.001857,	-114.039648,	42.002207, GeometryValue.Factory.GEOMETRY_FACTORY);
        pNevadaCRS84.setSRID(-4326);

        pMunichCRS84 = PolygonGenerator.fromBBox(11.387727, 48.036465, 11.698190, 48.218848, GeometryValue.Factory.GEOMETRY_FACTORY);
        pMunichCRS84.setSRID(-4326);

        pWorldCRS84 = PolygonGenerator.fromBBox(-180, -90, 180, 90, GeometryValue.Factory.GEOMETRY_FACTORY);
        pWorldCRS84.setSRID(-4326);

        // Washington Monument in Web Mercator (east/north)
        gWMSRID3857 = GeometryValue.Factory.GEOMETRY_FACTORY.createPoint(new Coordinate(-8575527.92007827, 4705847.723791289));
        gWMSRID3857.setSRID(3857);

        lDefault = GeometryValue.Factory.GEOMETRY_FACTORY.createLineString(new Coordinate[] {gWMCRS84.getCoordinate(), gMCRS84.getCoordinate()});
        lDefault.setSRID(-4326);

        lEquator = GeometryValue.Factory.GEOMETRY_FACTORY.createLineString(new Coordinate[] {new Coordinate(-180, 0), new Coordinate(180, 0)});
        lEquator.setSRID(-4326);

        lMeridian = GeometryValue.Factory.GEOMETRY_FACTORY.createLineString(new Coordinate[] {new Coordinate(0, -90), new Coordinate(0, 90)});
        lMeridian.setSRID(-4326);

        lNotSimple = GeometryValue.Factory.GEOMETRY_FACTORY.createLineString(new Coordinate[] {new Coordinate(-180, 0), new Coordinate(180, 0), new Coordinate(0, -90), new Coordinate(0, 90)});
        lNotSimple.setSRID(-4326);

        gEmpty = GeometryValue.Factory.GEOMETRY_FACTORY.createEmpty(0);
        gEmpty.setSRID(-4326);

        Point []pHomogeneous = {(Point) gWMCRS84, (Point) gMCRS84};
        gHomogeneousCollection = GeometryValue.Factory.GEOMETRY_FACTORY.createGeometryCollection(pHomogeneous);

        Geometry []pHeterogeneous = {(Point) gWMCRS84, (LineString)lDefault};
        gHeterogeneousCollection = GeometryValue.Factory.GEOMETRY_FACTORY.createGeometryCollection(pHeterogeneous);

    }
    public GeometryValueTest(Object geometry, Map<QName, String> otherXmlAttributes, XPathCompiler xPathCompiler, String comment, Object result, Boolean isValid) {
        this.value = geometry;
        this.otherXmlAttributes = otherXmlAttributes;
        this.xPathCompiler = xPathCompiler;
        this.comment = comment;
        this.result = result;
        this.isValid = isValid;
    }

    @Parameters
    public static Collection<Object[]> data() {
        Processor processor = new Processor(false);
        XPathCompiler xPathCompiler = processor.newXPathCompiler();

        JSONObject geojson = new JSONObject();
        geojson.putOpt("type", gWMCRS84.getGeometryType());
        geojson.putOpt("coordinates", new JSONArray(new double[] {gWMCRS84.getCoordinate().getX(), gWMCRS84.getCoordinate().getY()}));

        return Arrays.asList(
            // WKT encoding with default SRS (WGS84) and precision 1.0
            new Object[]{gWMCRS84.toString(), xmlAttributePrecision1, xPathCompiler, "WKT with using precision 1.0", gWMCRS84, true},

            // WKT encoding with default SRS (WGS84)
            new Object[]{gWMCRS84.toString(), null, xPathCompiler, "WKT with using default SRS", gWMCRS84, true},

            // WKT encoding with SRS in otherXMLAttributes: SRS
            new Object[]{gWMSRS4326.toString(), xmlAttributeSRS4326, xPathCompiler, "WKT using EPSG:4326 as attribute 'srs' in AttributeValue", gWMSRS4326, true},
            new Object[]{gWMCRS84.toString(), xmlAttributeWGS84, xPathCompiler, "WKT using WGS84 as attribute 'srs' in AttributeValue", gWMSRS4326, true},
            new Object[]{gWMCRS84.toString(), xmlAttributeCRS84, xPathCompiler, "WKT using CRS84 as attribute 'srs' in AttributeValue", gWMSRS4326, true},

            // WKT encoding with SRS in otherXMLAttributes: SRID
            new Object[]{gWMSRID4326.toString(), xmlAttributeSRID4326, xPathCompiler, "WKT using SRID as attribute 'srid' in AttributeValue", gWMSRID4326, true},

            // GeoJSON encoding with default SRS
            new Object[]{new SerializableJSONObject(geojson), null, xPathCompiler, "GeoJSON using WGS84", gWMCRS84, true},

            // Homogeneous collection is allowed
            new Object[]{gHomogeneousCollection.toString(), null, xPathCompiler, "Homogeneous Collection", gHomogeneousCollection, true},
            new Object[]{gHeterogeneousCollection.toString(), null, xPathCompiler, "Heterogeneous Collection", new IllegalArgumentException("GeometryCollection must be homogeneous"), false}
        );

    }

    @Test
    public void test() {
        LOGGER.info("Test Begin: " + comment);
        Boolean isValidResult = false;
        GeometryValue gv;

        try {
            if (this.value instanceof String)
                gv = GeometryValue.FACTORY.getInstance((String) this.value, this.otherXmlAttributes, Optional.empty());
            else
                gv = GeometryValue.FACTORY.getInstance((SerializableJSONObject) this.value, this.otherXmlAttributes, Optional.empty());

            LOGGER.debug("GeometryValue: " + gv);
            LOGGER.debug("Expected result: " + result);
            Geometry g = gv.getGeometry();
            if (result instanceof Geometry) {
                isValidResult = ((g.getSRID() == ((Geometry)result).getSRID()) && (g.equals(result)));
                Assert.assertEquals("Test failed on: '" + this.value + "' (" + this.comment + ")", isValid, isValidResult);
            }

            LOGGER.info("Test Success\n");
        }
        catch (AssertionError e) {
            LOGGER.error(e.getLocalizedMessage() + '\n');
        }
        catch (IllegalArgumentException e) {
            if (!isValid)
                LOGGER.info("Test Success\n");

            LOGGER.error(e.getLocalizedMessage() + '\n');
        } finally {

        }
    }

    private static class PolygonGenerator extends org.locationtech.jts.geom.Polygon {

        public PolygonGenerator(LinearRing shell, LinearRing[] holes, GeometryFactory factory) {
            super(shell, holes, factory);
        }

        public static Polygon fromBBox(double xmin, double ymin, double xmax, double ymax, GeometryFactory gf)
        {
            Coordinate c1 = new Coordinate(xmin, ymin);
            Coordinate c2 = new Coordinate(xmin, ymax);
            Coordinate c3 = new Coordinate(xmax, ymax);
            Coordinate c4 = new Coordinate(xmax, ymin);
            return gf.createPolygon(new Coordinate[] {c1, c2, c3, c4, c1});
        }
    }
}