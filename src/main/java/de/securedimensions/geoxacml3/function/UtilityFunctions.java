package de.securedimensions.geoxacml3.function;

import de.securedimensions.geoxacml3.crs.TransformGeometry;
import de.securedimensions.geoxacml3.datatype.GeometryValue;
import de.securedimensions.geoxacml3.identifiers.Definitions;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MissingAttributeDetail;
import org.locationtech.jts.geom.Geometry;
import org.ow2.authzforce.core.pdp.api.ImmutableXacmlStatus;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.securedimensions.geoxacml3.pdp.io.GeoXACMLRequestPreprocessor.XACML_ATTRIBUTE_ID_QNAME;
import static de.securedimensions.geoxacml3.pdp.io.GeoXACMLRequestPreprocessor.XACML_CATEGORY_ID_QNAME;
import static org.ow2.authzforce.xacml.identifiers.XacmlStatusCode.SYNTAX_ERROR;

public class UtilityFunctions {

    public boolean compare(GeometryValue gv1, GeometryValue gv2, String id) throws IndeterminateEvaluationException {
        Geometry g1 = gv1.getGeometry();
        Geometry g2 = gv2.getGeometry();
        ensurePrecision(g1, g2);
        ensureCRS(g1, g2);

        switch (id) {
            case TopologicalFunctions.Equals.EQUALS_SUFFIX:
                return g1.equals(g2);
            case TopologicalFunctions.Disjoint.DISJOINT_SUFFIX:
                return g1.disjoint(g2);
            case TopologicalFunctions.Touches.TOUCHES_SUFFIX:
                return g1.touches(g2);
            case TopologicalFunctions.Crosses.CROSSES_SUFFIX:
                return g1.crosses(g2);
            case TopologicalFunctions.Within.WITHIN_SUFFIX:
                return g1.within(g2);
            case TopologicalFunctions.Contains.CONTAINS_SUFFIX:
                return g1.contains(g2);
            case TopologicalFunctions.Overlaps.OVERLAPS_SUFFIX:
                return g1.overlaps(g2);
            case TopologicalFunctions.Intersects.INTERSECTS_SUFFIX:
                return g1.intersects(g2);
        }
        throw new IllegalArgumentException("Function: " + id + " unknown");
    }

    public void ensurePrecision(Geometry g1, Geometry g2) throws IndeterminateEvaluationException {

        Map<QName, String> otherXmlAttributesG1 = (g1.getUserData() == null) ? new HashMap<QName, String>() : (Map<QName, String>) g1.getUserData();
        final int precisionG1 = otherXmlAttributesG1.containsKey(Definitions.xmlPrecision) ? Integer.parseInt(otherXmlAttributesG1.get(Definitions.xmlPrecision)) : Integer.MAX_VALUE;
        final String sourceG1 = otherXmlAttributesG1.containsKey(Definitions.ATTR_SOURCE) ? otherXmlAttributesG1.get(Definitions.ATTR_SOURCE) : Definitions.ATTR_SOURCE_POLICY;

        Map<QName, String> otherXmlAttributesG2 = (g2.getUserData() == null) ? new HashMap<QName, String>() : (Map<QName, String>) g2.getUserData();
        final int precisionG2 = otherXmlAttributesG2.containsKey(Definitions.xmlPrecision) ? Integer.parseInt(otherXmlAttributesG2.get(Definitions.xmlPrecision)) : Integer.MAX_VALUE;
        final String sourceG2 = otherXmlAttributesG2.containsKey(Definitions.ATTR_SOURCE) ? otherXmlAttributesG2.get(Definitions.ATTR_SOURCE) : Definitions.ATTR_SOURCE_POLICY;

        if ((sourceG1.equalsIgnoreCase(Definitions.ATTR_SOURCE_DESIGNATOR)) &&
                (sourceG2.equalsIgnoreCase(Definitions.ATTR_SOURCE_POLICY)) &&
                (precisionG1 > precisionG2)) {
            // Report g1 in MissingAttributeDetail with precision from g2
            otherXmlAttributesG1.replace(Definitions.xmlPrecision, otherXmlAttributesG2.get(Definitions.xmlPrecision));
            final AttributeValueType av = new AttributeValueType(List.of(""), GeometryValue.DATATYPE.getId(), otherXmlAttributesG1);
            final MissingAttributeDetail missingAttributeDetail = new MissingAttributeDetail(List.of(av),
                    otherXmlAttributesG1.get(XACML_CATEGORY_ID_QNAME),
                    otherXmlAttributesG1.get(XACML_ATTRIBUTE_ID_QNAME),
                    GeometryValue.DATATYPE.getId(),
                    null);
            throw new IndeterminateEvaluationException("PEP requesting higher geometry precision than supported by the policy", missingAttributeDetail, Optional.of(Definitions.PRECISION_ERROR));
        }

        if ((sourceG2.equalsIgnoreCase(Definitions.ATTR_SOURCE_DESIGNATOR)) &&
                (sourceG1.equalsIgnoreCase(Definitions.ATTR_SOURCE_POLICY)) &&
                (precisionG2 > precisionG1)) {
            // Report g2 in MissingAttributeDetail with precision from g1
            otherXmlAttributesG2.replace(Definitions.xmlPrecision, otherXmlAttributesG1.get(Definitions.xmlPrecision));
            final AttributeValueType av = new AttributeValueType(List.of(""), GeometryValue.DATATYPE.getId(), otherXmlAttributesG2);
            final MissingAttributeDetail missingAttributeDetail = new MissingAttributeDetail(List.of(av),
                    otherXmlAttributesG2.get(XACML_CATEGORY_ID_QNAME),
                    otherXmlAttributesG2.get(XACML_ATTRIBUTE_ID_QNAME),
                    GeometryValue.DATATYPE.getId(),
                    null);
            throw new IndeterminateEvaluationException("PEP requesting higher geometry precision than supported by the policy", missingAttributeDetail, Optional.of(Definitions.PRECISION_ERROR));
        }

        if ((sourceG2.equalsIgnoreCase(Definitions.ATTR_SOURCE_DESIGNATOR)) &&
                (sourceG1.equalsIgnoreCase(Definitions.ATTR_SOURCE_DESIGNATOR)) &&
                (precisionG2 != precisionG1)) {
            throw new IndeterminateEvaluationException(
                    new ImmutableXacmlStatus("Processing ADR geometries with different precision", Optional.of(Definitions.PRECISION_ERROR)));
        }

        if ((sourceG2.equalsIgnoreCase(Definitions.ATTR_SOURCE_POLICY)) &&
                (sourceG1.equalsIgnoreCase(Definitions.ATTR_SOURCE_POLICY)) &&
                (precisionG2 != precisionG1)) {
            throw new IndeterminateEvaluationException(
                    new ImmutableXacmlStatus("Processing Policy geometries with different precision", Optional.of(SYNTAX_ERROR.name())));
        }

    }

    public void ensureCRS(Geometry g1, Geometry g2) throws IndeterminateEvaluationException {
        if (g1.getSRID() != g2.getSRID()) {
            TransformGeometry tg = new TransformGeometry();
            tg.transformCRS(g1, g2);
        }
    }
}
