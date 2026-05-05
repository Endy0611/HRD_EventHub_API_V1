package com.ksga.eventhub.service.impl;

import com.ksga.eventhub.exception.NotFoundException;
import com.ksga.eventhub.model.dto.generation.request.GenerationRequest;
import com.ksga.eventhub.model.dto.generation.response.GenerationResponse;
import com.ksga.eventhub.model.entity.AppUser;
import com.ksga.eventhub.model.entity.Generation;
import com.ksga.eventhub.repository.GenerationRepository;
import com.ksga.eventhub.service.GenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerationServiceImpl implements GenerationService {

    private final GenerationRepository generationRepository;
    private final ModelMapper modelMapper;

    @Override
    public GenerationResponse create(GenerationRequest request) {
        if (request.isCurrent()) {
            generationRepository.deactivateCurrent(Instant.now());
        }

        Generation generation = Generation.builder()
                .generationId(UUID.randomUUID())
                .name(request.getName())
                .year(request.getYear())
                .isCurrent(request.isCurrent())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .appUserId(getCurrentUserId())
                .build();

        generationRepository.save(generation);
        log.info("Generation created: {} by admin: {}", generation.getName(), generation.getAppUserId());
        return modelMapper.map(generation, GenerationResponse.class);
    }

    @Override
    public GenerationResponse getById(UUID generationId) {
        Generation generation = generationRepository.findById(generationId);
        if (generation == null) {
            throw new NotFoundException("Generation not found: " + generationId);
        }
        return modelMapper.map(generation, GenerationResponse.class);
    }

    @Override
    public GenerationResponse getCurrent() {
        Generation generation = generationRepository.findCurrent();
        if (generation == null) {
            throw new NotFoundException("No current generation found.");
        }
        return modelMapper.map(generation, GenerationResponse.class);
    }

    @Override
    public List<GenerationResponse> getAll() {
        return generationRepository.findAll()
                .stream()
                .map(g -> modelMapper.map(g, GenerationResponse.class))
                .toList();
    }

    @Override
    public GenerationResponse update(UUID generationId, GenerationRequest request) {
        Generation existing = generationRepository.findById(generationId);
        if (existing == null) {
            throw new NotFoundException("Generation not found: " + generationId);
        }

        if (request.isCurrent() && !existing.isCurrent()) {
            generationRepository.deactivateCurrent(Instant.now());
        }

        existing.setName(request.getName());
        existing.setYear(request.getYear());
        existing.setCurrent(request.isCurrent());
        existing.setUpdatedAt(Instant.now());

        generationRepository.update(existing);
        log.info("Generation updated: {}", existing.getName());
        return modelMapper.map(existing, GenerationResponse.class);
    }

    @Override
    public void delete(UUID generationId) {
        Generation existing = generationRepository.findById(generationId);
        if (existing == null) {
            throw new NotFoundException("Generation not found: " + generationId);
        }
        generationRepository.deleteById(generationId);
        log.info("Generation deleted: {}", generationId);
    }

    @Override
    public void rollToNextGeneration() {
        Generation current = generationRepository.findCurrent();
        if (current == null) {
            log.warn("No current generation found for rollover!");
            return;
        }

        int nextNumber = extractNumber(current.getName()) + 1;
        String nextYear = computeNextYear(current.getYear());

        generationRepository.deactivateCurrent(Instant.now());

        Generation next = Generation.builder()
                .generationId(UUID.randomUUID())
                .name("Generation " + nextNumber)
                .year(nextYear)
                .isCurrent(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .appUserId(null)
                .build();

        generationRepository.save(next);
        log.info("Rolled to: {} ({})", next.getName(), next.getYear());
    }

    private UUID getCurrentUserId() {
        AppUser user = (AppUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return user.getAppUserId();
    }

    private int extractNumber(String name) {
        return Integer.parseInt(name.replaceAll("[^0-9]", ""));
    }

    private String computeNextYear(String yearRange) {
        String[] parts = yearRange.split("-");
        int start = Integer.parseInt(parts[0].trim());
        int end = Integer.parseInt(parts[1].trim());
        return (start + 1) + "-" + (end + 1);
    }
}