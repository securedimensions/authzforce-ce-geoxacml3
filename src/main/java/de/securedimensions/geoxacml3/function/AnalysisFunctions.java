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
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.ow2.authzforce.core.pdp.api.ImmutableXacmlStatus;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;
import org.ow2.authzforce.core.pdp.api.expression.Expression;
import org.ow2.authzforce.core.pdp.api.func.BaseFirstOrderFunctionCall;
import org.ow2.authzforce.core.pdp.api.func.FirstOrderFunctionCall;
import org.ow2.authzforce.core.pdp.api.func.MultiParameterTypedFirstOrderFunction;
import org.ow2.authzforce.core.pdp.api.func.SingleParameterTypedFirstOrderFunction;
import org.ow2.authzforce.core.pdp.api.value.*;

import java.util.*;

public class AnalysisFunctions {

    public final static class Envelope extends SingleParameterTypedFirstOrderFunction<GeometryValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-envelope";

        public Envelope() {
            super(ID, GeometryValue.DATATYPE, true, Arrays.asList(GeometryValue.DATATYPE));
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
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-boundary";

        public Boundary() {
            super(ID, GeometryValue.DATATYPE, true, Arrays.asList(GeometryValue.DATATYPE));
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
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-buffer";

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
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-convex-hull";

        public ConvexHull() {
            super(ID, GeometryValue.DATATYPE, true, Arrays.asList(GeometryValue.DATATYPE));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) {

            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<GeometryValue, GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected GeometryValue evaluate(final Deque<GeometryValue> args) throws IndeterminateEvaluationException {
                    final Geometry g0 = args.poll().getGeometry();
                    final Geometry g = g0.convexHull();
                    g.setSRID(g0.getSRID());
                    return new GeometryValue(g);
                }

            };
        }
    }

    public final static class Centroid extends SingleParameterTypedFirstOrderFunction<GeometryValue, GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-centroid";

