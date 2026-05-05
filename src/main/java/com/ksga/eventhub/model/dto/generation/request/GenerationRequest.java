package com.ksga.eventhub.model.dto.generation.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Year is required")
    private String year;

    private boolean isCurrent;
}