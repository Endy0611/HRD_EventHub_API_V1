package com.ksga.eventhub.service.impl;

import com.ksga.eventhub.exception.BadRequestException;
import com.ksga.eventhub.exception.NotFoundException;
import com.ksga.eventhub.model.dto.ApiResponse;
import com.ksga.eventhub.model.dto.invitation.request.InvitationRequest;
import com.ksga.eventhub.model.dto.invitation.response.InvitationResponse;
import com.ksga.eventhub.model.dto.workspace.response.WorkspaceResponse;
import com.ksga.eventhub.model.entity.AppUser;
import com.ksga.eventhub.model.entity.Generation;
import com.ksga.eventhub.model.entity.Invitation;
import com.ksga.eventhub.model.entity.Role;
import com.ksga.eventhub.repository.*;
import com.ksga.eventhub.service.InvitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvititationServiceImpl implements InvitationService {

    private static final int DEFAULT_TTL_DAYS = 7;

    private final InvitationRepository invitationRepository;
    private final GenerationRepository generationRepository;
    private final WorkspaceRepository workspaceRepository;
    private final RoleRepository roleRepository;
    private final AppUserWorkspaceRepository appUserWorkspaceRepository;
    private final ProfileRepository profileRepository;
    private final ModelMapper modelMapper;

    @Override
    public InvitationResponse create(InvitationRequest request) {

        // 1. Validate workspace
        WorkspaceResponse workspace = workspaceRepository.findById(request.getWorkspaceId());
        if (workspace == null) throw new NotFoundException("Workspace not found.");

        // 2. Validate generation
        Generation generation = generationRepository.findById(request.getGenerationId());
        if (generation == null) throw new NotFoundException("Generation not found.");

        // 3. Auto determine role from generation
        String roleName = generation.isCurrent() ? "STUDENT" : "ALUMNI";

        // 4. Generate secure token
        String token = generateToken();

        int ttl = request.getTtlDays() != null ? request.getTtlDays() : DEFAULT_TTL_DAYS;

        Invitation invitation = Invitation.builder()
                .invitationId(UUID.randomUUID())
                .token(token)
                .roles(roleName)
                .createdAt(Instant.now())
                .expiredAt(Instant.now().plus(ttl, ChronoUnit.DAYS))
                .generationId(request.getGenerationId())
                .appUserId(getCurrentUserId())
                .workspaceId(request.getWorkspaceId())
                .build();

        invitationRepository.save(invitation);
        log.info("Invitation created: workspace={} generation={} role={}",
                request.getWorkspaceId(), generation.getName(), roleName);

        InvitationResponse response = modelMapper.map(invitation, InvitationResponse.class);
        response.setInviteLink("/api/v1/invitations/join/" + token);
        return response;
    }

    @Override
    public InvitationResponse getById(UUID invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId);
        if (invitation == null) throw new NotFoundException("Invitation not found.");

        InvitationResponse response = modelMapper.map(invitation, InvitationResponse.class);
        response.setInviteLink("/api/v1/invitations/join/" + invitation.getToken());
        return response;
    }

    @Override
    public List<InvitationResponse> getByWorkspaceId(UUID workspaceId) {
        return invitationRepository.findByWorkspaceId(workspaceId)
                .stream()
                .map(inv -> {
                    InvitationResponse r = modelMapper.map(inv, InvitationResponse.class);
                    r.setInviteLink("/api/v1/invitations/join/" + inv.getToken());
                    return r;
                })
                .toList();
    }

    @Override
    public List<InvitationResponse> getMyInvitations() {
        UUID currentUserId = getCurrentUserId();
        return invitationRepository.findByAppUserId(currentUserId)
                .stream()
                .map(inv -> {
                    InvitationResponse r = modelMapper.map(inv, InvitationResponse.class);
                    r.setInviteLink("/api/v1/invitations/join/" + inv.getToken());
                    return r;
                })
                .toList();
    }

    @Override
    public ApiResponse<Void> joinByToken(String token) {

        // 1. Find invitation by token
        Invitation invitation = invitationRepository.findByToken(token);
        if (invitation == null) throw new NotFoundException("Invitation not found.");

        // 2. Check expiry
        if (Instant.now().isAfter(invitation.getExpiredAt())) {
            throw new BadRequestException("Invitation has expired.");
        }

        // 3. Get current logged-in user
        UUID currentUserId = getCurrentUserId();

        // 4. Check already a member
        boolean alreadyMember = appUserWorkspaceRepository.isMember(
                currentUserId, invitation.getWorkspaceId());
        if (alreadyMember) {
            throw new BadRequestException("You are already a member of this workspace.");
        }

        // 5. Get generation from invite
        Generation generation = generationRepository.findById(invitation.getGenerationId());
        if (generation == null) throw new NotFoundException("Generation not found.");

        // 6. Auto determine role from generation
        String roleName = generation.isCurrent() ? "STUDENT" : "ALUMNI";

        // 7. Get role entity
        Role role = roleRepository.findByRoleName(roleName);
        if (role == null) throw new NotFoundException("Role not found: " + roleName);

        // 8. Assign role to user → app_user_roles
        roleRepository.assignRoleToUser(currentUserId, role.getRoleId());
        log.info("Role '{}' assigned to user '{}'", roleName, currentUserId);

        // 9. Set generation in profile → profile
        profileRepository.updateGeneration(currentUserId, generation.getGenerationId());
        log.info("Generation '{}' set to profile of user '{}'", generation.getName(), currentUserId);

        // 10. Add user to workspace → app_user_workspace
        appUserWorkspaceRepository.save(
                UUID.randomUUID(),
                currentUserId,
                invitation.getWorkspaceId(),
                Instant.now()
        );
        log.info("User '{}' joined workspace '{}'", currentUserId, invitation.getWorkspaceId());

        String message = String.format(
                "Successfully joined workspace as %s (Generation: %s)",
                roleName, generation.getName()
        );

        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .status(HttpStatus.OK)
                .payload(null)
                .timestamp(Instant.now())
                .build();
    }

    @Override
    public void delete(UUID invitationId) {
        Invitation existing = invitationRepository.findById(invitationId);
        if (existing == null) throw new NotFoundException("Invitation not found.");
        invitationRepository.deleteById(invitationId);
        log.info("Invitation deleted: {}", invitationId);
    }

    // Generate secure random token
    private String generateToken() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // Get current user from JWT
    private UUID getCurrentUserId() {
        AppUser user = (AppUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return user.getAppUserId();
    }
}
