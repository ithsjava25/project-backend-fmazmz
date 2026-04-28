package org.fmazmz.casemanager.user.application;

import org.fmazmz.casemanager.exception.AccessDeniedException;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.domain.rbac.RoleName;
import org.fmazmz.casemanager.user.repository.UserRoleMappingRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthorizationService {

    private final UserRoleMappingRepository userRoleMappingRepository;

    public AdminAuthorizationService(UserRoleMappingRepository userRoleMappingRepository) {
        this.userRoleMappingRepository = userRoleMappingRepository;
    }

    public void requireAdmin(User actor) {
        if (actor == null || actor.getId() == null) {
            throw new AccessDeniedException("Administrator role required");
        }
        if (!userRoleMappingRepository.existsByUserIdAndRoleName(actor.getId(), RoleName.ADMIN.name())) {
            throw new AccessDeniedException("Administrator role required");
        }
    }
}
