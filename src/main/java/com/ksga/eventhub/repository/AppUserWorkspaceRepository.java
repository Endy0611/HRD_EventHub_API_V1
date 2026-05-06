package com.ksga.eventhub.repository;

import com.ksga.eventhub.model.entity.AppUserWorkspace;
import com.ksga.eventhub.utils.TypeHandlerUUID;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Mapper
@Repository
public interface AppUserWorkspaceRepository {

    @Results(id = "AppUserWorkspaceMapper", value = {
            @Result(property = "uwId",        column = "uw_id",        typeHandler = TypeHandlerUUID.class),
            @Result(property = "appUserId",   column = "app_user_id",  typeHandler = TypeHandlerUUID.class),
            @Result(property = "workspaceId", column = "workspace_id", typeHandler = TypeHandlerUUID.class),
            @Result(property = "joinAt",      column = "join_at")
    })
    @Select("""
            SELECT * FROM app_user_workspace
            WHERE workspace_id = #{workspaceId}
            """)
    List<AppUserWorkspace> findByWorkspaceId(UUID workspaceId);

    @ResultMap("AppUserWorkspaceMapper")
    @Select("""
            SELECT * FROM app_user_workspace
            WHERE app_user_id = #{appUserId}
            """)
    List<AppUserWorkspace> findByAppUserId(UUID appUserId);

    @Insert("""
            INSERT INTO app_user_workspace (uw_id, app_user_id, workspace_id, join_at)
            VALUES (#{uwId}, #{appUserId}, #{workspaceId}, #{joinAt})
            """)
    void save(@Param("uwId")        UUID uwId,
              @Param("appUserId")   UUID appUserId,
              @Param("workspaceId") UUID workspaceId,
              @Param("joinAt") Instant joinAt);

    @Select("""
            SELECT COUNT(*) > 0 FROM app_user_workspace
            WHERE app_user_id  = #{appUserId}
            AND   workspace_id = #{workspaceId}
            """)
    boolean isMember(@Param("appUserId")   UUID appUserId,
                     @Param("workspaceId") UUID workspaceId);

    @Delete("""
            DELETE FROM app_user_workspace
            WHERE app_user_id  = #{appUserId}
            AND   workspace_id = #{workspaceId}
            """)
    void removeMember(@Param("appUserId")   UUID appUserId,
                      @Param("workspaceId") UUID workspaceId);
}
