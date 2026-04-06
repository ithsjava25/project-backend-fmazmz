package org.fmazmz.casemanager.user.application;

import lombok.extern.slf4j.Slf4j;
import org.fmazmz.casemanager.config.AppSecurityProperties;
import org.fmazmz.casemanager.user.domain.AuthProvider;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.domain.rbac.Role;
import org.fmazmz.casemanager.user.domain.rbac.RoleName;
import org.fmazmz.casemanager.user.domain.rbac.UserRoleId;
import org.fmazmz.casemanager.user.domain.rbac.UserRoleMapping;
import org.fmazmz.casemanager.user.repository.RoleRepository;
import org.fmazmz.casemanager.user.repository.UserRepository;
import org.fmazmz.casemanager.user.repository.UserRoleMappingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;

@Slf4j
@Component
@Order(20)
public class BootstrapAdminSeeder implements CommandLineRunner {

    private final AppSecurityProperties appSecurityProperties;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;

    public BootstrapAdminSeeder(
            AppSecurityProperties appSecurityProperties,
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleMappingRepository userRoleMappingRepository
    ) {
        this.appSecurityProperties = appSecurityProperties;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleMappingRepository = userRoleMappingRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Role adminRole = roleRepository.findByName(RoleName.ADMIN.name())
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found. RBAC seeding must run first."));

        for (String raw : appSecurityProperties.getBootstrapAdminEmails()) {
            String email = normalize(raw);
            if (email.isBlank()) {
                continue;
            }

            User user = userRepository.findByEmailIgnoreCase(email)
                    .orElseGet(() -> createBootstrapUser(email));

            if (!userRoleMappingRepository.existsByUser_IdAndRole_Name(user.getId(), RoleName.ADMIN.name())) {
                UserRoleMapping mapping = new UserRoleMapping();

                UserRoleId id = new UserRoleId();
                id.setUserId(user.getId());
                id.setRoleId(adminRole.getId());

                mapping.setId(id);
                mapping.setUser(user);
                mapping.setRole(adminRole);

                userRoleMappingRepository.save(mapping);
                log.info("Bootstrap admin role granted to {}", email);
            }
        }
    }

    private User createBootstrapUser(String email) {
        User user = new User();
        user.setProvider(AuthProvider.GITHUB);
        user.setProviderId(AuthProvider.GITHUB.unlinkedProviderId());
        user.setEmail(email);
        // Username is taken from OAuth provider login on first successful link.
        user.setUserName(null);
        user.setAvatarUrl(null);
        return userRepository.save(user);
    }

    private String normalize(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
