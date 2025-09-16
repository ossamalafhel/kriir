package org.cyberisk.platform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("asset")
public class Asset {

    @Id
    private String id;
    
    @Column("name")
    private String name;
    
    @Column("type")
    private String type;
    
    @Column("criticality")
    private String criticality;
    
    @Column("status")
    private String status = "ACTIVE";
    
    @Column("x")
    private double x;
    
    @Column("y")
    private double y;
    
    @Column("last_seen")
    private LocalDateTime lastSeen = LocalDateTime.now();

    public Asset() {
        this.id = UUID.randomUUID().toString();
    }

    public Asset(String name, String type, String criticality, double x, double y) {
        this();
        this.name = name;
        this.type = type;
        this.criticality = criticality;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCriticality() {
        return criticality;
    }

    public void setCriticality(String criticality) {
        this.criticality = criticality;
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

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Asset asset = (Asset) o;
        
        if (Double.compare(asset.x, x) != 0) return false;
        if (Double.compare(asset.y, y) != 0) return false;
        if (id != null ? !id.equals(asset.id) : asset.id != null) return false;
        if (name != null ? !name.equals(asset.name) : asset.name != null) return false;
        if (type != null ? !type.equals(asset.type) : asset.type != null) return false;
        if (criticality != null ? !criticality.equals(asset.criticality) : asset.criticality != null) return false;
        if (status != null ? !status.equals(asset.status) : asset.status != null) return false;
        return lastSeen != null ? lastSeen.equals(asset.lastSeen) : asset.lastSeen == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (criticality != null ? criticality.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (lastSeen != null ? lastSeen.hashCode() : 0);
        return result;
    }
}