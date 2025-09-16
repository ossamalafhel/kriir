# API Documentation - COP Platform

This document provides comprehensive API documentation for the COP - CyberRisk Open Platform with ransomware defense focus.

## Base URL

- **Development**: `http://localhost:8080`
- **Production**: `https://api.cop-platform.org`

## Authentication

- **Method**: OAuth 2.0 / JWT Bearer Token
- **Header**: `Authorization: Bearer <token>`

## Content Types

- **Request**: `application/json`
- **Response**: `application/json`
- **Server-Sent Events**: `text/event-stream`
- **WebSocket**: `ws://` or `wss://`

## Core API Endpoints

### 🎯 Ransomware Prediction API

#### Predict Ransomware Risk

**Endpoint**: `POST /api/v1/predictions/ransomware-risk`

**Description**: Predict ransomware attack probability for an organization.

**Request Body**:
```json
{
  "organization": {
    "name": "Acme Corp",
    "industry": "manufacturing",
    "size": "enterprise",
    "location": {
      "latitude": 40.7128,
      "longitude": -74.0060
    },
    "securityProfile": {
      "mfaEnabled": true,
      "backupFrequency": "daily",
      "patchingCadence": "monthly"
    }
  },
  "timeHorizon": "PT72H"
}
```

**Response**:
```json
{
  "riskScore": 7.8,
  "confidence": 0.92,
  "attackProbability": {
    "next24Hours": 0.15,
    "next72Hours": 0.45,
    "next7Days": 0.68
  },
  "vulnerableAttackPaths": [
    {
      "vector": "phishing",
      "probability": 0.34,
      "mitigations": ["user training", "email filtering"]
    },
    {
      "vector": "rdp_brute_force",
      "probability": 0.28,
      "mitigations": ["mfa", "vpn_only_access"]
    }
  ],
  "recommendedActions": [
    "Enable MFA on all admin accounts",
    "Update Windows systems (critical patches pending)",
    "Implement network segmentation"
  ]
}
```

**Example**:
```bash
curl -X POST "http://localhost:8080/api/v1/predictions/ransomware-risk" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "organization": {
      "name": "Test Corp",
      "industry": "healthcare",
      "size": "medium"
    },
    "timeHorizon": "PT48H"
  }'
```

### ⚡ Kill-Chain Monitoring API

#### Stream Kill-Chain Events

**Endpoint**: `GET /api/v1/killchain/stream`

**Description**: Real-time stream of ransomware kill-chain progression events.

**Query Parameters**:
- `severity`: Filter by severity (low, medium, high, critical)
- `stage`: Filter by kill-chain stage

**Response** (Server-Sent Events):
```
event: killchain-alert
data: {
  "id": "evt-123",
  "timestamp": "2025-01-15T10:30:00Z",
  "stage": "lateral_movement",
  "confidence": 0.89,
  "affectedAssets": ["srv-01", "wks-42"],
  "indicators": [
    "Mimikatz process detected",
    "Abnormal RDP connections"
  ],
  "recommendedResponse": "isolate_network"
}

event: killchain-update
data: {
  "id": "evt-123",
  "progression": "lateral_movement -> collection",
  "timeToEncryption": "PT2H30M",
  "urgency": "critical"
}
```

### 🛡️ Automated Defense API

#### Trigger Emergency Response

**Endpoint**: `POST /api/v1/defense/emergency-response`

**Description**: Activate automated ransomware defense measures.

**Request Body**:
```json
{
  "threatId": "evt-123",
  "responseType": "full_isolation",
  "affectedAssets": ["srv-01", "wks-42"],
  "backupTrigger": true,
  "notifyChannels": ["email", "sms", "slack"]
}
```

**Response**:
```json
{
  "responseId": "resp-456",
  "status": "initiated",
  "actions": [
    {
      "action": "network_isolation",
      "targets": 2,
      "status": "completed"
    },
    {
      "action": "emergency_backup",
      "status": "in_progress",
      "estimatedCompletion": "PT15M"
    },
    {
      "action": "critical_alert",
      "channels": ["email", "sms", "slack"],
      "status": "sent"
    }
  ],
  "timestamp": "2025-01-15T10:32:00Z"
}
```

### 💼 IT Asset Management API

#### List IT Assets

**Endpoint**: `GET /api/v1/assets`

**Description**: Retrieve all monitored IT assets with risk scores.

**Query Parameters**:
- `type`: Filter by asset type (server, workstation, network, iot, cloud)
- `criticality`: Filter by criticality (low, medium, high, critical)
- `status`: Filter by status (active, inactive, compromised)
- `page`: Page number (default: 0)
- `size`: Page size (default: 20)

