package de.securedimensions.geoxacml3.function;

import de.securedimensions.geoxacml3.crs.TransformGeometry;
import de.securedimensions.geoxacml3.datatype.GeometryValue;
import org.locationtech.jts.geom.Geometry;
import org.ow2.authzforce.core.pdp.api.ImmutableXacmlStatus;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;

import javax.xml.namespace.QName;
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
        Map<QName, String> otherXmlAttributesG1 = (Map<QName, String>) g1.getUserData();
        Map<QName, String> otherXmlAttributesG2 = (Map<QName, String>) g2.getUserData();

        double precisionG1 = (otherXmlAttributesG1 != null) && otherXmlAttributesG1.containsKey(xmlPrecision) ? Double.parseDouble(otherXmlAttributesG1.get(xmlPrecision)) : Double.MAX_VALUE;
        double precisionG2 = (otherXmlAttributesG2 != null) && otherXmlAttributesG2.containsKey(xmlPrecision) ? Double.parseDouble(otherXmlAttributesG2.get(xmlPrecision)) : Double.MAX_VALUE;

        String sourceG1 = (otherXmlAttributesG1 != null) && otherXmlAttributesG1.containsKey(SOURCE) ? otherXmlAttributesG1.get(SOURCE) : SOURCE_POLICY;
        String sourceG2 = (otherXmlAttributesG2 != null) && otherXmlAttributesG2.containsKey(SOURCE) ? otherXmlAttributesG2.get(SOURCE) : SOURCE_POLICY;
        if ((sourceG1 == SOURCE_ATTR_DESIGNATOR) &&
                (sourceG2 == SOURCE_POLICY) &&
                (precisionG1 > precisionG2))
            throw new IndeterminateEvaluationException(
                    new ImmutableXacmlStatus(PRECISION_ERROR, Optional.of("PEP requesting higher geometry precision than supported by the policy")));

        if ((sourceG2 == SOURCE_ATTR_DESIGNATOR) &&
                (sourceG1 == SOURCE_POLICY) &&
                (precisionG2 > precisionG1))
            throw new IndeterminateEvaluationException(
                    new ImmutableXacmlStatus(PRECISION_ERROR, Optional.of("PEP requesting higher geometry precision than supported by the policy")));

        if ((sourceG2 == SOURCE_ATTR_DESIGNATOR) &&
                (sourceG1 == SOURCE_ATTR_DESIGNATOR) &&
                (precisionG2 != precisionG1))
            throw new IndeterminateEvaluationException(
                    new ImmutableXacmlStatus(PRECISION_ERROR, Optional.of("Processing ADR geometries have different precision")));

    }

    public void ensureCRS(Geometry g1, Geometry g2) throws IndeterminateEvaluationException
    {
        if (g1.getSRID() != g2.getSRID()) {
            TransformGeometry tg = new TransformGeometry();
            tg.transformCRS(g1, g2);
        }
    }
}
