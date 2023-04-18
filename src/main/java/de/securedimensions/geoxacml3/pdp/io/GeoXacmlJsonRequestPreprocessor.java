/*
 * Copyright 2012-2022 THALES.
 *
 * This file is part of AuthzForce CE.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.securedimensions.geoxacml3.pdp.io;

import com.google.common.collect.ImmutableList;
import de.securedimensions.geoxacml3.identifiers.Definitions;
import net.sf.saxon.s9api.XdmNode;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attribute;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attributes;
import org.everit.json.schema.ValidationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ow2.authzforce.core.pdp.api.*;
import org.ow2.authzforce.core.pdp.api.expression.BasicImmutableXPathCompilerProxy;
import org.ow2.authzforce.core.pdp.api.expression.XPathCompilerProxy;
import org.ow2.authzforce.core.pdp.api.io.*;
import org.ow2.authzforce.core.pdp.api.value.AttributeBag;
import org.ow2.authzforce.core.pdp.api.value.AttributeValueFactoryRegistry;
import org.ow2.authzforce.core.pdp.io.xacml.json.BaseXacmlJsonRequestPreprocessor;
import org.ow2.authzforce.core.pdp.io.xacml.json.IndividualXacmlJsonRequest;
import org.ow2.authzforce.xacml.identifiers.XPathVersion;
import org.ow2.authzforce.xacml.identifiers.XacmlStatusCode;
import org.ow2.authzforce.xacml.json.model.XacmlJsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

import static de.securedimensions.geoxacml3.pdp.io.GeoXACMLRequestPreprocessor.XACML_ATTRIBUTE_ID_QNAME;
import static de.securedimensions.geoxacml3.pdp.io.GeoXACMLRequestPreprocessor.XACML_CATEGORY_ID_QNAME;

/**
 * Default GeoXACML/JSON - according to XACML JSON Profile - Request preprocessor for Individual Decision Requests only (no support of Multiple Decision Profile in particular)
 *
 * @version $Id: $
 */
public final class GeoXacmlJsonRequestPreprocessor implements DecisionRequestPreprocessor<JSONObject, IndividualXacmlJsonRequest>
{
    private static final DecisionRequestFactory<ImmutableDecisionRequest> DEFAULT_REQUEST_FACTORY = ImmutableDecisionRequest::getInstance;

    private static final IndeterminateEvaluationException MISSING_REQUEST_OBJECT_EXCEPTION = new IndeterminateEvaluationException("Missing Request object", XacmlStatusCode.SYNTAX_ERROR.value());

    private static final Logger LOGGER = LoggerFactory.getLogger(GeoXacmlJsonRequestPreprocessor.class);

    private static final IllegalArgumentException NULL_REQUEST_ARGUMENT_EXCEPTION = new IllegalArgumentException("Null request arg");
    private static final UnsupportedOperationException UNSUPPORTED_MODE_EXCEPTION = new UnsupportedOperationException(
            "Unsupported BaseXacmlJaxbRequestPreprocessor mode: allowAttributeDuplicates == false && strictAttributeIssuerMatch == false");
    private static final IndeterminateEvaluationException INVALID_REQUEST_CATEGORY_ARRAY_ELEMENT_TYPE_EXCEPTION = new IndeterminateEvaluationException(
            "Invalid Request/Category array: the type of one of the items is invalid (not JSON object as expected)", XacmlStatusCode.SYNTAX_ERROR.value());

    /**
     * Indeterminate exception to be thrown iff CombinedDecision element is not supported
     */
    private static final IndeterminateEvaluationException UNSUPPORTED_COMBINED_DECISION_EXCEPTION = new IndeterminateEvaluationException("Unsupported CombinedDecision value in Request: 'true'",
            XacmlStatusCode.SYNTAX_ERROR.value());
    private static final ImmutableXacmlStatus INVALID_REQ_ERR_STATUS = new ImmutableXacmlStatus(XacmlStatusCode.SYNTAX_ERROR.value(), Optional.of("Invalid Request"));

