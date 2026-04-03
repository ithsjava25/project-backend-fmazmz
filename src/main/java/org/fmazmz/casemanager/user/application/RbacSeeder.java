package org.fmazmz.casemanager.user.application;

import lombok.extern.slf4j.Slf4j;
import org.fmazmz.casemanager.ticket.domain.TicketAction;
import org.fmazmz.casemanager.user.domain.rbac.*;
import org.fmazmz.casemanager.user.repository.PermissionRepository;
import org.fmazmz.casemanager.user.repository.RolePermissionRepository;
import org.fmazmz.casemanager.user.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@Order(10)
public class RbacSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public RbacSeeder(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            RolePermissionRepository rolePermissionRepository
    ) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Map<String, Permission> permissionsByName = ensurePermissions();
        Map<RoleName, Role> rolesByName = ensureRoles();

        for (RoleName roleName : RoleName.values()) {
            grantPermissions(rolesByName.get(roleName), permissionsByName, roleName.defaultTicketActions());
        }
    }

    private Map<String, Permission> ensurePermissions() {
        Map<String, Permission> permissionsByName = new HashMap<>();

        for (TicketAction action : TicketAction.values()) {
            String permissionName = action.permissionName();
            Permission permission = permissionRepository
                    .findByName(permissionName)
                    .orElseGet(() -> {
                        Permission created = new Permission();
                        created.setName(permissionName);
                        return permissionRepository.save(created);
                    });
            permissionsByName.put(permissionName, permission);
        }

        return permissionsByName;
    }

    private Map<RoleName, Role> ensureRoles() {
        Map<RoleName, Role> rolesByName = new HashMap<>();
        for (RoleName roleName : RoleName.values()) {
            Role role = roleRepository
                    .findByName(roleName.name())
                    .orElseGet(() -> {
                        Role created = new Role();
                        created.setName(roleName.name());
                        return roleRepository.save(created);
                    });
            rolesByName.put(roleName, role);
        }
        return rolesByName;
    }

    private void grantPermissions(Role role, Map<String, Permission> permissionsByName, Set<TicketAction> actions) {
        for (TicketAction action : actions) {
            Permission permission = permissionsByName.get(action.permissionName());

            if (rolePermissionRepository.existsByRoleAndPermission(role, permission)) {
                continue;
            }

            RolePermission rolePermission = new RolePermission();
            RolePermissionId rolePermissionId = new RolePermissionId();
            rolePermissionId.setRoleId(role.getId());
            rolePermissionId.setPermissionId(permission.getId());

            rolePermission.setId(rolePermissionId);
            rolePermission.setRole(role);
            rolePermission.setPermission(permission);
            rolePermissionRepository.save(rolePermission);
        }
        log.info("RBAC seeded for role {}", role.getName());
    }
}
