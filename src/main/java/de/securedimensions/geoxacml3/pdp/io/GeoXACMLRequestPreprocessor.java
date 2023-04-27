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
package de.securedimensions.geoxacml3.pdp.io;

import com.google.common.collect.ImmutableList;
import de.securedimensions.geoxacml3.datatype.GeometryValue;

import de.securedimensions.geoxacml3.identifiers.Definitions;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attribute;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attributes;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Request;
import org.ow2.authzforce.core.pdp.api.*;
import org.ow2.authzforce.core.pdp.api.expression.XPathCompilerProxy;
import org.ow2.authzforce.core.pdp.api.io.*;
import org.ow2.authzforce.core.pdp.api.value.AttributeBag;
import org.ow2.authzforce.core.pdp.api.value.AttributeValue;
import org.ow2.authzforce.core.pdp.api.value.AttributeValueFactory;
import org.ow2.authzforce.core.pdp.api.value.AttributeValueFactoryRegistry;
import org.ow2.authzforce.core.pdp.impl.io.SingleDecisionXacmlJaxbRequestPreprocessor;
import org.ow2.authzforce.xacml.identifiers.XacmlNodeName;
import org.ow2.authzforce.xacml.identifiers.XacmlStatusCode;
import org.ow2.authzforce.xacml.identifiers.XacmlVersion;

import javax.xml.namespace.QName;
import java.util.*;

public final class GeoXACMLRequestPreprocessor {
    private static final DecisionRequestFactory<ImmutableDecisionRequest> DEFAULT_REQUEST_FACTORY = ImmutableDecisionRequest::getInstance;

    public static QName XACML_ATTRIBUTE_ID_QNAME = new QName(XacmlVersion.V3_0.getNamespace(), "AttributeId");
    public static QName XACML_CATEGORY_ID_QNAME = new QName(XacmlVersion.V3_0.getNamespace(), XacmlNodeName.ATTRIBUTES_CATEGORY.value());
    private static final class CustomNamedXacmlJaxbAttributeParser extends NamedXacmlAttributeParser<Attribute>
    {
        private static final IllegalArgumentException NULL_ATTRIBUTE_CATEGORY_ARGUMENT_EXCEPTION = new IllegalArgumentException("Undefined XACML attribute category");
        private static final IllegalArgumentException NULL_INPUT_ATTRIBUTE_ARGUMENT_EXCEPTION = new IllegalArgumentException("Undefined input XACML attribute arg (inputXacmlAttribute)");
        private static final IllegalArgumentException NO_JAXB_ATTRIBUTE_VALUE_LIST_ARGUMENT_EXCEPTION = new IllegalArgumentException(
                "Input XACML attribute values null/empty (nonEmptyJaxbAttributeValues)");

        private CustomNamedXacmlJaxbAttributeParser(AttributeValueFactoryRegistry attributeValueFactoryRegistry) throws IllegalArgumentException
        {
            super(attributeValueFactoryRegistry);
        }

        private static <AV extends AttributeValue> NamedXacmlAttributeParsingResult<AV> parseNamedAttribute(final AttributeFqn attName, final List<AttributeValueType> nonEmptyInputXacmlAttValues,
                                                                                                            final AttributeValueFactory<AV> attValFactory, final Optional<XPathCompilerProxy> xPathCompiler)
        {
            assert attName != null && nonEmptyInputXacmlAttValues != null && !nonEmptyInputXacmlAttValues.isEmpty() && attValFactory != null;

            final Collection<AV> attValues = new ArrayDeque<>(nonEmptyInputXacmlAttValues.size());
            if (attValFactory.getDatatype().getId().equalsIgnoreCase(Definitions.GEOMETRY)) {
                for (final AttributeValueType inputXacmlAttValue : nonEmptyInputXacmlAttValues) {
                    // Here is the actual custom transformation: adding AttributeId, Category to AttributeValues
                    final Map<QName, String> modifiedXmlAttributes = new HashMap<>(inputXacmlAttValue.getOtherAttributes());
                    modifiedXmlAttributes.put(XACML_CATEGORY_ID_QNAME, attName.getCategory());
                    modifiedXmlAttributes.put(XACML_ATTRIBUTE_ID_QNAME, attName.getId());
                    modifiedXmlAttributes.put(Definitions.ATTR_SOURCE, Definitions.ATTR_SOURCE_DESIGNATOR);
                    final AV resultValue = attValFactory.getInstance(inputXacmlAttValue.getContent(), modifiedXmlAttributes, xPathCompiler);
                    attValues.add(resultValue);
                }
            }
            return new ImmutableNamedXacmlAttributeParsingResult<>(attName, attValFactory.getDatatype(), ImmutableList.copyOf(attValues));
        }

