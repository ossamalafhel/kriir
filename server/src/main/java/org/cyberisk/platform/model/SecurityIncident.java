package org.cyberisk.platform.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("security_incidents")
public class SecurityIncident {

    @Id
    private String id;
    
    @Column("title")
    private String title;
    
    @Column("type")
    private String type;
    
    @Column("severity")
    private String severity;
    
    @Column("status")
    private String status = "OPEN";
    
    @Column("x")
    private double x;
    
    @Column("y")
    private double y;
    
    @Column("description")
    private String description;
    
    @Column("affected_asset_id")
    private String affectedAssetId;
    
    @Column("detected_at")
    private LocalDateTime detectedAt = LocalDateTime.now();
    
    @Column("resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column("assigned_to")
    private String assignedTo;

    public SecurityIncident() {
        this.id = UUID.randomUUID().toString();
    }

    public SecurityIncident(String title, String type, String severity, double x, double y) {
        this();
        this.title = title;
        this.type = type;
        this.severity = severity;
        this.x = x;
        this.y = y;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAffectedAssetId() {
        return affectedAssetId;
    }

    public void setAffectedAssetId(String affectedAssetId) {
        this.affectedAssetId = affectedAssetId;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
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
        if (affectedAssetId != null ? !affectedAssetId.equals(that.affectedAssetId) : that.affectedAssetId != null) return false;
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