**Response**:
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "DC-01",
      "type": "server",
      "criticality": "critical",
      "status": "active",
      "location": {
        "x": -74.0060,
        "y": 40.7128
      },
      "riskScore": 8.5,
      "vulnerabilities": 3,
      "lastSeen": "2025-01-15T10:00:00Z"
    }
  ],
  "totalElements": 150,
  "totalPages": 8,
  "number": 0
}
```

#### Create Asset

**Endpoint**: `POST /api/v1/assets`

**Description**: Register a new IT asset for monitoring.

**Request Body**:
```json
{
  "name": "WEB-03",
  "type": "server",
  "criticality": "high",
  "location": {
    "x": -74.0060,
    "y": 40.7128
  },
  "metadata": {
    "os": "Ubuntu 22.04",
    "services": ["nginx", "postgresql"],
    "owner": "web-team"
  }
}
```

#### Find Assets Within Radius

**Endpoint**: `GET /api/v1/assets/nearby`

**Description**: Find assets within a geographic radius (useful for incident correlation).

**Query Parameters**:
- `x`: Longitude
- `y`: Latitude
- `radius`: Radius in meters

**Example**:
```bash
curl "http://localhost:8080/api/v1/assets/nearby?x=-74.0060&y=40.7128&radius=1000"
```

### 🚨 Security Incident API

#### List Security Incidents

**Endpoint**: `GET /api/v1/incidents`

**Description**: Retrieve security incidents with ransomware indicators.

**Query Parameters**:
- `severity`: Filter by severity
- `type`: Filter by incident type
- `status`: Filter by status (open, investigating, contained, resolved)
- `ransomware`: Boolean flag for ransomware-related incidents

**Response**:
```json
{
  "content": [
    {
      "id": "inc-789",
      "title": "Potential Ransomware Activity Detected",
      "severity": "critical",
      "type": "ransomware_indicators",
      "status": "investigating",
      "detectedAt": "2025-01-15T09:45:00Z",
      "affectedAssets": 5,
      "indicators": [
        "Mass file encryption activity",
        "Shadow copy deletion",
        "Suspicious network scanning"
      ],
      "ransomwareConfidence": 0.87,
      "estimatedImpact": {
        "systems": 12,
        "dataAtRisk": "2.5TB",
        "estimatedDowntime": "P2D"
      }
    }
  ]
}
```

#### Report Security Incident

**Endpoint**: `POST /api/v1/incidents`

**Description**: Report a new security incident.

**Request Body**:
```json
{
  "title": "Suspicious encryption activity on FILE-01",
  "description": "Detected rapid file modifications with high entropy",
  "severity": "high",
  "type": "potential_ransomware",
  "affectedAssetIds": ["asset-123", "asset-456"],
  "indicators": [
    "File extension changes to .locked",
    "High CPU usage by unknown process",
    "Network connections to known C2 servers"
  ]
}
```

### 📊 Analytics API

#### Get Ransomware Trends

**Endpoint**: `GET /api/v1/analytics/ransomware-trends`

**Description**: Analyze ransomware attack trends and patterns.

**Query Parameters**:
- `period`: Time period (7d, 30d, 90d, 1y)
- `groupBy`: Grouping (day, week, month)

**Response**:
```json
{
  "period": "30d",
  "trends": [
    {
      "date": "2025-01-01",
      "attacks": 12,
      "prevented": 10,
      "successRate": 0.83
    }
  ],
  "topVectors": [
    {"vector": "phishing", "count": 45},
    {"vector": "rdp_brute_force", "count": 32}
  ],
  "targetedIndustries": [
    {"industry": "healthcare", "percentage": 0.28},
    {"industry": "manufacturing", "percentage": 0.22}
  ]
}
```

### 🔗 Insurance Integration API

#### Get Insurance Risk Score

**Endpoint**: `POST /api/v1/insurance/risk-score`

**Description**: Calculate ransomware insurance risk score.

**Request Body**:
```json
{
  "organizationId": "org-123",
  "coverageType": "ransomware",
  "assessmentDepth": "comprehensive"
}
```

**Response**:
```json
{
  "riskScore": 72,
  "rating": "medium-high",
  "factors": {
    "securityPosture": 65,
    "industryRisk": 78,
    "historicalIncidents": 45,
    "backupMaturity": 82
  },
  "premiumIndicator": {
    "baseRate": 2.3,
    "riskMultiplier": 1.4
  },
  "recommendations": [
    "Implement immutable backups",
    "Enhance employee security training",
    "Deploy EDR on all endpoints"
  ]
}
```

## WebSocket API

### Real-time Ransomware Alerts

**Endpoint**: `ws://localhost:8080/ws/v1/ransomware-alerts`

**Subscribe Message**:
```json
{
  "action": "subscribe",
  "channels": ["kill-chain", "predictions", "incidents"],
  "filters": {
    "severity": ["high", "critical"],
    "confidence": 0.7
  }
}
```

**Alert Messages**:
```json
{
  "type": "ransomware-alert",
  "severity": "critical",
  "data": {
    "alertId": "alert-123",
    "message": "High-confidence ransomware activity detected",
    "affectedSystems": 3,
    "killChainStage": "encryption",
    "timeToAction": "PT5M"
  }
}
```

## Error Handling

All endpoints follow consistent error response format:

```json
{
  "timestamp": "2025-01-15T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid organization data",
  "path": "/api/v1/predictions/ransomware-risk",
  "errors": [
    {
      "field": "organization.industry",
      "message": "Industry is required"
    }
  ]
}
```

## Rate Limiting

- **Default**: 100 requests per minute per IP
- **Authenticated**: 1000 requests per minute per user
- **Prediction API**: 20 requests per minute (resource intensive)

## Status Codes

- `200 OK`: Successful request
- `201 Created`: Resource created successfully
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Missing or invalid authentication
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `429 Too Many Requests`: Rate limit exceeded
- `500 Internal Server Error`: Server error
- `503 Service Unavailable`: Service temporarily unavailable

## API Versioning

The API uses URL versioning. Current version is `v1`. When breaking changes are introduced, a new version will be created while maintaining backward compatibility.

Example:
- Current: `/api/v1/assets`
- Future: `/api/v2/assets`

## OpenAPI Specification

The complete OpenAPI 3.0 specification is available at:
- **JSON**: `http://localhost:8080/v3/api-docs`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`

---

Built by **Ossama Lafhel** - [ossama.lafhel@kanpredict.com](mailto:ossama.lafhel@kanpredict.com)