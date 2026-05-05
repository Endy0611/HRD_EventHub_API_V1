package com.ksga.eventhub.model.dto.workspace.response;

import com.ksga.eventhub.model.dto.generation.response.GenerationResponse;
import com.ksga.eventhub.model.entity.Generation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkspaceResponse {
    private UUID workspaceId;
    private String name;
    private String description;
    private Boolean isPublic;
    private UUID createdBy;
    private Instant createdAt;
    private Instant updatedAt;
    private Generation generation;
}