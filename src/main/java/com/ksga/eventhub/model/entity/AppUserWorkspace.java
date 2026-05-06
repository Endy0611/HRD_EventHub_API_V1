package com.ksga.eventhub.model.entity;

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
public class AppUserWorkspace {
    private UUID uwId;
    private UUID appUserId;
    private UUID workspaceId;
    private Instant joinAt;
}
