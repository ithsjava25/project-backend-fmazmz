package org.fmazmz.casemanager.user.application;

import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.domain.AuthProvider;
import org.fmazmz.casemanager.user.domain.rbac.Role;
import org.fmazmz.casemanager.user.domain.rbac.RoleName;
import org.fmazmz.casemanager.user.domain.rbac.UserRoleId;
import org.fmazmz.casemanager.user.domain.rbac.UserRoleMapping;
import org.fmazmz.casemanager.user.repository.RoleRepository;
import org.fmazmz.casemanager.user.repository.UserRepository;
import org.fmazmz.casemanager.user.repository.UserRoleMappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserAdminService {

    private final AdminAuthorizationService adminAuthorizationService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;

    public UserAdminService(
            AdminAuthorizationService adminAuthorizationService,
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleMappingRepository userRoleMappingRepository
    ) {
        this.adminAuthorizationService = adminAuthorizationService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleMappingRepository = userRoleMappingRepository;
    }

    @Transactional
    public User createUser(User adminActor, String email, RoleName roleName) {
        adminAuthorizationService.requireAdmin(adminActor);
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        if (normalizedEmail.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalStateException("A user with this email already exists");
        }

        User user = new User();
        user.setProvider(AuthProvider.GITHUB);
        // Placeholder until first OAuth login links the real provider id.
        user.setProviderId(AuthProvider.GITHUB.pendingProviderId());
        user.setUserName(null);
        user.setEmail(normalizedEmail);
        userRepository.save(user);

        assignRoleSet(user, EnumSet.of(roleName));
        return user;
    }

    @Transactional
    public void replaceUserRolesByEmail(User adminActor, String email, Set<RoleName> newRoles) {
        adminAuthorizationService.requireAdmin(adminActor);
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        if (normalizedEmail.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (newRoles == null || newRoles.isEmpty()) {
            throw new IllegalArgumentException("At least one role is required");
        }
        User target = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        var targetUserId = target.getId();

        boolean wasAdmin = userRoleMappingRepository.existsByUserIdAndRoleName(targetUserId, RoleName.ADMIN.name());
        boolean newHasAdmin = newRoles.contains(RoleName.ADMIN);
        if (wasAdmin && !newHasAdmin) {
            long adminUsers = userRoleMappingRepository.countDistinctUsersWithRoleName(RoleName.ADMIN.name());
            if (adminUsers <= 1) {
                throw new IllegalArgumentException("Cannot remove the last administrator");
            }
        }
        assignRoleSet(target, newRoles);
    }

    private void assignRoleSet(User target, Set<RoleName> roles) {
        userRoleMappingRepository.deleteByUser(target);

        for (RoleName roleName : roles) {
            Role role = roleRepository.findByName(roleName.name())
                    .orElseThrow(() -> new IllegalStateException("Unknown role: " + roleName.name()));

            UserRoleMapping mapping = new UserRoleMapping();
            UserRoleId id = new UserRoleId();

            id.setUserId(target.getId());
            id.setRoleId(role.getId());

            mapping.setId(id);
            mapping.setUser(target);
            mapping.setRole(role);

            userRoleMappingRepository.save(mapping);
        }
    }

    public static Set<RoleName> parseRoleNames(Set<String> names) {
        if (names == null || names.isEmpty()) {
            throw new IllegalArgumentException("At least one role is required");
        }

        Set<RoleName> parsed = names.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(n -> {
                    try {
                        return RoleName.valueOf(n.toUpperCase(Locale.ROOT));
                    } catch (IllegalArgumentException ex) {
                        throw new IllegalArgumentException("Unknown role: " + n);
                    }
                })
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(RoleName.class)));

        if (parsed.isEmpty()) {
            throw new IllegalArgumentException("At least one role is required");
        }
        return parsed;
    }
}
