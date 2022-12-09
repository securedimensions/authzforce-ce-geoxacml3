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
package de.securedimensions.geoxacml3.crs;

import de.securedimensions.geoxacml3.datatype.GeometryValue;
import de.securedimensions.geoxacml3.identifiers.Definitions;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MissingAttributeDetail;
import org.locationtech.jts.geom.Geometry;
import org.ow2.authzforce.core.pdp.api.ImmutableXacmlStatus;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;

import javax.xml.namespace.QName;
import java.util.*;

import static de.securedimensions.geoxacml3.identifiers.Definitions.CRS_ERROR;
import static de.securedimensions.geoxacml3.pdp.io.GeoXACMLRequestPreprocessor.XACML_ATTRIBUTE_ID_QNAME;
import static de.securedimensions.geoxacml3.pdp.io.GeoXACMLRequestPreprocessor.XACML_CATEGORY_ID_QNAME;
import static org.ow2.authzforce.xacml.identifiers.XacmlStatusCode.SYNTAX_ERROR;

public class TransformGeometry {

    public TransformGeometry() {
    }

    public boolean transformCRS(Geometry g, int toSRID) throws IndeterminateEvaluationException {
        return transformCRS(g, toSRID, false);
    }

    public boolean transformCRS(Geometry g, int toSRID, boolean raiseException) throws IndeterminateEvaluationException {
        Map<QName, String> otherXmlAttributes = (g.getUserData() == null) ? new HashMap<QName, String>() : (Map<QName, String>) g.getUserData();

        boolean allowTransformG = Boolean.valueOf(otherXmlAttributes.getOrDefault(Definitions.ATTR_ALLOW_TRANSFORMATION, Boolean.FALSE.toString()));

        // just swapping axis for EPSG:4326 and WGS84 does not require to check 'allowTransformation'
        if (g.getSRID() == (-1) * toSRID) {
            g.apply(new SwapAxesCoordinateFilter());
            g.setSRID(toSRID);
            g.geometryChanged();
            return true;
        }

        if (allowTransformG) {
            g.apply(new TransformCoordinateFilter(g.getSRID(), Math.abs(toSRID), (g.getSRID() == 4326)));
            g.setSRID(toSRID);
            g.geometryChanged();
            // If the target CRS is 4326, we need to swap axis
            if (toSRID == 4326) {
                g.apply(new SwapAxesCoordinateFilter());
                g.setSRID(toSRID);
                g.geometryChanged();
            }
            return true;
        }

        if (raiseException) {
            if (otherXmlAttributes.containsKey(Definitions.ATTR_ALLOW_TRANSFORMATION))
                otherXmlAttributes.replace(Definitions.ATTR_ALLOW_TRANSFORMATION, Boolean.TRUE.toString());
            else
                otherXmlAttributes.put(Definitions.ATTR_ALLOW_TRANSFORMATION, Boolean.TRUE.toString());

            final AttributeValueType av = new AttributeValueType(List.of(""), GeometryValue.DATATYPE.getId(), otherXmlAttributes);
            final MissingAttributeDetail missingAttributeDetail = new MissingAttributeDetail(List.of(av),
                    otherXmlAttributes.get(XACML_CATEGORY_ID_QNAME),
                    otherXmlAttributes.get(XACML_ATTRIBUTE_ID_QNAME),
                    GeometryValue.DATATYPE.getId(),
                    null);
            throw new IndeterminateEvaluationException("CRS transformation prohibited by 'allowTransformation'", missingAttributeDetail, Optional.of(CRS_ERROR));
        }
        else
            return false;
    }