    /**
     * Indeterminate exception to be thrown iff MultiRequests element not supported by the request preprocessor
     */
    private static final IndeterminateEvaluationException UNSUPPORTED_MULTI_REQUESTS_EXCEPTION = new IndeterminateEvaluationException("Unsupported element in Request: <MultiRequests>",
            XacmlStatusCode.SYNTAX_ERROR.value());

    private final SingleCategoryXacmlAttributesParser.Factory<JSONObject> xacmlAttrsParserFactory;

    private final boolean isCombinedDecisionSupported;

    @Override
    public Class<JSONObject> getInputRequestType() {
        return JSONObject.class;
    }

    @Override
    public Class<IndividualXacmlJsonRequest> getOutputRequestType() {
        return IndividualXacmlJsonRequest.class;
    }



    /**
     *
     * Factory for this type of request preprocessor that allows duplicate &lt;Attribute&gt; with same meta-data in the same &lt;Attributes&gt; element of a Request (complying with XACML 3.0 core
     * spec, §7.3.3) but using JSON-Profile-defined format.
     *
     */
    public static final class LaxVariantFactory extends BaseXacmlJsonRequestPreprocessor.Factory
    {
        /**
         * Request preprocessor ID, as returned by {@link #getId()}
         */
        public static final String ID = "urn:de:securedimensions:feature:pdp:request-preproc:geoxacml-json:default-lax";

        /**
         * Constructor
         */
        public LaxVariantFactory()
        {
            super(ID);
        }

        @Override
        public DecisionRequestPreprocessor<JSONObject, IndividualXacmlJsonRequest> getInstance(final AttributeValueFactoryRegistry datatypeFactoryRegistry, final boolean strictAttributeIssuerMatch,
                                                                                               final boolean requireContentForXPath, final Set<String> extraPdpFeatures)
        {
            return new GeoXacmlJsonRequestPreprocessor(datatypeFactoryRegistry, DEFAULT_REQUEST_FACTORY, strictAttributeIssuerMatch, true, requireContentForXPath/* , xmlProcessor */,
                    extraPdpFeatures);
        }

        /**
         * Singleton instance of this factory
         *
         */
        public static final DecisionRequestPreprocessor.Factory<JSONObject, IndividualXacmlJsonRequest> INSTANCE = new LaxVariantFactory();
    }

    /**
     *
     * Factory for this type of request preprocessor that does NOT allow duplicate &lt;Attribute&gt; with same meta-data in the same &lt;Attributes&gt; element of a Request (NOT complying fully with
     * XACML 3.0 core spec, §7.3.3) but using JSON-Profile-defined format.
     *
     */
    public static final class StrictVariantFactory extends BaseXacmlJsonRequestPreprocessor.Factory
    {
        /**
         * Request preprocessor ID, as returned by {@link #getId()}
         */
        public static final String ID = "urn:de:securedimensions:feature:pdp:request-preproc:geoxacml-json:default-strict";

        /**
         * Constructor
         */
        public StrictVariantFactory()
        {
            super(ID);
        }

        @Override
        public DecisionRequestPreprocessor<JSONObject, IndividualXacmlJsonRequest> getInstance(final AttributeValueFactoryRegistry datatypeFactoryRegistry, final boolean strictAttributeIssuerMatch,
                                                                                               final boolean requireContentForXPath, final Set<String> extraPdpFeatures)
        {
            return new GeoXacmlJsonRequestPreprocessor(datatypeFactoryRegistry, DEFAULT_REQUEST_FACTORY, strictAttributeIssuerMatch, false, requireContentForXPath/* , xmlProcessor */,
                    extraPdpFeatures);
        }
    }

    private final DecisionRequestFactory<ImmutableDecisionRequest> reqFactory;

