package com.mobility.demo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    private Car car;

    @BeforeEach
    void setUp() {
        car = new Car(7.06064, 48.092971);
    }

    @Test
    void testCarCreationWithCoordinates() {
        assertNotNull(car);
        assertEquals(7.06064, car.getX(), 0.0001);
        assertEquals(48.092971, car.getY(), 0.0001);
        assertNotNull(car.getId());
        assertNotNull(car.getCoordinate());
    }

    @Test
    void testCarCreationWithIdAndCoordinates() {
        String testId = "test-car-id";
        Car carWithId = new Car(testId, 7.06064, 48.092971);
        
        assertNotNull(carWithId);
        assertEquals(testId, carWithId.getId());
        assertEquals(7.06064, carWithId.getX(), 0.0001);
        assertEquals(48.092971, carWithId.getY(), 0.0001);
        assertNotNull(carWithId.getCoordinate());
    }

    @Test
    void testCarEquality() {
        Car car1 = new Car("same-id", 7.06064, 48.092971);
        Car car2 = new Car("same-id", 7.06064, 48.092971);
        
        assertEquals(car1.getId(), car2.getId());
        assertEquals(car1.getX(), car2.getX(), 0.0001);
        assertEquals(car1.getY(), car2.getY(), 0.0001);
    }

    @Test
    void testCarCoordinateUpdate() {
        double newX = 7.5;
        double newY = 48.5;
        
        car.setX(newX);
        car.setY(newY);
        
        assertEquals(newX, car.getX(), 0.0001);
        assertEquals(newY, car.getY(), 0.0001);
    }
}