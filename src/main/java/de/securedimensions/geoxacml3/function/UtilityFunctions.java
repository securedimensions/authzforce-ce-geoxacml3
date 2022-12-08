package de.securedimensions.geoxacml3.function;

import de.securedimensions.geoxacml3.crs.TransformGeometry;
import de.securedimensions.geoxacml3.datatype.GeometryValue;
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

import static de.securedimensions.geoxacml3.datatype.GeometryValue.*;

public class UtilityFunctions {

    public boolean compare(GeometryValue gv1, GeometryValue gv2, String id) throws IndeterminateEvaluationException
    {
        Geometry g1 = gv1.getGeometry();
        Geometry g2 = gv2.getGeometry();
        ensurePrecision(g1, g2);
        ensureCRS(g1, g2);

        switch (id)
        {
            case TopologicalFunctions.Equal.EQUAL_SUFFIX:
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
        throw new IllegalArgumentException("Function: " + ID + " unknown");
    }

    public void ensurePrecision(Geometry g1, Geometry g2) throws IndeterminateEvaluationException
    {

        Map<QName, String> otherXmlAttributesG1 = (g1.getUserData() == null) ? new HashMap<QName, String>() : (Map<QName, String>) g1.getUserData();
        final double precisionG1 = otherXmlAttributesG1.containsKey(xmlPrecision) ? Double.parseDouble(otherXmlAttributesG1.get(xmlPrecision)) : Double.MAX_VALUE;
        final String sourceG1 = otherXmlAttributesG1.containsKey(SOURCE) ? otherXmlAttributesG1.get(SOURCE) : SOURCE_POLICY;

        Map<QName, String> otherXmlAttributesG2 = (g2.getUserData() == null) ? new HashMap<QName, String>() : (Map<QName, String>) g2.getUserData();
        final double precisionG2 = otherXmlAttributesG2.containsKey(xmlPrecision) ? Double.parseDouble(otherXmlAttributesG2.get(xmlPrecision)) : Double.MAX_VALUE;
        final String sourceG2 = otherXmlAttributesG2.containsKey(SOURCE) ? otherXmlAttributesG2.get(SOURCE) : SOURCE_POLICY;

        if ((sourceG1.equalsIgnoreCase(SOURCE_ATTR_DESIGNATOR)) &&
                (sourceG2.equalsIgnoreCase(SOURCE_POLICY)) &&
                (precisionG1 > precisionG2)) {
            // Report g1 in MissingAttributeDetail with precision from g2
            otherXmlAttributesG1.replace(xmlPrecision, otherXmlAttributesG2.get(xmlPrecision));
            final AttributeValueType av = new AttributeValueType(List.of(""), GeometryValue.DATATYPE.getId(), otherXmlAttributesG1);
            final MissingAttributeDetail missingAttributeDetail = new MissingAttributeDetail(List.of(av),
                    otherXmlAttributesG1.get(GeometryValue.xmlCategoryId),
                    otherXmlAttributesG1.get(GeometryValue.xmlAttributeId),
                    GeometryValue.DATATYPE.getId(),
                    null);
            throw new IndeterminateEvaluationException(PRECISION_ERROR, missingAttributeDetail, Optional.of("PEP requesting higher geometry precision than supported by the policy"));
        }

        if ((sourceG2.equalsIgnoreCase(SOURCE_ATTR_DESIGNATOR)) &&
                (sourceG1.equalsIgnoreCase(SOURCE_POLICY)) &&
                (precisionG2 > precisionG1)) {
            // Report g2 in MissingAttributeDetail with precision from g1
            otherXmlAttributesG2.replace(xmlPrecision, otherXmlAttributesG1.get(xmlPrecision));
            final AttributeValueType av = new AttributeValueType(List.of(""), GeometryValue.DATATYPE.getId(), otherXmlAttributesG2);
            final MissingAttributeDetail missingAttributeDetail = new MissingAttributeDetail(List.of(av),
                    otherXmlAttributesG2.get(GeometryValue.xmlCategoryId),
                    otherXmlAttributesG2.get(GeometryValue.xmlAttributeId),
                    GeometryValue.DATATYPE.getId(),
                    null);
            throw new IndeterminateEvaluationException(PRECISION_ERROR, missingAttributeDetail, Optional.of("PEP requesting higher geometry precision than supported by the policy"));
        }

        if ((sourceG2.equalsIgnoreCase(SOURCE_ATTR_DESIGNATOR)) &&
                (sourceG1.equalsIgnoreCase(SOURCE_ATTR_DESIGNATOR) )&&
                (precisionG2 != precisionG1)) {
            throw new IndeterminateEvaluationException(
                    new ImmutableXacmlStatus(PRECISION_ERROR, Optional.of("Processing ADR geometries with different precision")));
        }

        if ((sourceG2.equalsIgnoreCase(SOURCE_POLICY)) &&
                (sourceG1.equalsIgnoreCase(SOURCE_POLICY) )&&
                (precisionG2 != precisionG1)) {
            throw new IndeterminateEvaluationException(
                    new ImmutableXacmlStatus("urn:oasis:names:tc:xacml:1.0:status:syntax-error", Optional.of("Processing Policy geometries with different precision")));
        }

    }

    public void ensureCRS(Geometry g1, Geometry g2) throws IndeterminateEvaluationException
    {
        if (g1.getSRID() != g2.getSRID()) {
            TransformGeometry tg = new TransformGeometry();
            tg.transformCRS(g1, g2);
        }
    }
}
