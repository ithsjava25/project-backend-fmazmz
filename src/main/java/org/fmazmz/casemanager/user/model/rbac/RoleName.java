package org.fmazmz.casemanager.user.model.rbac;

import org.fmazmz.casemanager.ticket.model.TicketAction;

import java.util.EnumSet;
import java.util.Set;

public enum RoleName {
    ADMIN(EnumSet.allOf(TicketAction.class)),
    AGENT(EnumSet.of(
            TicketAction.CREATE,
            TicketAction.READ,
            TicketAction.UPDATE,
            TicketAction.RESOLVE,
            TicketAction.ASSIGN,
            TicketAction.CHANGE_STATUS,
            TicketAction.CHANGE_PRIORITY,
            TicketAction.COMMENT,
            TicketAction.UPLOAD_ATTACHMENT
    )),
    REPORTER(EnumSet.of(
            TicketAction.CREATE,
            TicketAction.READ,
            TicketAction.UPDATE,
            TicketAction.COMMENT,
            TicketAction.UPLOAD_ATTACHMENT
    )),
    VIEWER(EnumSet.of(
            TicketAction.READ
    ));

    private final Set<TicketAction> defaultTicketActions;

    RoleName(Set<TicketAction> defaultTicketActions) {
        this.defaultTicketActions = defaultTicketActions;
    }

    public Set<TicketAction> defaultTicketActions() {
        return defaultTicketActions;
    }
}
