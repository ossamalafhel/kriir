# KRIIR - Complete Technical Specification

## Executive Summary

**Product Name:** KRIIR - Open Source Ransomware Detection Platform  
**Vision:** Advanced behavioral detection and response for ransomware threats  
**Mission:** Democratize enterprise-grade ransomware detection through open source technology  
**Core Value:** Real-time ransomware detection with automated response capabilities

---

## 1. Product Definition

### 1.1 Problem Statement

Ransomware attacks cost organizations $20+ billion annually, with average recovery times of 287 days. Current solutions are either too expensive for SMBs ($50K-$500K/year) or lack the behavioral sophistication to detect modern ransomware variants before significant damage occurs.

### 1.2 Solution Overview

KRIIR provides real-time behavioral analysis specifically tuned for ransomware attack patterns, combining rule-based detection with machine learning to achieve high accuracy while minimizing false positives. The platform focuses on detecting ransomware during execution rather than attempting prediction.

### 1.3 Target Market

**Primary:** Small to medium businesses (100-5000 employees)
**Secondary:** Security researchers and open source contributors  
**Tertiary:** Enterprises seeking transparent, auditable security solutions

---

## 2. Technical Architecture

### 2.1 System Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   KRIIR Agent   │    │ Quarkus Server  │    │ Python ML API   │
│   (Go Binary)   │◄──►│  (Event Core)   │◄──►│ (Detection AI)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
    ┌────▼────┐            ┌─────▼─────┐         ┌──────▼──────┐
    │ OS APIs │            │PostgreSQL │         │  ML Models  │
    │Monitor  │            │   Redis   │         │ scikit-learn│
    └─────────┘            └───────────┘         └─────────────┘
```

### 2.2 Component Responsibilities

**Agent (Go):**
- File system monitoring via native APIs
- Process creation/termination tracking
- Network connection monitoring
- Event batching and secure transmission

**Quarkus Core (Java):**
- High-performance event ingestion
- Rule-based behavioral analysis
- Database persistence
- Alert management and routing

**ML Service (Python):**
- Advanced behavioral classification
- Model training and updates
- Feature engineering
- Anomaly detection

---

## 3. Agent Implementation

### 3.1 Agent Architecture

```go
package main

import (
    "context"
    "time"
    "crypto/tls"
    "encoding/json"
)

type KriirAgent struct {
    config          *AgentConfig
    monitors        []Monitor
    eventProcessor  *EventProcessor
    serverComm      *ServerCommunication
    ctx             context.Context
    cancel          context.CancelFunc
}

type SecurityEvent struct {
    ID              string                 `json:"id"`
    AgentID         string                 `json:"agent_id"`
    Timestamp       time.Time              `json:"timestamp"`
    EventType       string                 `json:"event_type"`
    ProcessID       int                    `json:"process_id,omitempty"`
    ProcessName     string                 `json:"process_name,omitempty"`
    ProcessPath     string                 `json:"process_path,omitempty"`
    ParentPID       int                    `json:"parent_pid,omitempty"`
    FilePath        string                 `json:"file_path,omitempty"`
    FileAction      string                 `json:"file_action,omitempty"`
    NetworkDest     string                 `json:"network_dest,omitempty"`
    NetworkPort     int                    `json:"network_port,omitempty"`
    UserContext     string                 `json:"user_context,omitempty"`
    CommandLine     string                 `json:"command_line,omitempty"`
    Metadata        map[string]interface{} `json:"metadata,omitempty"`
}

func (agent *KriirAgent) Start() error {
    agent.ctx, agent.cancel = context.WithCancel(context.Background())
    
    // Initialize monitoring components
    for _, monitor := range agent.monitors {
        if err := monitor.Start(agent.ctx); err != nil {
            return fmt.Errorf("failed to start monitor: %w", err)
        }
    }
    
    // Start event processing
    go agent.eventProcessor.Run(agent.ctx)
    
    // Start server communication
    go agent.serverComm.Run(agent.ctx)
    
    return nil
}

type FileSystemMonitor struct {
    eventChan chan<- SecurityEvent
    agentID   string
}

func (fsm *FileSystemMonitor) Start(ctx context.Context) error {
    // Platform-specific implementation
    return fsm.startPlatformMonitoring(ctx)
}

// macOS implementation using FSEvents
func (fsm *FileSystemMonitor) startMacOSMonitoring(ctx context.Context) error {
    // Implementation using CGO bindings to FSEvents API
    return nil
}

// Windows implementation using ETW
func (fsm *FileSystemMonitor) startWindowsMonitoring(ctx context.Context) error {
    // Implementation using ETW (Event Tracing for Windows)
    return nil
}

// Linux implementation using inotify
func (fsm *FileSystemMonitor) startLinuxMonitoring(ctx context.Context) error {
    // Implementation using inotify syscalls
    return nil
}
```

### 3.2 Event Processing

```go
type EventProcessor struct {
    eventQueue     chan SecurityEvent
    batchSize      int
    batchTimeout   time.Duration
    serverComm     *ServerCommunication
}

func (ep *EventProcessor) Run(ctx context.Context) {
    batch := make([]SecurityEvent, 0, ep.batchSize)
    ticker := time.NewTicker(ep.batchTimeout)
    defer ticker.Stop()
    
    for {
        select {
        case event := <-ep.eventQueue:
            batch = append(batch, event)
            if len(batch) >= ep.batchSize {
                ep.sendBatch(batch)
                batch = batch[:0]
            }
            
        case <-ticker.C:
            if len(batch) > 0 {
                ep.sendBatch(batch)
                batch = batch[:0]
            }
            
        case <-ctx.Done():
            if len(batch) > 0 {
                ep.sendBatch(batch)
            }
            return
        }
    }
}

func (ep *EventProcessor) sendBatch(events []SecurityEvent) {
    if err := ep.serverComm.SendEvents(events); err != nil {
        // Implement retry logic with exponential backoff
        log.Printf("Failed to send events: %v", err)
    }
}
```

---

## 4. Quarkus Core Service

### 4.1 Event Ingestion API

```java
@Path("/api/v1")
@ApplicationScoped
public class EventIngestionResource {

