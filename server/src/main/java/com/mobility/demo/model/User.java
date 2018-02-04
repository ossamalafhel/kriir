package com.mobility.demo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mobility.demo.util.GeometryFactory;
import com.vividsolutions.jts.geom.Geometry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Data
@Entity(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder(value = {
        "id",
        "x",
        "y"
})
public class User {
    @Id
    String id;
    double x; //'Coordonnées X	7,06064',
    double y; //'Coordonnées Y	48,092971',
    @JsonIgnore
    Geometry coordinate; //'definition sql des points pour bdd geospatiale',

    @JsonCreator
    public User(@JsonProperty("x") double x, @JsonProperty("y") double y) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.coordinate = GeometryFactory.createPoint(x, y);
    }

    @JsonCreator
    public User(@JsonProperty("id") String id, @JsonProperty("x") double x, @JsonProperty("y") double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.coordinate = GeometryFactory.createPoint(x, y);
    }
}
