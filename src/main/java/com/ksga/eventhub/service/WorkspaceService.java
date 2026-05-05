package com.ksga.eventhub.service;

import com.ksga.eventhub.model.dto.workspace.request.WorkspaceRequest;
import com.ksga.eventhub.model.dto.workspace.response.WorkspaceResponse;


import java.util.List;
import java.util.UUID;

public interface WorkspaceService {
    WorkspaceResponse create(WorkspaceRequest request);
    List<WorkspaceResponse> getAll();
    WorkspaceResponse getById(UUID workspaceId);
    List<WorkspaceResponse> getByGeneration(UUID generationId);
    WorkspaceResponse update(UUID workspaceId, WorkspaceRequest request);
    void delete(UUID workspaceId);
}