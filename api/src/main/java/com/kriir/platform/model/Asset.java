package com.kriir.platform.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "asset")
public class Asset extends PanacheEntityBase {

    @Id
    public String id;
    
    @Column(name = "name")
    public String name;
    
    @Column(name = "type")
    public String type;
    
    @Column(name = "criticality")
    public String criticality;
    
    @Column(name = "status")
    public String status = "ACTIVE";
    
    @Column(name = "x")
    public double x;
    
    @Column(name = "y")
    public double y;
    
    @Column(name = "last_seen")
    public LocalDateTime lastSeen = LocalDateTime.now();

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