    @Inject
    EventProcessor eventProcessor;
    
    @Inject
    SecurityEventRepository eventRepository;
    
    @Inject
    MetricsCollector metricsCollector;

    @POST
    @Path("/events")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Uni<Response> ingestEvents(List<SecurityEvent> events) {
        metricsCollector.recordEventsReceived(events.size());
        
        return eventProcessor.processEvents(events)
            .chain(results -> eventRepository.persistEvents(events)
                .map(ignored -> results))
            .map(results -> Response.ok(results).build())
            .onFailure().recoverWithItem(throwable -> {
                metricsCollector.recordProcessingError();
                return Response.status(500)
                    .entity(Map.of("error", throwable.getMessage()))
                    .build();
            });
    }
    
    @GET
    @Path("/agents/{agentId}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getAgentStatus(@PathParam("agentId") String agentId) {
        return eventRepository.getAgentLastSeen(agentId)
            .map(lastSeen -> {
                boolean isActive = lastSeen != null && 
                    Duration.between(lastSeen, Instant.now()).toMinutes() < 5;
                
                return Response.ok(Map.of(
                    "agentId", agentId,
                    "status", isActive ? "active" : "inactive",
                    "lastSeen", lastSeen
                )).build();
            });
    }
}

@ApplicationScoped
public class EventProcessor {
    
    @Inject
    RuleEngine ruleEngine;
    
    @Inject
    MLServiceClient mlClient;
    
    @Inject
    AlertManager alertManager;
    
    public Uni<List<DetectionResult>> processEvents(List<SecurityEvent> events) {
        return Uni.combine().all()
            .unis(
                ruleEngine.evaluateEvents(events),
                mlClient.predictThreatScore(events)
            )
            .combinedWith((ruleResults, mlScore) -> 
                combineResults(events, ruleResults, mlScore))
            .chain(results -> alertManager.processResults(results)
                .map(ignored -> results));
    }
    
    private List<DetectionResult> combineResults(
            List<SecurityEvent> events,
            RuleEvaluationResult ruleResults, 
            MLPredictionResult mlScore) {
        
        float combinedScore = (ruleResults.getScore() * 0.6f) + 
                             (mlScore.getThreatScore() * 0.4f);
        
        DetectionResult result = DetectionResult.builder()
            .eventBatchId(UUID.randomUUID())
            .timestamp(Instant.now())
            .threatScore(combinedScore)
            .confidence(calculateConfidence(ruleResults, mlScore))
            .ruleMatches(ruleResults.getMatchedRules())
            .mlFeatures(mlScore.getFeatures())
            .indicators(extractIndicators(events, ruleResults))
            .recommendedActions(generateActions(combinedScore))
            .build();
            
        return List.of(result);
    }
}
```

### 4.2 Rule Engine Implementation

```java
@ApplicationScoped
public class RuleEngine {
    
    private final Map<String, BehaviorRule> rules;
    
    @PostConstruct
    void initializeRules() {
        rules = Map.of(
            "mass_file_encryption", new MassFileEncryptionRule(),
            "shadow_copy_deletion", new ShadowCopyDeletionRule(),
            "lateral_movement", new LateralMovementRule(),
            "credential_access", new CredentialAccessRule(),
            "process_injection", new ProcessInjectionRule(),
            "backup_deletion", new BackupDeletionRule(),
            "ransom_note_creation", new RansomNoteCreationRule()
        );
    }
    
    public Uni<RuleEvaluationResult> evaluateEvents(List<SecurityEvent> events) {
        return Uni.createFrom().item(() -> {
            Map<String, Float> ruleScores = new HashMap<>();
            List<String> triggeredRules = new ArrayList<>();
            
            for (Map.Entry<String, BehaviorRule> entry : rules.entrySet()) {
                String ruleName = entry.getKey();
                BehaviorRule rule = entry.getValue();
                
                float score = rule.evaluate(events);
                ruleScores.put(ruleName, score);
                
                if (score > rule.getThreshold()) {
                    triggeredRules.add(ruleName);
                }
            }
            
            float combinedScore = calculateWeightedScore(ruleScores);
            
            return RuleEvaluationResult.builder()
                .score(combinedScore)
                .ruleScores(ruleScores)
                .matchedRules(triggeredRules)
                .evaluationTime(Instant.now())
                .build();
        }).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }
}

public class MassFileEncryptionRule implements BehaviorRule {
    
    private static final float THRESHOLD = 0.7f;
    private static final int MIN_FILES_FOR_ANALYSIS = 20;
    
    @Override
    public float evaluate(List<SecurityEvent> events) {
        List<SecurityEvent> fileEvents = events.stream()
            .filter(e -> "file_operation".equals(e.getEventType()))
            .filter(e -> "modify".equals(e.getFileAction()) || 
                         "create".equals(e.getFileAction()))
            .collect(Collectors.toList());
        
        if (fileEvents.size() < MIN_FILES_FOR_ANALYSIS) {
            return 0.0f;
        }
        
        // Calculate metrics
        long timeSpanSeconds = calculateTimeSpan(fileEvents);
        double modificationsPerSecond = (double) fileEvents.size() / 
                                       Math.max(timeSpanSeconds, 1);
        
        // Check for extension changes
        long extensionChanges = fileEvents.stream()
            .filter(e -> hasExtensionChange(e))
            .count();
        
        double extensionChangeRatio = (double) extensionChanges / fileEvents.size();
        
        // Check for entropy increase (simplified)
        double avgEntropyIncrease = fileEvents.stream()
            .mapToDouble(this::estimateEntropyIncrease)
            .average()
            .orElse(0.0);
        
        // Scoring algorithm
        float score = 0.0f;
        
        // File modification rate scoring
        if (modificationsPerSecond > 10.0) {
            score += 0.4f;
        } else if (modificationsPerSecond > 5.0) {
            score += 0.3f;
        } else if (modificationsPerSecond > 1.0) {
            score += 0.2f;
        }
        
        // Extension change scoring
        if (extensionChangeRatio > 0.8) {
            score += 0.3f;
        } else if (extensionChangeRatio > 0.5) {
            score += 0.2f;
        }
        
        // Entropy increase scoring
        if (avgEntropyIncrease > 0.5) {
            score += 0.3f;
        } else if (avgEntropyIncrease > 0.3) {
            score += 0.2f;
        }
        
        return Math.min(score, 1.0f);
    }
    
