package com.kriir.platform.dto;

import java.time.LocalDateTime;

public record SecurityIncidentDto(
    String id,
    String title,
    String type,
    String severity,
    String status,
    double x,
    double y,
    String description,
    String affectedAssetId,
    LocalDateTime detectedAt,
    LocalDateTime resolvedAt,
    String assignedTo
) {}