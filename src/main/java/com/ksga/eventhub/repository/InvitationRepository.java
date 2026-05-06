package com.ksga.eventhub.repository;

import com.ksga.eventhub.model.entity.Invitation;
import com.ksga.eventhub.utils.TypeHandlerUUID;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Mapper
@Repository
public interface InvitationRepository {

    @Results(id = "InvitationMapper", value = {
            @Result(property = "invitationId", column = "invitation_id", typeHandler = TypeHandlerUUID.class),
            @Result(property = "generationId", column = "generation_id", typeHandler = TypeHandlerUUID.class),
            @Result(property = "appUserId",    column = "app_user_id",   typeHandler = TypeHandlerUUID.class),
            @Result(property = "workspaceId",  column = "workspace_id",  typeHandler = TypeHandlerUUID.class),
            @Result(property = "createdAt",    column = "created_at"),
            @Result(property = "expiredAt",    column = "expired_at"),
            @Result(property = "token",        column = "token"),
            @Result(property = "roles",        column = "roles")
    })
    @Select("SELECT * FROM invitations WHERE invitation_id = #{invitationId}")
    Invitation findById(UUID invitationId);

    @ResultMap("InvitationMapper")
    @Select("SELECT * FROM invitations WHERE token = #{token}")
    Invitation findByToken(String token);

    @ResultMap("InvitationMapper")
    @Select("""
            SELECT * FROM invitations
            WHERE workspace_id = #{workspaceId}
            ORDER BY created_at DESC
            """)
    List<Invitation> findByWorkspaceId(UUID workspaceId);

    @ResultMap("InvitationMapper")
    @Select("""
            SELECT * FROM invitations
            WHERE app_user_id = #{appUserId}
            ORDER BY created_at DESC
            """)
    List<Invitation> findByAppUserId(UUID appUserId);

    @Insert("""
            INSERT INTO invitations (
                invitation_id, token, roles, created_at, expired_at,
                generation_id, app_user_id, workspace_id
            )
            VALUES (
                #{invitationId}, #{token}, #{roles}, #{createdAt}, #{expiredAt},
                #{generationId}, #{appUserId}, #{workspaceId}
            )
            """)
    void save(Invitation invitation);

    @Delete("DELETE FROM invitations WHERE invitation_id = #{invitationId}")
    void deleteById(UUID invitationId);
}