    @Override
    public float getThreshold() {
        return THRESHOLD;
    }
    
    private boolean hasExtensionChange(SecurityEvent event) {
        // Check if file extension was changed to common ransomware extensions
        String filePath = event.getFilePath();
        if (filePath == null) return false;
        
        String[] ransomwareExtensions = {
            ".encrypted", ".locked", ".crypto", ".crypt", ".enc",
            ".locky", ".zepto", ".cerber", ".dharma", ".sage"
        };
        
        return Arrays.stream(ransomwareExtensions)
            .anyMatch(ext -> filePath.toLowerCase().endsWith(ext));
    }
    
    private double estimateEntropyIncrease(SecurityEvent event) {
        // Simplified entropy estimation
        // In a real implementation, this would analyze file content
        return hasExtensionChange(event) ? 0.8 : 0.1;
    }
}
```

### 4.3 Database Layer

```java
@Entity
@Table(name = "security_events")
public class SecurityEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;
    
    @Column(name = "agent_id", nullable = false)
    public String agentId;
    
    @Column(name = "event_type", nullable = false)
    public String eventType;
    
    @Column(name = "timestamp", nullable = false)
    public Instant timestamp;
    
    @Column(name = "process_id")
    public Integer processId;
    
    @Column(name = "process_name")
    public String processName;
    
    @Column(name = "process_path")
    public String processPath;
    
    @Column(name = "file_path")
    public String filePath;
    
    @Column(name = "file_action")
    public String fileAction;
    
    @Column(name = "network_dest")
    public String networkDest;
    
    @Column(name = "user_context")
    public String userContext;
    
    @Type(JsonType.class)
    @Column(name = "metadata", columnDefinition = "jsonb")
    public Map<String, Object> metadata;
    
    @Column(name = "created_at")
    public Instant createdAt;
}

@ApplicationScoped
public class SecurityEventRepository implements PanacheRepository<SecurityEvent> {
    
    public Uni<Void> persistEvents(List<SecurityEvent> events) {
        return Panache.withTransaction(() -> {
            events.forEach(event -> {
                event.createdAt = Instant.now();
                persist(event);
            });
            return Uni.createFrom().voidItem();
        });
    }
    
    public Uni<List<SecurityEvent>> findRecentByAgent(String agentId, Duration duration) {
        Instant since = Instant.now().minus(duration);
        return find("agentId = ?1 and timestamp >= ?2 order by timestamp desc", 
                   agentId, since).list();
    }
    
    public Uni<Instant> getAgentLastSeen(String agentId) {
        return find("agentId = ?1 order by timestamp desc", agentId)
            .firstResult()
            .map(event -> event != null ? event.timestamp : null);
    }
    
    public Uni<Long> countEventsByTypeInWindow(String eventType, Duration window) {
        Instant since = Instant.now().minus(window);
        return count("eventType = ?1 and timestamp >= ?2", eventType, since);
    }
}

@Entity
@Table(name = "detection_results")
public class DetectionResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;
    
    @Column(name = "event_batch_id")
    public UUID eventBatchId;
    
    @Column(name = "agent_id")
    public String agentId;
    
    @Column(name = "threat_score")
    public Float threatScore;
    
    @Column(name = "confidence")
    public Float confidence;
    
    @Type(JsonType.class)
    @Column(name = "rule_matches", columnDefinition = "jsonb")
    public List<String> ruleMatches;
    
    @Type(JsonType.class)
    @Column(name = "indicators", columnDefinition = "jsonb")
    public Map<String, Object> indicators;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    public DetectionStatus status = DetectionStatus.PENDING;
    
    @Column(name = "timestamp")
    public Instant timestamp;
    
    public enum DetectionStatus {
        PENDING, CONFIRMED, FALSE_POSITIVE, INVESTIGATING
    }
}
```

---

## 5. Machine Learning Service

### 5.1 FastAPI ML Service

```python
from fastapi import FastAPI, HTTPException, BackgroundTasks
from typing import List, Dict, Optional
import numpy as np
import pandas as pd
import joblib
from pydantic import BaseModel, Field
from datetime import datetime
import logging
from sklearn.ensemble import RandomForestClassifier, IsolationForest
from sklearn.preprocessing import StandardScaler
import asyncio

app = FastAPI(
    title="KRIIR ML Service",
    description="Machine Learning API for ransomware detection",
    version="1.0.0"
)

logger = logging.getLogger(__name__)

class SecurityEvent(BaseModel):
    id: str
    agent_id: str
    timestamp: datetime
    event_type: str
    process_id: Optional[int] = None
    process_name: Optional[str] = None
    process_path: Optional[str] = None
    file_path: Optional[str] = None
    file_action: Optional[str] = None
    network_dest: Optional[str] = None
    user_context: Optional[str] = None
    metadata: Dict = Field(default_factory=dict)

class MLPredictionRequest(BaseModel):
    events: List[SecurityEvent]
    agent_id: str

class MLPredictionResult(BaseModel):
    threat_score: float = Field(..., ge=0.0, le=1.0)
    confidence: float = Field(..., ge=0.0, le=1.0)
    anomaly_score: float = Field(..., ge=0.0, le=1.0)
    features: Dict[str, float]
    model_version: str
    prediction_time: datetime