        public Centroid() {
            super(ID, GeometryValue.DATATYPE, true, Arrays.asList(GeometryValue.DATATYPE));
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

    public final static class GeometryIntersection extends MultiParameterTypedFirstOrderFunction<GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-geometry-intersection";

        public GeometryIntersection() {
            super(ID, GeometryValue.DATATYPE, true, Arrays.asList(GeometryValue.DATATYPE, GeometryValue.DATATYPE, StandardDatatypes.STRING));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected GeometryValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                    final Geometry g0 = ((GeometryValue) args.poll()).getGeometry();
                    final Geometry g1 = ((GeometryValue) args.poll()).getGeometry();

                    if (g0.getSRID() != g1.getSRID())
                        throw new IndeterminateEvaluationException(
                                new ImmutableXacmlStatus("urn:ogc:def:function:geoxacml:3.0:crs-error", Optional.of("Function " + ID + " expects same SRS for both geometry parameters")));

                    final Geometry g = g0.intersection(g1);
                    g.setSRID(g0.getSRID());
                    return new GeometryValue(g);
                }
            };
        }
    }

    public final static class GeometryUnion extends MultiParameterTypedFirstOrderFunction<GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-geometry-union";

        public GeometryUnion() {
            super(ID, GeometryValue.DATATYPE, true, Arrays.asList(GeometryValue.DATATYPE, GeometryValue.DATATYPE, StandardDatatypes.STRING));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected GeometryValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                    final Geometry g0 = ((GeometryValue) args.poll()).getGeometry();
                    final Geometry g1 = ((GeometryValue) args.poll()).getGeometry();

                    if (g0.getSRID() != g1.getSRID())
                        throw new IndeterminateEvaluationException(
                                new ImmutableXacmlStatus("urn:ogc:def:function:geoxacml:3.0:crs-error", Optional.of("Function " + ID + " expects same SRS for both geometry parameters")));

                    final Geometry g = g0.union(g1);
                    g.setSRID(g0.getSRID());
                    return new GeometryValue(g);
                }
            };
        }
    }

    public final static class GeometryDifference extends MultiParameterTypedFirstOrderFunction<GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-geometry-difference";

        public GeometryDifference() {
            super(ID, GeometryValue.DATATYPE, true, Arrays.asList(GeometryValue.DATATYPE, GeometryValue.DATATYPE, StandardDatatypes.STRING));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected GeometryValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                    final Geometry g0 = ((GeometryValue) args.poll()).getGeometry();
                    final Geometry g1 = ((GeometryValue) args.poll()).getGeometry();

                    if (g0.getSRID() != g1.getSRID())
                        throw new IndeterminateEvaluationException(
                                new ImmutableXacmlStatus("urn:ogc:def:function:geoxacml:3.0:crs-error", Optional.of("Function " + ID + " expects same SRS for both geometry parameters")));

                    final Geometry g = g0.difference(g1);
                    g.setSRID(g0.getSRID());
                    return new GeometryValue(g);
                }
            };
        }
    }

    public final static class GeometrySymDifference extends MultiParameterTypedFirstOrderFunction<GeometryValue> {
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-geometry-sym-difference";

        public GeometrySymDifference() {
            super(ID, GeometryValue.DATATYPE, true, Arrays.asList(GeometryValue.DATATYPE, GeometryValue.DATATYPE, StandardDatatypes.STRING));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval<GeometryValue>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected GeometryValue evaluate(Deque<AttributeValue> args) throws IndeterminateEvaluationException {
                    final Geometry g0 = ((GeometryValue) args.poll()).getGeometry();
                    final Geometry g1 = ((GeometryValue) args.poll()).getGeometry();

                    if (g0.getSRID() != g1.getSRID())
                        throw new IndeterminateEvaluationException(
                                new ImmutableXacmlStatus("urn:ogc:def:function:geoxacml:3.0:crs-error", Optional.of("Function " + ID + " expects same SRS for both geometry parameters")));

                    final Geometry g = g0.symDifference(g1);
                    g.setSRID(g0.getSRID());
                    return new GeometryValue(g);
                }
            };
        }
    }

    public static class GeometryBagFromGeometryCollection<AV extends AttributeValue> extends SingleParameterTypedFirstOrderFunction<Bag<GeometryValue>, GeometryValue> {
        /**
         * Function ID suffix for 'primitiveType-bag' functions
         */
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-bag-from-geometry-collection";

        public GeometryBagFromGeometryCollection() {
            super(ID, GeometryValue.FACTORY.getDatatype().getBagDatatype(), true, Collections.singletonList(GeometryValue.FACTORY.getDatatype()));

        }

        /**
         * Constructor
         *
         * @param paramType    bag's primitive datatype
         * @param paramBagType bag datatype
         */
        public GeometryBagFromGeometryCollection(final BagDatatype<GeometryValue> paramBagType, final Datatype<GeometryValue> paramType) {
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
                        gvu.add(new GeometryValue(gc.getGeometryN(ix), null));
                    return Bags.newBag(GeometryValue.FACTORY.getDatatype(), gvu);

                }
            };
        }
    }

    public static class GeometryBagToGeometryCollection<AV extends AttributeValue> extends SingleParameterTypedFirstOrderFunction<GeometryValue, Bag<GeometryValue>> {
        /**
         * Function ID suffix for 'primitiveType-collection' functions
         */
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-collection-from-geometry-bag";

        public GeometryBagToGeometryCollection() {
            super(ID, GeometryValue.FACTORY.getDatatype(), false, Collections.singletonList(GeometryValue.FACTORY.getDatatype().getBagDatatype()));
        }

        /**
         * Constructor
         *
         * @param paramType    bag's primitive datatype
         * @param paramBagType bag datatype
         */
        public GeometryBagToGeometryCollection(final Datatype<GeometryValue> paramType, final BagDatatype<GeometryValue> paramBagType) {
            super(ID, paramType, false, Collections.singletonList(paramBagType));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerBagEval<>(functionSignature, argExpressions) {

                @Override
                protected GeometryValue evaluate(final Bag<GeometryValue>[] bagArgs) throws IndeterminateEvaluationException {
                    final Iterator<GeometryValue> i = bagArgs[0].iterator();
                    Geometry[] gs = new Geometry[bagArgs[0].size()];
                    int ix = 0;
                    while (i.hasNext()) {
                        gs[ix++] = i.next().getGeometry();
                    }

                    return new GeometryValue(GeometryValue.Factory.GEOMETRY_FACTORY.createGeometryCollection(gs));
                }
            };
        }

    }

}
