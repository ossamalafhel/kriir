package com.cyberisk.platform.asset.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.cyberisk.platform.util.GeometryFactory;
import org.locationtech.jts.geom.Geometry;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "IT Asset entity representing infrastructure components with geospatial data")
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder(value = {
        "id",
        "name",
        "type",
        "criticality",
        "status",
        "x",
        "y",
        "lastSeen"
})
public class Asset {

    @Schema(description = "Asset unique identifier", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    private String id;
    
    @Schema(description = "Asset name", example = "Web Server EU-01")
    @Column(nullable = false)
    private String name;
    
    @Schema(description = "Asset type")
    @Enumerated(EnumType.STRING)
    private AssetType type;
    
    @Schema(description = "Business criticality level")
    @Enumerated(EnumType.STRING)
    private CriticalityLevel criticality;
    
    @Schema(description = "Current operational status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AssetStatus status = AssetStatus.ACTIVE;
    
    @Schema(description = "Longitude coordinate", example = "7.06064")
    @Column(nullable = false)
    private double x;
    
    @Schema(description = "Latitude coordinate", example = "48.092971")
    @Column(nullable = false)
    private double y;
    
    @Schema(description = "Last seen timestamp")
    @Builder.Default
    private LocalDateTime lastSeen = LocalDateTime.now();
    
    @JsonIgnore
    private Geometry coordinate;

    @JsonCreator
    public Asset(@JsonProperty("name") String name,
                @JsonProperty("type") AssetType type,
                @JsonProperty("criticality") CriticalityLevel criticality,
                @JsonProperty("x") double x, 
                @JsonProperty("y") double y) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.criticality = criticality;
        this.x = x;
        this.y = y;
        this.status = AssetStatus.ACTIVE;
        this.lastSeen = LocalDateTime.now();
        this.coordinate = GeometryFactory.createPoint(x, y);
    }

    @JsonCreator
    public Asset(@JsonProperty("id") String id,
                @JsonProperty("name") String name,
                @JsonProperty("type") AssetType type,
                @JsonProperty("criticality") CriticalityLevel criticality,
                @JsonProperty("x") double x, 
                @JsonProperty("y") double y) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.criticality = criticality;
        this.x = x;
        this.y = y;
        this.status = AssetStatus.ACTIVE;
        this.lastSeen = LocalDateTime.now();
        this.coordinate = GeometryFactory.createPoint(x, y);
    }

    public enum AssetType {
        SERVER,
        WORKSTATION,
        ROUTER,
        SWITCH,
        FIREWALL,
        DATABASE,
        CLOUD_INSTANCE,
        IOT_DEVICE,
        MOBILE_DEVICE
    }

    public enum CriticalityLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    public enum AssetStatus {
        ACTIVE,
        INACTIVE,
        COMPROMISED,
        UNDER_INVESTIGATION,
        QUARANTINED
    }
}