class ModelManager:
    def __init__(self):
        self.classifier = None
        self.anomaly_detector = None
        self.scaler = None
        self.feature_extractor = None
        self.model_version = "1.0.0"
        
    async def load_models(self):
        """Load pre-trained models"""
        try:
            self.classifier = joblib.load("models/ransomware_classifier_v1.pkl")
            self.anomaly_detector = joblib.load("models/anomaly_detector_v1.pkl")
            self.scaler = joblib.load("models/feature_scaler_v1.pkl")
            self.feature_extractor = FeatureExtractor()
            logger.info("Models loaded successfully")
        except FileNotFoundError:
            logger.warning("Pre-trained models not found, using defaults")
            self._create_default_models()
    
    def _create_default_models(self):
        """Create default models for initial deployment"""
        self.classifier = RandomForestClassifier(
            n_estimators=100,
            max_depth=10,
            random_state=42
        )
        self.anomaly_detector = IsolationForest(
            contamination=0.1,
            random_state=42
        )
        self.scaler = StandardScaler()
        self.feature_extractor = FeatureExtractor()
        
    def predict(self, events: List[SecurityEvent]) -> MLPredictionResult:
        """Make prediction on security events"""
        features = self.feature_extractor.extract_features(events)
        feature_vector = list(features.values())
        
        if self.scaler:
            feature_vector = self.scaler.transform([feature_vector])[0]
        
        # Classification prediction
        if self.classifier:
            threat_prob = self.classifier.predict_proba([feature_vector])[0]
            threat_score = threat_prob[1] if len(threat_prob) > 1 else threat_prob[0]
        else:
            threat_score = 0.5  # Default neutral score
        
        # Anomaly detection
        if self.anomaly_detector:
            anomaly_score = abs(self.anomaly_detector.decision_function([feature_vector])[0])
            anomaly_score = min(max(anomaly_score, 0.0), 1.0)  # Normalize to 0-1
        else:
            anomaly_score = 0.0
        
        # Calculate confidence
        confidence = self._calculate_confidence(threat_score, features)
        
        return MLPredictionResult(
            threat_score=float(threat_score),
            confidence=float(confidence),
            anomaly_score=float(anomaly_score),
            features=features,
            model_version=self.model_version,
            prediction_time=datetime.now()
        )
    
    def _calculate_confidence(self, threat_score: float, features: Dict) -> float:
        """Calculate prediction confidence based on feature quality and model certainty"""
        # Higher confidence for extreme scores (closer to 0 or 1)
        score_confidence = 2 * abs(threat_score - 0.5)
        
        # Feature quality assessment
        feature_count = len([v for v in features.values() if v > 0])
        feature_confidence = min(feature_count / 10.0, 1.0)  # Normalize by expected feature count
        
        return (score_confidence + feature_confidence) / 2

class FeatureExtractor:
    def extract_features(self, events: List[SecurityEvent]) -> Dict[str, float]:
        """Extract behavioral features from security events"""
        if not events:
            return {}
        
        df = pd.DataFrame([self._event_to_dict(event) for event in events])
        features = {}
        
        # Basic statistics
        features['total_events'] = len(events)
        features['unique_processes'] = df['process_name'].nunique() if 'process_name' in df else 0
        features['time_span_seconds'] = self._calculate_time_span(events)
        
        # File operation features
        file_events = df[df['event_type'] == 'file_operation'] if 'event_type' in df else pd.DataFrame()
        if not file_events.empty:
            features['file_operations_count'] = len(file_events)
            features['file_modifications_per_second'] = len(file_events) / max(features['time_span_seconds'], 1)
            features['unique_file_extensions'] = self._count_unique_extensions(file_events)
            features['suspicious_extensions_ratio'] = self._calculate_suspicious_extension_ratio(file_events)
            features['rapid_file_changes'] = self._detect_rapid_file_changes(file_events)
        else:
            features.update({
                'file_operations_count': 0,
                'file_modifications_per_second': 0,
                'unique_file_extensions': 0,
                'suspicious_extensions_ratio': 0,
                'rapid_file_changes': 0
            })
        
        # Process features
        process_events = df[df['event_type'] == 'process_creation'] if 'event_type' in df else pd.DataFrame()
        if not process_events.empty:
            features['process_creations'] = len(process_events)
            features['admin_processes_ratio'] = self._calculate_admin_ratio(process_events)
            features['suspicious_process_names'] = self._count_suspicious_processes(process_events)
        else:
            features.update({
                'process_creations': 0,
                'admin_processes_ratio': 0,
                'suspicious_process_names': 0
            })
        
        # Network features
        network_events = df[df['event_type'] == 'network_connection'] if 'event_type' in df else pd.DataFrame()
        if not network_events.empty:
            features['network_connections'] = len(network_events)
            features['external_connections_ratio'] = self._calculate_external_ratio(network_events)
            features['unique_destinations'] = network_events['network_dest'].nunique() if 'network_dest' in network_events else 0
        else:
            features.update({
                'network_connections': 0,
                'external_connections_ratio': 0,
                'unique_destinations': 0
            })
        
        # Behavioral patterns
        features['events_per_second'] = len(events) / max(features['time_span_seconds'], 1)
        features['process_diversity'] = features['unique_processes'] / max(features['total_events'], 1)
        features['activity_burst_score'] = self._calculate_activity_burst(events)
        
        return features
    
    def _event_to_dict(self, event: SecurityEvent) -> dict:
        """Convert SecurityEvent to dictionary"""
        return {
            'event_type': event.event_type,
            'process_name': event.process_name,
            'file_path': event.file_path,
            'file_action': event.file_action,
            'network_dest': event.network_dest,
            'user_context': event.user_context,
            'timestamp': event.timestamp
        }
    
    def _calculate_time_span(self, events: List[SecurityEvent]) -> float:
        """Calculate time span of events in seconds"""
        if len(events) < 2:
            return 1.0
        
        timestamps = [event.timestamp for event in events]
        time_span = (max(timestamps) - min(timestamps)).total_seconds()
        return max(time_span, 1.0)
    
    def _count_unique_extensions(self, file_events: pd.DataFrame) -> int:
        """Count unique file extensions"""
        if 'file_path' not in file_events.columns:
            return 0
        
        extensions = file_events['file_path'].dropna().str.split('.').str[-1].unique()
        return len(extensions)
    
    def _calculate_suspicious_extension_ratio(self, file_events: pd.DataFrame) -> float:
        """Calculate ratio of files with suspicious extensions"""
        if 'file_path' not in file_events.columns or file_events.empty:
            return 0.0
        
        suspicious_extensions = {
            'encrypted', 'locked', 'crypto', 'crypt', 'enc', 'locky',
            'zepto', 'cerber', 'dharma', 'sage', 'wallet'
        }
        
        total_files = len(file_events['file_path'].dropna())
        if total_files == 0:
            return 0.0
        
        suspicious_count = sum(
            1 for path in file_events['file_path'].dropna()
            if any(ext in path.lower() for ext in suspicious_extensions)
        )
        
        return suspicious_count / total_files
    
    def _detect_rapid_file_changes(self, file_events: pd.DataFrame) -> float:
        """Detect rapid changes to the same files"""
        if 'file_path' not in file_events.columns or file_events.empty:
            return 0.0
        
        file_counts = file_events['file_path'].value_counts()
        max_changes = file_counts.max() if not file_counts.empty else 0
        
        return min(max_changes / 10.0, 1.0)  # Normalize to 0-1
    
    def _calculate_admin_ratio(self, process_events: pd.DataFrame) -> float:
        """Calculate ratio of processes running with admin privileges"""
        if 'user_context' not in process_events.columns or process_events.empty:
            return 0.0
        
        total_processes = len(process_events)
        admin_processes = sum(
            1 for context in process_events['user_context'].fillna('')
            if 'admin' in context.lower() or 'root' in context.lower()
        )
        
        return admin_processes / total_processes if total_processes > 0 else 0.0
    
    def _count_suspicious_processes(self, process_events: pd.DataFrame) -> float:
        """Count suspicious process names"""
        if 'process_name' not in process_events.columns or process_events.empty:
            return 0.0
        
        suspicious_processes = {
            'powershell.exe', 'cmd.exe', 'wmic.exe', 'vssadmin.exe',
            'bcdedit.exe', 'wbadmin.exe', 'schtasks.exe'
        }
        
        total_processes = len(process_events)
        suspicious_count = sum(
            1 for name in process_events['process_name'].fillna('')
            if name.lower() in suspicious_processes
        )
        
        return suspicious_count / total_processes if total_processes > 0 else 0.0
    
    def _calculate_external_ratio(self, network_events: pd.DataFrame) -> float:
        """Calculate ratio of external network connections"""
        if 'network_dest' not in network_events.columns or network_events.empty:
            return 0.0
        
        total_connections = len(network_events)
        internal_patterns = ['192.168.', '10.', '172.16.', '127.', 'localhost']
        
        external_count = sum(
            1 for dest in network_events['network_dest'].fillna('')
            if not any(pattern in dest for pattern in internal_patterns)
        )
        
        return external_count / total_connections if total_connections > 0 else 0.0
    
    def _calculate_activity_burst(self, events: List[SecurityEvent]) -> float:
        """Calculate activity burst score"""
        if len(events) < 10:
            return 0.0
        
        # Simple burst detection based on time distribution
        timestamps = sorted([event.timestamp for event in events])
        intervals = [
            (timestamps[i+1] - timestamps[i]).total_seconds()
            for i in range(len(timestamps)-1)
        ]
        
        if not intervals:
            return 0.0
        
        avg_interval = sum(intervals) / len(intervals)
        short_intervals = sum(1 for interval in intervals if interval < avg_interval / 2)
        
        return min(short_intervals / len(intervals), 1.0)

