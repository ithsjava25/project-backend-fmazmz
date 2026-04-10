package org.fmazmz.casemanager.user.repository;

import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.domain.rbac.UserRoleId;
import org.fmazmz.casemanager.user.domain.rbac.UserRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRoleMappingRepository extends JpaRepository<UserRoleMapping, UserRoleId> {

    void deleteByUser(User user);

    boolean existsByUser_IdAndRole_Name(UUID userId, String roleName);

    @Query("select urm.role.name from UserRoleMapping urm where urm.user.id = :userId")
    List<String> findRoleNamesByUserId(@Param("userId") UUID userId);

    @Query("select count(distinct urm.user.id) from UserRoleMapping urm where urm.role.name = :roleName")
    long countDistinctUsersWithRoleName(@Param("roleName") String roleName);
}
