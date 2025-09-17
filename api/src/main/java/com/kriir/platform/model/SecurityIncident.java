package com.kriir.platform.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "security_incidents")
public class SecurityIncident extends PanacheEntityBase {

    @Id
    public String id;
    
    @Column(name = "title")
    public String title;
    
    @Column(name = "type")
    public String type;
    
    @Column(name = "severity")
    public String severity;
    
    @Column(name = "status")
    public String status = "OPEN";
    
    @Column(name = "x")
    public double x;
    
    @Column(name = "y")
    public double y;
    
    @Column(name = "description")
    public String description;
    
    @Column(name = "affected_asset_id")
    public String affectedAssetId;
    
    @Column(name = "detected_at")
    public LocalDateTime detectedAt = LocalDateTime.now();
    
    @Column(name = "resolved_at")
    public LocalDateTime resolvedAt;
    
    @Column(name = "assigned_to")
    public String assignedTo;

    public SecurityIncident() {
        this.id = UUID.randomUUID().toString();
    }

    public SecurityIncident(String title, String type, String severity, double x, double y, String description) {
        this();
        this.title = title;
        this.type = type;
        this.severity = severity;
        this.x = x;
        this.y = y;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        SecurityIncident that = (SecurityIncident) o;
        
        if (Double.compare(that.x, x) != 0) return false;
        if (Double.compare(that.y, y) != 0) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (severity != null ? !severity.equals(that.severity) : that.severity != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (affectedAssetId != null ? !affectedAssetId.equals(that.affectedAssetId) : that.affectedAssetId != null)
            return false;
        if (detectedAt != null ? !detectedAt.equals(that.detectedAt) : that.detectedAt != null) return false;
        if (resolvedAt != null ? !resolvedAt.equals(that.resolvedAt) : that.resolvedAt != null) return false;
        return assignedTo != null ? assignedTo.equals(that.assignedTo) : that.assignedTo == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (severity != null ? severity.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (affectedAssetId != null ? affectedAssetId.hashCode() : 0);
        result = 31 * result + (detectedAt != null ? detectedAt.hashCode() : 0);
        result = 31 * result + (resolvedAt != null ? resolvedAt.hashCode() : 0);
        result = 31 * result + (assignedTo != null ? assignedTo.hashCode() : 0);
        return result;
    }
}