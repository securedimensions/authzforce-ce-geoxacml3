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
import de.securedimensions.geoxacml3.identifiers.Definitions;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MissingAttributeDetail;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;
import org.ow2.authzforce.core.pdp.api.expression.Expression;
import org.ow2.authzforce.core.pdp.api.func.BaseFirstOrderFunctionCall;
import org.ow2.authzforce.core.pdp.api.func.FirstOrderFunctionCall;
import org.ow2.authzforce.core.pdp.api.func.MultiParameterTypedFirstOrderFunction;
import org.ow2.authzforce.core.pdp.api.func.SingleParameterTypedFirstOrderFunction;
import org.ow2.authzforce.core.pdp.api.value.*;
import org.ow2.authzforce.xacml.identifiers.XacmlStatusCode;

import javax.xml.namespace.QName;
import java.util.*;

import static de.securedimensions.geoxacml3.identifiers.Definitions.*;
import static de.securedimensions.geoxacml3.pdp.io.GeoXACMLRequestPreprocessor.XACML_ATTRIBUTE_ID_QNAME;
import static de.securedimensions.geoxacml3.pdp.io.GeoXACMLRequestPreprocessor.XACML_CATEGORY_ID_QNAME;

public class CoreFunctions {

    public final static class Length extends SingleParameterTypedFirstOrderFunction<DoubleValue, GeometryValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-length";

