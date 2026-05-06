package com.ksga.eventhub.repository;

import com.ksga.eventhub.model.entity.Role;
import com.ksga.eventhub.utils.TypeHandlerUUID;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Mapper
@Repository
public interface RoleRepository {

    @Results(id = "RoleMapper", value = {
            @Result(property = "roleId",   column = "role_id",   typeHandler = TypeHandlerUUID.class),
            @Result(property = "roleName", column = "role_name")
    })
    @Select("SELECT * FROM roles WHERE role_name = #{roleName}")
    Role findByRoleName(String roleName);

    @ResultMap("RoleMapper")
    @Select("SELECT * FROM roles WHERE role_id = #{roleId}")
    Role findById(UUID roleId);

    @ResultMap("RoleMapper")
    @Select("SELECT * FROM roles")
    List<Role> findAll();

    // ✅ returns List<Role> — used by @Many in AppUserRepository
    @ResultMap("RoleMapper")
    @Select("""
            SELECT r.role_id, r.role_name
            FROM roles r
            JOIN app_user_roles aur ON r.role_id = aur.role_id
            WHERE aur.app_user_id = #{appUserId}
            """)
    List<Role> findRoleObjectsByUserId(UUID appUserId);

    // returns List<String> — keep for other uses
    @Select("""
            SELECT r.role_name FROM roles r
            JOIN app_user_roles aur ON r.role_id = aur.role_id
            WHERE aur.app_user_id = #{appUserId}
            """)
    List<String> findRolesByUserId(UUID appUserId);

    @Insert("""
            INSERT INTO app_user_roles (app_user_id, role_id)
            VALUES (#{appUserId}, #{roleId})
            """)
    void assignRoleToUser(@Param("appUserId") UUID appUserId,
                          @Param("roleId")    UUID roleId);

    @Delete("""
            DELETE FROM app_user_roles
            WHERE app_user_id = #{appUserId}
            AND   role_id     = #{roleId}
            """)
    void removeRoleFromUser(@Param("appUserId") UUID appUserId,
                            @Param("roleId")    UUID roleId);
}