# Initialize model manager
model_manager = ModelManager()

@app.on_event("startup")
async def startup_event():
    await model_manager.load_models()

@app.post("/predict", response_model=MLPredictionResult)
async def predict_threat(request: MLPredictionRequest):
    """Predict ransomware threat from security events"""
    try:
        result = model_manager.predict(request.events)
        return result
    except Exception as e:
        logger.error(f"Prediction error: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Prediction failed: {str(e)}")

@app.get("/health")
async def health_check():
    """Health check endpoint"""
    return {
        "status": "healthy",
        "model_loaded": model_manager.classifier is not None,
        "version": model_manager.model_version
    }

@app.get("/models/info")
async def model_info():
    """Get information about loaded models"""
    return {
        "classifier_type": type(model_manager.classifier).__name__ if model_manager.classifier else None,
        "anomaly_detector_type": type(model_manager.anomaly_detector).__name__ if model_manager.anomaly_detector else None,
        "model_version": model_manager.model_version,
        "features_count": len(model_manager.feature_extractor.extract_features([]))
    }
```

---

## 6. Performance Targets and Monitoring

### 6.1 Performance Specifications

```java
@ApplicationScoped
public class PerformanceTargets {
    
    // Detection accuracy targets
    public static final double TARGET_DETECTION_RATE = 0.85;          // 85%
    public static final double TARGET_FALSE_POSITIVE_RATE = 0.05;     // 5%
    public static final double TARGET_FALSE_NEGATIVE_RATE = 0.15;     // 15%
    
    // Response time targets
    public static final int TARGET_EVENT_PROCESSING_MS = 30000;       // 30 seconds
    public static final int TARGET_API_RESPONSE_MS = 200;             // 200ms
    public static final int TARGET_ALERT_GENERATION_MS = 60000;       // 1 minute
    
    // Throughput targets
    public static final int TARGET_EVENTS_PER_SECOND = 1000;          // 1K events/sec
    public static final int TARGET_CONCURRENT_AGENTS = 10000;         // 10K agents
    public static final int TARGET_BATCH_SIZE = 100;                  // 100 events/batch
    
    // Resource usage targets (agent)
    public static final int TARGET_AGENT_MEMORY_MB = 100;             // <100MB
    public static final double TARGET_AGENT_CPU_PERCENT = 0.05;       // <5% CPU
    public static final int TARGET_AGENT_NETWORK_KB_SEC = 50;         // <50KB/sec
    
    // Availability targets
    public static final double TARGET_SYSTEM_UPTIME = 0.999;          // 99.9%
    public static final int TARGET_MAX_DOWNTIME_MINUTES = 43;         // 43 min/month
}
```

### 6.2 Metrics Collection

```java
@ApplicationScoped
public class MetricsCollector {
    
    @Inject
    MeterRegistry meterRegistry;
    
    private final Counter eventsReceived;
    private final Counter eventsProcessed;
    private final Counter detectionsGenerated;
    private final Counter falsePositivesReported;
    private final Timer eventProcessingTime;
    private final Timer mlPredictionTime;
    private final Gauge activeAgents;
    private final Gauge systemLoad;
    
