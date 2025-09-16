# API Documentation

This document provides comprehensive API documentation for the Reactive Transactional Mobility Platform.

## Base URL

- **Development**: `http://localhost:8080`
- **Production**: `https://your-domain.com/api`

## Authentication

Currently, the API doesn't require authentication, but it's designed to be easily extensible with authentication mechanisms.

## Content Types

- **Request**: `application/json`
- **Response**: `application/json`
- **Server-Sent Events**: `text/event-stream`

## Car Endpoints

### Get All Cars

**Endpoint**: `GET /cars`

**Description**: Retrieve all cars in the system.

**Response**:
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "x": 7.06064,
    "y": 48.092971
  }
]
```

**Example**:
```bash
curl -X GET "http://localhost:8080/cars" \
  -H "Accept: application/json"
```

### Create Car

**Endpoint**: `POST /cars`

**Description**: Create a new car with coordinates.

**Request Body**:
```json
{
  "x": 7.06064,
  "y": 48.092971
}
```

**Response**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "x": 7.06064,
  "y": 48.092971
}
```

**Example**:
```bash
curl -X POST "http://localhost:8080/cars" \
  -H "Content-Type: application/json" \
  -d '{
    "x": 7.06064,
    "y": 48.092971
  }'
```

### Real-time Car Updates

**Endpoint**: `GET /cars/flux`

**Description**: Server-Sent Events stream for real-time car updates.

**Response**: Stream of car data
```
data: {"id":"550e8400-e29b-41d4-a716-446655440000","x":7.06064,"y":48.092971}

data: {"id":"550e8400-e29b-41d4-a716-446655440000","x":7.06070,"y":48.092980}
```

**Example**:
```bash
curl -N -H "Accept: text/event-stream" \
  "http://localhost:8080/cars/flux"
```

**JavaScript Example**:
```javascript
const eventSource = new EventSource('http://localhost:8080/cars/flux');
eventSource.onmessage = function(event) {
  const carData = JSON.parse(event.data);
  console.log('Car update:', carData);
};
```

### Get Cars Within Area

**Endpoint**: `GET /cars/inside`

**Description**: Get cars within a specified distance from a point.

**Parameters**:
- `x` (required): X coordinate (longitude)
- `y` (required): Y coordinate (latitude)
- `distance` (required): Distance in meters

**Example**:
```bash
curl -X GET "http://localhost:8080/cars/inside?x=7.06064&y=48.092971&distance=1000"
```

**Response**:
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "x": 7.06064,
    "y": 48.092971
  }
]
```

## User Endpoints

### Get All Users

**Endpoint**: `GET /users`

**Description**: Retrieve all users in the system.

**Response**:
```json
[
  {
    "id": "660e8400-e29b-41d4-a716-446655440000",
    "x": 7.1,
    "y": 48.1
  }
]
```

### Create User

**Endpoint**: `POST /users`

**Description**: Create a new user with coordinates.

**Request Body**:
```json
{
  "x": 7.1,
  "y": 48.1
}
```

**Response**:
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440000",
  "x": 7.1,
  "y": 48.1
}
```

### Real-time User Updates

**Endpoint**: `GET /users/flux`

**Description**: Server-Sent Events stream for real-time user updates.

**Response**: Stream of user data
```
data: {"id":"660e8400-e29b-41d4-a716-446655440000","x":7.1,"y":48.1}

data: {"id":"660e8400-e29b-41d4-a716-446655440000","x":7.105,"y":48.105}
```

## Error Responses

### Error Format

All error responses follow this structure:

```json
{
  "timestamp": "2025-01-16T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid coordinates provided",
  "path": "/cars"
}
```

### HTTP Status Codes

- `200 OK`: Successful request
- `201 Created`: Resource successfully created
- `400 Bad Request`: Invalid request parameters or body
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

### Common Error Scenarios

#### Invalid Coordinates

**Request**:
```bash
curl -X POST "http://localhost:8080/cars" \
  -H "Content-Type: application/json" \
  -d '{"x": "invalid", "y": 48.092971}'
```

**Response** (400):
```json
{
  "timestamp": "2025-01-16T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid coordinate format",
  "path": "/cars"
}
```

#### Missing Required Fields

**Request**:
```bash
curl -X POST "http://localhost:8080/cars" \
  -H "Content-Type: application/json" \
  -d '{"x": 7.06064}'
```

**Response** (400):
```json
{
  "timestamp": "2025-01-16T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Required field 'y' is missing",
  "path": "/cars"
}
```

