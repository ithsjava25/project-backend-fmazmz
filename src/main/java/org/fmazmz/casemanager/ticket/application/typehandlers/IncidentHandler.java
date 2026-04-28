package org.fmazmz.casemanager.ticket.application.typehandlers;

import lombok.extern.slf4j.Slf4j;
import org.fmazmz.casemanager.assignmentgroup.repository.AssignmentGroupRepository;
import org.fmazmz.casemanager.ticket.domain.Priority;
import org.fmazmz.casemanager.ticket.domain.Ticket;
import org.fmazmz.casemanager.ticket.domain.TicketType;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IncidentHandler implements TypeHandler {
    private final AssignmentGroupRepository assignmentGroupRepository;

    public IncidentHandler(AssignmentGroupRepository assignmentGroupRepository) {
        this.assignmentGroupRepository = assignmentGroupRepository;
    }

    @Override
    public TicketType supports() {
        return TicketType.INCIDENT;
    }

    @Override
    public void applyDefaults(Ticket ticket) {
        if (ticket.getPriority() == null) {
            ticket.setPriority(Priority.P3);
        }
        if (ticket.getAssignmentGroup() == null) {
            assignmentGroupRepository.findByName("L1").ifPresentOrElse(
                    ticket::setAssignmentGroup,
                    () -> log.warn("Default assignment group L1 not found; incident will remain unassigned")
            );
        }
    }
}
