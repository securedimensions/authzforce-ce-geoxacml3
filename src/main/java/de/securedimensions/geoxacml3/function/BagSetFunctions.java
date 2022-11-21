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
import org.ow2.authzforce.core.pdp.api.ImmutableXacmlStatus;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;
import org.ow2.authzforce.core.pdp.api.expression.Expression;
import org.ow2.authzforce.core.pdp.api.func.*;
import org.ow2.authzforce.core.pdp.api.value.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 * @author Andreas Matheus, Secure Dimensions GmbH.
 *
 */

public class BagSetFunctions {

    final static AttributeDatatype<GeometryValue> paramType = GeometryValue.FACTORY.getDatatype();
    final static BagDatatype<GeometryValue> paramBagType = paramType.getBagDatatype();
    final static Class<GeometryValue[]> paramArrayClass = paramType.getArrayClass();
    private static final Logger LOGGER = LoggerFactory.getLogger(BagSetFunctions.class);

    public static class SingletonBagToPrimitive<AV extends AttributeValue> extends SingleParameterTypedFirstOrderFunction<GeometryValue, Bag<GeometryValue>> {
        /**
         * Function ID suffix for 'primitiveType-collection' functions
         */
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-one-and-only";

        public SingletonBagToPrimitive() {
            super(ID, paramType, false, Collections.singletonList(paramBagType));
        }

        /**
         * Constructor
         *
         * @param paramType
         *            bag's primitive datatype
         * @param paramBagType
         *            bag datatype
         */
        public SingletonBagToPrimitive(final Datatype<GeometryValue> paramType, final BagDatatype<GeometryValue> paramBagType) {
            super(ID, paramType, false, Collections.singletonList(paramBagType));
        }

