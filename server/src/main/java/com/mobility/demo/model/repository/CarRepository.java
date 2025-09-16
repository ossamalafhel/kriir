package com.mobility.demo.model.repository;

import com.mobility.demo.model.Car;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, String> {

    @Query(value = "select * from car c "
            + "where st_dwithin(cast(c.coordinate as geography), cast(:point as geography), :distance) = true "
            + "limit 10000 ",
            nativeQuery = true)
    List<Car> getCarsInside(
            @Param("point") Geometry point,
            @Param("distance") Integer distance
    ) throws Exception;

}
