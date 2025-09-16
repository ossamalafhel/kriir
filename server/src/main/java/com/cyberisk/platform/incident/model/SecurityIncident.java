package com.cyberisk.platform.incident.model;

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
import jakarta.persistence.Lob;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Security incident with geospatial context")
@Data
@Entity(name = "security_incidents")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder(value = {
        "id",
        "title",
        "type",
        "severity",
        "status",
        "x",
        "y",
        "detectedAt",
        "resolvedAt"
})
public class SecurityIncident {

    @Schema(description = "Incident unique identifier", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    private String id;
    
    @Schema(description = "Incident title", example = "Malware detected on workstation")
    @Column(nullable = false)
    private String title;
    
    @Schema(description = "Type of security incident")
    @Enumerated(EnumType.STRING)
    private IncidentType type;
    
    @Schema(description = "Severity level of the incident")
    @Enumerated(EnumType.STRING)
    private SeverityLevel severity;
    
    @Schema(description = "Current incident status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private IncidentStatus status = IncidentStatus.OPEN;
    
    @Schema(description = "Longitude coordinate where incident occurred", example = "7.06064")
    @Column(nullable = false)
    private double x;
    
    @Schema(description = "Latitude coordinate where incident occurred", example = "48.092971")
    @Column(nullable = false)
    private double y;
    
    @Schema(description = "Incident description")
    @Lob
    private String description;
    
    @Schema(description = "Affected asset ID")
    private String affectedAssetId;
    
    @Schema(description = "Detection timestamp")
    @Builder.Default
    private LocalDateTime detectedAt = LocalDateTime.now();
    
    @Schema(description = "Resolution timestamp")
    private LocalDateTime resolvedAt;
    
    @Schema(description = "Assigned analyst")
    private String assignedTo;
    
    @JsonIgnore
    private Geometry coordinate;

    @JsonCreator
    public SecurityIncident(@JsonProperty("title") String title,
                           @JsonProperty("type") IncidentType type,
                           @JsonProperty("severity") SeverityLevel severity,
                           @JsonProperty("x") double x, 
                           @JsonProperty("y") double y) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.type = type;
        this.severity = severity;
        this.x = x;
        this.y = y;
        this.status = IncidentStatus.OPEN;
        this.detectedAt = LocalDateTime.now();
        this.coordinate = GeometryFactory.createPoint(x, y);
    }

    @JsonCreator
    public SecurityIncident(@JsonProperty("id") String id,
                           @JsonProperty("title") String title,
                           @JsonProperty("type") IncidentType type,
                           @JsonProperty("severity") SeverityLevel severity,
                           @JsonProperty("x") double x, 
                           @JsonProperty("y") double y) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.severity = severity;
        this.x = x;
        this.y = y;
        this.status = IncidentStatus.OPEN;
        this.detectedAt = LocalDateTime.now();
        this.coordinate = GeometryFactory.createPoint(x, y);
    }

    public enum IncidentType {
        MALWARE_DETECTION,
        UNAUTHORIZED_ACCESS,
        DATA_BREACH,
        PHISHING_ATTACK,
        RANSOMWARE,
        DDoS_ATTACK,
        INSIDER_THREAT,
        VULNERABILITY_EXPLOIT,
        SUSPICIOUS_ACTIVITY,
        POLICY_VIOLATION
    }

    public enum SeverityLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    public enum IncidentStatus {
        OPEN,
        IN_PROGRESS,
        RESOLVED,
        CLOSED,
        FALSE_POSITIVE
    }
}