    /**
     * Creates instance of default request preprocessor
     *
     * @param datatypeFactoryRegistry
     *            attribute datatype registry
     * @param requestFactory
     *            decision request factory
     * @param strictAttributeIssuerMatch
     *            true iff strict attribute Issuer match must be enforced (in particular request attributes with empty Issuer only match corresponding AttributeDesignators with empty Issuer)
     * @param allowAttributeDuplicates
     *            true iff duplicate Attribute (with same metadata) elements in Request (for multi-valued attributes) must be allowed
     * @param requireContentForXPath
     *            true iff Content elements must be parsed, else ignored
     * @param extraPdpFeatures
     *            extra - not mandatory per XACML 3.0 core specification - features supported by the PDP engine. This preprocessor checks whether it is supported by the PDP before processing the
     *            request further.
     */
    public GeoXacmlJsonRequestPreprocessor(final AttributeValueFactoryRegistry datatypeFactoryRegistry, final DecisionRequestFactory<ImmutableDecisionRequest> requestFactory,
                                                      final boolean strictAttributeIssuerMatch, final boolean allowAttributeDuplicates, final boolean requireContentForXPath/* , final Processor xmlProcessor */,
                                                      final Set<String> extraPdpFeatures)
    {
        assert requestFactory != null;
        reqFactory = requestFactory;

        final NamedXacmlAttributeParser<JSONObject> namedXacmlAttParser = new GeoXacmlJsonParsingUtils.NamedXacmlJsonAttributeParser(datatypeFactoryRegistry);
        if (allowAttributeDuplicates)
        {
            final XacmlRequestAttributeParser<JSONObject, MutableAttributeBag<?>> xacmlAttributeParser = strictAttributeIssuerMatch ? new NonIssuedLikeIssuedLaxXacmlAttributeParser<>(
                    namedXacmlAttParser) : new IssuedToNonIssuedCopyingLaxXacmlAttributeParser<>(namedXacmlAttParser);
            this.xacmlAttrsParserFactory = requireContentForXPath ? new GeoXacmlJsonParsingUtils.FullXacmlJsonAttributesParserFactory<>(xacmlAttributeParser,
                    SingleCategoryAttributes.MUTABLE_TO_CONSTANT_ATTRIBUTE_ITERATOR_CONVERTER/* , xmlProcessor */) : new GeoXacmlJsonParsingUtils.ContentSkippingXacmlJsonAttributesParserFactory<>(xacmlAttributeParser,
                    SingleCategoryAttributes.MUTABLE_TO_CONSTANT_ATTRIBUTE_ITERATOR_CONVERTER);
        }
        else // allowAttributeDuplicates == false
            if (strictAttributeIssuerMatch)
            {
                final XacmlRequestAttributeParser<JSONObject, AttributeBag<?>> xacmlAttributeParser = new NonIssuedLikeIssuedStrictXacmlAttributeParser<>(namedXacmlAttParser);
                this.xacmlAttrsParserFactory = requireContentForXPath ? new GeoXacmlJsonParsingUtils.FullXacmlJsonAttributesParserFactory<>(xacmlAttributeParser, SingleCategoryAttributes.IDENTITY_ATTRIBUTE_ITERATOR_CONVERTER/*
                 * ,
                 * xmlProcessor
                 */)
                        : new GeoXacmlJsonParsingUtils.ContentSkippingXacmlJsonAttributesParserFactory<>(xacmlAttributeParser, SingleCategoryAttributes.IDENTITY_ATTRIBUTE_ITERATOR_CONVERTER);
            }
            else
            {
                /*
                 * allowAttributeDuplicates == false && strictAttributeIssuerMatch == false is not supported, because it would require using mutable bags for "Issuer-less" attributes (updated for each
                 * possible Attribute with same meta-data except a defined Issuer), whereas the goal of 'allowAttributeDuplicates == false' is to use immutable Bags in the first place, i.e. to avoid going
                 * through mutable bags. A solution would consist in creating two collections of attributes, one with immutable bags, and the other with mutable ones for Issuer-less attributes. However, we
                 * consider it is not worth providing an implementation for this natively, so far. Can always been a custom RequestPreprocessor provided as an extension.
                 */
                throw UNSUPPORTED_MODE_EXCEPTION;
            }

        this.isCombinedDecisionSupported = extraPdpFeatures.contains(DecisionResultPostprocessor.Features.XACML_MULTIPLE_DECISION_PROFILE_COMBINED_DECISION);
    }

