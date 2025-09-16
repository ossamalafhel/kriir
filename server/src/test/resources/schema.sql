-- Create schema for tests
CREATE TABLE IF NOT EXISTS asset (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(50),
    criticality VARCHAR(20),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    x DOUBLE,
    y DOUBLE,
    last_seen TIMESTAMP
);

CREATE TABLE IF NOT EXISTS security_incident (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255),
    type VARCHAR(50),
    severity VARCHAR(20),
    status VARCHAR(50) DEFAULT 'OPEN',
    x DOUBLE,
    y DOUBLE,
    description TEXT,
    affected_asset_id VARCHAR(36),
    detected_at TIMESTAMP,
    resolved_at TIMESTAMP,
    assigned_to VARCHAR(255)
);