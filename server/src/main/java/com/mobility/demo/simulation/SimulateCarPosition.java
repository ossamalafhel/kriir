package com.mobility.demo.simulation;

import com.mobility.demo.controller.CarController;
import com.mobility.demo.controller.UserController;
import com.mobility.demo.model.Car;
import com.mobility.demo.model.User;
import com.mobility.demo.model.repository.CarRepository;
import com.mobility.demo.model.repository.UserRepository;
import com.mobility.demo.util.GeometryFactory;
import org.locationtech.jts.geom.Geometry;
import lombok.Builder;
import lombok.Data;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@Log
@Data
@Builder
public class SimulateCarPosition implements CommandLineRunner{


    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final CarController carController;
    private final UserController userController;

    @Override
    public void run(String... args) throws InterruptedException {

        carRepository.deleteAll();
        userRepository.deleteAll();
        Car car = new Car(48.9833, 2.2667);
        User user = new User(48.9833, 2.2667);
        carRepository.save(car);
        userRepository.save(user);
        while (true){
            Thread.sleep(1000);
            double newX = car.getX() - Math.random()/1000;
            double newY = car.getY() + Math.random()/1000;
            Geometry coord = GeometryFactory.createPoint(newX, newY);
            car.setX(newX);
            car.setY(newY);
            car.setCoordinate(coord);
            newX = user.getX() + Math.random()/1000;
            newY = user.getY() - Math.random()/1000;
            coord = GeometryFactory.createPoint(newX, newY);
            user.setX(newX);
            user.setY(newY);
            user.setCoordinate(coord);
            carController.saveCar(car);
            userController.saveUser(user);
        }

    }
}
