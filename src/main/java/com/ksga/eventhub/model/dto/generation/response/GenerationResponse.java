package com.ksga.eventhub.model.dto.generation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationResponse {
    private UUID generationId;
    private String name;
    private String year;
    private boolean isCurrent;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID appUserId;
}