        public Length() {
            super(ID, StandardDatatypes.DOUBLE, true, List.of(GeometryValue.DATATYPE));
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
        public static final String ID = Definitions.FUNCTION_PREFIX + "-area";

        public Area() {
            super(ID, StandardDatatypes.DOUBLE, true, List.of(GeometryValue.DATATYPE));
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
        public static final String ID = Definitions.FUNCTION_PREFIX + "-dimension";

        public Dimension() {
            super(ID, StandardDatatypes.INTEGER, true, List.of(GeometryValue.DATATYPE));
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
        public static final String ID = Definitions.FUNCTION_PREFIX + "-type";

        public GeometryType() {
            super(ID, StandardDatatypes.STRING, true, List.of(GeometryValue.DATATYPE));
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
        public static final String ID = Definitions.FUNCTION_PREFIX + "-srid";

        public SRID() {
            super(ID, StandardDatatypes.INTEGER, true, List.of(GeometryValue.DATATYPE));
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
        public static final String ID = Definitions.FUNCTION_PREFIX + "-is-simple";

        public IsSimple() {
            super(ID, StandardDatatypes.BOOLEAN, true, List.of(GeometryValue.DATATYPE));
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
        public static final String ID = Definitions.FUNCTION_PREFIX + "-is-empty";

        public IsEmpty() {
            super(ID, StandardDatatypes.BOOLEAN, true, List.of(GeometryValue.DATATYPE));
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
        public static final String ID = Definitions.FUNCTION_PREFIX + "-srid-equals";

        public SRIDEquals() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(StandardDatatypes.INTEGER, GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<BooleanValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                    final int srid = (((IntegerValue) args.poll()).getUnderlyingValue()).intValue();
                    final Geometry g = ((GeometryValue) args.poll()).getGeometry();
                    return new BooleanValue(srid == g.getSRID());
                }
            };
        }
    }

    public final static class Distance extends SingleParameterTypedFirstOrderFunction<DoubleValue, GeometryValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-distance";

        public Distance() {
            super(ID, StandardDatatypes.DOUBLE, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<DoubleValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<DoubleValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected DoubleValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 2)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly two arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    Geometry g1 = args.poll().getGeometry();
                    Geometry g2 = args.poll().getGeometry();
                    UtilityFunctions uf = new UtilityFunctions();
                    uf.ensurePrecision(g1, g2);
                    uf.ensureCRS(g1, g2);

                    return new DoubleValue(g1.distance(g2));
                }

            };
        }
    }

    public final static class IsWithinDistance extends MultiParameterTypedFirstOrderFunction<BooleanValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-is-within-distance";

        public IsWithinDistance() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(StandardDatatypes.DOUBLE, GeometryValue.DATATYPE, GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<BooleanValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 3)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly three arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    final Double d = ((DoubleValue) args.poll()).getUnderlyingValue();
                    GeometryValue gv1 = (GeometryValue) args.poll();
                    GeometryValue gv2 = (GeometryValue) args.poll();
                    Geometry g1 = gv1.getGeometry();
                    Geometry g2 = gv2.getGeometry();
                    UtilityFunctions uf = new UtilityFunctions();
                    uf.ensurePrecision(g1, g2);
                    uf.ensureCRS(g1, g2);

                    return new BooleanValue(g1.isWithinDistance(g2, d));
                }
            };
        }
    }

    public final static class DistanceEquals extends MultiParameterTypedFirstOrderFunction<BooleanValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-distance-equals";

        public DistanceEquals() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(StandardDatatypes.DOUBLE, GeometryValue.DATATYPE, GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<BooleanValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 3)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly three arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    final Double d = ((DoubleValue) args.poll()).getUnderlyingValue();

                    GeometryValue gv1 = (GeometryValue) args.poll();
                    GeometryValue gv2 = (GeometryValue) args.poll();

                    Geometry g1 = gv1.getGeometry();
                    Geometry g2 = gv2.getGeometry();
                    UtilityFunctions uf = new UtilityFunctions();
                    uf.ensurePrecision(g1, g2);
                    uf.ensureCRS(g1, g2);

                    return new BooleanValue(g1.distance(g2) == d);
                }
            };
        }
    }

    public final static class Relate extends MultiParameterTypedFirstOrderFunction<BooleanValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-relate";

        public Relate() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(StandardDatatypes.STRING, GeometryValue.DATATYPE, GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<BooleanValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                    if (args.size() != 3)
                        throw new IndeterminateEvaluationException("Function " + ID + " requires exactly three arguments but given " + args.size(), XacmlStatusCode.PROCESSING_ERROR.name());

                    final String r = ((StringValue) args.poll()).getUnderlyingValue();

                    GeometryValue gv1 = (GeometryValue) args.poll();
                    GeometryValue gv2 = (GeometryValue) args.poll();

                    Geometry g1 = gv1.getGeometry();
                    Geometry g2 = gv2.getGeometry();
                    UtilityFunctions uf = new UtilityFunctions();
                    uf.ensurePrecision(g1, g2);
                    uf.ensureCRS(g1, g2);

                    return new BooleanValue(g1.relate(g2, r));
                }
            };
        }
    }

    public static class GeometryBagFromCollection<AV extends AttributeValue> extends SingleParameterTypedFirstOrderFunction<Bag<GeometryValue>, GeometryValue> {
        /**
         * Function ID suffix for 'primitiveType-bag' functions
         */
        public static final String ID = Definitions.FUNCTION_PREFIX + "-bag-from-collection";

        public GeometryBagFromCollection() {
            super(ID, GeometryValue.FACTORY.getDatatype().getBagDatatype(), true, Collections.singletonList(GeometryValue.FACTORY.getDatatype()));

        }

        /**
         * Constructor
         *
         * @param paramType    bag's primitive datatype
         * @param paramBagType bag datatype
         */
        public GeometryBagFromCollection(final BagDatatype<GeometryValue> paramBagType, final Datatype<GeometryValue> paramType) {
            super(ID, paramBagType, true, Collections.singletonList(paramType));
        }

        @Override
        public FirstOrderFunctionCall<Bag<GeometryValue>> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected Bag<GeometryValue> evaluate(final Deque<GeometryValue> args) {
                    List<GeometryValue> gvu = new ArrayList<GeometryValue>();
                    final GeometryValue gv = args.getFirst();
                    GeometryCollection gc = (GeometryCollection) gv.getGeometry();
                    int n = gc.getNumGeometries();
                    for (int ix = 0; ix < n; ix++)
                        gvu.add(new GeometryValue(gc.getGeometryN(ix)));
                    return Bags.newBag(GeometryValue.FACTORY.getDatatype(), gvu);

                }
            };
        }
    }

    public static class GeometryBagToHomogeneousCollection<AV extends AttributeValue> extends SingleParameterTypedFirstOrderFunction<GeometryValue, Bag<GeometryValue>> {
        /**
         * Function ID suffix for 'primitiveType-collection' functions
         */
        public static final String ID = Definitions.FUNCTION_PREFIX + "-bag-to-collection";

        public GeometryBagToHomogeneousCollection() {
            super(ID, GeometryValue.FACTORY.getDatatype(), true, Collections.singletonList(GeometryValue.FACTORY.getDatatype().getBagDatatype()));
        }

        /**
         * Constructor
         *
         * @param paramType    bag's primitive datatype
         * @param paramBagType bag datatype
         */
        public GeometryBagToHomogeneousCollection(final Datatype<GeometryValue> paramType, final BagDatatype<GeometryValue> paramBagType) {
            super(ID, paramType, false, Collections.singletonList(paramBagType));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerBagEval<>(functionSignature, argExpressions) {

                @Override
                protected GeometryValue evaluate(final Bag<GeometryValue>[] bagArgs) throws IndeterminateEvaluationException {
                    final Iterator<GeometryValue> i = bagArgs[0].iterator();
                    Geometry[] gs = new Geometry[bagArgs[0].size()];
                    String geometryType = null;
                    int ix = 0;
                    while (i.hasNext()) {
                        Geometry g = i.next().getGeometry();
                        if (geometryType == null)
                            geometryType = g.getGeometryType();
                        else if (geometryType != g.getGeometryType())
                            throw new IllegalArgumentException("A GeometryCollection must be homogeneous and therefore cannot be created from a bag containing geometries of different types");

                        gs[ix++] = g;
                    }

                    return new GeometryValue(GeometryValue.Factory.GEOMETRY_FACTORY.createGeometryCollection(gs));
                }
            };
        }

    }

    public final static class EnsureSRID extends MultiParameterTypedFirstOrderFunction<GeometryValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-ensure-srid";

        public EnsureSRID() {
            super(ID, GeometryValue.DATATYPE, true, Arrays.asList(StandardDatatypes.INTEGER, GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected GeometryValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                    final int srid = ((IntegerValue) args.poll()).getUnderlyingValue().intValue();
                    final GeometryValue gv = ((GeometryValue) args.poll());
                    Geometry g = gv.getGeometry();

                    if (g.getSRID() == srid) {
                        return gv;
                    } else {
                        TransformGeometry tg = new TransformGeometry();
                        tg.transformCRS(g, srid, true);
                    }

                    return new GeometryValue(g);
                }
            };
        }
    }

    public final static class EnsurePrecision extends MultiParameterTypedFirstOrderFunction<GeometryValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-ensure-precision";

        public EnsurePrecision() {
            super(ID, GeometryValue.DATATYPE, true, Arrays.asList(StandardDatatypes.INTEGER, GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected GeometryValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                    final int precision = ((IntegerValue) args.poll()).getUnderlyingValue().intValue();
                    final GeometryValue gv = ((GeometryValue) args.poll());
                    Geometry g = gv.getGeometry();

                    Map<QName, String> otherAttributes = (g.getUserData() != null) ? (Map<QName, String>) g.getUserData() : new HashMap<>();
                    final int precisionG = Integer.parseInt(otherAttributes.getOrDefault(Definitions.xmlPrecision, String.valueOf(Integer.MAX_VALUE)));
                    if (precision == precisionG) {
                        return gv;
                    } else if (precision > precisionG) {
                        // the requested precision is higher than possible
                        final AttributeValueType av = new AttributeValueType(List.of(""), GeometryValue.DATATYPE.getId(), otherAttributes);
                        final MissingAttributeDetail missingAttributeDetail = new MissingAttributeDetail(List.of(av),
                                otherAttributes.get(XACML_CATEGORY_ID_QNAME),
                                otherAttributes.get(XACML_ATTRIBUTE_ID_QNAME),
                                GeometryValue.DATATYPE.getId(),
                                null);
                        throw new IndeterminateEvaluationException("Requested precision cannot be achieved", missingAttributeDetail, Optional.of(PRECISION_ERROR));
                    } else {
                        // we can reduce precision as required
                        try {
                            double scale = Math.pow(10.0, precision);
                            GeometryFactory gf = new GeometryFactory(new PrecisionModel(scale));
                            WKTReader wktReader = new WKTReader(gf);
                            return new GeometryValue(wktReader.read(g.toText()));
                        } catch (ParseException e) {
                            final AttributeValueType av = new AttributeValueType(List.of(""), GeometryValue.DATATYPE.getId(), otherAttributes);
                            final MissingAttributeDetail missingAttributeDetail = new MissingAttributeDetail(List.of(av),
                                    otherAttributes.get(XACML_CATEGORY_ID_QNAME),
                                    otherAttributes.get(XACML_ATTRIBUTE_ID_QNAME),
                                    GeometryValue.DATATYPE.getId(),
                                    null);
                            throw new IndeterminateEvaluationException(e.getMessage(), missingAttributeDetail, Optional.of(GEOMETRY_ERROR));
                        }
                    }
                }
            };
        }
    }

    public final static class HasPrecision extends MultiParameterTypedFirstOrderFunction<BooleanValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-has-precision";

        public HasPrecision() {
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(StandardDatatypes.INTEGER, GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<BooleanValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                    final int precision = (((IntegerValue) args.poll()).getUnderlyingValue()).intValue();
                    final Geometry g = ((GeometryValue) args.poll()).getGeometry();
                    final Map <QName, String> otherAttributes = (g.getUserData() != null) ? (Map<QName, String>) g.getUserData() : new HashMap<>();
                    final int precisionG = Integer.parseInt(otherAttributes.getOrDefault(xmlPrecision, String.valueOf(Integer.MAX_VALUE)));
                    return new BooleanValue(precision <= precisionG);
                }
            };
        }
    }

    public final static class Precision extends SingleParameterTypedFirstOrderFunction<IntegerValue, GeometryValue> {
        public static final String ID = Definitions.FUNCTION_PREFIX + "-precision";

        public Precision() {
            super(ID, StandardDatatypes.INTEGER, true, List.of(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<IntegerValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<IntegerValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected IntegerValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    Geometry g = args.poll().getGeometry();
                    Map<QName, String> otherAttributes = (g.getUserData() != null) ? (Map<QName, String>) g.getUserData() : new HashMap<>();
                    int precision = Integer.parseInt(otherAttributes.getOrDefault(xmlPrecision, String.valueOf(Integer.MAX_VALUE)));
                    return IntegerValue.valueOf(precision);
                }

            };
        }
    }

}
