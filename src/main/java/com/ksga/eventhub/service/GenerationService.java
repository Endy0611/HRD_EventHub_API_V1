package com.ksga.eventhub.service;

import com.ksga.eventhub.model.dto.generation.request.GenerationRequest;
import com.ksga.eventhub.model.dto.generation.response.GenerationResponse;

import java.util.List;
import java.util.UUID;

public interface GenerationService {
    GenerationResponse create(GenerationRequest request);
    GenerationResponse getById(UUID generationId);
    GenerationResponse getCurrent();
    List<GenerationResponse> getAll();
    GenerationResponse update(UUID generationId, GenerationRequest request);
    void delete(UUID generationId);
    void rollToNextGeneration();
}