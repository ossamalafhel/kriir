package com.mobility.demo.util;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Geometry;
import static org.junit.jupiter.api.Assertions.*;

class GeometryFactoryTest {

    @Test
    void testCreatePointWithDoubles() {
        double x = 7.06064;
        double y = 48.092971;
        
        Point point = GeometryFactory.createPoint(x, y);
        
        assertNotNull(point);
        assertEquals(x, point.getX(), 0.0001);
        assertEquals(y, point.getY(), 0.0001);
    }

    @Test
    void testCreatePointWithStrings() {
        String x = "7.06064";
        String y = "48.092971";
        
        Point point = GeometryFactory.createPoint(x, y);
        
        assertNotNull(point);
        assertEquals(Double.parseDouble(x), point.getX(), 0.0001);
        assertEquals(Double.parseDouble(y), point.getY(), 0.0001);
    }

    @Test
    void testCreatePolygonValid() {
        String polygon = "(0 0, 1 0, 1 1, 0 1, 0 0)";
        
        Geometry geometry = GeometryFactory.createPolygon(polygon);
        
        assertNotNull(geometry);
        assertEquals("Polygon", geometry.getGeometryType());
    }

    @Test
    void testCreatePolygonInvalid() {
        String invalidPolygon = "invalid polygon string";
        
        Geometry geometry = GeometryFactory.createPolygon(invalidPolygon);
        
        assertNull(geometry);
    }

    @Test
    void testCreatePointWithZeroCoordinates() {
        Point point = GeometryFactory.createPoint(0.0, 0.0);
        
        assertNotNull(point);
        assertEquals(0.0, point.getX(), 0.0001);
        assertEquals(0.0, point.getY(), 0.0001);
    }

    @Test
    void testCreatePointWithNegativeCoordinates() {
        double x = -7.06064;
        double y = -48.092971;
        
        Point point = GeometryFactory.createPoint(x, y);
        
        assertNotNull(point);
        assertEquals(x, point.getX(), 0.0001);
        assertEquals(y, point.getY(), 0.0001);
    }
}