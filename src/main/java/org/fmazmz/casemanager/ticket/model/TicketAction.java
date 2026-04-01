package org.fmazmz.casemanager.ticket.model;

public enum TicketAction {
    CREATE,
    READ,
    UPDATE,
    RESOLVE,
    ASSIGN,
    CHANGE_STATUS,
    CHANGE_PRIORITY,
    COMMENT,
    UPLOAD_ATTACHMENT;

    public String permissionName() {
        return "ticket." + name().toLowerCase();
    }

    public static TicketAction fromPermissionName(String permissionName) {
        if (permissionName == null || !permissionName.startsWith("ticket.")) {
            throw new IllegalArgumentException("Invalid ticket permission name: " + permissionName);
        }
        return TicketAction.valueOf(permissionName.substring("ticket.".length()).toUpperCase());
    }
}
