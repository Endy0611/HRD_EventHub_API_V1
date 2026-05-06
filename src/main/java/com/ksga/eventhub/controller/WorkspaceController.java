package com.ksga.eventhub.controller;

import com.ksga.eventhub.model.dto.ApiResponse;
import com.ksga.eventhub.model.dto.workspace.request.WorkspaceRequest;
import com.ksga.eventhub.model.dto.workspace.response.WorkspaceResponse;
import com.ksga.eventhub.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
//@Tag(name = "Workspace", description = "Workspace management APIs")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @Operation(summary = "Create a new workspace")
    @PostMapping
    public ResponseEntity<ApiResponse<WorkspaceResponse>> create(
            @RequestBody @Valid WorkspaceRequest request) {
        return ApiResponse.created("Workspace created successfully.",
                workspaceService.create(request));
    }

    @Operation(summary = "Get all workspaces")
    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkspaceResponse>>> getAll() {
        return ApiResponse.success("Workspaces fetched successfully.",
                workspaceService.getAll());
    }

    @Operation(summary = "Get workspace by ID")
    @GetMapping("/{workspaceId}")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> getById(
            @PathVariable UUID workspaceId) {
        return ApiResponse.success("Workspace fetched successfully.",
                workspaceService.getById(workspaceId));
    }

    @Operation(summary = "Get workspaces by generation")
    @GetMapping("/generation/{generationId}")
    public ResponseEntity<ApiResponse<List<WorkspaceResponse>>> getByGeneration(
            @PathVariable UUID generationId) {
        return ApiResponse.success("Workspaces fetched successfully.",
                workspaceService.getByGeneration(generationId));
    }

    @Operation(summary = "Update workspace")
    @PutMapping("/{workspaceId}")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> update(
            @PathVariable UUID workspaceId,
            @RequestBody @Valid WorkspaceRequest request) {
        return ApiResponse.success("Workspace updated successfully.",
                workspaceService.update(workspaceId, request));
    }

    @Operation(summary = "Delete workspace")
    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID workspaceId) {
        workspaceService.delete(workspaceId);
        return ApiResponse.success("Workspace deleted successfully.", null);
    }
}