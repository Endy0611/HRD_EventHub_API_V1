package com.ksga.eventhub.repository;

import com.ksga.eventhub.model.dto.auth.request.AppUserRequest;
import com.ksga.eventhub.model.dto.auth.response.AppUserResponse;
import com.ksga.eventhub.model.entity.AppUser;
import com.ksga.eventhub.utils.TypeHandlerUUID;
import org.apache.ibatis.annotations.*;

import java.util.Optional;
import java.util.UUID;

@Mapper
public interface AppUserRepository {

    @Results(id = "appUserMapper", value = {
            @Result(property = "appUserId",          column = "app_user_id",         typeHandler = TypeHandlerUUID.class),
            @Result(property = "username",            column = "username"),
            @Result(property = "firstName",           column = "first_name"),
            @Result(property = "lastName",            column = "last_name"),
            @Result(property = "email",               column = "email"),
            @Result(property = "password",            column = "password"),
            @Result(property = "dateOfBirth",         column = "date_of_birth"),
            @Result(property = "phoneNumber",         column = "phone_number"),
            @Result(property = "active",              column = "is_active"),
            @Result(property = "verified",            column = "is_verified"),
            @Result(property = "telegramSubscribed",  column = "telegram_subscribed"),
            @Result(property = "createdAt",           column = "created_at"),
            @Result(property = "updatedAt",           column = "updated_at")
    })
    @Select("""
        SELECT * FROM app_users
        WHERE email = #{email}
        LIMIT 1
    """)
    Optional<AppUser> getUserByEmail(String email);

    @Select("""
    INSERT INTO app_users (
        app_user_id,
        username,
        first_name,
        last_name,
        email,
        password,
        date_of_birth,
        phone_number,
        is_active,
        is_verified
    ) VALUES (
        gen_random_uuid(),
        #{request.username},
        #{request.firstName},
        #{request.lastName},
        #{request.email},
        #{request.password},
        #{request.dateOfBirth}::date,
        #{request.phoneNumber},
        true,
        false
    )
    RETURNING *
""")
    @ResultMap("appUserMapper")
    AppUser register(@Param("request") AppUserRequest request);

    @ResultMap("appUserMapper")
    @Select("""
        SELECT * FROM app_users
        WHERE app_user_id = #{appUserId}
        LIMIT 1
    """)
    Optional<AppUser> getUserById(UUID appUserId);

    @ResultMap("appUserMapper")
    @Select("""
        SELECT * FROM app_users
        WHERE app_user_id = #{appUserId}
        LIMIT 1
    """)
    Optional<AppUserResponse> getUserResponseById(@Param("appUserId") UUID appUserId);

    @Select("""
        SELECT EXISTS(SELECT 1 FROM app_users WHERE email = #{email})
    """)
    boolean existsUserEmail(String email);

    @Select("""
        SELECT EXISTS(SELECT 1 FROM app_users WHERE username = #{username})
    """)
    boolean existsUserName(String username);   //  param name matches #{username}

    @Update("""
        UPDATE app_users
        SET is_verified = true,
            updated_at  = now()
        WHERE email = #{email}
    """)
    void verifyUser(String email);

    @ResultMap("appUserMapper")
    @Select("""
        SELECT * FROM app_users
        WHERE email = #{identifier} OR username = #{identifier}
        LIMIT 1
    """)
    Optional<AppUser> getUserByEmailOrUsername(@Param("identifier") String identifier);

    @Update("""
    UPDATE app_users
    SET password   = #{password},
        updated_at = now()
    WHERE email = #{email}
    """)
    void updatePassword(@Param("email") String email, @Param("password") String password);
}