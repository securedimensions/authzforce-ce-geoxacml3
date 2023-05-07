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
package de.securedimensions.geoxacml3.function;

import de.securedimensions.geoxacml3.datatype.GeometryValue;
import de.securedimensions.geoxacml3.identifiers.Definitions;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;
import org.ow2.authzforce.core.pdp.api.expression.Expression;
import org.ow2.authzforce.core.pdp.api.func.BaseFirstOrderFunctionCall;
import org.ow2.authzforce.core.pdp.api.func.FirstOrderFunctionCall;
import org.ow2.authzforce.core.pdp.api.func.MultiParameterTypedFirstOrderFunction;
import org.ow2.authzforce.core.pdp.api.func.SingleParameterTypedFirstOrderFunction;
import org.ow2.authzforce.core.pdp.api.value.AttributeValue;
import org.ow2.authzforce.core.pdp.api.value.Datatype;
import org.ow2.authzforce.core.pdp.api.value.DoubleValue;
import org.ow2.authzforce.core.pdp.api.value.StandardDatatypes;
import org.ow2.authzforce.xacml.identifiers.XacmlStatusCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class AnalysisFunctions {

    public final static class Envelope extends SingleParameterTypedFirstOrderFunction<GeometryValue, GeometryValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-envelope";

        public Envelope() {
            super(ID, GeometryValue.DATATYPE, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<GeometryValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected GeometryValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    Geometry g = args.poll().getGeometry();
                    Geometry e = g.getEnvelope();
                    e.setSRID(g.getSRID());
                    return new GeometryValue(e);
                }

            };
        }
    }

    public final static class Boundary extends SingleParameterTypedFirstOrderFunction<GeometryValue, GeometryValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-boundary";

        public Boundary() {
            super(ID, GeometryValue.DATATYPE, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<GeometryValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected GeometryValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    Geometry g = args.poll().getGeometry();
                    Geometry b = g.getBoundary();
                    b.setSRID(g.getSRID());
                    return new GeometryValue(b);
                }

            };
        }
    }

    public final static class Buffer extends MultiParameterTypedFirstOrderFunction<GeometryValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-buffer";

        public Buffer() {
            super(ID, GeometryValue.DATATYPE, true, Arrays.asList(GeometryValue.DATATYPE, StandardDatatypes.DOUBLE));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected GeometryValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                    final Geometry g0 = ((GeometryValue) args.poll()).getGeometry();

                    final Double d = ((DoubleValue) args.poll()).getUnderlyingValue();
                    Geometry g = g0.buffer(d);
                    g.setSRID(g0.getSRID());
                    return new GeometryValue(g);
                }
            };
        }
    }

    public final static class ConvexHull extends SingleParameterTypedFirstOrderFunction<GeometryValue, GeometryValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-convex-hull";

        public ConvexHull() {
            super(ID, GeometryValue.DATATYPE, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<GeometryValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected GeometryValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    final Geometry g0 = args.poll().getGeometry();
                    final Geometry g = g0.convexHull();
                    g.setSRID(g0.getSRID());
                    if (g instanceof GeometryCollection) {
                        int n = g.getNumGeometries();
                        List<GeometryValue> gvu = new ArrayList<GeometryValue>();
                        for (int ix = 0; ix < n; ix++) {
                            // test for homogeneous
                            // throw IllegalArgumentException if not homogeneous
                        }
                    }
                    return new GeometryValue(g);
                }

            };
        }
    }

    public final static class Centroid extends SingleParameterTypedFirstOrderFunction<GeometryValue, GeometryValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-centroid";

        public Centroid() {
            super(ID, GeometryValue.DATATYPE, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<GeometryValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected GeometryValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    final Geometry g0 = args.poll().getGeometry();
                    final Geometry g = g0.getCentroid();
                    g.setSRID(g0.getSRID());
                    return new GeometryValue(g);
                }

            };
        }
    }

    public final static class GeometryIntersection extends SingleParameterTypedFirstOrderFunction<GeometryValue, GeometryValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-geometry-intersection";

        public GeometryIntersection() {
            super(ID, GeometryValue.DATATYPE, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<GeometryValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected GeometryValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    Geometry g1 = args.poll().getGeometry();
                    Geometry g2 = args.poll().getGeometry();
                    UtilityFunctions uf = new UtilityFunctions();
                    uf.ensurePrecision(g1, g2);
                    uf.ensureCRS(g1, g2);

                    Geometry g = g1.intersection(g2);
                    g.setSRID(g1.getSRID());
                    return new GeometryValue(g);
                }

            };
        }
    }

    public final static class GeometryUnion extends SingleParameterTypedFirstOrderFunction<GeometryValue, GeometryValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-geometry-union";

        public GeometryUnion() {
            super(ID, GeometryValue.DATATYPE, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<GeometryValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected GeometryValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    Geometry g1 = args.poll().getGeometry();
                    Geometry g2 = args.poll().getGeometry();
                    UtilityFunctions uf = new UtilityFunctions();
                    uf.ensurePrecision(g1, g2);
                    uf.ensureCRS(g1, g2);

                    Geometry g = g1.union(g2);
                    g.setSRID(g1.getSRID());
                    return new GeometryValue(g);
                }

            };
        }
    }

    public final static class GeometryDifference extends SingleParameterTypedFirstOrderFunction<GeometryValue, GeometryValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-geometry-difference";

        public GeometryDifference() {
            super(ID, GeometryValue.DATATYPE, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<GeometryValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected GeometryValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    Geometry g1 = args.poll().getGeometry();
                    Geometry g2 = args.poll().getGeometry();
                    UtilityFunctions uf = new UtilityFunctions();
                    uf.ensurePrecision(g1, g2);
                    uf.ensureCRS(g1, g2);

                    Geometry g = g1.difference(g2);
                    g.setSRID(g1.getSRID());
                    return new GeometryValue(g);
                }

            };
        }
    }

    public final static class GeometrySymDifference extends SingleParameterTypedFirstOrderFunction<GeometryValue, GeometryValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-geometry-sym-difference";

        public GeometrySymDifference() {
            super(ID, GeometryValue.DATATYPE, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<GeometryValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected GeometryValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    Geometry g1 = args.poll().getGeometry();
                    Geometry g2 = args.poll().getGeometry();
                    UtilityFunctions uf = new UtilityFunctions();
                    uf.ensurePrecision(g1, g2);
                    uf.ensureCRS(g1, g2);

                    Geometry g = g1.symDifference(g2);
                    g.setSRID(g1.getSRID());
                    return new GeometryValue(g);
                }

            };
        }
    }

}
