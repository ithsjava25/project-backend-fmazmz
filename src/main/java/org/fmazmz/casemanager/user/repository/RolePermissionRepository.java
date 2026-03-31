package org.fmazmz.casemanager.user.repository;

import org.fmazmz.casemanager.user.model.rbac.Permission;
import org.fmazmz.casemanager.user.model.rbac.Role;
import org.fmazmz.casemanager.user.model.rbac.RolePermission;
import org.fmazmz.casemanager.user.model.rbac.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    boolean existsByRoleAndPermission(Role role, Permission permission);
}
