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

import org.locationtech.jts.geom.Geometry;
import org.ow2.authzforce.core.pdp.api.ImmutableXacmlStatus;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.Optional;

public class TransformGeometry {

    public TransformGeometry()
    {
    }

    public boolean dynamicCRS(Geometry g1, Geometry g2) throws IndeterminateEvaluationException {

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

        // If we have EPSG:4326 and WGS84, we just need to swap axis. Just do it
        if (g1.getSRID() == (-1) * g2.getSRID()) {
            if (g1.getCoordinates().length <= g2.getCoordinates().length) {
                g1.apply(new SwapAxesCoordinateFilter());
                g1.setSRID(g2.getSRID());
                g1.geometryChanged();
            }
            else {
                g2.apply(new SwapAxesCoordinateFilter());
                g2.setSRID(g1.getSRID());
                g2.geometryChanged();
            }
            return true;
        }

        // Does the PEP prevented CRS transformation...
        Map<QName, String> otherXmlAttributesG1 = (Map<QName, String>) g1.getUserData();
        boolean allowTransformG1 = false;
        if ((otherXmlAttributesG1 != null) && (Boolean.getBoolean(otherXmlAttributesG1.get("allowTransformation"))))
            allowTransformG1 = true;

        // Does the PEP prevented CRS transformation...
        Map<QName, String> otherXmlAttributesG2 = (Map<QName, String>) g2.getUserData();
        boolean allowTransformG2 = false;
        if ((otherXmlAttributesG2 != null) && (Boolean.getBoolean(otherXmlAttributesG2.get("allowTransformation"))))
            allowTransformG2 = true;

        // We try to find the geometry to transform based on fewest coordinates first
        if ((g1.getCoordinates().length <= g2.getCoordinates().length) && allowTransformG1)
        {
            // g1 has fewer coordinates and we are allowed to transform it
        }
        else if ((g1.getCoordinates().length > g2.getCoordinates().length) && allowTransformG2)
        {
            // g2 has fewer coordinates and we are allowed to transform it
        }
        else if (allowTransformG1)
        {
            // g1 has more coordinates but we are allowed to transform
        }
        else if (allowTransformG2)
        {
            // g2 has more coordinates but we are allowed to transform
        }
        else
            throw new IndeterminateEvaluationException(
                    new ImmutableXacmlStatus("urn:ogc:def:function:geoxacml:3.0:crs-error", Optional.of("SRS transformation prohibited")));

        return false;
    }

}