        @Override
        public FirstOrderFunctionCall<GeometryValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerBagEval<>(functionSignature, argExpressions) {

                @Override
                protected GeometryValue evaluate(final Bag<GeometryValue>[] bagArgs) throws IndeterminateEvaluationException {
                    if (bagArgs[0].size() != 1)
                        throw new IllegalArgumentException("bag must contain exactly one GeometryValue");

                    return bagArgs[0].getSingleElement();
                }
            };
        }

    }

    public static class BagSize extends FirstOrderBagFunctions.BagSize<GeometryValue> {
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + NAME_SUFFIX_BAG_SIZE;

        /**
         * Function identifier
         * public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-bag-size";
         * <li>{@code -bag-size}: gives the size of a given bag</li>
         */

        public BagSize() {
            super(paramBagType);
        }

    }

    public static class BagContains<AV extends AttributeValue> extends MultiParameterTypedFirstOrderFunction<BooleanValue> {
        /**
         * Function ID suffix for 'primitiveType-is-in' functions
         */
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-is-in";

        private final Class<GeometryValue[]> arrayClass;

        private final BagDatatype<GeometryValue> bagType;

        public BagContains() {
            this(paramType, paramBagType, paramType.getArrayClass());
        }

        /**
         * Constructor
         *
         * @param paramType
         *            bag's primitive datatype
         * @param paramBagType
         *            bag datatype
         * @param paramArrayClass
         *            primitive value array class
         */
        public BagContains(final Datatype<GeometryValue> paramType, final BagDatatype<GeometryValue> paramBagType, final Class<GeometryValue[]> paramArrayClass) {
            super(ID, StandardDatatypes.BOOLEAN, false, Arrays.asList(paramType, paramBagType));
            this.arrayClass = paramArrayClass;
            this.bagType = paramBagType;
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerPartlyBagEval<>(functionSignature, bagType, arrayClass, argExpressions, remainingArgTypes) {

                @Override
                protected BooleanValue evaluate(Deque<GeometryValue> primArgsBeforeBag, Bag<GeometryValue>[] bagArgs, GeometryValue[] remainingArgs) throws IndeterminateEvaluationException {
                    return BooleanValue.valueOf(eval(primArgsBeforeBag.getFirst(), bagArgs[0]));
                }

            };
        }

        /**
         * Tests whether a bag contains a given primitive value
         *
         * @param arg0
         *            primitive value
         * @param bag
         *            bag
         * @return true iff {@code arg0} is in {@code bag}
         */
        private <V extends AttributeValue> boolean eval(final V arg0, final Bag<V> bag) throws IndeterminateEvaluationException {
            final Geometry g = ((GeometryValue) arg0).getGeometry();
            final Iterator<GeometryValue> i = (Iterator<GeometryValue>) bag.iterator();
            while (i.hasNext()) {
                final Geometry gi = i.next().getGeometry();
                if (gi.getSRID() != g.getSRID()) {
                    throw new IndeterminateEvaluationException(
                            new ImmutableXacmlStatus("urn:ogc:def:function:geoxacml:3.0:crs-error", Optional.of("Function " + ID + " expects same SRS for both geometry parameters")));
                }
                if (g.equals(gi))
                    return true;
            }
            return false;
        }
    }

    public static class AtLeastOneMemberOf extends FirstOrderBagFunctions.AtLeastOneMemberOf<GeometryValue> {
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + NAME_SUFFIX_AT_LEAST_ONE_MEMBER_OF;

        /**
         * Function identifier
         * public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-at-least-one-member-of";
         * <li>{@code -at-least-one-member-of}: tests whether one of the values in a given bag is in another given bag</li>
         */

        public AtLeastOneMemberOf() {
            super(paramBagType);
        }

        @Override
        protected BooleanValue eval(final Bag<GeometryValue>[] bagArgs) {
            return BooleanValue.valueOf(eval(bagArgs[0], bagArgs[1]));
        }

        private <V extends AttributeValue> boolean eval(final Bag<V> bag0, final Bag<V> bag1) {
            final Iterator<V> g0i = bag0.iterator();
            final Iterator<V> g1i = bag1.iterator();

            while (g0i.hasNext()) {
                final GeometryValue gv0 = (GeometryValue) g0i.next();
                final Geometry g0 = gv0.getGeometry();
                while (g1i.hasNext()) {
                    final GeometryValue gv1 = (GeometryValue) g1i.next();
                    final Geometry g1 = gv1.getGeometry();
                    if (g0.equals(g1))
                        return true;
                }
            }

            return false;
        }
    }

    public static class Intersection extends FirstOrderBagFunctions.Intersection<GeometryValue> {
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + NAME_SUFFIX_INTERSECTION;

        /**
         * Function identifier
         * public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-intersection";
         * <li>{@code -intersection}: computes the intersection set of two bags</li>
         */

        public Intersection() {
            super(paramType, paramBagType);
        }

        @Override
        protected Bag<GeometryValue> eval(final Bag<GeometryValue>[] bagArgs) {
            return Bags.newBag(paramType, eval(bagArgs[0], bagArgs[1]));
        }

        private <V extends AttributeValue> Collection<GeometryValue> eval(final Bag<V> bag0, final Bag<V> bag1) {
            final Iterator<V> g0i = bag0.iterator();
            final Iterator<V> g1i = bag1.iterator();

            Collection<GeometryValue> intersection = new ArrayList<GeometryValue>();
            while (g0i.hasNext()) {
                final GeometryValue gv0 = (GeometryValue) g0i.next();
                final Geometry g0 = gv0.getGeometry();
                while (g1i.hasNext()) {
                    final GeometryValue gv1 = (GeometryValue) g1i.next();
                    final Geometry g1 = gv1.getGeometry();
                    if (g0.equals(g1))
                        intersection.add(gv1);
                }
            }

            return intersection;
        }
    }

    public static class Union extends FirstOrderBagFunctions.Union<GeometryValue> {
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + NAME_SUFFIX_UNION;

        /**
         * Function identifier
         * public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-union";
         * <li>{@code -union}: computes the union set of two bags</li>
         */

        public Union() {
            super(paramType, paramBagType);
        }

        @Override
        protected Bag<GeometryValue> eval(final Bag<GeometryValue>[] bagArgs) {
            return Bags.newBag(paramType, eval(bagArgs[0], bagArgs[1]));
        }

        private <V extends AttributeValue> Collection<GeometryValue> eval(final Bag<V> bag0, final Bag<V> bag1) {
            final Iterator<V> g0i = bag0.iterator();
            final Iterator<V> g1i = bag1.iterator();

            Collection<GeometryValue> union = new ArrayList<GeometryValue>();
            while (g0i.hasNext()) {
                final GeometryValue gv0 = (GeometryValue) g0i.next();
                final Geometry g0 = gv0.getGeometry();
                while (g1i.hasNext()) {
                    final GeometryValue gv1 = (GeometryValue) g1i.next();
                    final Geometry g1 = gv1.getGeometry();
                    if (!g0.equals(g1))
                        union.add(gv1);
                }
            }

            return union;
        }
    }

    public static class Subset extends FirstOrderBagFunctions.Subset<GeometryValue> {
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + NAME_SUFFIX_SUBSET;

        /**
         * Function identifier
         * public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-subset";
         * <li>{@code -subset}: tests if bag from first argument is subset of bag from second argument</li>
         */

        public Subset() {
            super(paramBagType);
        }

        @Override
        protected BooleanValue eval(final Bag<GeometryValue>[] bagArgs) {
            return BooleanValue.valueOf(eval(bagArgs[0], bagArgs[1]));
        }


        private <V extends AttributeValue> boolean eval(final Bag<V> bag0, final Bag<V> bag1) {
            final Iterator<V> g0i = bag0.iterator();
            final Iterator<V> g1i = bag1.iterator();

            while (g0i.hasNext()) {
                final GeometryValue gv0 = (GeometryValue) g0i.next();
                final Geometry g0 = gv0.getGeometry();
                boolean status = false;
                while (g1i.hasNext()) {
                    final GeometryValue gv1 = (GeometryValue) g1i.next();
                    final Geometry g1 = gv1.getGeometry();
                    if (g0.equals(g1)) {
                        status = true;
                        break;
                    }
                }
                if (status == false)
                    return false;
            }

            return true;
        }
    }

    public static class SetEquals extends FirstOrderBagFunctions.SetEquals<GeometryValue> {
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + NAME_SUFFIX_SET_EQUALS;

        /**
         * Function identifier
         * public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-set-equals";
         * <li>{@code -set-equals}: tests if bag from first argument is subset of bag from second argument</li>
         */

        public SetEquals() {
            super(paramBagType);
        }

        @Override
        protected BooleanValue eval(final Bag<GeometryValue>[] bagArgs) {
            return BooleanValue.valueOf(eval(bagArgs[0], bagArgs[1]));
        }


        private <V extends AttributeValue> boolean eval(final Bag<V> bag0, final Bag<V> bag1) {
            final Iterator<V> g0i = bag0.iterator();
            final Iterator<V> g1i = bag1.iterator();

            if (bag0.size() != bag1.size())
                return false;

            while (g0i.hasNext()) {
                final GeometryValue gv0 = (GeometryValue) g0i.next();
                final Geometry g0 = gv0.getGeometry();
                boolean status = false;
                while (g1i.hasNext()) {
                    final GeometryValue gv1 = (GeometryValue) g1i.next();
                    final Geometry g1 = gv1.getGeometry();
                    if (g0.equals(g1)) {
                        status = true;
                        break;
                    }
                }
                if (status == false)
                    return false;
            }

            while (g1i.hasNext()) {
                final GeometryValue gv1 = (GeometryValue) g1i.next();
                final Geometry g1 = gv1.getGeometry();
                boolean status = false;
                while (g0i.hasNext()) {
                    final GeometryValue gv0 = (GeometryValue) g0i.next();
                    final Geometry g0 = gv0.getGeometry();
                    if (g1.equals(g0)) {
                        status = true;
                        break;
                    }
                }
                if (status == false)
                    return false;
            }

            return true;
        }
    }

    public static class GeometryBag<AV extends AttributeValue> extends SingleParameterTypedFirstOrderFunction<Bag<GeometryValue>, GeometryValue> {
        /**
         * Function ID suffix for 'primitiveType-bag' functions
         */
        public static final String ID = "urn:ogc:def:function:geoxacml:3.0:geometry-bag";

        public GeometryBag() {
            super(ID, GeometryValue.FACTORY.getDatatype().getBagDatatype(), true, Collections.singletonList(GeometryValue.FACTORY.getDatatype()));

        }

        /**
         * Constructor
         *
         * @param paramType    bag's primitive datatype
         * @param paramBagType bag datatype
         */
        public GeometryBag(final BagDatatype<GeometryValue> paramBagType, final Datatype<GeometryValue> paramType) {
            super(ID, paramBagType, true, Collections.singletonList(paramType));
        }

        @Override
        public FirstOrderFunctionCall<Bag<GeometryValue>> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerSinglePrimitiveTypeEval<>(functionSignature, argExpressions, remainingArgTypes) {

                @Override
                protected Bag<GeometryValue> evaluate(final Deque<GeometryValue> args) {
                    List<GeometryValue> gvu = new ArrayList<GeometryValue>();
                    final Iterator<GeometryValue> i = args.iterator();
                    while (i.hasNext())
                        gvu.add(i.next());

                    return Bags.newBag(GeometryValue.FACTORY.getDatatype(), gvu);

                }
            };
        }
    }


}
