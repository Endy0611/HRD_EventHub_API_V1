package com.ksga.eventhub.model.entity;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkSpace {
    private UUID workspaceId;
    private String name;
    private String description;
    private boolean isPublic;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdBy;
    private UUID generationId;
}
