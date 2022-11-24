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
import net.sf.saxon.value.BooleanValue;
import org.locationtech.jts.geom.Geometry;
import org.ow2.authzforce.core.pdp.api.ImmutableXacmlStatus;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.Optional;

public class TransformGeometry {

    public TransformGeometry() {
    }

    public boolean transformCRS(Geometry g, int toSRID) {
        Map<QName, String> otherXmlAttributes = (Map<QName, String>) g.getUserData();
        boolean allowTransformG = (otherXmlAttributes != null) ? Boolean.valueOf(otherXmlAttributes.get(GeometryValue.xmlAllowTransformation)) : false;

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

        Map<QName, String> otherXmlAttributesG1 = (Map<QName, String>) g1.getUserData();
        boolean allowTransformG1 = (otherXmlAttributesG1 != null) ? Boolean.valueOf(otherXmlAttributesG1.get(GeometryValue.xmlAllowTransformation)) : false;

        Map<QName, String> otherXmlAttributesG2 = (Map<QName, String>) g2.getUserData();
        boolean allowTransformG2 = (otherXmlAttributesG2 != null) ? Boolean.valueOf(otherXmlAttributesG2.get(GeometryValue.xmlAllowTransformation)) : false;

        if (!allowTransformG1 && !allowTransformG2)
            throw new IndeterminateEvaluationException(
                    new ImmutableXacmlStatus("urn:ogc:def:function:geoxacml:3.0:crs-error", Optional.of("SRS transformation prohibited")));


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
