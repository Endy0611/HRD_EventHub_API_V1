package com.ksga.eventhub.model.dto.workspace.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceRequest {

    @NotBlank
    private String name;

    private String description;

    private Boolean isPublic;

    @NotNull
    private UUID generationId;
}