    public List<IndividualXacmlJsonRequest> process(final JSONArray jsonArrayOfRequestAttributeCategoryObjects, final SingleCategoryXacmlAttributesParser<JSONObject> xacmlAttrsParser,
                                                    final boolean isApplicablePolicyIdListReturned, final boolean combinedDecision, final Optional<XPathCompilerProxy> xPathCompiler, final Map<String, String> namespaceURIsByPrefix)
            throws IndeterminateEvaluationException
    {
        final Map<AttributeFqn, AttributeBag<?>> namedAttributes = HashCollections.newUpdatableMap(jsonArrayOfRequestAttributeCategoryObjects.length());
        /*
         * TODO: Content object not supported yet (optional in XACML)
         */
        final Map<String, XdmNode> extraContentsByCategory = Collections.emptyMap() /* HashCollections.newUpdatableMap(requestAttributeCategoryObjects.length()) */;

        /*
         * requestAttributeCategoryObjectsIncludedInResult.size() <= jsonArrayOfRequestAttributeCategoryObjects.size()
         */
        final List<JSONObject> requestAttributeCategoryObjectsIncludedInResult = new ArrayList<>(jsonArrayOfRequestAttributeCategoryObjects.length());

        for (final Object requestAttributeCategoryObject : jsonArrayOfRequestAttributeCategoryObjects)
        {
            if (!(requestAttributeCategoryObject instanceof JSONObject))
            {
                throw INVALID_REQUEST_CATEGORY_ARRAY_ELEMENT_TYPE_EXCEPTION;
            }

            final JSONObject requestAttCatJsonObj = (JSONObject) requestAttributeCategoryObject;
            final SingleCategoryAttributes<?, JSONObject> categorySpecificAttributes = this.xacmlAttrsParserFactory.getInstance().parseAttributes(requestAttCatJsonObj, xPathCompiler);
            if (categorySpecificAttributes == null)
            {
                // skip this empty Attributes
                continue;
            }

            /*
             * TODO: Content object not supported yet (optional in XACML)
             */
            // final XdmNode newContentNode = categorySpecificAttributes.getExtraContent();
            // if (newContentNode != null)
            // {
            // final XdmNode duplicate = extraContentsByCategory.putIfAbsent(categoryName, newContentNode);
            // /*
            // * No support for Multiple Decision Profile -> no support for repeated categories as specified in Multiple Decision Profile. So we must check duplicate attribute categories.
            // */
            // if (duplicate != null)
            // {
            // throw new IndeterminateEvaluationException("Unsupported repetition of Attributes[@Category='" + categoryName
            // + "'] (feature 'urn:oasis:names:tc:xacml:3.0:profile:multiple:repeated-attribute-categories' is not supported)", StatusHelper.STATUS_SYNTAX_ERROR);
            // }
            // }

            /*
             * Convert growable (therefore mutable) bag of attribute values to immutable ones. Indeed, we must guarantee that attribute values remain constant during the evaluation of the request, as
             * mandated by the XACML spec, section 7.3.5: <p> <i>
             * "Regardless of any dynamic modifications of the request context during policy evaluation, the PDP SHALL behave as if each bag of attribute values is fully populated in the context before it is first tested, and is thereafter immutable during evaluation. (That is, every subsequent test of that attribute shall use the same bag of values that was initially tested.)"
             * </i></p>
             */
            for (final Entry<AttributeFqn, AttributeBag<?>> attrEntry : categorySpecificAttributes)
            {
                namedAttributes.put(attrEntry.getKey(), attrEntry.getValue());
            }

            final JSONObject catSpecificAttrsToIncludeInResult = categorySpecificAttributes.getAttributesToIncludeInResult();
            if (catSpecificAttrsToIncludeInResult != null)
            {
                requestAttributeCategoryObjectsIncludedInResult.add(catSpecificAttrsToIncludeInResult);
            }
        }

        final ImmutableDecisionRequest pdpEngineReq = reqFactory.getInstance(namedAttributes, extraContentsByCategory, isApplicablePolicyIdListReturned);
        return Collections.singletonList(new IndividualXacmlJsonRequest(pdpEngineReq, ImmutableList.copyOf(requestAttributeCategoryObjectsIncludedInResult)));
    }

