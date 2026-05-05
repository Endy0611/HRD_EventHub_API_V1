package com.ksga.eventhub.utils;

import com.ksga.eventhub.model.entity.AppUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HandleCurrentUser {
    public UUID getUserIdOfCurrentUser() {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        UUID userId = appUser.getAppUserId();
        System.out.println(userId);
        return userId;
    }

    public String getUserByEmail() {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String email = appUser.getEmail();
        System.out.println(email);
        return email;
    }
}