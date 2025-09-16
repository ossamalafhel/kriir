package com.mobility.demo.controller;

import com.mobility.demo.model.Car;
import com.mobility.demo.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSaveCar() throws Exception {
        Car car = new Car(7.06064, 48.092971);
        when(carService.save(any(Car.class))).thenReturn(car);

        mockMvc.perform(post("/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").value(7.06064))
                .andExpect(jsonPath("$.y").value(48.092971));
    }

    @Test
    void testGetAllCars() throws Exception {
        mockMvc.perform(get("/cars")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCarsInside() throws Exception {
        mockMvc.perform(get("/cars/inside")
                .param("x", "7.06064")
                .param("y", "48.092971")
                .param("distance", "1000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCarsInsideInvalidParameters() throws Exception {
        mockMvc.perform(get("/cars/inside")
                .param("x", "invalid")
                .param("y", "48.092971")
                .param("distance", "1000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}