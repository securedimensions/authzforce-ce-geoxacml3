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

import com.google.common.collect.Maps;
import de.securedimensions.geoxacml3.function.AnalysisFunctions;
import de.securedimensions.geoxacml3.function.BagSetFunctions;
import de.securedimensions.geoxacml3.function.CoreFunctions;
import de.securedimensions.geoxacml3.function.TopologicalFunctions;
import de.securedimensions.geoxacml3.identifiers.Definitions;
import org.ow2.authzforce.core.pdp.api.HashCollections;
import org.ow2.authzforce.core.pdp.api.PdpExtensionRegistry;
import org.ow2.authzforce.core.pdp.api.func.Function;
import org.ow2.authzforce.core.pdp.api.func.GenericHigherOrderFunctionFactory;
import org.ow2.authzforce.core.pdp.api.value.IntegerValue;
import org.ow2.authzforce.core.pdp.api.value.StringParseableValue;
import org.ow2.authzforce.core.pdp.impl.func.FunctionRegistry;
import org.ow2.authzforce.core.pdp.impl.func.ImmutableFunctionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public enum GeometryFunction {
    FUNCTION_PREFIX(Definitions.FUNCTION_PREFIX);

    private static final Logger LOGGER = LoggerFactory.getLogger(GeometryFunction.class);
    private static final PdpExtensionRegistry.PdpExtensionComparator<Function<?>> FUNCTION_COMPARATOR = new PdpExtensionRegistry.PdpExtensionComparator();
    private static final PdpExtensionRegistry.PdpExtensionComparator<GenericHigherOrderFunctionFactory> FUNCTION_FACTORY_COMPARATOR = new PdpExtensionRegistry.PdpExtensionComparator();
    private static final Map<String, GeometryFunction> ID_TO_STD_FUNC_MAP = Maps.uniqueIndex(Arrays.asList(values()), (input) -> {
        assert input != null;

        return input.getId();
    });
    private final String id;

    GeometryFunction(String id) {
        this.id = id;
    }

    public static GeometryFunction getInstance(String functionId) {
        return ID_TO_STD_FUNC_MAP.get(functionId);
    }

    public static FunctionRegistry getRegistry(boolean enableXPath, StringParseableValue.Factory<IntegerValue> stdIntValueFactory) {
        Set geometryFunctions = HashCollections.newUpdatableSet();

        /* Conformance Class Core */
        // Testing Topological Relations
        geometryFunctions.add(new TopologicalFunctions.Equals());
        geometryFunctions.add(new TopologicalFunctions.Disjoint());
        geometryFunctions.add(new TopologicalFunctions.Touches());
        geometryFunctions.add(new TopologicalFunctions.Crosses());
        geometryFunctions.add(new TopologicalFunctions.Within());
        geometryFunctions.add(new TopologicalFunctions.Contains());
        geometryFunctions.add(new TopologicalFunctions.Overlaps());
        geometryFunctions.add(new TopologicalFunctions.Intersects());

        /* Conformance Class Core */
        // Core Functions
        geometryFunctions.add(new CoreFunctions.Length());
        geometryFunctions.add(new CoreFunctions.Area());
        geometryFunctions.add(new CoreFunctions.Distance());
        geometryFunctions.add(new CoreFunctions.IsWithinDistance());
        geometryFunctions.add(new CoreFunctions.DistanceEquals());
        geometryFunctions.add(new CoreFunctions.Relate());
        geometryFunctions.add(new CoreFunctions.Dimension());
        geometryFunctions.add(new CoreFunctions.GeometryType());
        geometryFunctions.add(new CoreFunctions.SRID());
        geometryFunctions.add(new CoreFunctions.IsSimple());
        geometryFunctions.add(new CoreFunctions.IsEmpty());
        geometryFunctions.add(new CoreFunctions.SRIDEquals());
        geometryFunctions.add(new CoreFunctions.EnsureSRID());
        geometryFunctions.add(new CoreFunctions.EnsurePrecision());
        geometryFunctions.add(new CoreFunctions.HasPrecision());
        geometryFunctions.add(new CoreFunctions.Precision());
        // Constructive Functions supporting GeometryCollection
        geometryFunctions.add(new CoreFunctions.GeometryBagFromCollection());
        geometryFunctions.add(new CoreFunctions.GeometryBagToHomogeneousCollection());

        /* Conformance Class Core */
        // Bag Set Functions
        geometryFunctions.add(new BagSetFunctions.BagContains());
        geometryFunctions.add(new BagSetFunctions.BagSize());
        geometryFunctions.add(new BagSetFunctions.AtLeastOneMemberOf());
        geometryFunctions.add(new BagSetFunctions.Intersection());
        geometryFunctions.add(new BagSetFunctions.Union());
        geometryFunctions.add(new BagSetFunctions.Subset());
        geometryFunctions.add(new BagSetFunctions.SetEquals());
        geometryFunctions.add(new BagSetFunctions.SingletonBagToPrimitive());
        geometryFunctions.add(new BagSetFunctions.GeometryBag());

        /* Conformance Class Analysis */
        // Advanced Functions
        geometryFunctions.add(new AnalysisFunctions.Envelope());
        geometryFunctions.add(new AnalysisFunctions.Boundary());
        geometryFunctions.add(new AnalysisFunctions.Buffer());
        geometryFunctions.add(new AnalysisFunctions.ConvexHull());
        geometryFunctions.add(new AnalysisFunctions.Centroid());
        geometryFunctions.add(new AnalysisFunctions.GeometryIntersection());
        geometryFunctions.add(new AnalysisFunctions.GeometryUnion());
        geometryFunctions.add(new AnalysisFunctions.GeometryDifference());
        geometryFunctions.add(new AnalysisFunctions.GeometrySymDifference());

        return new ImmutableFunctionRegistry(geometryFunctions, null);

    }

    public String getId() {
        return this.id;
    }
}