    public List<IndividualXacmlJaxbRequest> process(List<Attributes> attributesList, SingleCategoryXacmlAttributesParser<Attributes> xacmlAttrsParser, boolean isApplicablePolicyIdListReturned, boolean combinedDecision, Optional<XPathCompilerProxy> xPathCompiler, Map<String, String> namespaceURIsByPrefix) throws IndeterminateEvaluationException {
        Map<AttributeFqn, AttributeBag<?>> namedAttributes = HashCollections.newUpdatableMap(attributesList.size());
        Map<String, XdmNode> extraContentsByCategory = HashCollections.newUpdatableMap(attributesList.size());
        List<Attributes> attributesToIncludeInResult = new ArrayList(attributesList.size());
        Iterator ali = attributesList.iterator();

        while (true) {
            SingleCategoryAttributes categorySpecificAttributes;
            do {
                if (!ali.hasNext()) {
                    return Collections.singletonList(new IndividualXacmlJaxbRequest(this.reqFactory.getInstance(namedAttributes, extraContentsByCategory, isApplicablePolicyIdListReturned), ImmutableList.copyOf(attributesToIncludeInResult)));
                }

                Attributes jaxbAttributes = (Attributes) ali.next();
                // Add AttributeId and SubjectId to otherXMLAttribute
                Iterator<Attribute> ia = jaxbAttributes.getAttributes().listIterator();
                while (ia.hasNext()) {
                    Attribute attribute = ia.next();
                    ListIterator<AttributeValueType> iav = attribute.getAttributeValues().listIterator();
                    while (iav.hasNext()) {
                        AttributeValueType av = iav.next();
                        av.getOtherAttributes().put(XACML_ATTRIBUTE_ID_QNAME, attribute.getAttributeId());
                        av.getOtherAttributes().put(XACML_CATEGORY_ID_QNAME, jaxbAttributes.getCategory());
                        av.getOtherAttributes().put(Definitions.ATTR_SOURCE, Definitions.ATTR_SOURCE_DESIGNATOR);
                    }
                }
                categorySpecificAttributes = xacmlAttrsParser.parseAttributes(jaxbAttributes, xPathCompiler);
            } while (categorySpecificAttributes == null);

            String categoryId = categorySpecificAttributes.getCategoryId();
            XdmNode newContentNode = categorySpecificAttributes.getExtraContent();
            if (newContentNode != null) {
                XdmNode duplicate = extraContentsByCategory.putIfAbsent(categoryId, newContentNode);
                if (duplicate != null) {
                    throw new IndeterminateEvaluationException("Unsupported repetition of Attributes[@Category='" + categoryId + "'] (feature 'urn:oasis:names:tc:xacml:3.0:profile:multiple:repeated-attribute-categories' is not supported)", XacmlStatusCode.SYNTAX_ERROR.value());
                }
            }

            Iterator csai = categorySpecificAttributes.iterator();

            while (csai.hasNext()) {
                Map.Entry<AttributeFqn, AttributeBag<?>> attrEntry = (Map.Entry) csai.next();
                namedAttributes.put(attrEntry.getKey(), attrEntry.getValue());
            }

            Attributes catSpecificAttrsToIncludeInResult = (Attributes) categorySpecificAttributes.getAttributesToIncludeInResult();
            if (catSpecificAttrsToIncludeInResult != null) {
                attributesToIncludeInResult.add(catSpecificAttrsToIncludeInResult);
            }
        }
    }

