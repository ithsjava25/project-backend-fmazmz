package org.fmazmz.casemanager.ticket.workflow;

import org.fmazmz.casemanager.user.repository.PermissionRepository;
import org.fmazmz.casemanager.user.model.User;
import org.springframework.stereotype.Service;

@Service
public class PermissionEvaluator {

    private final PermissionRepository permissionRepository;

    public PermissionEvaluator(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean hasPermission(User actingUser, String permissionName) {
        if (actingUser == null || actingUser.getId() == null) {
            return false;
        }
        if (permissionName == null || permissionName.isBlank()) {
            return false;
        }

        long count = permissionRepository.countByUserIdAndPermissionName(
                actingUser.getId(),
                permissionName
        );
        return count > 0;
    }
}

