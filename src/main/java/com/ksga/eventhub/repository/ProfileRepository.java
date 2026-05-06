package com.ksga.eventhub.repository;

import com.ksga.eventhub.utils.TypeHandlerUUID;
import org.apache.ibatis.annotations.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Mapper
@Repository
public interface ProfileRepository {

    @Results(id = "ProfileMapper", value = {
            @Result(property = "profileId",          column = "profile_id",           typeHandler = TypeHandlerUUID.class),
            @Result(property = "generationId",       column = "generation_id",         typeHandler = TypeHandlerUUID.class),
            @Result(property = "appUserId",          column = "app_user_id",           typeHandler = TypeHandlerUUID.class),
            @Result(property = "profileSharingMode", column = "profile_sharing_mode"),
            @Result(property = "currentStatus",      column = "current_status"),
            @Result(property = "cvResumeUrl",        column = "cv_resume_url"),
            @Result(property = "socialAccount",      column = "social_account")
    })
    @Select("SELECT * FROM profile WHERE app_user_id = #{appUserId}")
    Profile findByAppUserId(UUID appUserId);

    @Update("""
            UPDATE profile
            SET generation_id = #{generationId}
            WHERE app_user_id = #{appUserId}
            """)
    void updateGeneration(@Param("appUserId")   UUID appUserId,
                          @Param("generationId") UUID generationId);
}