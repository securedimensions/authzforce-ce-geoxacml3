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

import de.securedimensions.geoxacml3.datatype.GeometryValue;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.ow2.authzforce.core.pdp.api.EvaluationContext;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;
import org.ow2.authzforce.core.pdp.api.expression.ConstantPrimitiveAttributeValueExpression;
import org.ow2.authzforce.core.pdp.api.expression.Expression;
import org.ow2.authzforce.core.pdp.api.expression.ExpressionFactory;
import org.ow2.authzforce.core.pdp.api.expression.FunctionExpression;
import org.ow2.authzforce.core.pdp.api.func.Function;
import org.ow2.authzforce.core.pdp.api.func.FunctionCall;

import org.ow2.authzforce.core.pdp.api.value.*;
import org.ow2.authzforce.core.pdp.impl.expression.DepthLimitingExpressionFactory;
import org.ow2.authzforce.xacml.identifiers.XacmlStatusCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * An abstract class to easily test a function evaluation, according to a given function name, a list of arguments, and expected result. In order to perform a function test, simply extend this class
 * and give the test values on construction.
 */
public abstract class GeometryFunctionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeometryFunctionTest.class);

    /**
     * GeoXACML standard Expression factory/parser
     */
    private static final ExpressionFactory GEOMETRY_EXPRESSION_FACTORY;

    static {
        try {
            GEOMETRY_EXPRESSION_FACTORY = new DepthLimitingExpressionFactory(StandardAttributeValueFactories.getRegistry(true, Optional.empty()),
                    GeometryFunction.getRegistry(true, StandardAttributeValueFactories.BIG_INTEGER), 0, false, false, Optional.empty());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    private final Value expectedResult;
    private final String toString;
    private final boolean areBagsComparedAsSets;
    private FunctionCall<?> funcCall;
    private boolean isTestOkBeforeFuncCall = false;


    /**
     * Creates instance
     *
     * @param functionName      The fully qualified name of the function to be tested. The function must be supported by the StandardFunctionRegistry.
     * @param inputs            The list of the function arguments as expressions, in order.
     * @param expectedResult    The expected function evaluation result, according to the given inputs; null if evaluation expected to throw an error (IndeterminateEvaluationException)
     * @param compareBagsAsSets true iff result bags should be compared as sets for equality check
     */
    private GeometryFunctionTest(final String functionName, final List<Expression<?>> inputs, final boolean compareBagsAsSets, final Value expectedResult) {
        // Determine whether this is a higher-order function, i.e. first parameter is a sub-function
        final Datatype<? extends AttributeValue> subFuncReturnType;
        if (inputs.isEmpty()) {
            subFuncReturnType = null;
        } else {
            final Expression<?> xpr0 = inputs.get(0);
            if (xpr0 instanceof FunctionExpression) {
                subFuncReturnType = ((FunctionExpression) xpr0).getValue().get().getReturnType();
            } else {
                subFuncReturnType = null;
            }
        }

        FunctionExpression functionExp = GEOMETRY_EXPRESSION_FACTORY.getFunction(functionName, subFuncReturnType);
        if (functionExp == null) {
            throw new IllegalArgumentException("Function " + functionName + " not valid/supported "
                    + (subFuncReturnType == null ? "as first-order function" : "as higher-order function with sub-function return type = " + subFuncReturnType));
        }

        final Function<?> function = functionExp.getValue().get();

        try {
            funcCall = function.newCall(inputs);
        } catch (final IllegalArgumentException e) {
            /*
             * Some syntax errors might be caught at initialization time, which is expected if expectedResult == null
             */
            if (expectedResult != null) {
                /*
                 * IllegalArgumentException should not have been thrown, since we expect a result of the function call
                 */
                throw new RuntimeException("expectedResult != null but invalid args in test definition prevented the function call", e);
            }

            funcCall = null;
            // expectedResult == null
            isTestOkBeforeFuncCall = true;
        }

        /*
         * If test not yet OK, we need to run the function call (funcCall.evaluate(...)), so funcCall must be defined
         */
        if (!isTestOkBeforeFuncCall && funcCall == null) {
            throw new RuntimeException("Failed to initialize function call for unknown reason");
        }

        this.expectedResult = expectedResult;
        this.toString = function + "( " + inputs + " )";

        this.areBagsComparedAsSets = compareBagsAsSets;
    }

    // @Before
    // public void skipIfFunctionNotSupported()
    // {
    // // assume test OK if function not supported -> skip it
    // org.junit.Assume.assumeTrue(function == null);
    // }

    /**
     * Creates instance
     *
     * @param functionName      The fully qualified name of the function to be tested. The function must be supported by the StandardFunctionRegistry.
     * @param subFunctionName   (optional) sub-function specified iff {@code functionName} corresponds to a higher-order function; else null
     * @param inputs            The list of the function arguments as constant values, in order. Specify a null argument to indicate it is undefined. It will be considered as Indeterminate (wrapped in a Expression
     *                          that always evaluate to Indeterminate result). This is useful to test specific function behavior when one (or more) of the arguments is indeterminate; e.g. logical or/and/n-of
     *                          functions are able to return False/True even if some of the arguments are Indeterminate.
     * @param expectedResult    The expected function evaluation result, according to the given inputs; null if evaluation expected to throw an error (IndeterminateEvaluationException)
     * @param compareBagsAsSets true iff result bags should be compared as sets for equality check
     */
    public GeometryFunctionTest(final String functionName, final String subFunctionName, final List<Value> inputs, final boolean compareBagsAsSets, final Value expectedResult) {
        this(functionName, toExpressions(subFunctionName, inputs), compareBagsAsSets, expectedResult);
    }

    /**
     * Creates instance
     *
     * @param functionName    The fully qualified name of the function to be tested. The function must be supported by the StandardFunctionRegistry.
     * @param subFunctionName (optional) sub-function specified iff {@code functionName} corresponds to a higher-order function; else null
     * @param inputs          The list of the function arguments, as constant values, in order.
     * @param expectedResult  The expected function evaluation result, according to the given inputs; null if evaluation expected to throw an error (IndeterminateEvaluationException)
     */
    public GeometryFunctionTest(final String functionName, final String subFunctionName, final List<Value> inputs, final Value expectedResult) {
        this(functionName, subFunctionName, inputs, false, expectedResult);
    }

    private static <V extends AttributeValue> Expression<?> createValueExpression(final Datatype<V> datatype, final AttributeValue rawValue) {
        // static expression only if not xpathExpression
        return new ConstantPrimitiveAttributeValueExpression<>(datatype, datatype.cast(rawValue));
    }

    // private static <V extends Value> IndeterminateExpression<V> newIndeterminateExpression

    private static <V extends Bag<?>> Expression<?> createValueExpression(final Datatype<V> datatype, final Bag<?> rawValue) {
        return new GeometryBagValueExpression<>(datatype, datatype.cast(rawValue));
    }

    private static List<Expression<?>> toExpressions(final String subFunctionName, final List<Value> values) {
        final List<Expression<?>> inputExpressions = new ArrayList<>();
        if (subFunctionName != null) {
            // sub-function of higher-order function
            final FunctionExpression subFuncExp = GEOMETRY_EXPRESSION_FACTORY.getFunction(subFunctionName);
            if (subFuncExp == null) {
                throw new UnsupportedOperationException("Function " + subFunctionName + " not valid/supported (as first-order function)");
            }

            inputExpressions.add(subFuncExp);
        }

        final AttributeValueFactoryRegistry stdDatatypeFactoryRegistry = StandardAttributeValueFactories.getRegistry(true, Optional.empty());
        for (final Value val : values) {
            final Expression<?> valExpr;
            if (val instanceof AttributeValue) {
                final AttributeValue primVal = (AttributeValue) val;
                if (primVal instanceof GeometryValue)
                    valExpr = createValueExpression(GeometryValue.FACTORY.getDatatype(), primVal);
                else if (primVal instanceof DoubleValue)
                    valExpr = createValueExpression(StandardDatatypes.DOUBLE, primVal);
                else if (primVal instanceof IntegerValue)
                    valExpr = createValueExpression(StandardDatatypes.INTEGER, primVal);
                else
                    valExpr = createValueExpression(StandardDatatypes.STRING, primVal);
            } else if (val instanceof Bag) {
                final Bag<?> bagVal = (Bag<?>) val;
                final AttributeValueFactory<?> datatypeFactory = stdDatatypeFactoryRegistry.getExtension(bagVal.getElementDatatype().getId());
                if (datatypeFactory != null)
                    valExpr = createValueExpression(datatypeFactory.getDatatype().getBagDatatype(), bagVal);
                else
                    valExpr = createValueExpression(GeometryValue.FACTORY.getDatatype().getBagDatatype(), bagVal);
            } else {
                throw new UnsupportedOperationException("Unsupported type of Value: " + val.getClass());
            }

            inputExpressions.add(valExpr);
        }

        return inputExpressions;
    }

    private static Set<PrimitiveValue> bagToSet(final Bag<?> bag) {
        final Set<PrimitiveValue> set = new HashSet<>();
        for (final PrimitiveValue val : bag) {
            set.add(val);
        }

        return set;
    }

    @Test
    public void testEvaluate() throws IndeterminateEvaluationException {
        if (isTestOkBeforeFuncCall) {
            /*
             * Test already OK (syntax error was expected and occurred when creating the function call already), no need to carry on the function call
             */
            return;
        }

        /*
         * Use null context as all inputs given as values in function tests, therefore already provided as inputs to function call
         */
        try {
            /*
             * funcCall != null (see constructor)
             */
            final Value actualResult = funcCall.evaluate(null, Optional.empty());
            if (expectedResult instanceof Bag && actualResult instanceof Bag && areBagsComparedAsSets) {
                final Set<?> expectedSet = bagToSet((Bag<?>) expectedResult);
                final Set<?> actualSet = bagToSet((Bag<?>) actualResult);
                Assert.assertEquals(toString, expectedSet, actualSet);
            } else if (expectedResult != null) {
                if (expectedResult instanceof BooleanValue)
                    Assert.assertEquals(toString, expectedResult, actualResult);
                else if (expectedResult instanceof IntegerValue)
                    Assert.assertEquals(toString, expectedResult, actualResult);
                else if (expectedResult instanceof DoubleValue)
                    Assert.assertEquals(toString, expectedResult, actualResult);
                else if (expectedResult instanceof StringValue)
                    Assert.assertEquals(toString, expectedResult, actualResult);
                else if (expectedResult instanceof GeometryValue) {
                    Geometry expectedG = ((GeometryValue) expectedResult).getGeometry();
                    Geometry actualG = ((GeometryValue) actualResult).getGeometry();
                    boolean cond = true;
                    if ((actualG instanceof GeometryCollection) && (expectedG instanceof GeometryCollection)) {
                        GeometryCollection actualGC = (GeometryCollection) actualG;
                        GeometryCollection expectedGC = (GeometryCollection) expectedG;

                        int actualN = actualGC.getNumGeometries();
                        int expextedN = expectedGC.getNumGeometries();
                        if (actualN != expextedN)
                            Assert.assertTrue(toString, false);

                        for (int ix = 0; ix < actualN; ix++) {
                            cond = (
                                    ((expectedGC.getGeometryN(ix).equals(actualGC.getGeometryN(ix))) &&
                                            (expectedGC.getGeometryN(ix).getSRID() == actualGC.getGeometryN(ix).getSRID())));
                            if (cond == false)
                                break;
                        }
                    } else if (expectedG.isEmpty() && actualG.isEmpty())
                        cond = true;
                    else
                        cond = ((expectedG.equals(actualG)) && (expectedG.getSRID() == actualG.getSRID()));

                    Assert.assertTrue(toString, cond);
                }
                else if (expectedResult instanceof Bag && actualResult instanceof Bag)
                {
                    Iterator<GeometryValue> ie = ((Bag<GeometryValue>) expectedResult).iterator();
                    Iterator<GeometryValue> ia = ((Bag<GeometryValue>) actualResult).iterator();
                    boolean cond = true;
                    while (ie.hasNext() && ia.hasNext())
                    {
                        Geometry ge = ie.next().getGeometry();
                        Geometry ga = ia.next().getGeometry();
                        cond = ((ge.equals(ga)) && (ge.getSRID() == ga.getSRID()));
                        if (cond == false)
                            break;
                    }
                    Assert.assertTrue(toString, cond);
                }
                else
                    Assert.assertTrue(toString, false);
            }
        } catch (final IndeterminateEvaluationException e) {
            if (expectedResult != null) {
                // unexpected error
                throw e;
            }
        }
        catch (final IllegalArgumentException e) {
            if (expectedResult != null) {
                // unexpected error
                throw e;
            }
        }
    }

    private static final class IndeterminateExpression<V extends Value> implements Expression<V> {
        private final Datatype<V> returnType;

        private IndeterminateExpression(final Datatype<V> returnType) {
            this.returnType = returnType;
        }

        @Override
        public Datatype<V> getReturnType() {
            return returnType;
        }

        @Override
        public V evaluate(final EvaluationContext context, Optional<EvaluationContext> mdpContext) throws IndeterminateEvaluationException {
            throw new IndeterminateEvaluationException("Missing attribute", XacmlStatusCode.MISSING_ATTRIBUTE.value());
        }

        @Override
        public Optional<V> getValue() {
            return Optional.empty();
        }

    }
}
