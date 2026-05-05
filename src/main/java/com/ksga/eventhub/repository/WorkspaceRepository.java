package com.ksga.eventhub.repository;

import com.ksga.eventhub.model.dto.workspace.response.WorkspaceResponse;
import com.ksga.eventhub.model.entity.Workspace;
import com.ksga.eventhub.utils.TypeHandlerUUID;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;@Mapper
@Repository
public interface WorkspaceRepository {

    @Results(id = "WorkspaceMapper", value = {
            @Result(property = "workspaceId", column = "workspace_id", typeHandler = TypeHandlerUUID.class),
            @Result(property = "isPublic",    column = "is_public"),
            @Result(property = "createdBy",   column = "created_by",   typeHandler = TypeHandlerUUID.class),
            @Result(property = "createdAt",   column = "created_at"),
            @Result(property = "updatedAt",   column = "updated_at"),
            @Result(property = "generation",  column = "generation_id",
                    one = @One(select = "com.ksga.eventhub.repository.GenerationRepository.findById"))
    })
    @Select("SELECT * FROM workspace WHERE workspace_id = #{workspaceId, jdbcType=OTHER}")
    WorkspaceResponse findById(UUID workspaceId);

    @ResultMap("WorkspaceMapper")
    @Select("SELECT * FROM workspace ORDER BY created_at DESC")
    List<WorkspaceResponse> findAll();

    @ResultMap("WorkspaceMapper")
    @Select("SELECT * FROM workspace WHERE generation_id = #{generationId, jdbcType=OTHER} ORDER BY created_at DESC")
    List<WorkspaceResponse> findByGenerationId(UUID generationId);

    @Insert("""
            INSERT INTO workspace (workspace_id, name, description, is_public, generation_id, created_by, created_at, updated_at)
            VALUES (
                #{workspaceId, jdbcType=OTHER},
                #{name},
                #{description},
                #{isPublic},
                #{generationId, jdbcType=OTHER},
                #{createdBy,    jdbcType=OTHER},
                #{createdAt},
                #{updatedAt}
            )
            """)
    void save(Workspace workspace);

    @Update("""
            UPDATE workspace
            SET name          = #{name},
                description   = #{description},
                is_public     = #{isPublic},
                generation_id = #{generationId, jdbcType=OTHER},
                updated_at    = #{updatedAt}
            WHERE workspace_id = #{workspaceId, jdbcType=OTHER}
            """)
    void update(Workspace workspace);

    @Delete("DELETE FROM workspace WHERE workspace_id = #{workspaceId, jdbcType=OTHER}")
    void deleteById(UUID workspaceId);
}