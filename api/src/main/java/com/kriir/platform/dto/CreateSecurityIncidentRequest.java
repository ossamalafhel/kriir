package com.kriir.platform.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record CreateSecurityIncidentRequest(
    @NotBlank(message = "Title is required")
    String title,
    
    @NotBlank(message = "Type is required")
    String type,
    
    @NotBlank(message = "Severity is required")
    String severity,
    
    @NotNull(message = "X coordinate is required")
    Double x,
    
    @NotNull(message = "Y coordinate is required")
    Double y,
    
    String description,
    String affectedAssetId
) {}