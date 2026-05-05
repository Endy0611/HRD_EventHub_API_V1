    package com.ksga.eventhub.model.entity;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;

    import java.time.Instant;
    import java.time.LocalDate;
    import java.util.Collection;
    import java.util.List;
    import java.util.UUID;
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class AppUser implements UserDetails {
        private UUID    appUserId;
        private String  username;
        private String  firstName;
        private String  lastName;
        private String  email;
        @JsonIgnore
        private String  password;
        private LocalDate dateOfBirth;
        private String  phoneNumber;
        private boolean active;
        private boolean verified;
        private boolean telegramSubscribed;
        private Instant createdAt;
        private Instant updatedAt;

        @Override
        @JsonIgnore
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(); // no role system for now
        }

        @Override
        @JsonIgnore
        public String getPassword() { return password; }

        @Override
        @JsonIgnore
        public String getUsername() { return email; }

        @Override
        @JsonIgnore
        public boolean isAccountNonExpired() { return true; }

        @Override
        @JsonIgnore
        public boolean isAccountNonLocked() { return active; }

        @Override
        @JsonIgnore
        public boolean isCredentialsNonExpired() { return true; }

        @Override
        @JsonIgnore
        public boolean isEnabled() { return verified; }
    }