package org.fmazmz.casemanager.ticket.application.workflow;

import org.fmazmz.casemanager.ticket.dto.ChangeTicketStatusRequest;
import org.fmazmz.casemanager.ticket.domain.TicketStatus;
import org.fmazmz.casemanager.ticket.domain.TicketTransition;
import org.fmazmz.casemanager.ticket.repository.TransitionRepository;
import org.springframework.stereotype.Service;

@Service
public class TicketWorkflowValidator {

    private final TransitionRepository transitionRepository;

    public TicketWorkflowValidator(TransitionRepository transitionRepository) {
        this.transitionRepository = transitionRepository;
    }

    public TicketTransition requireTransition(TicketStatus from, TicketStatus to) {
        return transitionRepository
                .findByFromStatusAndToStatus(from, to)
                .orElseThrow(() -> new IllegalStateException(
                        "No workflow transition configured for " + from + " -> " + to
                ));
    }

    public String requiredPermissionName(TicketStatus from, TicketStatus to) {
        TicketTransition transition = requireTransition(from, to);
        return transition.getRequiredPermission().getName();
    }

    public void validateRequiredTransitionFields(TicketStatus toStatus, ChangeTicketStatusRequest request) {
        if (toStatus == TicketStatus.WORK_IN_PROGRESS && request.assignee() == null) {
            throw new IllegalArgumentException("Transition to WORK_IN_PROGRESS requires an assignee");
        }
        if (toStatus == TicketStatus.WORK_IN_PROGRESS
                && (request.internalComment() == null || request.internalComment().isBlank())) {
            throw new IllegalArgumentException("Transition to WORK_IN_PROGRESS requires an internal comment");
        }
        if (toStatus == TicketStatus.AWAITING_USER_INFO
                && (request.publicComment() == null || request.publicComment().isBlank())) {
            throw new IllegalArgumentException("Transition to AWAITING_USER_INFO requires a public comment");
        }

        if (toStatus == TicketStatus.RESOLVED
                && (request.resolutionNotes() == null || request.resolutionNotes().isBlank())) {
            throw new IllegalArgumentException("Transition to RESOLVED requires resolution notes");
        }
    }
}

