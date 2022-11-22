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

import de.securedimensions.geoxacml3.crs.TransformGeometry;
import de.securedimensions.geoxacml3.datatype.GeometryValue;
import org.locationtech.jts.geom.Geometry;
import org.ow2.authzforce.core.pdp.api.ImmutableXacmlStatus;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;
import org.ow2.authzforce.core.pdp.api.expression.Expression;
import org.ow2.authzforce.core.pdp.api.func.BaseFirstOrderFunctionCall;
import org.ow2.authzforce.core.pdp.api.func.FirstOrderFunctionCall;
import org.ow2.authzforce.core.pdp.api.func.SingleParameterTypedFirstOrderFunction;
import org.ow2.authzforce.core.pdp.api.value.BooleanValue;
import org.ow2.authzforce.core.pdp.api.value.Datatype;
import org.ow2.authzforce.core.pdp.api.value.StandardDatatypes;
import org.ow2.authzforce.xacml.identifiers.XacmlStatusCode;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public final class TopologicalFunctions {

    /**
     * required by XACML3 "type-equal" (not equal*s* as defined in Simple Features)
     */
    public static final class Equals extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-equals";

        public Equals() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    Geometry g1 = args.poll().getGeometry();
                    Geometry g2 = args.poll().getGeometry();

                    if (g1.getSRID() != g2.getSRID()) {
                        TransformGeometry tg = new TransformGeometry();
                        if (!tg.transformCRS(g1, g2)) {
                            throw new IndeterminateEvaluationException(
                                    new ImmutableXacmlStatus("urn:ogc:def:function:geoxacml:3.0:crs-error", Optional.of("Function " + ID + " expects same SRS for both geometry parameters")));
                        }
                    }

                    return new BooleanValue(g1.equals(g2));
                }

            };
        }

    }

    public final static class Equal extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-equal";

        public Equal() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    Geometry g1 = args.poll().getGeometry();
                    Geometry g2 = args.poll().getGeometry();

                    if (g1.getSRID() != g2.getSRID()) {
                        TransformGeometry tg = new TransformGeometry();
                        if (!tg.transformCRS(g1, g2)) {
                            throw new IndeterminateEvaluationException(
                                    new ImmutableXacmlStatus("urn:ogc:def:function:geoxacml:3.0:crs-error", Optional.of("Function " + ID + " expects same SRS for both geometry parameters")));
                        }
                    }

                    return new BooleanValue(g1.equals(g2));
                }

            };
        }
    }

    public final static class Disjoint extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-disjoint";

        public Disjoint() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    Geometry g1 = args.poll().getGeometry();
                    Geometry g2 = args.poll().getGeometry();

                    if (g1.getSRID() != g2.getSRID()) {
                        TransformGeometry tg = new TransformGeometry();
                        if (!tg.transformCRS(g1, g2)) {
                            throw new IndeterminateEvaluationException(
                                    new ImmutableXacmlStatus("urn:ogc:def:function:geoxacml:3.0:crs-error", Optional.of("Function " + ID + " expects same SRS for both geometry parameters")));
                        }
                    }

                    return new BooleanValue(g1.disjoint(g2));
                }

            };
        }
    }

    public final static class Touches extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-touches";

        public Touches() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    Geometry g1 = args.poll().getGeometry();
                    Geometry g2 = args.poll().getGeometry();

                    if (g1.getSRID() != g2.getSRID()) {
                        TransformGeometry tg = new TransformGeometry();
                        if (!tg.transformCRS(g1, g2)) {
                            throw new IndeterminateEvaluationException(
                                    new ImmutableXacmlStatus("urn:ogc:def:function:geoxacml:3.0:crs-error", Optional.of("Function " + ID + " expects same SRS for both geometry parameters")));
                        }
                    }

                    return new BooleanValue(g1.touches(g2));
                }

            };
        }
    }

    public final static class Crosses extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-crosses";

        public Crosses() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    Geometry g1 = args.poll().getGeometry();
                    Geometry g2 = args.poll().getGeometry();

                    if (g1.getSRID() != g2.getSRID()) {
                        TransformGeometry tg = new TransformGeometry();
                        if (!tg.transformCRS(g1, g2)) {
                            throw new IndeterminateEvaluationException(
                                    new ImmutableXacmlStatus("urn:ogc:def:function:geoxacml:3.0:crs-error", Optional.of("Function " + ID + " expects same SRS for both geometry parameters")));
                        }
                    }

                    return new BooleanValue(g1.crosses(g2));
                }

            };
        }
    }

    public final static class Within extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-within";

        public Within() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    Geometry g1 = args.poll().getGeometry();
                    Geometry g2 = args.poll().getGeometry();

                    if (g1.getSRID() != g2.getSRID()) {
                        TransformGeometry tg = new TransformGeometry();
                        if (!tg.transformCRS(g1, g2)) {
                            throw new IndeterminateEvaluationException(
                                    new ImmutableXacmlStatus("urn:ogc:def:function:geoxacml:3.0:crs-error", Optional.of("Function " + ID + " expects same SRS for both geometry parameters")));
                        }
                    }

                    return new BooleanValue(g1.within(g2));
                }

            };
        }
    }

    public final static class Contains extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-contains";

        public Contains() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    Geometry g1 = args.poll().getGeometry();
                    Geometry g2 = args.poll().getGeometry();

                    if (g1.getSRID() != g2.getSRID()) {
                        TransformGeometry tg = new TransformGeometry();
                        if (!tg.transformCRS(g1, g2)) {
                            throw new IndeterminateEvaluationException(
                                    new ImmutableXacmlStatus("urn:ogc:def:function:geoxacml:3.0:crs-error", Optional.of("Function " + ID + " expects same SRS for both geometry parameters")));
                        }
                    }

                    return new BooleanValue(g1.contains(g2));
                }

            };
        }
    }

    public final static class Overlaps extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-overlaps";

        public Overlaps() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    Geometry g1 = args.poll().getGeometry();
                    Geometry g2 = args.poll().getGeometry();

                    if (g1.getSRID() != g2.getSRID()) {
                        TransformGeometry tg = new TransformGeometry();
                        if (!tg.transformCRS(g1, g2)) {
                            throw new IndeterminateEvaluationException(
                                    new ImmutableXacmlStatus("urn:ogc:def:function:geoxacml:3.0:crs-error", Optional.of("Function " + ID + " expects same SRS for both geometry parameters")));
                        }
                    }

                    return new BooleanValue(g1.overlaps(g2));
                }

            };
        }
    }

    public final static class Intersects extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-intersects";

        public Intersects() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    Geometry g1 = args.poll().getGeometry();
                    Geometry g2 = args.poll().getGeometry();

                    if (g1.getSRID() != g2.getSRID()) {
                        TransformGeometry tg = new TransformGeometry();
                        if (!tg.transformCRS(g1, g2)) {
                            throw new IndeterminateEvaluationException(
                                    new ImmutableXacmlStatus("urn:ogc:def:function:geoxacml:3.0:crs-error", Optional.of("Function " + ID + " expects same SRS for both geometry parameters")));
                        }
                    }

                    return new BooleanValue(g1.intersects(g2));
                }

            };
        }
    }


}