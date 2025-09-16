package com.mobility.demo.controller;

import com.mobility.demo.model.Car;
import com.mobility.demo.service.CarService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;

@Log
@RestController
public class CarController {

    private final CarService carService;
    private List<FluxSink<Car>> carHandlers = new ArrayList<>();

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @ApiOperation(
            value = "save car update",
            produces = MediaType.APPLICATION_JSON_VALUE,
            response = Car.class
    )
    @PostMapping("/saveCar")
    @ResponseStatus(HttpStatus.OK)
    public void saveCar(@ModelAttribute Car save) {
         carService.save(save);
    }


    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.GET, value = "/cars")
    @ApiOperation(
            value = "Retrieve all cars update",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE,
            response = Car.class
    )
    public Flux<Car> cars() {
        return Flux.push(sink -> {
            carHandlers.add(sink);
            sink.onCancel(() -> carHandlers.remove(sink));
        });
    }

    public void handleCar(Car car) {
        carHandlers.forEach(han -> han.next(car));
    }

}
