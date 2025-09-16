package com.mobility.demo.integration;

import com.mobility.demo.model.Car;
import com.mobility.demo.model.repository.CarRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class CarIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CarRepository carRepository;

    @Test
    @Transactional
    void testSaveAndFindCar() {
        // Given
        Car car = new Car(7.06064, 48.092971);
        
        // When
        Car savedCar = carRepository.save(car);
        entityManager.flush();
        
        // Then
        assertNotNull(savedCar.getId());
        assertEquals(7.06064, savedCar.getX(), 0.0001);
        assertEquals(48.092971, savedCar.getY(), 0.0001);
        
        // Verify it can be found
        Car foundCar = carRepository.findById(savedCar.getId()).orElse(null);
        assertNotNull(foundCar);
        assertEquals(savedCar.getId(), foundCar.getId());
    }

    @Test
    @Transactional
    void testDeleteCar() {
        // Given
        Car car = new Car(7.06064, 48.092971);
        Car savedCar = carRepository.save(car);
        entityManager.flush();
        
        // When
        carRepository.deleteById(savedCar.getId());
        entityManager.flush();
        
        // Then
        assertFalse(carRepository.findById(savedCar.getId()).isPresent());
    }

    @Test
    @Transactional
    void testFindAllCars() {
        // Given
        Car car1 = new Car(7.06064, 48.092971);
        Car car2 = new Car(7.1, 48.1);
        
        carRepository.save(car1);
        carRepository.save(car2);
        entityManager.flush();
        
        // When
        long count = carRepository.count();
        
        // Then
        assertEquals(2, count);
    }
}