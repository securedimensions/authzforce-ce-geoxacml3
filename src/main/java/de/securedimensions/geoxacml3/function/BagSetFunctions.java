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
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MissingAttributeDetail;
import org.locationtech.jts.geom.Geometry;
import org.ow2.authzforce.core.pdp.api.ImmutableXacmlStatus;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;
import org.ow2.authzforce.core.pdp.api.expression.Expression;
import org.ow2.authzforce.core.pdp.api.func.*;
import org.ow2.authzforce.core.pdp.api.value.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.*;

import static de.securedimensions.geoxacml3.datatype.GeometryValue.*;
import static org.ow2.authzforce.core.pdp.api.func.FirstOrderBagFunctions.AtLeastOneMemberOf.NAME_SUFFIX_AT_LEAST_ONE_MEMBER_OF;
import static org.ow2.authzforce.core.pdp.api.func.FirstOrderBagFunctions.BagContains.NAME_SUFFIX_IS_IN;
import static org.ow2.authzforce.core.pdp.api.func.FirstOrderBagFunctions.Intersection.NAME_SUFFIX_INTERSECTION;
import static org.ow2.authzforce.core.pdp.api.func.FirstOrderBagFunctions.PrimitiveToBag.NAME_SUFFIX_BAG;
import static org.ow2.authzforce.core.pdp.api.func.FirstOrderBagFunctions.SetEquals.NAME_SUFFIX_SET_EQUALS;
import static org.ow2.authzforce.core.pdp.api.func.FirstOrderBagFunctions.SingletonBagToPrimitive.NAME_SUFFIX_ONE_AND_ONLY;
import static org.ow2.authzforce.core.pdp.api.func.FirstOrderBagFunctions.Subset.NAME_SUFFIX_SUBSET;
import static org.ow2.authzforce.core.pdp.api.func.FirstOrderBagFunctions.Union.NAME_SUFFIX_UNION;

/**
 *
 * @author Andreas Matheus, Secure Dimensions GmbH.
 *
 */

public class BagSetFunctions {

    final static AttributeDatatype<GeometryValue> paramType = GeometryValue.FACTORY.getDatatype();
    final static BagDatatype<GeometryValue> paramBagType = paramType.getBagDatatype();

    private static final Logger LOGGER = LoggerFactory.getLogger(BagSetFunctions.class);

