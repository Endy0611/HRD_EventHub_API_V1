package com.ksga.eventhub.controller;

import com.ksga.eventhub.model.dto.ApiResponse;
import com.ksga.eventhub.model.dto.generation.request.GenerationRequest;
import com.ksga.eventhub.model.dto.generation.response.GenerationResponse;
import com.ksga.eventhub.service.GenerationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/generations")
@RequiredArgsConstructor
public class GenerationController {

    private final GenerationService generationService;

    @Operation(summary = "Create a new generation")
    @PostMapping
    public ResponseEntity<ApiResponse<GenerationResponse>> create(
            @RequestBody @Valid GenerationRequest request) {
        GenerationResponse created = generationService.create(request);
        ApiResponse<GenerationResponse> response = ApiResponse.<GenerationResponse>builder()
                .success(true)
                .message("Generation created successfully.")
                .status(HttpStatus.CREATED)
                .payload(created)
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all generations")
    @GetMapping
    public ResponseEntity<ApiResponse<List<GenerationResponse>>> getAll() {
        return ApiResponse.success("Generations fetched successfully.", generationService.getAll());
    }

    @Operation(summary = "Get generation by ID")
    @GetMapping("/{generationId}")
    public ResponseEntity<ApiResponse<GenerationResponse>> getById(
            @PathVariable UUID generationId) {
        return ApiResponse.success("Generation fetched successfully.", generationService.getById(generationId));
    }

    @Operation(summary = "Get current generation")
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<GenerationResponse>> getCurrent() {
        return ApiResponse.success("Current generation fetched successfully.", generationService.getCurrent());
    }

    @Operation(summary = "Update generation")
    @PutMapping("/{generationId}")
    public ResponseEntity<ApiResponse<GenerationResponse>> update(
            @PathVariable UUID generationId,
            @RequestBody @Valid GenerationRequest request) {
        return ApiResponse.success("Generation updated successfully.", generationService.update(generationId, request));
    }

    @Operation(summary = "Delete generation")
    @DeleteMapping("/{generationId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID generationId) {
        generationService.delete(generationId);
        return ApiResponse.success("Generation deleted successfully.", null);
    }

    @Operation(summary = "Manual generation rollover")
    @PostMapping("/rollover")
    public ResponseEntity<ApiResponse<Void>> rollover() {
        generationService.rollToNextGeneration();
        return ApiResponse.success("Generation rolled over successfully.", null);
    }
}