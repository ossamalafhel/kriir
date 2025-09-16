package com.kriir.platform.dto;

import java.time.LocalDateTime;

public record AssetDto(
    String id,
    String name,
    String type,
    String criticality,
    String status,
    double x,
    double y,
    LocalDateTime lastSeen
) {}