    @Override
    public List<IndividualXacmlJsonRequest> process(final JSONObject request, final Map<String, String> namespaceURIsByPrefix) throws IndeterminateEvaluationException
    {
        if (request == null)
        {
            throw NULL_REQUEST_ARGUMENT_EXCEPTION;
        }

        try
        {
            GeoXacmlJsonUtils.REQUEST_SCHEMA.validate(request);
            //XacmlJsonUtils.REQUEST_SCHEMA.validate(request);
        }
        catch (final ValidationException e)
        {
            LOGGER.debug(e.toJSON().toString(4));
            throw new IndeterminateEvaluationException(INVALID_REQ_ERR_STATUS, e);
        }

        final JSONObject requestJsonObj = request.optJSONObject("Request");
        if (requestJsonObj == null)
        {
            throw MISSING_REQUEST_OBJECT_EXCEPTION;
        }

        /*
         * No support for MultiRequests (§2.4 of Multiple Decision Profile).
         */
        if (requestJsonObj.has("MultiRequests"))
        {
            /*
             * According to 7.19.1 Unsupported functionality, return Indeterminate with syntax-error code for unsupported element
             */
            throw UNSUPPORTED_MULTI_REQUESTS_EXCEPTION;
        }

        /*
         * No support for CombinedDecision = true if result processor does not support it. (The use of the CombinedDecision attribute is specified in Multiple Decision Profile.)
         */
        final boolean combinedDecisionRequested;
        if (requestJsonObj.optBoolean("CombinedDecision", false))
        {
            if (!this.isCombinedDecisionSupported)
            {
                /*
                 * According to XACML core spec, 5.42, <i>If the PDP does not implement the relevant functionality in [Multiple Decision Profile], then the PDP must return an Indeterminate with a status
                 * code of urn:oasis:names:tc:xacml:1.0:status:processing-error if it receives a request with this attribute set to "true".</i>
                 */
                throw UNSUPPORTED_COMBINED_DECISION_EXCEPTION;
            }

            combinedDecisionRequested = true;
        }
        else
        {
            combinedDecisionRequested = false;
        }

        final boolean returnPolicyIdList = requestJsonObj.optBoolean("ReturnPolicyIdList", false);
        final Map<String, String> newNsPrefixToUriMap;
        final Optional<XPathCompilerProxy> xPathCompiler;
        if(requestJsonObj.has("XPathVersion")) {
            try
            {
                final XPathVersion xPathVersion = XPathVersion.fromURI(requestJsonObj.getString("XPathVersion"));
                xPathCompiler = Optional.of(new BasicImmutableXPathCompilerProxy(xPathVersion, namespaceURIsByPrefix));
				/*
				namespaceURIsByPrefix already held by xPathCompiler and retrievable from it with getDeclaredNamespacePrefixToUriMap().
				 */
                newNsPrefixToUriMap = Map.of();
            } catch(IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid/unsupported XPathVersion in JSON Request/XPathVersion", e);
            }

        } else {
            xPathCompiler = Optional.empty();
            newNsPrefixToUriMap = namespaceURIsByPrefix;
        }

        final SingleCategoryXacmlAttributesParser<JSONObject> xacmlAttrsParser = xacmlAttrsParserFactory.getInstance();
        return process(requestJsonObj.optJSONArray("Category"), xacmlAttrsParser, returnPolicyIdList, combinedDecisionRequested, xPathCompiler, newNsPrefixToUriMap);
    }
}

