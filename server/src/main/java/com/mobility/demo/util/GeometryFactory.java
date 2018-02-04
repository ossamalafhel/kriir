package com.mobility.demo.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory to build Geometry data from String values.
 *
 */
@Slf4j
public abstract class GeometryFactory {

    private static final com.vividsolutions.jts.geom.GeometryFactory GEOMETRY_FACTORY = new com.vividsolutions.jts.geom.GeometryFactory();
    private static final WKTReader READER = new WKTReader(GEOMETRY_FACTORY);

    private GeometryFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * Build geometry point from Latitude / Longitude.
     *
     * @param x Longitude of point
     * @param y Latitude of point
     * @return Point in geometry
     */
    public static Point createPoint(String x, String y) {
        return createPoint(Double.valueOf(x), Double.valueOf(y));
    }

    /**
     * Build geometry point from Latitude / Longitude.
     *
     * @param x Longitude of point
     * @param y Latitude of point
     * @return Point in geometry
     */
    public static Point createPoint(double x, double y) {
        return GEOMETRY_FACTORY.createPoint(new Coordinate(x, y));
    }

    /**
     * Build geometry polygon from String.
     *
     * @param polygon String representation of polygon
     * @return Geometry polygon object
     */
    public static Geometry createPolygon(String polygon) {
        try {
            return READER.read("POLYGON("+polygon+")");
        } catch (ParseException e) {
            log.error("Cannot deserialize polygon", e);
            return null;
        }
    }
}
