//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package de.securedimensions.geoxacml3.pdp.io;

import com.google.common.collect.ImmutableList;
import de.securedimensions.geoxacml3.datatype.GeometryValue;
import net.sf.saxon.s9api.XdmNode;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attribute;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attributes;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Request;
import org.ow2.authzforce.core.pdp.api.*;
import org.ow2.authzforce.core.pdp.api.expression.XPathCompilerProxy;
import org.ow2.authzforce.core.pdp.api.io.BaseXacmlJaxbRequestPreprocessor;
import org.ow2.authzforce.core.pdp.api.io.IndividualXacmlJaxbRequest;
import org.ow2.authzforce.core.pdp.api.io.SingleCategoryAttributes;
import org.ow2.authzforce.core.pdp.api.io.SingleCategoryXacmlAttributesParser;
import org.ow2.authzforce.core.pdp.api.value.AttributeBag;
import org.ow2.authzforce.core.pdp.api.value.AttributeValueFactoryRegistry;
import org.ow2.authzforce.xacml.identifiers.XacmlStatusCode;

import java.util.*;

public final class GeoXACMLRequestPreprocessor extends BaseXacmlJaxbRequestPreprocessor {
    private static final DecisionRequestFactory<ImmutableDecisionRequest> DEFAULT_REQUEST_FACTORY = ImmutableDecisionRequest::getInstance;
    private final DecisionRequestFactory<ImmutableDecisionRequest> reqFactory;

    public GeoXACMLRequestPreprocessor(AttributeValueFactoryRegistry datatypeFactoryRegistry, DecisionRequestFactory<ImmutableDecisionRequest> requestFactory, boolean strictAttributeIssuerMatch, boolean allowAttributeDuplicates, boolean requireContentForXPath, Set<String> extraPdpFeatures) {
        super(datatypeFactoryRegistry, strictAttributeIssuerMatch, allowAttributeDuplicates, requireContentForXPath, extraPdpFeatures);

        assert requestFactory != null;

        this.reqFactory = requestFactory;
    }

    @Override
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
                        av.getOtherAttributes().put(GeometryValue.xmlAttributeId, attribute.getAttributeId());
                        av.getOtherAttributes().put(GeometryValue.xmlCategoryId, jaxbAttributes.getCategory());
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

    public static final class StrictVariantFactory extends BaseXacmlJaxbRequestPreprocessor.Factory {
        public static final String ID = "urn:de:securedimensions:feature:pdp:request-preproc:xacml-xml:default-strict";
        public static final DecisionRequestPreprocessor.Factory<Request, IndividualXacmlJaxbRequest> INSTANCE = new StrictVariantFactory();

        public StrictVariantFactory() {
            super(ID);
        }

        @Override
        public DecisionRequestPreprocessor<Request, IndividualXacmlJaxbRequest> getInstance(AttributeValueFactoryRegistry datatypeFactoryRegistry, boolean strictAttributeIssuerMatch, boolean requireContentForXPath, Set<String> extraPdpFeatures) {
            return new GeoXACMLRequestPreprocessor(datatypeFactoryRegistry, DEFAULT_REQUEST_FACTORY, strictAttributeIssuerMatch, false, requireContentForXPath, extraPdpFeatures);
        }
    }

    public static final class LaxVariantFactory extends BaseXacmlJaxbRequestPreprocessor.Factory {
        public static final String ID = "urn:de:securedimensions:feature:pdp:request-preproc:xacml-xml:default-lax";
        public static final DecisionRequestPreprocessor.Factory<Request, IndividualXacmlJaxbRequest> INSTANCE = new LaxVariantFactory();

        public LaxVariantFactory() {
            super(ID);
        }

        @Override
        public DecisionRequestPreprocessor<Request, IndividualXacmlJaxbRequest> getInstance(AttributeValueFactoryRegistry datatypeFactoryRegistry, boolean strictAttributeIssuerMatch, boolean requireContentForXPath, Set<String> extraPdpFeatures) {
            return new GeoXACMLRequestPreprocessor(datatypeFactoryRegistry, DEFAULT_REQUEST_FACTORY, strictAttributeIssuerMatch, true, requireContentForXPath, extraPdpFeatures);
        }
    }
}
