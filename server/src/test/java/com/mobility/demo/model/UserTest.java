package com.mobility.demo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(7.06064, 48.092971);
    }

    @Test
    void testUserCreationWithCoordinates() {
        assertNotNull(user);
        assertEquals(7.06064, user.getX(), 0.0001);
        assertEquals(48.092971, user.getY(), 0.0001);
        assertNotNull(user.getId());
        assertNotNull(user.getCoordinate());
    }

    @Test
    void testUserCreationWithIdAndCoordinates() {
        String testId = "test-user-id";
        User userWithId = new User(testId, 7.06064, 48.092971);
        
        assertNotNull(userWithId);
        assertEquals(testId, userWithId.getId());
        assertEquals(7.06064, userWithId.getX(), 0.0001);
        assertEquals(48.092971, userWithId.getY(), 0.0001);
        assertNotNull(userWithId.getCoordinate());
    }

    @Test
    void testUserEquality() {
        User user1 = new User("same-id", 7.06064, 48.092971);
        User user2 = new User("same-id", 7.06064, 48.092971);
        
        assertEquals(user1.getId(), user2.getId());
        assertEquals(user1.getX(), user2.getX(), 0.0001);
        assertEquals(user1.getY(), user2.getY(), 0.0001);
    }

    @Test
    void testUserCoordinateUpdate() {
        double newX = 7.5;
        double newY = 48.5;
        
        user.setX(newX);
        user.setY(newY);
        
        assertEquals(newX, user.getX(), 0.0001);
        assertEquals(newY, user.getY(), 0.0001);
    }
}