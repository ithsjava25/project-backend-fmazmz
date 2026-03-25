package org.fmazmz.casemanager.user.repository;

import org.fmazmz.casemanager.user.model.rbac.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    /**
     * Counts how many RolePermission rows grant the given permission name
     * to the given user (via user->roles->rolePermissions).
     */
    @Query("""
            select count(p)
            from UserRoleMapping urm
            join RolePermission rp on rp.role = urm.role
            join rp.permission p
            where urm.user.id = :userId
              and p.name = :permissionName
            """)
    long countByUserIdAndPermissionName(@Param("userId") UUID userId, @Param("permissionName") String permissionName);
}

