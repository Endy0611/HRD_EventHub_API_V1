package com.ksga.eventhub.service.impl;


import com.ksga.eventhub.model.dto.generation.response.GenerationResponse;
import com.ksga.eventhub.model.dto.workspace.request.WorkspaceRequest;
import com.ksga.eventhub.model.dto.workspace.response.WorkspaceResponse;
import com.ksga.eventhub.model.entity.Generation;
import com.ksga.eventhub.model.entity.Workspace;
import com.ksga.eventhub.repository.GenerationRepository;
import com.ksga.eventhub.repository.WorkspaceRepository;
import com.ksga.eventhub.service.WorkspaceService;
import com.ksga.eventhub.utils.HandleCurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final GenerationRepository generationRepository;
    private final HandleCurrentUser handleCurrentUser;

    @Override
    public WorkspaceResponse create(WorkspaceRequest request) {
        Generation generation = generationRepository.findById(request.getGenerationId());
        if (generation == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Generation not found");

        Workspace workspace = Workspace.builder()
                .workspaceId(UUID.randomUUID())
                .name(request.getName())
                .description(request.getDescription())
                .isPublic(request.getIsPublic())
                .generationId(request.getGenerationId())
                .createdBy(handleCurrentUser.getUserIdOfCurrentUser())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        workspaceRepository.save(workspace);
        return workspaceRepository.findById(workspace.getWorkspaceId()); // 👈 fetch with JOIN
    }

    @Override
    public List<WorkspaceResponse> getAll() {
        return workspaceRepository.findAll(); // 👈 JOIN already done in repo
    }

    @Override
    public WorkspaceResponse getById(UUID workspaceId) {
        WorkspaceResponse workspace = workspaceRepository.findById(workspaceId);
        if (workspace == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found");
        return workspace;
    }

    @Override
    public List<WorkspaceResponse> getByGeneration(UUID generationId) {
        return workspaceRepository.findByGenerationId(generationId);
    }

    @Override
    public WorkspaceResponse update(UUID workspaceId, WorkspaceRequest request) {
        WorkspaceResponse existing = workspaceRepository.findById(workspaceId);
        if (existing == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found");

        Generation generation = generationRepository.findById(request.getGenerationId());
        if (generation == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Generation not found");

        Workspace workspace = Workspace.builder()
                .workspaceId(workspaceId)
                .name(request.getName())
                .description(request.getDescription())
                .isPublic(request.getIsPublic())
                .generationId(request.getGenerationId())
                .updatedAt(Instant.now())
                .build();

        workspaceRepository.update(workspace);
        return workspaceRepository.findById(workspaceId);
    }

    @Override
    public void delete(UUID workspaceId) {
        if (workspaceRepository.findById(workspaceId) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found");
        workspaceRepository.deleteById(workspaceId);
    }
}