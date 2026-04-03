package org.fmazmz.casemanager.ticket.domain;

import lombok.Getter;

public enum TicketType {
    INCIDENT("INC"),
    REQUEST("REQ");

    @Getter
    private final String label;

    TicketType(String label) {
        this.label = label;
    }
}