    @PostConstruct
    void initializeMetrics() {
        eventsReceived = Counter.builder("kriir.events.received")
            .description("Total events received from agents")
            .register(meterRegistry);
            
        eventsProcessed = Counter.builder("kriir.events.processed")
            .description("Total events successfully processed")
            .register(meterRegistry);
            
        detectionsGenerated = Counter.builder("kriir.detections.generated")
            .description("Total detections generated")
            .tag("severity", "high")
            .register(meterRegistry);
            
        falsePositivesReported = Counter.builder("kriir.false_positives")
            .description("False positives reported by users")
            .register(meterRegistry);
            
        eventProcessingTime = Timer.builder("kriir.processing.time")
            .description("Time to process event batches")
            .register(meterRegistry);
            
        mlPredictionTime = Timer.builder("kriir.ml.prediction.time")
            .description("ML prediction response time")
            .register(meterRegistry);
    }
    
    public void recordEventsReceived(int count) {
        eventsReceived.increment(count);
    }
    
    public void recordEventProcessed() {
        eventsProcessed.increment();
    }
    
    public void recordDetection(String severity) {
        detectionsGenerated.increment(Tags.of("severity", severity));
    }
    
    public void recordProcessingTime(Duration duration) {
        eventProcessingTime.record(duration);
    }
    
    public Timer.Sample startMLTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void recordMLPredictionTime(Timer.Sample sample) {
        sample.stop(mlPredictionTime);
    }
    
    // Calculate derived metrics
    public double calculateDetectionRate() {
        double totalDetections = detectionsGenerated.count();
        double truePositives = totalDetections - falsePositivesReported.count();
        return truePositives / Math.max(totalDetections, 1);
    }
    
    public double calculateFalsePositiveRate() {
        double totalDetections = detectionsGenerated.count();
        double falsePositives = falsePositivesReported.count();
        return falsePositives / Math.max(totalDetections, 1);
    }
}
```

---

## 7. Deployment and Infrastructure

### 7.1 Docker Configuration

```yaml
# docker-compose.yml
version: '3.8'

services:
  kriir-core:
    build: 
      context: ./kriir-core
      dockerfile: src/main/docker/Dockerfile.jvm
    ports:
      - "8080:8080"
    environment:
      - QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://kriir-db:5432/kriir
      - QUARKUS_DATASOURCE_USERNAME=kriir
      - QUARKUS_DATASOURCE_PASSWORD=kriir123
      - QUARKUS_REDIS_HOSTS=redis://kriir-redis:6379
      - KRIIR_ML_SERVICE_URL=http://kriir-ml:8000
      - QUARKUS_LOG_LEVEL=INFO
    depends_on:
      - kriir-db
      - kriir-redis
      - kriir-ml
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/q/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    restart: unless-stopped
    
  kriir-ml:
    build: ./kriir-ml
    ports:
      - "8000:8000"
    environment:
      - MODEL_PATH=/app/models
      - LOG_LEVEL=INFO
      - WORKERS=4
    volumes:
      - ./models:/app/models:ro
      - ./ml-data:/app/data
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8000/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 30s
    restart: unless-stopped
    
  kriir-dashboard:
    build: ./kriir-dashboard
    ports:
      - "3000:3000"
    environment:
      - REACT_APP_API_URL=http://localhost:8080
      - REACT_APP_WS_URL=ws://localhost:8080
    depends_on:
      - kriir-core
    restart: unless-stopped
    
  kriir-db:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=kriir
      - POSTGRES_USER=kriir
      - POSTGRES_PASSWORD=kriir123
      - POSTGRES_INITDB_ARGS=--auth-host=scram-sha-256
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./database/init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "5432:5432"
    restart: unless-stopped
    
  kriir-redis:
    image: redis:7-alpine
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data
    ports:
      - "6379:6379"
    restart: unless-stopped
    
  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
    restart: unless-stopped
    
  grafana:
    image: grafana/grafana:latest
    ports:
      - "3001:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin123
    volumes:
      - grafana_data:/var/lib/grafana
      - ./monitoring/grafana/dashboards:/var/lib/grafana/dashboards
      - ./monitoring/grafana/provisioning:/etc/grafana/provisioning
    restart: unless-stopped

volumes:
  postgres_data:
  redis_data:
  prometheus_data:
  grafana_data:

networks:
  default:
    name: kriir-network
```

### 7.2 Production Kubernetes Deployment

```yaml
# kubernetes/kriir-core-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: kriir-core
  namespace: kriir
spec:
  replicas: 3
  selector:
    matchLabels:
      app: kriir-core
  template:
    metadata:
      labels:
        app: kriir-core
    spec:
      containers:
      - name: kriir-core
        image: kriir/core:1.0.0
        ports:
        - containerPort: 8080
          name: http
        env:
        - name: QUARKUS_DATASOURCE_JDBC_URL
          valueFrom:
            secretKeyRef:
              name: kriir-secrets
              key: database-url
        - name: QUARKUS_DATASOURCE_USERNAME
          valueFrom:
            secretKeyRef:
              name: kriir-secrets
              key: database-username
        - name: QUARKUS_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: kriir-secrets
              key: database-password
        - name: QUARKUS_REDIS_HOSTS
          value: "redis://kriir-redis:6379"
        - name: KRIIR_ML_SERVICE_URL
          value: "http://kriir-ml:8000"
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "1500m"
        livenessProbe:
          httpGet:
            path: /q/health/live
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
          timeoutSeconds: 10
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /q/health/ready
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
---
apiVersion: v1
kind: Service
metadata:
  name: kriir-core
  namespace: kriir
spec:
  selector:
    app: kriir-core
  ports:
  - port: 8080
    targetPort: 8080
    name: http
  type: ClusterIP
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: kriir-ml
  namespace: kriir
