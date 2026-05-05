package com.ksga.eventhub.model.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppUserResponse {
    private UUID    appUserId;
    private String  username;
    private String  firstName;
    private String  lastName;
    private String  email;
    private LocalDate dateOfBirth;
    private String  phoneNumber;
    private boolean active;
    private boolean verified;
    private boolean telegramSubscribed;
    private Instant createdAt;
    private Instant updatedAt;
}