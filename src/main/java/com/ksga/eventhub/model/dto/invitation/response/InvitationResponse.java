package com.ksga.eventhub.model.dto.invitation.response;

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
public class InvitationResponse {
    private UUID invitationId;
    private String token;
    private String roles;
    private Instant createdAt;
    private Instant expiredAt;
    private UUID generationId;
    private UUID appUserId;
    private UUID workspaceId;
    private String inviteLink;
}