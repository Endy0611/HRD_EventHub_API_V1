package com.ksga.eventhub.model.dto.invitation.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationRequest {

    @NotNull(message = "Workspace is required")
    private UUID workspaceId;

    @NotNull(message = "Generation is required")
    private UUID generationId;

    private Integer ttlDays; // optional, default 7
}
