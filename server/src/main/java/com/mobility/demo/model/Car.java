package com.mobility.demo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mobility.demo.util.GeometryFactory;
import org.locationtech.jts.geom.Geometry;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;

@Schema(description = "Car entity representing vehicle data with coordinates")
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

    @Schema(description = "Identifiant du véhicule", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    String id;
    
    @Schema(description = "Coordonnées X", example = "7.06064")
    double x; //'Coordonnées X	7,06064',
    
    @Schema(description = "Coordonnées Y", example = "48.092971")
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
