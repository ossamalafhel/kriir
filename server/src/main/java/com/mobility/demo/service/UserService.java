package com.mobility.demo.service;

import com.mobility.demo.dto.request.CarsSearchWithinRadius;
import com.mobility.demo.model.Car;
import com.mobility.demo.model.User;
import com.mobility.demo.model.repository.CarRepository;
import com.mobility.demo.model.repository.UserRepository;
import com.mobility.demo.util.GeometryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final UserRepository userRepository;

  /**
   * Save a user
   *
   * @return enriched polygon
   * @param user
   */
  public void save(User user) {
      User car = new User(user.getX(), user.getY());
      if(!user.getId().isEmpty()){
          car.setId(user.getId());
      }
      userRepository.save(car);
  }


}
