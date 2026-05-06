package com.ksga.eventhub.controller;

import com.ksga.eventhub.model.dto.ApiResponse;
import com.ksga.eventhub.model.dto.invitation.request.InvitationRequest;
import com.ksga.eventhub.model.dto.invitation.response.InvitationResponse;
import com.ksga.eventhub.service.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class InvitationController {

    private final InvitationService invitationService;

    @Operation(summary = "Create invitation link")
    @PostMapping
    public ResponseEntity<ApiResponse<InvitationResponse>> create(
            @RequestBody @Valid InvitationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<InvitationResponse>builder()
                        .success(true)
                        .message("Invitation created successfully.")
                        .status(HttpStatus.CREATED)
                        .payload(invitationService.create(request))
                        .timestamp(Instant.now())
                        .build()
        );
    }

    @Operation(summary = "Get invitation by ID")
    @GetMapping("/{invitationId}")
    public ResponseEntity<ApiResponse<InvitationResponse>> getById(
            @PathVariable UUID invitationId) {
        return ResponseEntity.ok(
                ApiResponse.<InvitationResponse>builder()
                        .success(true)
                        .message("Invitation fetched successfully.")
                        .status(HttpStatus.OK)
                        .payload(invitationService.getById(invitationId))
                        .timestamp(Instant.now())
                        .build()
        );
    }

    @Operation(summary = "Get invitations by workspace")
    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> getByWorkspace(
            @PathVariable UUID workspaceId) {
        return ResponseEntity.ok(
                ApiResponse.<List<InvitationResponse>>builder()
                        .success(true)
                        .message("Invitations fetched successfully.")
                        .status(HttpStatus.OK)
                        .payload(invitationService.getByWorkspaceId(workspaceId))
                        .timestamp(Instant.now())
                        .build()
        );
    }

    @Operation(summary = "Get my invitations")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> getMyInvitations() {
        return ResponseEntity.ok(
                ApiResponse.<List<InvitationResponse>>builder()
                        .success(true)
                        .message("My invitations fetched successfully.")
                        .status(HttpStatus.OK)
                        .payload(invitationService.getMyInvitations())
                        .timestamp(Instant.now())
                        .build()
        );
    }

    @Operation(summary = "Join workspace via invite token")
    @PostMapping("/join/{token}")
    public ResponseEntity<ApiResponse<Void>> join(@PathVariable String token) {
        return ResponseEntity.ok(invitationService.joinByToken(token));
    }

    @Operation(summary = "Delete invitation")
    @DeleteMapping("/{invitationId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID invitationId) {
        invitationService.delete(invitationId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Invitation deleted successfully.")
                        .status(HttpStatus.OK)
                        .payload(null)
                        .timestamp(Instant.now())
                        .build()
        );
    }
}