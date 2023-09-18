package de.securedimensions.geoxacml3.identifiers;

import javax.xml.namespace.QName;

public class Definitions {
    public static final String GEOMETRY = "urn:ogc:def:geoxacml:3.0:data-type:geometry";
    public static final String FUNCTION_PREFIX = "urn:ogc:def:geoxacml:3.0:function:geometry";
    public static final String ERROR_PREFIX = "urn:ogc:def:geoxacml:3.0:status:";
    public static final String PRECISION_ERROR = ERROR_PREFIX + "precision-error";
    public static final String GEOMETRYCOLLECTION_ERROR = ERROR_PREFIX + "geometrycollection-error";
    public static final String GEOMETRY_ERROR = ERROR_PREFIX + "geometry-error";
    public static final String CRS_ERROR = ERROR_PREFIX + "crs-error";
    public static final String NAMESPACE = "http://www.opengis.net/geoxacml/3.0";
    public static final QName xmlENCODING = new QName(NAMESPACE, "encoding");
    public static final QName xmlSRID = new QName(NAMESPACE, "srid");
    public static final QName xmlCRS = new QName(NAMESPACE, "crs");
    public static final QName ATTR_ALLOW_TRANSFORMATION = new QName(NAMESPACE, "allowTransformation");
    public static final QName xmlPrecision = new QName(NAMESPACE, "precision");
    public static final QName jsonSRID = new QName(NAMESPACE, "SRID");
    public static final QName jsonAllowTransformation = new QName(NAMESPACE, "AllowTransformation");
    public static final QName jsonPrecision = new QName(NAMESPACE, "Precision");
    public static final QName ATTR_SOURCE = new QName(NAMESPACE, "AttributeSource");
    public static final String ATTR_SOURCE_DESIGNATOR = "AttributeSourceDesignator";
    public static final String ATTR_SOURCE_POLICY = "AttributeSourcePolicy";
    public static final String DEFAULT_CRS = "urn:ogc:def:crs:OGC::CRS84";
    public static final int DEFAULT_SRID = -4326;
    public static final String DEFAULT_ENCODING = "WKT";

}
