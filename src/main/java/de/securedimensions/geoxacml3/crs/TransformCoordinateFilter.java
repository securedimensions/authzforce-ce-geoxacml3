/**
 * Copyright 2019-2022 Secure Dimensions GmbH.
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

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateFilter;
import org.locationtech.proj4j.*;

public class TransformCoordinateFilter implements CoordinateFilter {
    private static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
    private static final CRSFactory crsFactory = new CRSFactory();
    private final ProjCoordinate fromCoordinate;
    private final ProjCoordinate toCoordinate;
    private CoordinateTransform trans;
    private boolean swapAxis;

    //private static CRSMetadata crsMetadata = CRSMetadata.getInstance();

    private TransformCoordinateFilter() {
        fromCoordinate = new ProjCoordinate();
        toCoordinate = new ProjCoordinate();
    }

    public TransformCoordinateFilter(CoordinateReferenceSystem fromCRS, CoordinateReferenceSystem toCRS, boolean swapAxis) {
        this();
        this.swapAxis = swapAxis;

        trans = ctFactory.createTransform(fromCRS, toCRS);
    }

    public TransformCoordinateFilter(int fromSRID, int toSRID, boolean swapAxis) {
        // abs(SRID) ensures that -4326 is mapped to 4326
        this(crsFactory.createFromName("EPSG:" + Math.abs(fromSRID)), crsFactory.createFromName("EPSG:" + Math.abs(toSRID)), swapAxis);

    }

    public void filter(Coordinate coord) {

        if (swapAxis) {
            fromCoordinate.y = coord.x;
            fromCoordinate.x = coord.y;
        } else {
            fromCoordinate.x = coord.x;
            fromCoordinate.y = coord.y;
        }

        trans.transform(fromCoordinate, toCoordinate);

        coord.x = toCoordinate.x;
        coord.y = toCoordinate.y;
    }

    public boolean isDone() {
        return true;
    }

    public boolean isGeometryChanged() {
        return true;
    }

}