# Deployment Guide

This guide covers deployment strategies for the Reactive Transactional Mobility Platform across different environments.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Environment Configuration](#environment-configuration)
3. [Docker Deployment](#docker-deployment)
4. [Production Deployment](#production-deployment)
5. [Cloud Deployment](#cloud-deployment)
6. [Monitoring and Maintenance](#monitoring-and-maintenance)
7. [Troubleshooting](#troubleshooting)

## Prerequisites

### System Requirements

- **Docker**: 20.10+ with Docker Compose 2.0+
- **Memory**: Minimum 4GB RAM (8GB+ recommended for production)
- **CPU**: 2+ cores (4+ cores recommended for production)
- **Storage**: 20GB+ available disk space
- **Network**: Ports 3000, 8080, 5432 available

### Optional Requirements

- **Domain name**: For production deployment
- **SSL certificate**: For HTTPS (Let's Encrypt recommended)
- **Load balancer**: For high availability
- **Monitoring tools**: Prometheus, Grafana, ELK stack

## Environment Configuration

### Environment Variables

Create environment-specific `.env` files:

#### `.env.development`
```bash
# Application
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080
FRONTEND_PORT=3000

# Database
POSTGRES_DB=mobility_db
POSTGRES_USER=rci
POSTGRES_PASSWORD=rci_dev_password
POSTGRES_HOST=localhost
POSTGRES_PORT=5432

# Frontend
REACT_APP_API_URL=http://localhost:8080
REACT_APP_MAPBOX_TOKEN=your_mapbox_token_here
REACT_APP_ENVIRONMENT=development

# Java/JVM
JAVA_OPTS=-Xmx512m -Xms256m

# Docker
COMPOSE_PROJECT_NAME=reactive-transactional-dev
```

#### `.env.production`
```bash
# Application
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
FRONTEND_PORT=3000

# Database
POSTGRES_DB=mobility_db
POSTGRES_USER=rci
POSTGRES_PASSWORD=your_secure_production_password
POSTGRES_HOST=postgis
POSTGRES_PORT=5432

# Frontend
REACT_APP_API_URL=https://api.yourdomain.com
REACT_APP_MAPBOX_TOKEN=your_production_mapbox_token
REACT_APP_ENVIRONMENT=production

# Java/JVM
JAVA_OPTS=-Xmx1g -Xms512m -XX:+UseG1GC

# Docker
COMPOSE_PROJECT_NAME=reactive-transactional-prod

# SSL (if using)
SSL_CERT_PATH=/path/to/cert.pem
SSL_KEY_PATH=/path/to/key.pem
```

## Docker Deployment

### Development Environment

```bash
# Clone repository
git clone https://github.com/your-org/reactive-transactional.git
cd reactive-transactional

# Create development environment file
cp .env.development .env

# Start development environment
docker-compose -f docker-compose.dev.yml up --build

# Verify deployment
curl http://localhost:8080/actuator/health
curl http://localhost:3000/
```

### Production Environment

```bash
# Create production environment file
cp .env.production .env

# Update environment variables
nano .env

# Build and start production services
docker-compose up --build -d

# Verify deployment
docker-compose ps
docker-compose logs -f
```

### Service Management

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# Restart specific service
docker-compose restart server

# View logs
docker-compose logs -f server
docker-compose logs -f front

# Scale services (if load balancing is configured)
docker-compose up -d --scale server=3

# Update and redeploy
git pull
docker-compose up --build -d
```

## Production Deployment

### 1. Server Preparation

#### Ubuntu/Debian
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Install additional tools
sudo apt install -y htop curl wget git
```

#### CentOS/RHEL
```bash
# Update system
sudo yum update -y

# Install Docker
sudo yum install -y yum-utils
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo yum install -y docker-ce docker-ce-cli containerd.io
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 2. Application Deployment

```bash
# Create application directory
sudo mkdir -p /opt/reactive-transactional
cd /opt/reactive-transactional

# Clone application
git clone https://github.com/your-org/reactive-transactional.git .

# Set up environment
cp .env.production .env
sudo chown -R $USER:$USER /opt/reactive-transactional

# Configure secrets (use Docker secrets in production)
echo "your_secure_db_password" | docker secret create postgres_password -
echo "your_mapbox_token" | docker secret create mapbox_token -

# Deploy application
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### 3. Reverse Proxy Configuration

#### Nginx Configuration

Create `/etc/nginx/sites-available/reactive-transactional`:

```nginx
upstream backend {
    server localhost:8080 max_fails=3 fail_timeout=30s;
    # Add more servers for load balancing
    # server server2:8080 max_fails=3 fail_timeout=30s;
}

upstream frontend {
    server localhost:3000 max_fails=3 fail_timeout=30s;
}

# Redirect HTTP to HTTPS
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;
    return 301 https://$server_name$request_uri;
}

# HTTPS Configuration
server {
    listen 443 ssl http2;
    server_name yourdomain.com www.yourdomain.com;

    # SSL Configuration
    ssl_certificate /path/to/certificate.pem;
    ssl_certificate_key /path/to/private_key.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # Security Headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;

    # Frontend (React application)
    location / {
        proxy_pass http://frontend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Handle React Router
        try_files $uri $uri/ /index.html;
    }

    # Backend API
    location /api/ {
        proxy_pass http://backend/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Server-Sent Events (SSE) configuration
    location /cars/flux {
        proxy_pass http://backend/cars/flux;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # SSE specific configuration
        proxy_http_version 1.1;
        proxy_set_header Connection '';
        proxy_cache_bypass $http_upgrade;
        proxy_buffering off;
        proxy_read_timeout 24h;
    }

    location /users/flux {
        proxy_pass http://backend/users/flux;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # SSE specific configuration
        proxy_http_version 1.1;
        proxy_set_header Connection '';
        proxy_cache_bypass $http_upgrade;
        proxy_buffering off;
        proxy_read_timeout 24h;
    }

    # Static file caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
        add_header X-Frame-Options "SAMEORIGIN" always;
        add_header X-Content-Type-Options "nosniff" always;
    }

    # Health checks
    location /health {
        proxy_pass http://backend/actuator/health;
        access_log off;
    }
}
```

Enable the site:
```bash
sudo ln -s /etc/nginx/sites-available/reactive-transactional /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### 4. SSL Certificate Setup

#### Using Let's Encrypt (Certbot)

```bash
# Install Certbot
sudo apt install -y certbot python3-certbot-nginx

# Obtain certificate
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com

# Automatic renewal
sudo crontab -e
# Add: 0 12 * * * /usr/bin/certbot renew --quiet
```

## Cloud Deployment

### AWS Deployment

#### Using ECS (Elastic Container Service)

1. **Create ECR Repositories**:
```bash
aws ecr create-repository --repository-name reactive-transactional/frontend
aws ecr create-repository --repository-name reactive-transactional/backend
```

2. **Build and Push Images**:
```bash
# Get ECR login
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 123456789.dkr.ecr.us-east-1.amazonaws.com

# Build and tag images
docker build -t reactive-transactional/frontend ./front
docker build -t reactive-transactional/backend ./server

# Tag for ECR
docker tag reactive-transactional/frontend:latest 123456789.dkr.ecr.us-east-1.amazonaws.com/reactive-transactional/frontend:latest
docker tag reactive-transactional/backend:latest 123456789.dkr.ecr.us-east-1.amazonaws.com/reactive-transactional/backend:latest

# Push to ECR
docker push 123456789.dkr.ecr.us-east-1.amazonaws.com/reactive-transactional/frontend:latest
docker push 123456789.dkr.ecr.us-east-1.amazonaws.com/reactive-transactional/backend:latest
```

3. **ECS Task Definition** (task-definition.json):
```json
{
  "family": "reactive-transactional",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "1024",
  "memory": "2048",
  "executionRoleArn": "arn:aws:iam::123456789:role/ecsTaskExecutionRole",
  "containerDefinitions": [
    {
      "name": "backend",
      "image": "123456789.dkr.ecr.us-east-1.amazonaws.com/reactive-transactional/backend:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "essential": true,
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        },
        {
          "name": "POSTGRES_HOST",
          "value": "your-rds-endpoint"
        }
      ],
      "secrets": [
        {
          "name": "POSTGRES_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:123456789:secret:prod/db/password"
        }
      ]
    },
    {
      "name": "frontend",
      "image": "123456789.dkr.ecr.us-east-1.amazonaws.com/reactive-transactional/frontend:latest",
      "portMappings": [
        {
          "containerPort": 3000,
          "protocol": "tcp"
        }
      ],
      "essential": true
    }
  ]
}
```

### Google Cloud Platform

#### Using Cloud Run

```bash
# Build and deploy backend
gcloud builds submit --tag gcr.io/YOUR_PROJECT_ID/reactive-transactional-backend ./server
gcloud run deploy reactive-transactional-backend \
  --image gcr.io/YOUR_PROJECT_ID/reactive-transactional-backend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated

# Build and deploy frontend
gcloud builds submit --tag gcr.io/YOUR_PROJECT_ID/reactive-transactional-frontend ./front
gcloud run deploy reactive-transactional-frontend \
  --image gcr.io/YOUR_PROJECT_ID/reactive-transactional-frontend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

### Kubernetes Deployment

#### Kubernetes Manifests

**namespace.yaml**:
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: reactive-transactional
```

**configmap.yaml**:
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
  namespace: reactive-transactional
data:
  SPRING_PROFILES_ACTIVE: "prod"
  POSTGRES_HOST: "postgresql-service"
  POSTGRES_DB: "mobility_db"
  POSTGRES_USER: "rci"
```

**secret.yaml**:
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
  namespace: reactive-transactional
type: Opaque
data:
  POSTGRES_PASSWORD: <base64-encoded-password>
  MAPBOX_TOKEN: <base64-encoded-token>
```

**deployment.yaml**:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend-deployment
  namespace: reactive-transactional
spec:
  replicas: 3
  selector:
    matchLabels:
      app: backend
  template:
    metadata:
      labels:
        app: backend
    spec:
      containers:
      - name: backend
        image: your-registry/reactive-transactional-backend:latest
        ports:
        - containerPort: 8080
        envFrom:
        - configMapRef:
            name: app-config
        - secretRef:
            name: app-secrets
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend-deployment
  namespace: reactive-transactional
spec:
  replicas: 2
  selector:
    matchLabels:
      app: frontend
  template:
    metadata:
      labels:
        app: frontend
    spec:
      containers:
      - name: frontend
        image: your-registry/reactive-transactional-frontend:latest
        ports:
        - containerPort: 3000
        resources:
          requests:
            memory: "256Mi"
            cpu: "100m"
          limits:
            memory: "512Mi"
            cpu: "200m"
```

**service.yaml**:
```yaml
apiVersion: v1
kind: Service
metadata:
  name: backend-service
  namespace: reactive-transactional
spec:
  selector:
    app: backend
  ports:
  - protocol: TCP
    port: 8080
    targetPort: 8080
  type: ClusterIP
---
apiVersion: v1
kind: Service
metadata:
  name: frontend-service
  namespace: reactive-transactional
spec:
  selector:
    app: frontend
  ports:
  - protocol: TCP
    port: 3000
    targetPort: 3000
  type: ClusterIP
```

**ingress.yaml**:
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: app-ingress
  namespace: reactive-transactional
  annotations:
    kubernetes.io/ingress.class: nginx
    cert-manager.io/cluster-issuer: letsencrypt-prod
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  tls:
  - hosts:
    - yourdomain.com
    secretName: app-tls
  rules:
  - host: yourdomain.com
    http:
      paths:
      - path: /api
        pathType: Prefix
        backend:
          service:
            name: backend-service
            port:
              number: 8080
      - path: /
        pathType: Prefix
        backend:
          service:
            name: frontend-service
            port:
              number: 3000
```

Deploy to Kubernetes:
```bash
kubectl apply -f namespace.yaml
kubectl apply -f configmap.yaml
kubectl apply -f secret.yaml
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl apply -f ingress.yaml
```

## Monitoring and Maintenance

### Health Checks

```bash
#!/bin/bash
# health-check.sh

echo "=== Health Check Report ==="
echo "Timestamp: $(date)"

# Check services
echo "=== Docker Services ==="
docker-compose ps

# Check backend health
echo "=== Backend Health ==="
curl -s http://localhost:8080/actuator/health | jq '.'

# Check frontend
echo "=== Frontend Health ==="
curl -s -o /dev/null -w "Status: %{http_code}\n" http://localhost:3000/

# Check database
echo "=== Database Health ==="
docker exec reactive-transactional-postgis pg_isready -U rci -d mobility_db

# Check disk space
echo "=== Disk Usage ==="
df -h | grep -E '^/dev/'

# Check memory usage
echo "=== Memory Usage ==="
free -h

echo "=== Health Check Complete ==="
```

### Backup Strategy

```bash
#!/bin/bash
# backup.sh

BACKUP_DIR="/opt/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="mobility_db_backup_$DATE.sql"

# Create backup directory
mkdir -p $BACKUP_DIR

# Database backup
docker exec reactive-transactional-postgis pg_dump -U rci -d mobility_db > $BACKUP_DIR/$BACKUP_FILE

# Compress backup
gzip $BACKUP_DIR/$BACKUP_FILE

# Keep only last 7 days of backups
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete

echo "Backup completed: $BACKUP_DIR/$BACKUP_FILE.gz"
```

### Log Rotation

```bash
# Setup logrotate for Docker logs
sudo tee /etc/logrotate.d/docker-container > /dev/null <<EOF
/var/lib/docker/containers/*/*.log {
    rotate 7
    daily
    compress
    size=1M
    missingok
    delaycompress
    copytruncate
}
EOF
```

## Troubleshooting

### Common Issues

#### 1. Database Connection Issues

```bash
# Check database container
docker-compose logs postgis

# Test database connection
docker exec reactive-transactional-server ping -c 3 postgis

# Check database from application container
docker exec reactive-transactional-server nc -zv postgis 5432
```

#### 2. Frontend Build Issues

```bash
# Clear Docker build cache
docker system prune -a

# Rebuild with no cache
docker-compose build --no-cache front

# Check Node.js memory
docker-compose logs front | grep "FATAL ERROR"
```

#### 3. Performance Issues

```bash
# Check container resource usage
docker stats

# Check JVM memory usage
docker exec reactive-transactional-server jstat -gc 1

# Monitor database performance
docker exec reactive-transactional-postgis psql -U rci -d mobility_db -c "SELECT * FROM pg_stat_activity;"
```

### Debugging Commands

```bash
# Enter container shell
docker exec -it reactive-transactional-server /bin/sh
docker exec -it reactive-transactional-front /bin/sh
docker exec -it reactive-transactional-postgis /bin/bash

# View application logs
docker-compose logs -f --tail=100 server
docker-compose logs -f --tail=100 front

# Check network connectivity
docker network ls
docker network inspect reactive-transactional_mobility-network

# Test API endpoints
curl -v http://localhost:8080/actuator/health
curl -v http://localhost:8080/cars
curl -v http://localhost:3000/
```

### Recovery Procedures

#### Database Recovery

```bash
# Stop application
docker-compose down

# Restore database from backup
gunzip -c /opt/backups/mobility_db_backup_YYYYMMDD_HHMMSS.sql.gz | \
docker exec -i reactive-transactional-postgis psql -U rci -d mobility_db

# Start application
docker-compose up -d
```

#### Complete System Recovery

```bash
# Pull latest images
docker-compose pull

# Restart all services
docker-compose down
docker-compose up -d

# Verify system health
./health-check.sh
```

This deployment guide provides comprehensive instructions for deploying the Reactive Transactional Mobility Platform in various environments with proper monitoring and maintenance procedures.