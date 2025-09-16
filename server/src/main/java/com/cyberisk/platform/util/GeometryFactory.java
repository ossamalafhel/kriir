package com.cyberisk.platform.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory to build Geometry data from String values.
 *
 */
@Slf4j
public abstract class GeometryFactory {

    private static final org.locationtech.jts.geom.GeometryFactory GEOMETRY_FACTORY = new org.locationtech.jts.geom.GeometryFactory();
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
