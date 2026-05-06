package com.ksga.eventhub.service;

import com.ksga.eventhub.model.dto.ApiResponse;
import com.ksga.eventhub.model.dto.invitation.request.InvitationRequest;
import com.ksga.eventhub.model.dto.invitation.response.InvitationResponse;

import java.util.List;
import java.util.UUID;

public interface InvitationService {
    InvitationResponse create(InvitationRequest request);
    InvitationResponse getById(UUID invitationId);
    List<InvitationResponse> getByWorkspaceId(UUID workspaceId);
    List<InvitationResponse> getMyInvitations();
    ApiResponse<Void> joinByToken(String token);
    void delete(UUID invitationId);
}