spec:
  replicas: 2
  selector:
    matchLabels:
      app: kriir-ml
  template:
    metadata:
      labels:
        app: kriir-ml
    spec:
      containers:
      - name: kriir-ml
        image: kriir/ml:1.0.0
        ports:
        - containerPort: 8000
          name: http
        env:
        - name: MODEL_PATH
          value: "/app/models"
        - name: WORKERS
          value: "4"
        volumeMounts:
        - name: model-storage
          mountPath: /app/models
          readOnly: true
        resources:
          requests:
            memory: "2Gi"
            cpu: "1000m"
          limits:
            memory: "4Gi"
            cpu: "2000m"
        livenessProbe:
          httpGet:
            path: /health
            port: 8000
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /health
            port: 8000
          initialDelaySeconds: 30
          periodSeconds: 10
      volumes:
      - name: model-storage
        persistentVolumeClaim:
          claimName: kriir-model-storage
---
apiVersion: v1
kind: Service
metadata:
  name: kriir-ml
  namespace: kriir
spec:
  selector:
    app: kriir-ml
  ports:
  - port: 8000
    targetPort: 8000
    name: http
  type: ClusterIP
```

---

## 8. Security and Authentication

### 8.1 Agent Authentication

```java
@ApplicationScoped
public class AgentAuthenticationService {
    
    @Inject
    @ConfigProperty(name = "kriir.agent.jwt.secret")
    String jwtSecret;
    
    @Inject
    AgentRepository agentRepository;
    
    public String generateAgentToken(String agentId) {
        return Jwt.issuer("kriir-server")
            .audience("kriir-agents")
            .subject(agentId)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(Duration.ofDays(30)))
            .sign();
    }
    
    public boolean validateAgentToken(String token) {
        try {
            JsonWebToken jwt = Jwt.parse(token);
            String agentId = jwt.getSubject();
            
            return agentRepository.findByIdOptional(agentId)
                .map(agent -> agent.status == AgentStatus.ACTIVE)
                .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }
}

@ApplicationScoped
public class SecurityEventValidator {
    
    public boolean validateEventBatch(List<SecurityEvent> events, String agentId) {
        if (events == null || events.isEmpty()) {
            return false;
        }
        
        // Validate all events belong to the authenticated agent
        boolean agentIdMatch = events.stream()
            .allMatch(event -> agentId.equals(event.getAgentId()));
        
        // Validate timestamp recency (events should be within last hour)
        Instant oneHourAgo = Instant.now().minus(Duration.ofHours(1));
        boolean recentTimestamps = events.stream()
            .allMatch(event -> event.getTimestamp().isAfter(oneHourAgo));
        
        // Validate event structure
        boolean validStructure = events.stream()
            .allMatch(this::validateEventStructure);
        
        return agentIdMatch && recentTimestamps && validStructure;
    }
    
    private boolean validateEventStructure(SecurityEvent event) {
        return event.getId() != null &&
               event.getAgentId() != null &&
               event.getEventType() != null &&
               event.getTimestamp() != null;
    }
}
```

### 8.2 Data Protection

```yaml
# Data protection configuration
kriir:
  security:
    encryption:
      enabled: true
      algorithm: "AES-256-GCM"
      key-rotation-days: 30
    
    data-retention:
      security-events-days: 90
      detection-results-days: 365
      audit-logs-days: 2555  # 7 years
    
    privacy:
      anonymize-user-data: true
      hash-file-paths: true
      exclude-sensitive-metadata: true
    
    communication:
      tls-version: "1.3"
      certificate-validation: true
      client-certificates: true
```

---

## 9. Monitoring and Observability

### 9.1 Application Monitoring

```yaml
# monitoring/prometheus.yml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "kriir-alerts.yml"

scrape_configs:
  - job_name: 'kriir-core'
    static_configs:
      - targets: ['kriir-core:8080']
    metrics_path: '/q/metrics'
    scrape_interval: 10s
    
  - job_name: 'kriir-ml'
    static_configs:
      - targets: ['kriir-ml:8000']
    metrics_path: '/metrics'
    scrape_interval: 15s
    
  - job_name: 'postgres'
    static_configs:
      - targets: ['postgres-exporter:9187']

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093
```

### 9.2 Custom Alerts

```yaml
# monitoring/kriir-alerts.yml
groups:
- name: kriir-performance
  rules:
  - alert: HighEventProcessingLatency
    expr: histogram_quantile(0.95, kriir_processing_time_seconds) > 30
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: "High event processing latency detected"
      description: "95th percentile processing time is {{ $value }} seconds"
      
  - alert: MLServiceDown
    expr: up{job="kriir-ml"} == 0
    for: 1m
    labels:
      severity: critical
    annotations:
      summary: "ML service is down"
      description: "The ML prediction service has been down for more than 1 minute"
      
  - alert: HighFalsePositiveRate
    expr: (kriir_false_positives_total / kriir_detections_generated_total) > 0.1
    for: 10m
    labels:
      severity: warning
    annotations:
      summary: "High false positive rate detected"
      description: "False positive rate is {{ $value | humanizePercentage }}"
      
  - alert: AgentConnectivityIssues
    expr: increase(kriir_agent_connection_errors_total[5m]) > 10
    for: 2m
    labels:
      severity: warning
    annotations:
      summary: "Multiple agent connectivity issues"
      description: "{{ $value }} agent connection errors in the last 5 minutes"
```

---

## 10. Testing Strategy

### 10.1 Unit Testing

```java
@QuarkusTest
class RuleEngineTest {
    
    @Inject
    RuleEngine ruleEngine;
    
    @Test
    void testMassFileEncryptionDetection() {
        // Given
        List<SecurityEvent> events = createMassFileEncryptionEvents();
        
        // When
        RuleEvaluationResult result = ruleEngine.evaluateEvents(events).await().indefinitely();
        
        // Then
        assertThat(result.getScore()).isGreaterThan(0.7f);
        assertThat(result.getMatchedRules()).contains("mass_file_encryption");
    }
    
    @Test
    void testNormalActivityNoDetection() {
        // Given
        List<SecurityEvent> events = createNormalActivityEvents();
        
        // When
        RuleEvaluationResult result = ruleEngine.evaluateEvents(events).await().indefinitely();
        
        // Then
        assertThat(result.getScore()).isLessThan(0.3f);
        assertThat(result.getMatchedRules()).isEmpty();
    }
    