    public boolean transformCRS(Geometry g1, Geometry g2) throws IndeterminateEvaluationException {

        /*
         * GEOMETRY AXIS ORDER CONFUSION
         *
         * In order to deal with the axis order confusion for code 4326 and be able to compare geometries
         * independent of encoding, all geometry encodings using EPSG:4326 assume LAT/LON using EASTING or NORTHING
         * So for example for a geometry encoded with
         *  - 'EPSG:4326' the axis order is LAT/LON
         *  - 'urn:ogc:def:crs:OGC::CRS84' the axis order is (LON/LAT) - default for GeoXACML 3 (from GeoJSON)
         *  - '' (southing) will have the LAT value inverted
         *  - '' (westing) will have the LON value inverted
         * This implementation uses SRID=-4326 to represent urn:ogc:def:crs:OGC::CRS84 and
         * SRID=4326 to represent EPSG:4326
         * A transformation from LAT/LON to LON/LAT between -4326 and 4326 (and vice versa) is simple
         */

        // just swapping axis for EPSG:4326 and WGS84 does not require to check 'allowTransformation'
        if (g1.getSRID() == (-1) * g2.getSRID()) {
            if (g1.getCoordinates().length <= g2.getCoordinates().length) {
                g1.apply(new SwapAxesCoordinateFilter());
                g1.setSRID(g2.getSRID());
                g1.geometryChanged();
            } else {
                g2.apply(new SwapAxesCoordinateFilter());
                g2.setSRID(g1.getSRID());
                g2.geometryChanged();
            }
            return true;
        }

        Map<QName, String> otherXmlAttributesG1 = (g1.getUserData() == null) ? new HashMap<QName, String>() : (Map<QName, String>) g1.getUserData();
        boolean allowTransformG1 = Boolean.valueOf(otherXmlAttributesG1.getOrDefault(Definitions.ATTR_ALLOW_TRANSFORMATION, Boolean.FALSE.toString()));

        Map<QName, String> otherXmlAttributesG2 = (g2.getUserData() == null) ? new HashMap<QName, String>() : (Map<QName, String>) g2.getUserData();
        boolean allowTransformG2 = Boolean.valueOf(otherXmlAttributesG2.getOrDefault(Definitions.ATTR_ALLOW_TRANSFORMATION, Boolean.FALSE.toString()));

        if (!allowTransformG1 && !allowTransformG2) {
            // In case both geometries come from Policy, we have a defect in the Policy and return syntax-error
            final String originG1 = otherXmlAttributesG1.getOrDefault(Definitions.ATTR_SOURCE, Definitions.ATTR_SOURCE_POLICY);
            final String originG2 = otherXmlAttributesG2.getOrDefault(Definitions.ATTR_SOURCE, Definitions.ATTR_SOURCE_POLICY);
            if (originG1.equalsIgnoreCase(Definitions.ATTR_SOURCE_POLICY) && originG2.equalsIgnoreCase(Definitions.ATTR_SOURCE_POLICY))
                throw new IndeterminateEvaluationException(
                        new ImmutableXacmlStatus("SRS transformation prohibited", Optional.of(SYNTAX_ERROR.name())));

            // In case that one geometry is from the ADR and the other from the policy, we can send MissingAttributeDetail in the StatusDetail
            // indicating which SRS (or SRID) to use.
            if (!originG1.equalsIgnoreCase(Definitions.ATTR_SOURCE_POLICY))
            {
                // geometry 1 is contained in the ADR
                // to indicate the CRS to be used, geometry 1 gets the CRS from geometry 2
                if (otherXmlAttributesG1.containsKey(Definitions.xmlSRID)) {
                    otherXmlAttributesG1.replace(Definitions.xmlSRID, otherXmlAttributesG2.get(Definitions.xmlSRID));
                    otherXmlAttributesG1.remove(Definitions.xmlCRS);
                }
                else if (otherXmlAttributesG1.containsKey(Definitions.xmlCRS)) {
                    otherXmlAttributesG1.replace(Definitions.xmlCRS, otherXmlAttributesG2.get(Definitions.xmlCRS));
                    otherXmlAttributesG1.remove(Definitions.xmlSRID);
                }
                else {
                    otherXmlAttributesG1.put(Definitions.xmlCRS, "EPSG:" + g2.getSRID());
                }
                final AttributeValueType av = new AttributeValueType(List.of(""), GeometryValue.DATATYPE.getId(), otherXmlAttributesG1);
                final MissingAttributeDetail missingAttributeDetail = new MissingAttributeDetail(List.of(av),
                        otherXmlAttributesG1.get(XACML_CATEGORY_ID_QNAME),
                        otherXmlAttributesG1.get(XACML_ATTRIBUTE_ID_QNAME),
                        GeometryValue.DATATYPE.getId(),
                        null);
                throw new IndeterminateEvaluationException("Geometry must be encoded using specified CRS", missingAttributeDetail, Optional.of(CRS_ERROR));
            }

            if (!originG2.equalsIgnoreCase(Definitions.ATTR_SOURCE_POLICY))
            {
                // geometry 2 is contained in the ADR
                // to indicate the CRS to be used, geometry 2 gets the CRS from geometry 1
                if (otherXmlAttributesG2.containsKey(Definitions.xmlSRID)) {
                    otherXmlAttributesG2.replace(Definitions.xmlSRID, otherXmlAttributesG1.get(Definitions.xmlSRID));
                    otherXmlAttributesG2.remove(Definitions.xmlCRS);
                }
                else if (otherXmlAttributesG2.containsKey(Definitions.xmlCRS)) {
                    otherXmlAttributesG2.replace(Definitions.xmlCRS, otherXmlAttributesG1.get(Definitions.xmlCRS));
                    otherXmlAttributesG2.remove(Definitions.xmlSRID);
                }
                else {
                    otherXmlAttributesG2.put(Definitions.xmlCRS, "EPSG:" + g1.getSRID());
                }
                final AttributeValueType av = new AttributeValueType(List.of(""), GeometryValue.DATATYPE.getId(), otherXmlAttributesG2);
                final MissingAttributeDetail missingAttributeDetail = new MissingAttributeDetail(List.of(av),
                        otherXmlAttributesG2.get(XACML_CATEGORY_ID_QNAME),
                        otherXmlAttributesG2.get(XACML_ATTRIBUTE_ID_QNAME),
                        GeometryValue.DATATYPE.getId(),
                        null);
                throw new IndeterminateEvaluationException("Geometry must be encoded using specified CRS", missingAttributeDetail, Optional.of(CRS_ERROR));
            }

        }

        // We try to find the geometry to transform based on fewest coordinates first
        if ((g1.getCoordinates().length <= g2.getCoordinates().length) && allowTransformG1) {
            // g1 has fewer coordinates and we are allowed to transform it
            g1.apply(new TransformCoordinateFilter(g1.getSRID(), g2.getSRID(), g1.getSRID() == 4326));
            g1.setSRID(g2.getSRID());
            g1.geometryChanged();
        } else if ((g1.getCoordinates().length > g2.getCoordinates().length) && allowTransformG2) {
            // g2 has fewer coordinates and we are allowed to transform it
            g2.apply(new TransformCoordinateFilter(g2.getSRID(), g1.getSRID(), g2.getSRID() == 4326));
            g2.setSRID(g1.getSRID());
            g2.geometryChanged();
        } else if (allowTransformG1) {
            // g1 has more coordinates but we are allowed to transform
            g1.apply(new TransformCoordinateFilter(g1.getSRID(), g2.getSRID(), g1.getSRID() == 4326));
            g1.setSRID(g2.getSRID());
            g1.geometryChanged();
        } else {
            // g2 has more coordinates but we are allowed to transform
            g2.apply(new TransformCoordinateFilter(g2.getSRID(), g1.getSRID(), g2.getSRID() == 4326));
            g2.setSRID(g1.getSRID());
            g2.geometryChanged();
        }

        return true;
    }

}