## Rate Limiting

Currently, no rate limiting is implemented, but the system is designed to handle high concurrent loads through reactive programming.

## Pagination

For large datasets, pagination can be implemented using standard Spring Boot pagination parameters:

- `page`: Page number (0-based)
- `size`: Page size (default: 20)
- `sort`: Sort criteria

**Example**:
```bash
curl "http://localhost:8080/cars?page=0&size=10&sort=id,asc"
```

## Filtering and Search

### Geospatial Queries

The `/cars/inside` and `/users/inside` endpoints support geospatial filtering:

- **Point-in-radius**: Find entities within a circular area
- **Distance-based**: Specify distance in meters

**Advanced Example**:
```bash
# Find all cars within 5km of a specific point
curl "http://localhost:8080/cars/inside?x=7.06064&y=48.092971&distance=5000"
```

## WebSocket Alternative

While the current implementation uses Server-Sent Events, the architecture supports WebSocket implementation for bidirectional communication:

```javascript
// Future WebSocket implementation
const ws = new WebSocket('ws://localhost:8080/ws/cars');
ws.onmessage = function(event) {
  const data = JSON.parse(event.data);
  console.log('Real-time car update:', data);
};
```

## SDK Examples

### JavaScript/TypeScript Client

```typescript
class MobilityApiClient {
  private baseUrl: string;

  constructor(baseUrl: string = 'http://localhost:8080') {
    this.baseUrl = baseUrl;
  }

  async getCars(): Promise<Car[]> {
    const response = await fetch(`${this.baseUrl}/cars`);
    return response.json();
  }

  async createCar(car: CreateCarRequest): Promise<Car> {
    const response = await fetch(`${this.baseUrl}/cars`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(car)
    });
    return response.json();
  }

  subscribeToCars(callback: (car: Car) => void): EventSource {
    const eventSource = new EventSource(`${this.baseUrl}/cars/flux`);
    eventSource.onmessage = (event) => {
      callback(JSON.parse(event.data));
    };
    return eventSource;
  }

  async getCarsInArea(x: number, y: number, distance: number): Promise<Car[]> {
    const response = await fetch(
      `${this.baseUrl}/cars/inside?x=${x}&y=${y}&distance=${distance}`
    );
    return response.json();
  }
}

// Usage
const client = new MobilityApiClient();
const cars = await client.getCars();
const eventSource = client.subscribeToCars((car) => {
  console.log('Car update:', car);
});
```

### Python Client

```python
import requests
import json
from typing import List, Dict
import sseclient

class MobilityApiClient:
    def __init__(self, base_url: str = "http://localhost:8080"):
        self.base_url = base_url

    def get_cars(self) -> List[Dict]:
        response = requests.get(f"{self.base_url}/cars")
        response.raise_for_status()
        return response.json()

    def create_car(self, x: float, y: float) -> Dict:
        data = {"x": x, "y": y}
        response = requests.post(
            f"{self.base_url}/cars",
            json=data,
            headers={"Content-Type": "application/json"}
        )
        response.raise_for_status()
        return response.json()

    def subscribe_to_cars(self, callback):
        response = requests.get(
            f"{self.base_url}/cars/flux",
            stream=True,
            headers={"Accept": "text/event-stream"}
        )
        
        client = sseclient.SSEClient(response)
        for event in client.events():
            if event.data:
                car_data = json.loads(event.data)
                callback(car_data)

# Usage
client = MobilityApiClient()
cars = client.get_cars()
client.subscribe_to_cars(lambda car: print(f"Car update: {car}"))
```

## Testing the API

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    }
  }
}
```

### Integration Test Script

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"

# Test car creation
echo "Creating car..."
CAR_RESPONSE=$(curl -s -X POST "$BASE_URL/cars" \
  -H "Content-Type: application/json" \
  -d '{"x": 7.06064, "y": 48.092971}')

echo "Car created: $CAR_RESPONSE"

# Test getting all cars
echo "Getting all cars..."
ALL_CARS=$(curl -s "$BASE_URL/cars")
echo "All cars: $ALL_CARS"

# Test geospatial query
echo "Getting cars within 1000m..."
NEARBY_CARS=$(curl -s "$BASE_URL/cars/inside?x=7.06064&y=48.092971&distance=1000")
echo "Nearby cars: $NEARBY_CARS"

echo "API tests completed!"
```

This comprehensive API documentation provides all the information needed to integrate with the Reactive Transactional Mobility Platform.