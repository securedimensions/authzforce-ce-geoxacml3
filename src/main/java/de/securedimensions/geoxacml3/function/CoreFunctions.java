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
import org.ow2.authzforce.core.pdp.api.func.MultiParameterTypedFirstOrderFunction;
import org.ow2.authzforce.core.pdp.api.func.SingleParameterTypedFirstOrderFunction;
import org.ow2.authzforce.core.pdp.api.value.*;
import org.ow2.authzforce.xacml.identifiers.XacmlStatusCode;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class CoreFunctions {

    public final static class Length extends SingleParameterTypedFirstOrderFunction<DoubleValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-length";

        public Length() {
            super(ID, StandardDatatypes.DOUBLE, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<DoubleValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<DoubleValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected DoubleValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    return new DoubleValue((args.poll().getGeometry().getLength()));
                }

            };
        }
    }

    public final static class Area extends SingleParameterTypedFirstOrderFunction<DoubleValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-area";

        public Area() {
            super(ID, StandardDatatypes.DOUBLE, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<DoubleValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<DoubleValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected DoubleValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    return new DoubleValue((args.poll().getGeometry().getArea()));
                }

            };
        }
    }

    public final static class Dimension extends SingleParameterTypedFirstOrderFunction<IntegerValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-dimension";

        public Dimension() {
            super(ID, StandardDatatypes.INTEGER, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<IntegerValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<IntegerValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected IntegerValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    return IntegerValue.valueOf((args.poll().getGeometry().getDimension()));
                }

            };
        }
    }

    public final static class GeometryType extends SingleParameterTypedFirstOrderFunction<StringValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-type";

        public GeometryType() {
            super(ID, StandardDatatypes.STRING, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<StringValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<StringValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected StringValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    return StringValue.parse(((args.poll().getGeometry().getGeometryType())));
                }

            };
        }
    }

    public final static class SRID extends SingleParameterTypedFirstOrderFunction<IntegerValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-srid";

        public SRID() {
            super(ID, StandardDatatypes.INTEGER, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<IntegerValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<IntegerValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected IntegerValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    return IntegerValue.valueOf((args.poll().getGeometry().getSRID()));
                }

            };
        }
    }

    public final static class IsSimple extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-is-simple";

        public IsSimple() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    return new BooleanValue(args.poll().getGeometry().isSimple());
                }

            };
        }
    }

    public final static class IsEmpty extends SingleParameterTypedFirstOrderFunction<BooleanValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-is-empty";

        public IsEmpty() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<BooleanValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    return new BooleanValue(args.poll().getGeometry().isEmpty());
                }

            };
        }
    }

    public final static class SRIDEquals extends MultiParameterTypedFirstOrderFunction<BooleanValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-srid-equals";

        public SRIDEquals() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(GeometryValue.DATATYPE, StandardDatatypes.INTEGER));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<BooleanValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                    final Geometry g = ((GeometryValue)args.poll()).getGeometry();
                    final int srid = (((IntegerValue) args.poll()).getUnderlyingValue()).intValue();
                    return new BooleanValue(srid == g.getSRID());
                }
            };
        }
    }

    public final static class SRSEquals extends MultiParameterTypedFirstOrderFunction<BooleanValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-srs-equals";

        public SRSEquals() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(GeometryValue.DATATYPE, StandardDatatypes.STRING));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<BooleanValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                    final Geometry g = ((GeometryValue)args.poll()).getGeometry();
                    final String srs = ((StringValue) args.poll()).getUnderlyingValue();
                    int srid = 0;
                    if (srs.toUpperCase().contains("WGS84") || srs.toUpperCase().contains("CRS84"))
                        srid = -4326;
                    else {
                        String []tokens = srs.split(":");
                        if (tokens.length != 2)
                            throw new IllegalArgumentException("SRS pattern is EPSG:<srid>");
                        else if (!tokens[0].equalsIgnoreCase("EPSG"))
                            throw new IllegalArgumentException("SRS must start with authority string 'EPSG'");
                        else
                            srid = Integer.parseInt(tokens[1]);
                    }
                    return new BooleanValue(g.getSRID() == srid);
                }
            };
        }
    }

    public final static class Distance extends SingleParameterTypedFirstOrderFunction<DoubleValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-distance";

        public Distance() {
            super(ID, StandardDatatypes.DOUBLE, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<DoubleValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<DoubleValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected DoubleValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    return new DoubleValue(args.poll().distance(args.poll()));
                }

            };
        }
    }

    public final static class IsWithinDistance extends MultiParameterTypedFirstOrderFunction<BooleanValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-is-within-distance";

        public IsWithinDistance() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(GeometryValue.DATATYPE, GeometryValue.DATATYPE, StandardDatatypes.DOUBLE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<BooleanValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 3)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly three arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    GeometryValue gv1 = (GeometryValue)args.poll();
                    GeometryValue gv2 = (GeometryValue)args.poll();
                    final Double d = ((DoubleValue) args.poll()).getUnderlyingValue();
                    return new BooleanValue(gv1.isWithinDistance(gv2, d));
                }
            };
        }
    }

    public final static class EqualsDistance extends MultiParameterTypedFirstOrderFunction<BooleanValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-equals-distance";

        public EqualsDistance() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(GeometryValue.DATATYPE, GeometryValue.DATATYPE, StandardDatatypes.DOUBLE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<BooleanValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 3)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly three arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    GeometryValue gv1 = (GeometryValue)args.poll();
                    GeometryValue gv2 = (GeometryValue)args.poll();

                    final Double d = ((DoubleValue) args.poll()).getUnderlyingValue();
                    return new BooleanValue(gv1.equalsDistance(gv2, d));
                }
            };
        }
    }

    public final static class Relate extends MultiParameterTypedFirstOrderFunction<BooleanValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-relate";

        public Relate() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(GeometryValue.DATATYPE, GeometryValue.DATATYPE, StandardDatatypes.STRING));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<BooleanValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 3)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly three arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    GeometryValue gv1 = (GeometryValue)args.poll();
                    GeometryValue gv2 = (GeometryValue)args.poll();

                    final String r = ((StringValue) args.poll()).getUnderlyingValue();
                    return new BooleanValue(gv1.relate(gv2, r));
                }
            };
        }
    }

}
