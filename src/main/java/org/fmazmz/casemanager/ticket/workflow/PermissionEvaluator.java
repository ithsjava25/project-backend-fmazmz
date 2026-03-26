package org.fmazmz.casemanager.ticket.workflow;

import org.fmazmz.casemanager.ticket.model.TicketAction;
import org.fmazmz.casemanager.user.model.User;
import org.fmazmz.casemanager.user.repository.PermissionRepository;
import org.springframework.stereotype.Service;

@Service
public class PermissionEvaluator {

    private final PermissionRepository permissionRepository;

    public PermissionEvaluator(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean hasPermission(User actingUser, TicketAction action) {
        if (actingUser == null || actingUser.getId() == null) {
            return false;
        }
        if (action == null) {
            return false;
        }

        long count = permissionRepository.countByUserIdAndPermissionName(
                actingUser.getId(),
                action.permissionName()
        );
        return count > 0;
    }
}