        @Override
        protected NamedXacmlAttributeParsingResult<?> parseNamedAttribute(String attributeCategoryId, Attribute inputXacmlAttribute, Optional<XPathCompilerProxy> xPathCompiler) throws IllegalArgumentException
        {
            if (attributeCategoryId == null)
            {
                throw NULL_ATTRIBUTE_CATEGORY_ARGUMENT_EXCEPTION;
            }

            if (inputXacmlAttribute == null)
            {
                throw NULL_INPUT_ATTRIBUTE_ARGUMENT_EXCEPTION;
            }

            final List<AttributeValueType> inputXacmlAttValues = inputXacmlAttribute.getAttributeValues();
            if (inputXacmlAttValues == null || inputXacmlAttValues.isEmpty())
            {
                throw NO_JAXB_ATTRIBUTE_VALUE_LIST_ARGUMENT_EXCEPTION;
            }

            final AttributeFqn attName = AttributeFqns.newInstance(attributeCategoryId, Optional.ofNullable(inputXacmlAttribute.getIssuer()), inputXacmlAttribute.getAttributeId());

            /*
             * Determine the attribute datatype to make sure it is supported and all values are of the same datatype. Indeed, XACML spec says for Attribute Bags (7.3.2): "There SHALL be no notion of a
             * bag containing bags, or a bag containing values of differing types; i.e., a bag in XACML SHALL contain only values that are of the same data-type."
             * <p>
             * So we can obtain the datatypeURI/datatype class from the first value.
             */
            final AttributeValueFactory<?> attValFactory = getAttributeValueFactory(inputXacmlAttValues.get(0).getDataType(), attName);
            return parseNamedAttribute(attName, inputXacmlAttValues, attValFactory, xPathCompiler);
        }
    }

    public static final class StrictVariantFactory extends BaseXacmlJaxbRequestPreprocessor.Factory {
        public static final String ID = "urn:de:securedimensions:feature:pdp:request-preproc:geoxacml-xml:default-strict";
        public static final DecisionRequestPreprocessor.Factory<Request, IndividualXacmlJaxbRequest> INSTANCE = new StrictVariantFactory();


        public StrictVariantFactory() {
            super(ID);
        }

        @Override
        public DecisionRequestPreprocessor<Request, IndividualXacmlJaxbRequest> getInstance(AttributeValueFactoryRegistry datatypeFactoryRegistry, boolean strictAttributeIssuerMatch, boolean requireContentForXPath, Set<String> extraPdpFeatures) {
            return new SingleDecisionXacmlJaxbRequestPreprocessor(datatypeFactoryRegistry, DEFAULT_REQUEST_FACTORY, strictAttributeIssuerMatch, true, requireContentForXPath, extraPdpFeatures, Optional.of(new CustomNamedXacmlJaxbAttributeParser(datatypeFactoryRegistry)));
        }


    }

    public static final class LaxVariantFactory extends BaseXacmlJaxbRequestPreprocessor.Factory {
        public static final String ID = "urn:de:securedimensions:feature:pdp:request-preproc:geoxacml-xml:default-lax";
        public static final DecisionRequestPreprocessor.Factory<Request, IndividualXacmlJaxbRequest> INSTANCE = new LaxVariantFactory();

        public LaxVariantFactory() {
            super(ID);
        }

        @Override
        public DecisionRequestPreprocessor<Request, IndividualXacmlJaxbRequest> getInstance(AttributeValueFactoryRegistry datatypeFactoryRegistry, boolean strictAttributeIssuerMatch, boolean requireContentForXPath, Set<String> extraPdpFeatures) {
            return new SingleDecisionXacmlJaxbRequestPreprocessor(datatypeFactoryRegistry, DEFAULT_REQUEST_FACTORY, strictAttributeIssuerMatch, true, requireContentForXPath, extraPdpFeatures, Optional.of(new CustomNamedXacmlJaxbAttributeParser(datatypeFactoryRegistry)));
        }

    }
}