    private List<SecurityEvent> createMassFileEncryptionEvents() {
        List<SecurityEvent> events = new ArrayList<>();
        Instant baseTime = Instant.now();
        
        // Simulate rapid file modifications
        for (int i = 0; i < 100; i++) {
            SecurityEvent event = new SecurityEvent();
            event.setAgentId("test-agent");
            event.setEventType("file_operation");
            event.setFileAction("modify");
            event.setFilePath("/home/user/document" + i + ".encrypted");
            event.setTimestamp(baseTime.plusSeconds(i));
            events.add(event);
        }
        
        return events;
    }
}
```

### 10.2 Integration Testing

```python
import pytest
import asyncio
from httpx import AsyncClient
from fastapi.testclient import TestClient
from kriir_ml.main import app

class TestMLService:
    
    @pytest.fixture
    def client(self):
        return TestClient(app)
    
    def test_health_endpoint(self, client):
        response = client.get("/health")
        assert response.status_code == 200
        assert response.json()["status"] == "healthy"
    
    def test_prediction_endpoint(self, client):
        # Given
        test_events = [
            {
                "id": "test-1",
                "agent_id": "test-agent",
                "timestamp": "2024-01-01T10:00:00Z",
                "event_type": "file_operation",
                "file_action": "modify",
                "file_path": "/test/file.encrypted"
            }
        ]
        
        # When
        response = client.post("/predict", json={
            "events": test_events,
            "agent_id": "test-agent"
        })
        
        # Then
        assert response.status_code == 200
        result = response.json()
        assert "threat_score" in result
        assert 0 <= result["threat_score"] <= 1
        assert "confidence" in result
        assert "features" in result
```

### 10.3 Performance Testing

```java
@QuarkusTest
class PerformanceTest {
    
    @Test
    void testEventIngestionThroughput() {
        // Test that system can handle 1000 events/second
        int eventsPerSecond = 1000;
        int testDurationSeconds = 10;
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (int second = 0; second < testDurationSeconds; second++) {
            for (int event = 0; event < eventsPerSecond; event++) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    // Send event to API
                    sendTestEvent();
                });
                futures.add(future);
            }
            
            // Wait 1 second
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .join();
        
        // Verify no errors and acceptable response times
        // Implementation depends on specific metrics collection
    }
}
```

---

## 11. Development Roadmap

### 11.1 Phase 1: MVP (Months 1-3)

**Core Infrastructure:**
- Basic agent with file/process monitoring (Go)
- Quarkus event ingestion API
- PostgreSQL event storage
- Simple rule-based detection (5 rules)
- Basic Python ML service
- Docker deployment

**Success Criteria:**
- Process 100 events/second
- 80% detection accuracy on test dataset
- <10% false positive rate
- Agent memory usage <150MB

### 11.2 Phase 2: Enhanced Detection (Months 4-6)

**Advanced Features:**
- Enhanced rule engine (15 behavioral rules)
- Improved ML models with feature engineering
- Real-time alerting system
- Web dashboard (React)
- Redis caching layer
- Basic API authentication

**Success Criteria:**
- Process 500 events/second
- 85% detection accuracy
- <5% false positive rate
- Support 1000+ concurrent agents

### 11.3 Phase 3: Production Ready (Months 7-9)

**Enterprise Features:**
- Kubernetes deployment
- Comprehensive monitoring
- Advanced security features
- Multi-tenant support
- API rate limiting
- Community features (signature sharing)

**Success Criteria:**
- Process 1000 events/second
- 85%+ detection accuracy
- <5% false positive rate
- 99.9% system uptime
- Support 10,000+ agents

### 11.4 Phase 4: Advanced Capabilities (Months 10-12)

**Advanced Features:**
- Advanced behavioral analysis
- Custom rule engine for enterprises
- Integration APIs for SIEM/SOAR
- Advanced threat intelligence
- Performance optimization

**Success Criteria:**
- Industry-leading accuracy benchmarks
- Enterprise customer adoption
- Active open source community
- Sustainable business model

---

## 12. Business Model and Pricing

### 12.1 Open Core Strategy

**Open Source (Free Forever):**
- Core detection engine
- Basic behavioral rules
- Agent software
- Self-hosted deployment
- Community support
- API access (rate limited)

**Commercial Features:**
- Advanced ML models
- Enterprise dashboard
- 24/7 support and SLA
- Professional services
- Compliance reporting
- Priority feature requests
- Enhanced security features

### 12.2 Target Pricing

**Community Edition:** Free
- Up to 100 agents
- Basic detection capabilities
- Community support

**Professional:** $5/agent/month
- Up to 10,000 agents
- Advanced ML models
- Email support
- Basic reporting

**Enterprise:** $15/agent/month
- Unlimited agents
- Premium features
- 24/7 support
- Professional services
- Custom SLAs

---

## 13. Success Metrics

### 13.1 Technical KPIs

**Detection Performance:**
- Ransomware detection rate: >85%
- False positive rate: <5%
- Processing latency: <30 seconds
- System availability: >99.9%

**Scalability Metrics:**
- Events processed/second: >1000
- Concurrent agents supported: >10,000
- API response time: <200ms
- Agent resource usage: <5% CPU, <100MB RAM

### 13.2 Business KPIs

**Adoption Metrics:**
- GitHub stars: 1,000 (Year 1) → 10,000 (Year 3)
- Active deployments: 100 (Year 1) → 10,000 (Year 3)
- Community contributors: 10 (Year 1) → 100 (Year 3)

**Revenue Metrics:**
- Enterprise customers: 0 (Year 1) → 100 (Year 3)
- Annual recurring revenue: $0 (Year 1) → $5M (Year 3)
- Monthly growth rate: >20%

### 13.3 Community Metrics

**Open Source Health:**
- Active contributors per month: >10
- Issues resolved per month: >50
- Documentation completeness: >90%
- Test coverage: >80%

---

This comprehensive specification provides a realistic foundation for building KRIIR as an open-source ransomware detection platform. The architecture balances technical ambition with practical implementation constraints, focusing on achievable goals while maintaining the vision of democratizing enterprise-grade cybersecurity through open source technology.
