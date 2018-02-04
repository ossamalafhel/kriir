package com.mobility.demo.service;

import com.mobility.demo.dto.request.CarSave;
import com.mobility.demo.dto.request.CarsSearchWithinRadius;
import com.mobility.demo.model.Car;
import com.mobility.demo.model.repository.CarRepository;
import com.mobility.demo.util.GeometryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

  private final CarRepository carRepository;

  /**
   * Retrieve all cars
   *
   * @return enriched polygon
   * @param save
   */
  public void save(Car save) {
      Car car = new Car(save.getX(), save.getY());
      if(!save.getId().isEmpty()){
          car.setId(save.getId());
      }
       carRepository.save(car);
  }

    /**
     * Retrieve all cars
     *
     * @return enriched polygon
     */
    public List<Car> getAll() {
        return carRepository.findAll();
    }


  /**
   * Search cars for a radius
   *
   * @param request polygone id request
   * @return enriched polygon
   */
  public List<Car> findCarsWithinRadius(CarsSearchWithinRadius request) throws Exception {
    return carRepository.getCarsInside(GeometryFactory.createPoint(request.getLon(), request.getLat()), request.getDist());
  }

}
