package com.ksga.eventhub.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ksga.eventhub.model.dto.auth.response.AppUserResponse;
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
public class Generation {
    private UUID generationId;
    private String name;
    private String year;
    private boolean isCurrent;
    private Instant createdAt;
    private Instant updatedAt;
    @JsonIgnore
    private UUID appUserId;
    private AppUser appUser;
}
