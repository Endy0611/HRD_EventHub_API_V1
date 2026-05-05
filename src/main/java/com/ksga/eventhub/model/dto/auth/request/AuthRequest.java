package com.ksga.eventhub.model.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    @NotBlank
    @NotNull
    @Schema(example = "username or email")
    private String identifier;
    @NotNull
    @NotBlank
    @Schema(example = "Qwer123@")
    private String password;
}