    public static class SingletonBagToPrimitive<AV extends AttributeValue> extends SingleParameterTypedFirstOrderFunction<GeometryValue, Bag<GeometryValue>> {
        /**
         * Function ID suffix for 'primitiveType-collection' functions
         */
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + NAME_SUFFIX_ONE_AND_ONLY;

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
                        throw new IllegalArgumentException("Function " + ID + " bag must contain exactly one GeometryValue");

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
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + NAME_SUFFIX_IS_IN;

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
            super(ID, StandardDatatypes.BOOLEAN, true, Arrays.asList(paramType, paramBagType));
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
            Geometry g = ((GeometryValue) arg0).getGeometry();
            UtilityFunctions uf = new UtilityFunctions();
            final Iterator<GeometryValue> i = (Iterator<GeometryValue>) bag.iterator();
            while (i.hasNext()) {
                Geometry gi = i.next().getGeometry();
                uf.ensurePrecision(g, gi);
                if (gi.getSRID() != g.getSRID()) {
                    TransformGeometry tg = new TransformGeometry();
                    // first, we try to transform g as it is only one geometry
                    if (!tg.transformCRS(g, gi.getSRID())) {
                        // we are not allowed to transform g due to 'allowTransformation=false', so let's try gi
                        if (!tg.transformCRS(gi, g.getSRID())) {
                            // we are also not allowed to transform gi -> throw exception
                            // preparing StatusDetail
                            List<AttributeValueType> attributeValues = new ArrayList<>();
                            Map<QName, String> userDataG = (Map<QName, String>) g.getUserData();
                            if ((userDataG != null)&& (!userDataG.isEmpty()))
                            {
                                String categoryId = userDataG.get(GeometryValue.xmlCategoryId);
                                String attributeId = userDataG.get(GeometryValue.xmlAttributeId);
                                Map<QName, String> otherAttributes = new HashMap<>();
                                otherAttributes.put(GeometryValue.xmlAttributeId, userDataG.get(GeometryValue.xmlAttributeId));
                                AttributeValueType av = new AttributeValueType(List.of(g), GeometryValue.DATATYPE.getId(), otherAttributes);
                                attributeValues.add(av);
                                MissingAttributeDetail mad = new MissingAttributeDetail(attributeValues, categoryId, attributeId, GeometryValue.DATATYPE.getId(), null);

                            }
                            Map<QName, String> userDataGi = (Map<QName, String>) gi.getUserData();
                            if ((userDataGi != null)&& (!userDataGi.isEmpty()))
                            {
                                String categoryId = userDataGi.get(GeometryValue.xmlCategoryId);
                                String attributeId = userDataGi.get(GeometryValue.xmlAttributeId);
                                Map<QName, String> otherAttributes = new HashMap<>();
                                otherAttributes.put(GeometryValue.xmlAttributeId, userDataGi.get(GeometryValue.xmlAttributeId));
                                AttributeValueType av = new AttributeValueType(List.of(gi), GeometryValue.DATATYPE.getId(), otherAttributes);
                                attributeValues.add(av);
                                MissingAttributeDetail mad = new MissingAttributeDetail(attributeValues, categoryId, attributeId, GeometryValue.DATATYPE.getId(), null);
                                mad.getAttributeValues();
                            }
                            throw new IndeterminateEvaluationException(
                                    new ImmutableXacmlStatus(GeometryValue.SRS_ERROR, Optional.of("Function " + ID + " expects same SRS for both geometry parameters")));
                        }
                    }
                }
                if (g.equals(gi))
                    return true;
            }
            return false;
        }

    }

    public static class AtLeastOneMemberOf extends SingleParameterTypedFirstOrderFunction<BooleanValue, Bag<GeometryValue>>{

        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + NAME_SUFFIX_AT_LEAST_ONE_MEMBER_OF;

        public AtLeastOneMemberOf()
        {
            super(ID, StandardDatatypes.BOOLEAN, true, List.of(GeometryValue.DATATYPE.getBagDatatype()));
        }
        /**
         * Constructor that creates a function from its signature definition
         *
         * @param name           function name
         * @param returnType     function return type
         * @param varargs        true iff the function takes a variable number of arguments
         * @param parameterTypes function parameter types. Note: the "? extends" allows using {@link BagDatatype}.
         * @throws IllegalArgumentException if ( {@code name == null || returnType == null || parameterTypes == null || parameterTypes.size() < 1 })
         */
        public AtLeastOneMemberOf(String name, Datatype<BooleanValue> returnType, boolean varargs, List<? extends Datatype<Bag<GeometryValue>>> parameterTypes) throws IllegalArgumentException {
            super(name, returnType, varargs, parameterTypes);
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerBagEval<>(functionSignature, argExpressions) {

                @Override
                protected BooleanValue evaluate(final Bag<GeometryValue>[] bagArgs) throws IndeterminateEvaluationException {
                    if (bagArgs.length != 2)
                        throw new IllegalArgumentException("Function " + ID + " requires exactly two bag arguments but given " + bagArgs.length);

                    return new BooleanValue(eval(bagArgs[0], bagArgs[1]));
                }
            };
        }

        private <V extends AttributeValue> boolean eval(final Bag<V> bag0, final Bag<V> bag1) throws IndeterminateEvaluationException {
            final Iterator<V> g0i = bag0.iterator();

            UtilityFunctions uf = new UtilityFunctions();
            while (g0i.hasNext()) {
                final GeometryValue gv0 = (GeometryValue) g0i.next();
                Geometry g0 = gv0.getGeometry();
                final Iterator<V> g1i = bag1.iterator();
                while (g1i.hasNext()) {
                    final GeometryValue gv1 = (GeometryValue) g1i.next();
                    Geometry g1 = gv1.getGeometry();
                    uf.ensurePrecision(g0, g1);
                    if (g0.getSRID() != g1.getSRID()) {
                        TransformGeometry tg = new TransformGeometry();
                        tg.transformCRS(g0, g1);
                    }
                    if (g0.equals(g1))
                        return true;
                }
            }

            return false;
        }

    }

    public static class Intersection extends SingleParameterTypedFirstOrderFunction<Bag<GeometryValue>, Bag<GeometryValue>>{

        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + NAME_SUFFIX_INTERSECTION;

        public Intersection()
        {
            super(ID, GeometryValue.DATATYPE.getBagDatatype(), true, List.of(GeometryValue.DATATYPE.getBagDatatype()));
        }
        /**
         * Constructor that creates a function from its signature definition
         *
         * @param name           function name
         * @param returnType     function return type
         * @param varargs        true iff the function takes a variable number of arguments
         * @param parameterTypes function parameter types. Note: the "? extends" allows using {@link BagDatatype}.
         * @throws IllegalArgumentException if ( {@code name == null || returnType == null || parameterTypes == null || parameterTypes.size() < 1 })
         */
        public Intersection(String name, Datatype<Bag<GeometryValue>> returnType, boolean varargs, List<? extends Datatype<Bag<GeometryValue>>> parameterTypes) throws IllegalArgumentException {
            super(name, returnType, varargs, parameterTypes);
        }

        @Override
        public FirstOrderFunctionCall<Bag<GeometryValue>> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerBagEval<>(functionSignature, argExpressions) {

                @Override
                protected Bag<GeometryValue> evaluate(final Bag<GeometryValue>[] bagArgs) throws IndeterminateEvaluationException {
                    if (bagArgs.length != 2)
                        throw new IllegalArgumentException("Function " + ID + " requires exactly two bag arguments but given " + bagArgs.length);

                    return Bags.newBag(paramType, eval(bagArgs[0], bagArgs[1]));
                }
            };
        }

        private <V extends AttributeValue> Collection<GeometryValue> eval(final Bag<V> bag0, final Bag<V> bag1) throws IndeterminateEvaluationException {
            final Iterator<V> g0i = bag0.iterator();
            final Iterator<V> g1i = bag1.iterator();

            UtilityFunctions uf = new UtilityFunctions();

            Collection<GeometryValue> intersection = new ArrayList<GeometryValue>();
            while (g0i.hasNext()) {
                final GeometryValue gv0 = (GeometryValue) g0i.next();
                Geometry g0 = gv0.getGeometry();
                while (g1i.hasNext()) {
                    final GeometryValue gv1 = (GeometryValue) g1i.next();
                    Geometry g1 = gv1.getGeometry();
                    uf.ensurePrecision(g0, g1);
                    if (g0.getSRID() != g1.getSRID()) {
                        TransformGeometry tg = new TransformGeometry();
                        tg.transformCRS(g0, g1);
                    }
                    if (g0.equals(g1))
                        intersection.add(gv1);
                }
            }

            return intersection;
        }

    }

    public static class Union extends SingleParameterTypedFirstOrderFunction<Bag<GeometryValue>, Bag<GeometryValue>>{

        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + NAME_SUFFIX_UNION;

        public Union()
        {
            super(ID, GeometryValue.DATATYPE.getBagDatatype(), true, List.of(GeometryValue.DATATYPE.getBagDatatype()));
        }
        /**
         * Constructor that creates a function from its signature definition
         *
         * @param name           function name
         * @param returnType     function return type
         * @param varargs        true iff the function takes a variable number of arguments
         * @param parameterTypes function parameter types. Note: the "? extends" allows using {@link BagDatatype}.
         * @throws IllegalArgumentException if ( {@code name == null || returnType == null || parameterTypes == null || parameterTypes.size() < 1 })
         */
        public Union(String name, Datatype<Bag<GeometryValue>> returnType, boolean varargs, List<? extends Datatype<Bag<GeometryValue>>> parameterTypes) throws IllegalArgumentException {
            super(name, returnType, varargs, parameterTypes);
        }

        @Override
        public FirstOrderFunctionCall<Bag<GeometryValue>> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerBagEval<>(functionSignature, argExpressions) {

                @Override
                protected Bag<GeometryValue> evaluate(final Bag<GeometryValue>[] bagArgs) throws IndeterminateEvaluationException {
                    if (bagArgs.length != 2)
                        throw new IllegalArgumentException("Function " + ID + " requires exactly two bag arguments but given " + bagArgs.length);

                    return Bags.newBag(paramType, eval(bagArgs[0], bagArgs[1]));
                }
            };
        }

        private <V extends AttributeValue> Collection<GeometryValue> eval(final Bag<V> bag0, final Bag<V> bag1) throws IndeterminateEvaluationException {

            Collection<GeometryValue> union = new ArrayList<GeometryValue>();
            // initialize the result with the contents from bag0
            union.addAll((Collection<? extends GeometryValue>) bag0.elements());

            UtilityFunctions uf = new UtilityFunctions();

            final Iterator<GeometryValue> g1i = (Iterator<GeometryValue>) bag1.iterator();
            while (g1i.hasNext())
            {
                final GeometryValue gv1 = g1i.next();
                Geometry g1 = gv1.getGeometry();
                final Iterator<GeometryValue> g0i = (Iterator<GeometryValue>) bag0.iterator();
                boolean duplicate = true;
                while (g0i.hasNext())
                {
                    Geometry g0 = g0i.next().getGeometry();
                    uf.ensurePrecision(g0, g1);
                    if (g0.getSRID() != g1.getSRID()) {
                        TransformGeometry tg = new TransformGeometry();
                        tg.transformCRS(g0, g1.getSRID(), true);
                    }
                    if (g0.equals(g1))
                    {
                        duplicate = true;
                        break;
                    }
                }
                if (!duplicate)
                    union.add(gv1);
            }

            return union;
        }

    }

    public static class Subset extends SingleParameterTypedFirstOrderFunction<BooleanValue, Bag<GeometryValue>>{

        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + NAME_SUFFIX_SUBSET;

        public Subset()
        {
            super(ID, StandardDatatypes.BOOLEAN, true, List.of(GeometryValue.DATATYPE.getBagDatatype()));
        }
        /**
         * Constructor that creates a function from its signature definition
         *
         * @param name           function name
         * @param returnType     function return type
         * @param varargs        true iff the function takes a variable number of arguments
         * @param parameterTypes function parameter types. Note: the "? extends" allows using {@link BagDatatype}.
         * @throws IllegalArgumentException if ( {@code name == null || returnType == null || parameterTypes == null || parameterTypes.size() < 1 })
         */
        public Subset(String name, Datatype<BooleanValue> returnType, boolean varargs, List<? extends Datatype<Bag<GeometryValue>>> parameterTypes) throws IllegalArgumentException {
            super(name, returnType, varargs, parameterTypes);
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerBagEval<>(functionSignature, argExpressions) {

                @Override
                protected BooleanValue evaluate(final Bag<GeometryValue>[] bagArgs) throws IndeterminateEvaluationException {
                    if (bagArgs.length != 2)
                        throw new IllegalArgumentException("Function " + ID + " requires exactly two bag arguments but given " + bagArgs.length);

                    return new BooleanValue(eval(bagArgs[0], bagArgs[1]));
                }
            };
        }

        private <V extends AttributeValue> boolean eval(final Bag<V> bag0, final Bag<V> bag1) throws IndeterminateEvaluationException {
            final Iterator<V> g0i = bag0.iterator();
            final Iterator<V> g1i = bag1.iterator();

            UtilityFunctions uf = new UtilityFunctions();
            while (g0i.hasNext()) {
                final GeometryValue gv0 = (GeometryValue) g0i.next();
                Geometry g0 = gv0.getGeometry();
                boolean status = false;
                while (g1i.hasNext()) {
                    final GeometryValue gv1 = (GeometryValue) g1i.next();
                    Geometry g1 = gv1.getGeometry();
                    uf.ensurePrecision(g0, g1);
                    if (g0.getSRID() != g1.getSRID()) {
                        TransformGeometry tg = new TransformGeometry();
                        tg.transformCRS(g0, g1);
                    }
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

    public static class SetEquals extends SingleParameterTypedFirstOrderFunction<BooleanValue, Bag<GeometryValue>>{

        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + NAME_SUFFIX_SET_EQUALS;

        public SetEquals()
        {
            super(ID, StandardDatatypes.BOOLEAN, true, List.of(GeometryValue.DATATYPE.getBagDatatype()));
        }
        /**
         * Constructor that creates a function from its signature definition
         *
         * @param name           function name
         * @param returnType     function return type
         * @param varargs        true iff the function takes a variable number of arguments
         * @param parameterTypes function parameter types. Note: the "? extends" allows using {@link BagDatatype}.
         * @throws IllegalArgumentException if ( {@code name == null || returnType == null || parameterTypes == null || parameterTypes.size() < 1 })
         */
        public SetEquals(String name, Datatype<BooleanValue> returnType, boolean varargs, List<? extends Datatype<Bag<GeometryValue>>> parameterTypes) throws IllegalArgumentException {
            super(name, returnType, varargs, parameterTypes);
        }

        @Override
        public FirstOrderFunctionCall<BooleanValue> newCall(List<Expression<?>> argExpressions, Datatype<?>... remainingArgTypes) throws IllegalArgumentException {
            return new BaseFirstOrderFunctionCall.EagerBagEval<>(functionSignature, argExpressions) {

                @Override
                protected BooleanValue evaluate(final Bag<GeometryValue>[] bagArgs) throws IndeterminateEvaluationException {
                    if (bagArgs.length != 2)
                        throw new IllegalArgumentException("Function " + ID + " requires exactly two bag arguments but given " + bagArgs.length);

                    return new BooleanValue(eval(bagArgs[0], bagArgs[1]));
                }
            };
        }

        private <V extends AttributeValue> boolean eval(final Bag<V> bag0, final Bag<V> bag1) throws IndeterminateEvaluationException {
            final Iterator<V> g0i = bag0.iterator();
            final Iterator<V> g1i = bag1.iterator();

            if (bag0.size() != bag1.size())
                return false;

            UtilityFunctions uf = new UtilityFunctions();
            while (g0i.hasNext()) {
                final GeometryValue gv0 = (GeometryValue) g0i.next();
                Geometry g0 = gv0.getGeometry();
                boolean status = false;
                while (g1i.hasNext()) {
                    final GeometryValue gv1 = (GeometryValue) g1i.next();
                    Geometry g1 = gv1.getGeometry();
                    uf.ensurePrecision(g0, g1);
                    if (g0.getSRID() != g1.getSRID()) {
                        TransformGeometry tg = new TransformGeometry();
                        tg.transformCRS(g0, g1);
                    }
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
                Geometry g1 = gv1.getGeometry();
                boolean status = false;
                while (g0i.hasNext()) {
                    final GeometryValue gv0 = (GeometryValue) g0i.next();
                    Geometry g0 = gv0.getGeometry();
                    uf.ensurePrecision(g0, g1);
                    if (g1.getSRID() != g0.getSRID()) {
                        TransformGeometry tg = new TransformGeometry();
                        tg.transformCRS(g1, g0);
                    }
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
        public static final String ID = GeometryValue.FACTORY.getDatatype().getFunctionIdPrefix() + NAME_SUFFIX_BAG;

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
                protected Bag<GeometryValue> evaluate(final Deque<GeometryValue> args)  throws IllegalArgumentException {
                    int srid = 0;
                    List<GeometryValue> gvu = new ArrayList<GeometryValue>();
                    final Iterator<GeometryValue> i = args.iterator();
                    while (i.hasNext()) {
                        GeometryValue gv = i.next();
                        // All geometries must have the same SRS
                        if (srid == 0)
                            srid = gv.getGeometry().getSRID();
                        else if (srid != gv.getGeometry().getSRID())
                            throw new IllegalArgumentException("Function " + ID + " requires each bag member to have same SRS");

                        gvu.add(gv);
                    }

                    return Bags.newBag(GeometryValue.FACTORY.getDatatype(), gvu);

                }
            };
        }
    }


}
