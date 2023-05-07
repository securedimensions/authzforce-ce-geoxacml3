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
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;
import org.ow2.authzforce.core.pdp.api.expression.Expression;
import org.ow2.authzforce.core.pdp.api.func.BaseFirstOrderFunctionCall;
import org.ow2.authzforce.core.pdp.api.func.FirstOrderFunctionCall;
import org.ow2.authzforce.core.pdp.api.func.SingleParameterTypedFirstOrderFunction;
import org.ow2.authzforce.core.pdp.api.value.BooleanValue;
import org.ow2.authzforce.core.pdp.api.value.Datatype;
import org.ow2.authzforce.core.pdp.api.value.StandardDatatypes;
import org.ow2.authzforce.xacml.identifiers.XacmlStatusCode;

import java.util.Deque;
import java.util.List;

public final class TopologicalFunctions {

    /**
     * required by XACML3 "type-equal" (not equal*s* as defined in Simple Features)
     */
    public final static class Equal extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String EQUAL_SUFFIX = "-equal";
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + EQUAL_SUFFIX;

        public Equal() {
            super(ID, StandardDatatypes.BOOLEAN, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    UtilityFunctions uf = new UtilityFunctions();
                    return new BooleanValue(uf.compare(args.poll(), args.poll(), EQUAL_SUFFIX));
                }

            };
        }
    }

    public static final class Equals extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String EQUALS_SUFFIX = "-equals";
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + EQUALS_SUFFIX;

        public Equals() {
            super(ID, StandardDatatypes.BOOLEAN, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    UtilityFunctions uf = new UtilityFunctions();
                    return new BooleanValue(uf.compare(args.poll(), args.poll(), EQUALS_SUFFIX));
                }

            };
        }

    }

    public final static class Disjoint extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String DISJOINT_SUFFIX = "-disjoint";
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + DISJOINT_SUFFIX;

        public Disjoint() {
            super(ID, StandardDatatypes.BOOLEAN, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    UtilityFunctions uf = new UtilityFunctions();
                    return new BooleanValue(uf.compare(args.poll(), args.poll(), DISJOINT_SUFFIX));
                }

            };
        }
    }

    public final static class Touches extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String TOUCHES_SUFFIX = "-touches";
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + TOUCHES_SUFFIX;

        public Touches() {
            super(ID, StandardDatatypes.BOOLEAN, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    UtilityFunctions uf = new UtilityFunctions();
                    return new BooleanValue(uf.compare(args.poll(), args.poll(), TOUCHES_SUFFIX));
                }

            };
        }
    }

    public final static class Crosses extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String CROSSES_SUFFIX = "-crosses";
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + CROSSES_SUFFIX;

        public Crosses() {
            super(ID, StandardDatatypes.BOOLEAN, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    UtilityFunctions uf = new UtilityFunctions();
                    return new BooleanValue(uf.compare(args.poll(), args.poll(), CROSSES_SUFFIX));
                }

            };
        }
    }

    public final static class Within extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String WITHIN_SUFFIX = "-within";
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + WITHIN_SUFFIX;

        public Within() {
            super(ID, StandardDatatypes.BOOLEAN, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    UtilityFunctions uf = new UtilityFunctions();
                    return new BooleanValue(uf.compare(args.poll(), args.poll(), WITHIN_SUFFIX));
                }

            };
        }
    }

    public final static class Contains extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String CONTAINS_SUFFIX = "-contains";
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + CONTAINS_SUFFIX;

        public Contains() {
            super(ID, StandardDatatypes.BOOLEAN, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    UtilityFunctions uf = new UtilityFunctions();
                    return new BooleanValue(uf.compare(args.poll(), args.poll(), CONTAINS_SUFFIX));
                }

            };
        }
    }

    public final static class Overlaps extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String OVERLAPS_SUFFIX = "-overlaps";
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + OVERLAPS_SUFFIX;

        public Overlaps() {
            super(ID, StandardDatatypes.BOOLEAN, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    UtilityFunctions uf = new UtilityFunctions();
                    return new BooleanValue(uf.compare(args.poll(), args.poll(), OVERLAPS_SUFFIX));
                }

            };
        }
    }

    public final static class Intersects extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String INTERSECTS_SUFFIX = "-intersects";
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + INTERSECTS_SUFFIX;

        public Intersects() {
            super(ID, StandardDatatypes.BOOLEAN, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    UtilityFunctions uf = new UtilityFunctions();
                    return new BooleanValue(uf.compare(args.poll(), args.poll(), INTERSECTS_SUFFIX));
                }

            };
        }
    }


}