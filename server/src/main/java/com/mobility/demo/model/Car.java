package com.mobility.demo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mobility.demo.util.GeometryFactory;
import com.vividsolutions.jts.geom.Geometry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@ApiModel
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder(value = {
        "id",
        "x",
        "y"
})
public class Car {

    @ApiModelProperty(value = "Identifiant du véhicule", readOnly = true)
    @Id
    String id;
    @ApiModelProperty(value = "Coordonnées X", readOnly = true)
    double x; //'Coordonnées X	7,06064',
    @ApiModelProperty(value = "Coordonnées Y", readOnly = true)
    double y; //'Coordonnées Y	48,092971',
    @JsonIgnore
    Geometry coordinate; //'definition sql des points pour bdd geospatiale',

    @JsonCreator
    public Car(@JsonProperty("x") double x, @JsonProperty("y") double y) {
        this.id= UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.coordinate = GeometryFactory.createPoint(x, y);
    }

    @JsonCreator
    public Car(@JsonProperty("id") String id, @JsonProperty("x") double x, @JsonProperty("y") double y) {
        this.id= id;
        this.x = x;
        this.y = y;
        this.coordinate = GeometryFactory.createPoint(x, y);
    }

}
