# Contributing to COP - CyberRisk Open Platform

Thank you for your interest in contributing to the COP ransomware defense platform! This guide will help you contribute to making organizations safer from ransomware attacks.

## Table of Contents

1. [Code of Conduct](#code-of-conduct)
2. [Getting Started](#getting-started)
3. [Development Setup](#development-setup)
4. [Contributing Process](#contributing-process)
5. [Coding Standards](#coding-standards)
6. [Testing Guidelines](#testing-guidelines)
7. [Security Considerations](#security-considerations)
8. [ML Model Contributions](#ml-model-contributions)
9. [Documentation Standards](#documentation-standards)
10. [Pull Request Process](#pull-request-process)

## Code of Conduct

This project follows the [Contributor Covenant Code of Conduct](https://www.contributor-covenant.org/version/2/1/code_of_conduct/). By participating, you are expected to uphold this code. Please report unacceptable behavior to [ossama.lafhel@kanpredict.com](mailto:ossama.lafhel@kanpredict.com).

### Our Pledge

We pledge to make participation in our ransomware defense project a harassment-free experience for everyone, with special emphasis on:
- Collaborative security research
- Responsible disclosure practices
- Ethical AI development
- Privacy-preserving contributions

## Getting Started

### Prerequisites

- **Java 17+** (OpenJDK recommended)
- **Maven 3.6.3+**
- **Docker & Docker Compose**
- **Node.js 18+** (for frontend)
- **PostgreSQL 15** with PostGIS 3.4
- **Python 3.9+** (for ML contributions)
- **Git** with GPG signing (recommended)

### Security Requirements

- Enable 2FA on your GitHub account
- Sign commits with GPG
- Never commit secrets or credentials
- Follow secure coding practices

### Fork and Clone

```bash
# Fork the repository on GitHub

# Clone your fork
git clone https://github.com/YOUR_USERNAME/cyberisk-open-platform.git
cd cyberisk-open-platform

# Add upstream
git remote add upstream https://github.com/ossamalafhel/cyberisk-open-platform.git

# Configure GPG signing
git config commit.gpgsign true
```

## Development Setup

### Quick Start

```bash
# Start development environment with security services
docker-compose -f docker-compose.dev.yml up -d

# Backend API (with hot reload)
cd api
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Frontend Dashboard (with hot reload)
cd dashboard
npm install
npm start
```

### IDE Setup

#### IntelliJ IDEA

1. **Import Project**:
   - Import as Maven project
   - Enable annotation processing
   - Configure Java 17 SDK

2. **Plugins**:
   - Spring Boot
   - Docker
   - SonarLint (security analysis)
   - CheckStyle

3. **Security Analysis**:
   - Enable OWASP Dependency Check
   - Configure SpotBugs for security

#### VS Code

1. **Extensions**:
   - Java Extension Pack
   - Spring Boot Extension Pack
   - Docker
   - ESLint
   - GitLens
   - SonarLint

2. **Settings** (.vscode/settings.json):
```json
{
  "java.configuration.updateBuildConfiguration": "automatic",
  "java.compile.nullAnalysis.mode": "automatic",
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.organizeImports": true
  },
  "sonarlint.rules": {
    "java:S5344": {
      "level": "on"
    }
  }
}
```

## Contributing Process

### 1. Choose an Issue

**Priority Areas**:
- 🔴 **Critical**: Ransomware detection algorithms
- 🟡 **High**: Kill-chain monitoring features
- 🟢 **Normal**: UI/UX improvements
- 🔵 **Research**: ML model enhancements

Look for labels:
- `ransomware-defense` - Core defense features
- `ml-models` - Machine learning improvements
- `security` - Security enhancements
- `good-first-issue` - Beginner-friendly

### 2. Create a Branch

```bash
# Update your fork
git fetch upstream
git checkout main
git rebase upstream/main

# Create feature branch
git checkout -b feature/ransomware-detection-enhancement
# or
git checkout -b fix/killchain-false-positive
# or
git checkout -b ml/improve-prediction-accuracy
```

### 3. Development Guidelines

**Security First**:
- Validate all inputs
- Use parameterized queries
- Implement proper authentication
- Follow OWASP guidelines

**Performance**:
- Real-time processing (<100ms)
- Efficient memory usage
- Reactive patterns

### 4. Testing

```bash
# Run all tests
mvn clean test

# Run with coverage (must be 100%)
mvn clean test jacoco:report

# Security tests
mvn verify -Psecurity

# Performance tests
mvn verify -Pperformance
```

## Coding Standards

### Java/Spring Boot

#### Security-Focused Patterns

```java
@RestController
@RequestMapping("/api/v1/predictions")
@SecurityRequirement(name = "bearer-key")
@Validated
public class RansomwarePredictionController {

  private final PredictionService predictionService;
  private final AuditService auditService;

  @PostMapping("/ransomware-risk")
  @PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
  @Timed(value = "prediction.ransomware.risk")
  public Mono<RiskPredictionResponse> predictRisk(
      @Valid @RequestBody RiskPredictionRequest request,
      Authentication auth) {
    
    return auditService.logAccess("PREDICT_RISK", auth)
        .then(validateRequest(request))
        .flatMap(predictionService::predictRansomwareRisk)
        .doOnSuccess(result -> metricsService.recordPrediction(result))
        .onErrorMap(this::handlePredictionError);
  }

  private Mono<RiskPredictionRequest> validateRequest(RiskPredictionRequest request) {
    return Mono.just(request)
        .filter(r -> r.getOrganization() != null)
        .switchIfEmpty(Mono.error(new ValidationException("Organization required")));
  }
}
```

#### Reactive Service Pattern

```java
@Service
@Slf4j
public class KillChainMonitorService {

  private final KillChainDetector detector;
  private final DefenseOrchestrator orchestrator;
  private final AlertingService alertingService;

  public Flux<KillChainEvent> monitorKillChain(String organizationId) {
    return detector.detectStages(organizationId)
        .filter(event -> event.getConfidence() > 0.7)
        .doOnNext(event -> log.warn("Kill-chain stage detected: {}", event))
        .flatMap(event -> orchestrator.initiateDefense(event)
            .thenReturn(event))
        .doOnNext(alertingService::sendCriticalAlert)
        .onErrorResume(error -> {
          log.error("Kill-chain monitoring error", error);
          return Flux.empty();
        });
  }
}
```

### Frontend (React/TypeScript)

#### Security Component Example

```typescript
import React, { useEffect, useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { RansomwareRiskGauge } from './RansomwareRiskGauge';
import { encryptSensitiveData } from '../utils/crypto';

interface RansomwareDashboardProps {
  organizationId: string;
}

export const RansomwareDashboard: React.FC<RansomwareDashboardProps> = ({ 
  organizationId 
}) => {
  const { user, hasPermission } = useAuth();
  const [riskData, setRiskData] = useState<RiskData | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!hasPermission('VIEW_RISK_DATA')) {
      console.error('Insufficient permissions');
      return;
    }

    const fetchRiskData = async () => {
      try {
        const response = await secureApi.get(`/predictions/ransomware-risk`, {
          params: { organizationId: encryptSensitiveData(organizationId) }
        });
        setRiskData(response.data);
      } catch (error) {
        console.error('Failed to fetch risk data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchRiskData();
  }, [organizationId, hasPermission]);

  if (!hasPermission('VIEW_RISK_DATA')) {
    return <AccessDenied />;
  }

  return (
    <div className="ransomware-dashboard">
      {loading ? (
        <LoadingSpinner />
      ) : (
        <RansomwareRiskGauge 
          riskScore={riskData?.riskScore} 
          confidence={riskData?.confidence}
        />
      )}
    </div>
  );
};
```

### Database Standards

#### Secure Migration Example

```sql
-- V2.0.0__Add_ransomware_predictions_table.sql

CREATE TABLE ransomware_predictions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id),
    risk_score DECIMAL(3,1) NOT NULL CHECK (risk_score >= 0 AND risk_score <= 10),
    confidence DECIMAL(3,2) NOT NULL CHECK (confidence >= 0 AND confidence <= 1),
    attack_probability JSONB NOT NULL,
    vulnerable_paths JSONB,
    prediction_timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    model_version VARCHAR(20) NOT NULL,
    encrypted_metadata BYTEA, -- For sensitive data
    
    CONSTRAINT valid_prediction CHECK (
        jsonb_typeof(attack_probability) = 'object' AND
        attack_probability ? 'next24Hours' AND
        attack_probability ? 'next72Hours'
    )
);

-- Security indexes
CREATE INDEX idx_predictions_org_time ON ransomware_predictions(organization_id, prediction_timestamp DESC);
CREATE INDEX idx_predictions_high_risk ON ransomware_predictions(risk_score) WHERE risk_score > 7;

-- Row-level security
ALTER TABLE ransomware_predictions ENABLE ROW LEVEL SECURITY;

CREATE POLICY org_isolation ON ransomware_predictions
    FOR ALL
    USING (organization_id = current_setting('app.current_org_id')::uuid);

-- Audit trigger
CREATE TRIGGER audit_predictions
    AFTER INSERT OR UPDATE ON ransomware_predictions
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();
```

## Testing Guidelines

### Security Testing

```java
@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

  @Test
  @DisplayName("Should reject unauthenticated prediction requests")
  void shouldRejectUnauthenticated() throws Exception {
    mockMvc.perform(post("/api/v1/predictions/ransomware-risk")
            .contentType(MediaType.APPLICATION_JSON)
            .content(validRequest()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "USER")
  @DisplayName("Should reject insufficient permissions")
  void shouldRejectInsufficientPermissions() throws Exception {
    mockMvc.perform(post("/api/v1/predictions/ransomware-risk")
            .contentType(MediaType.APPLICATION_JSON)
            .content(validRequest()))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should prevent SQL injection attempts")
  void shouldPreventSqlInjection() throws Exception {
    String maliciousInput = "'; DROP TABLE assets; --";
    
    AssetSearchRequest request = new AssetSearchRequest();
    request.setName(maliciousInput);
    
    mockMvc.perform(post("/api/v1/assets/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Invalid input"));
  }
}
```

### Performance Testing

```java
@Test
@DisplayName("Prediction should complete within 100ms")
void predictionPerformance() {
    StepVerifier.create(predictionService.predictRansomwareRisk(testRequest))
        .expectNextMatches(response -> response.getRiskScore() > 0)
        .expectComplete()
        .verify(Duration.ofMillis(100));
}
```

## Security Considerations

### Threat Model

1. **Data Protection**:
   - Encrypt sensitive prediction data
   - Use field-level encryption for PII
   - Implement data retention policies

2. **Access Control**:
   - Role-based permissions
   - API rate limiting
   - Geographic restrictions

3. **Audit Trail**:
   - Log all predictions
   - Track model usage
   - Monitor for anomalies

### Secure Coding Checklist

- [ ] Input validation on all endpoints
- [ ] Output encoding for XSS prevention
- [ ] CSRF protection enabled
- [ ] SQL injection prevention
- [ ] Secure session management
- [ ] Error messages don't leak information
- [ ] Secrets stored securely
- [ ] Dependencies scanned for vulnerabilities

## ML Model Contributions

### Model Submission Process

1. **Model Requirements**:
   - Accuracy: >90% on test set
   - False positive rate: <5%
   - Inference time: <50ms
   - Explainable predictions

2. **Submission Format**:
```python
# models/ransomware_predictor_v3.py
class RansomwarePredictor:
    """
    Ransomware prediction model using gradient boosting.
    
    Accuracy: 92.3% on test set
    FPR: 3.2%
    Average inference: 35ms
    """
    
    def __init__(self, model_path: str):
        self.model = self._load_model(model_path)
        self.feature_importance = self._calculate_importance()
    
    def predict(self, features: Dict[str, Any]) -> PredictionResult:
        # Validate input features
        validated = self._validate_features(features)
        
        # Make prediction
        risk_score = self.model.predict_proba(validated)[0][1]
        
        # Generate explanation
        explanation = self._explain_prediction(validated, risk_score)
        
        return PredictionResult(
            risk_score=risk_score,
            confidence=self._calculate_confidence(validated),
            explanation=explanation,
            contributing_factors=self._get_top_factors(validated)
        )
```

3. **Testing Requirements**:
   - Unit tests for all methods
   - Performance benchmarks
   - Bias testing
   - Adversarial robustness

## Documentation Standards

### API Documentation

```java
/**
 * Predicts ransomware attack probability for an organization.
 * 
 * @param request Organization details and security profile
 * @return Risk prediction with confidence scores and recommendations
 * 
 * @throws ValidationException if request data is invalid
 * @throws InsufficientDataException if not enough historical data
 * @throws ModelException if prediction model fails
 * 
 * @security Requires ANALYST or ADMIN role
 * @rateLimit 100 requests per minute per organization
 * 
 * @example
 * POST /api/v1/predictions/ransomware-risk
 * {
 *   "organization": {
 *     "name": "Acme Corp",
 *     "industry": "healthcare",
 *     "size": "medium"
 *   },
 *   "timeHorizon": "PT72H"
 * }
 */
@PostMapping("/ransomware-risk")
public Mono<RiskPredictionResponse> predictRisk(@Valid @RequestBody RiskPredictionRequest request);
```

### Code Comments

```java
// SECURITY: Rate limit to prevent model abuse
@RateLimiter(name = "prediction-api")
public Mono<PredictionResult> predict(PredictionRequest request) {
    return Mono.just(request)
        // VALIDATION: Ensure all required fields present
        .filter(this::isValid)
        // SECURITY: Check organization permissions
        .filterWhen(this::hasPermission)
        // PERFORMANCE: Cache similar predictions
        .flatMap(this::checkCache)
        // ML: Run through prediction pipeline
        .switchIfEmpty(runPrediction(request))
        // AUDIT: Log all predictions for compliance
        .doOnNext(this::auditLog);
}
```

## Pull Request Process

### PR Template

```markdown
## Description
Brief description of changes and their security impact.

## Type of Change
- [ ] 🐛 Bug fix (ransomware detection issue)
- [ ] ✨ New feature (enhanced defense capability)
- [ ] 🚨 Security fix (vulnerability patch)
- [ ] 🤖 ML improvement (model accuracy)
- [ ] 📚 Documentation

## Security Impact
- [ ] Changes authentication/authorization
- [ ] Handles sensitive data
- [ ] Modifies security controls
- [ ] Updates dependencies

## Testing
- [ ] Unit tests pass (100% coverage)
- [ ] Integration tests pass
- [ ] Security tests pass
- [ ] Performance benchmarks met
- [ ] ML model validation complete

## Checklist
- [ ] Code follows security guidelines
- [ ] No hardcoded secrets
- [ ] Input validation implemented
- [ ] Error handling doesn't leak info
- [ ] Documentation updated
- [ ] CHANGELOG.md updated

## Performance Impact
- Prediction latency: before/after
- Memory usage: before/after
- API response time: before/after

## Screenshots (if UI changes)
Include before/after screenshots
```

### Review Process

1. **Automated Checks**:
   - Security scanning (SonarQube)
   - Dependency check (OWASP)
   - Test coverage (100%)
   - Performance tests

2. **Manual Review**:
   - Security review for critical changes
   - Code quality review
   - Documentation completeness
   - ML model validation

3. **Approval Requirements**:
   - 1 approval for normal changes
   - 2 approvals for security changes
   - 3 approvals for ML model updates

## Getting Help

- **Security Issues**: [security@cop-platform.org](mailto:security@cop-platform.org)
- **Discord**: #cop-contributors channel
- **Office Hours**: Thursdays 3-4 PM UTC
- **Documentation**: [docs.cop-platform.org](https://docs.cop-platform.org)

## Recognition

Contributors are recognized in:
- CONTRIBUTORS.md file
- Release notes
- Annual security researcher awards
- COP Hall of Fame

## Responsible Disclosure

For security vulnerabilities:
1. Email [security@cop-platform.org](mailto:security@cop-platform.org)
2. Use PGP encryption (key in SECURITY.md)
3. Allow 90 days for patching
4. Coordinated disclosure

---

Thank you for contributing to making the world safer from ransomware! 🛡️

Built by **Ossama Lafhel** - [ossama.lafhel@kanpredict.com](mailto:ossama.lafhel@kanpredict.com)