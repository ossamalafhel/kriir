package com.mobility.demo.service;

import com.mobility.demo.model.Car;
import com.mobility.demo.model.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

  private final CarRepository carRepository;

  /**
   * Save car update
   *
   * @param save
   */
  public void save(Car save) {
      Car car = new Car(save.getX(), save.getY());
      if(!save.getId().isEmpty()){
          car.setId(save.getId());
      }
       carRepository.save(car);
  }


}
