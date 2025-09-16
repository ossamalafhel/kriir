package org.cyberisk.platform.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record CreateAssetRequest(
    @NotBlank(message = "Name is required")
    String name,
    
    @NotBlank(message = "Type is required")
    String type,
    
    @NotBlank(message = "Criticality is required")
    String criticality,
    
    @NotNull(message = "X coordinate is required")
    Double x,
    
    @NotNull(message = "Y coordinate is required")
    Double y
) {}