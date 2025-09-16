-- COP - CyberRisk Open Platform Database Schema
-- PostgreSQL with PostGIS Extension

-- Enable PostGIS extension if not already enabled
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Drop existing tables if needed
DROP TABLE IF EXISTS security_incidents CASCADE;
DROP TABLE IF EXISTS asset CASCADE;

-- Create Asset table for IT infrastructure components
CREATE TABLE IF NOT EXISTS asset (
    id VARCHAR(255) PRIMARY KEY DEFAULT uuid_generate_v4()::text,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    criticality VARCHAR(20) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    x DOUBLE PRECISION NOT NULL,
    y DOUBLE PRECISION NOT NULL,
    coordinate GEOMETRY(Point, 4326),
    last_seen TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for Asset table
CREATE INDEX idx_asset_type ON asset(type);
CREATE INDEX idx_asset_criticality ON asset(criticality);
CREATE INDEX idx_asset_status ON asset(status);
CREATE INDEX idx_asset_coordinate ON asset USING GIST(coordinate);
CREATE INDEX idx_asset_last_seen ON asset(last_seen);

-- Create Security Incidents table
CREATE TABLE IF NOT EXISTS security_incidents (
    id VARCHAR(255) PRIMARY KEY DEFAULT uuid_generate_v4()::text,
    title VARCHAR(500) NOT NULL,
    type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    x DOUBLE PRECISION NOT NULL,
    y DOUBLE PRECISION NOT NULL,
    coordinate GEOMETRY(Point, 4326),
    description TEXT,
    affected_asset_id VARCHAR(255) REFERENCES asset(id) ON DELETE SET NULL,
    detected_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP WITH TIME ZONE,
    assigned_to VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for Security Incidents table
CREATE INDEX idx_incident_type ON security_incidents(type);
CREATE INDEX idx_incident_severity ON security_incidents(severity);
CREATE INDEX idx_incident_status ON security_incidents(status);
CREATE INDEX idx_incident_coordinate ON security_incidents USING GIST(coordinate);
CREATE INDEX idx_incident_detected_at ON security_incidents(detected_at);
CREATE INDEX idx_incident_asset ON security_incidents(affected_asset_id);

-- Create trigger to update coordinate column from x,y
CREATE OR REPLACE FUNCTION update_coordinate()
RETURNS TRIGGER AS $$
BEGIN
    NEW.coordinate = ST_SetSRID(ST_MakePoint(NEW.x, NEW.y), 4326);
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply trigger to asset table
CREATE TRIGGER asset_coordinate_update
    BEFORE INSERT OR UPDATE ON asset
    FOR EACH ROW
    EXECUTE FUNCTION update_coordinate();

-- Apply trigger to security_incidents table
CREATE TRIGGER incident_coordinate_update
    BEFORE INSERT OR UPDATE ON security_incidents
    FOR EACH ROW
    EXECUTE FUNCTION update_coordinate();

-- Create notification function for real-time updates
CREATE OR REPLACE FUNCTION notify_change()
RETURNS TRIGGER AS $$
DECLARE
    data json;
    notification json;
BEGIN
    IF TG_OP = 'INSERT' OR TG_OP = 'UPDATE' THEN
        data = row_to_json(NEW);
    ELSE
        data = row_to_json(OLD);
    END IF;
    
    notification = json_build_object(
        'table', TG_TABLE_NAME,
        'action', TG_OP,
        'data', data
    );
    
    PERFORM pg_notify('cop_changes', notification::text);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply notification triggers
CREATE TRIGGER asset_notify
    AFTER INSERT OR UPDATE OR DELETE ON asset
    FOR EACH ROW
    EXECUTE FUNCTION notify_change();

CREATE TRIGGER incident_notify
    AFTER INSERT OR UPDATE OR DELETE ON security_incidents
    FOR EACH ROW
    EXECUTE FUNCTION notify_change();

-- Insert sample data for testing
INSERT INTO asset (name, type, criticality, status, x, y)
VALUES 
    ('Web Server EU-01', 'SERVER', 'CRITICAL', 'ACTIVE', 7.06064, 48.092971),
    ('Database Server EU-01', 'DATABASE', 'CRITICAL', 'ACTIVE', 7.08123, 48.102834),
    ('Firewall Paris-01', 'FIREWALL', 'HIGH', 'ACTIVE', 2.3522, 48.8566),
    ('Workstation Admin-01', 'WORKSTATION', 'MEDIUM', 'ACTIVE', 7.05532, 48.089231),
    ('IoT Sensor-01', 'IOT_DEVICE', 'LOW', 'ACTIVE', 7.07123, 48.095432);

INSERT INTO security_incidents (title, type, severity, status, x, y, description)
VALUES 
    ('Malware detected on Web Server', 'MALWARE_DETECTION', 'HIGH', 'OPEN', 7.06064, 48.092971, 'Trojan detected by antivirus scan'),
    ('Failed login attempts on Database', 'UNAUTHORIZED_ACCESS', 'MEDIUM', 'IN_PROGRESS', 7.08123, 48.102834, 'Multiple failed login attempts from unknown IP'),
    ('DDoS Attack on Paris region', 'DDoS_ATTACK', 'CRITICAL', 'RESOLVED', 2.3522, 48.8566, 'Distributed denial of service attack mitigated');

-- Useful queries for COP platform

-- Find assets within 5km radius
-- SELECT * FROM asset 
-- WHERE ST_DWithin(coordinate, ST_SetSRID(ST_MakePoint(7.06064, 48.092971), 4326), 5000);

-- Find incidents in the last 24 hours
-- SELECT * FROM security_incidents 
-- WHERE detected_at >= NOW() - INTERVAL '24 hours';

-- Count incidents by severity in a geographic area
-- SELECT severity, COUNT(*) FROM security_incidents 
-- WHERE ST_Within(coordinate, ST_MakeEnvelope(2.0, 48.0, 8.0, 49.0, 4326))
-- GROUP BY severity;