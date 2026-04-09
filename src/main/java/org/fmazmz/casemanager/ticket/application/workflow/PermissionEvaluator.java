package org.fmazmz.casemanager.ticket.application.workflow;

import org.fmazmz.casemanager.exception.AccessDeniedException;
import org.fmazmz.casemanager.ticket.domain.TicketAction;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.repository.PermissionRepository;
import org.springframework.stereotype.Service;

@Service
public class PermissionEvaluator {

    private final PermissionRepository permissionRepository;

    public PermissionEvaluator(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    /**
     * Whether API responses should include internal (non-public) ticket comments for this user.
     */
    public boolean includeInternalComments(User actor) {
        return hasPermission(actor, TicketAction.COMMENT_INTERNAL);
    }

    public void requirePermission(User actingUser, TicketAction requiredAction) {
        if (!hasPermission(actingUser, requiredAction)) {
            throw new AccessDeniedException("User is not authorized to perform action: " + requiredAction);
        }
    }

    public boolean hasPermission(User actingUser, TicketAction action) {
        if (actingUser == null || actingUser.getId() == null) {
            return false;
        }
        if (action == null) {
            return false;
        }
        return hasPermission(actingUser, action.permissionName());
    }

    public boolean hasPermission(User actingUser, String permissionName) {
        if (actingUser == null || actingUser.getId() == null) {
            return false;
        }
        if (permissionName == null || permissionName.isBlank()) {
            return false;
        }
        long count = permissionRepository.countByUserIdAndPermissionName(actingUser.getId(), permissionName);
        return count > 0;
    }
}

