package com.ksga.eventhub.repository;

import com.ksga.eventhub.model.entity.Generation;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Mapper
@Repository
public interface GenerationRepository {

    @Results(id = "GenerationMapper", value = {
            @Result(property = "generationId", column = "generation_id"),
            @Result(property = "updatedAt",    column = "updated_at"),
            @Result(property = "createdAt",    column = "created_at"),
            @Result(property = "appUser", column = "app_user_id",
                    one = @One(select = "com.ksga.eventhub.repository.AppUserRepository.getUserById"))
    })
    @Select("SELECT * FROM generations WHERE generation_id = #{generationId}::uuid")
    Generation findById(UUID generationId);

    @ResultMap("GenerationMapper")
    @Select("SELECT * FROM generations WHERE is_current = true LIMIT 1")
    Generation findCurrent();

    @ResultMap("GenerationMapper")
    @Select("SELECT * FROM generations ORDER BY created_at DESC LIMIT 1")
    Generation findLatest();

    @ResultMap("GenerationMapper")
    @Select("SELECT * FROM generations ORDER BY created_at ASC")
    List<Generation> findAll();

    @Insert("""
            INSERT INTO generations (generation_id, name, year, is_current, created_at, updated_at, app_user_id)
            VALUES (#{generationId}::uuid, #{name}, #{year}, #{isCurrent}, #{createdAt}, #{updatedAt}, #{appUserId}::uuid)
            """)
    void save(Generation generation);

    @Update("""
            UPDATE generations
            SET name       = #{name},
                year       = #{year},
                is_current = #{isCurrent},
                updated_at = #{updatedAt}
            WHERE generation_id = #{generationId}::uuid
            """)
    void update(Generation generation);

    @Update("""
            UPDATE generations
            SET is_current = false,
                updated_at = #{updatedAt}
            WHERE is_current = true
            """)
    void deactivateCurrent(Instant updatedAt);

    @Delete("DELETE FROM generations WHERE generation_id = #{generationId}::uuid")
    void deleteById(UUID generationId);
}