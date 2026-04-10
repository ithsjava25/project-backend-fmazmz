package org.fmazmz.casemanager.ticket.application.workflow;

import org.fmazmz.casemanager.assignmentgroup.domain.AssignmentGroup;
import org.fmazmz.casemanager.assignmentgroup.repository.AssignmentGroupRepository;
import org.fmazmz.casemanager.ticket.dto.ChangeTicketStatusRequest;
import org.fmazmz.casemanager.ticket.domain.TicketStatus;
import org.fmazmz.casemanager.ticket.domain.TicketTransition;
import org.fmazmz.casemanager.ticket.repository.TransitionRepository;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class TicketWorkflowValidator {

    private final TransitionRepository transitionRepository;
    private final AssignmentGroupRepository assignmentGroupRepository;
    private final UserRepository userRepository;

    public TicketWorkflowValidator(
            TransitionRepository transitionRepository,
            AssignmentGroupRepository assignmentGroupRepository,
            UserRepository userRepository
    ) {
        this.transitionRepository = transitionRepository;
        this.assignmentGroupRepository = assignmentGroupRepository;
        this.userRepository = userRepository;
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
        if (toStatus == TicketStatus.ASSIGNED) {
            if (request.assignmentGroup() == null) {
                throw new IllegalArgumentException("Transition to ASSIGNED requires an assignment group");
            }
            if (request.assignee() == null) {
                throw new IllegalArgumentException("Transition to ASSIGNED requires an assignee");
            }
        }
        if (toStatus == TicketStatus.WORK_IN_PROGRESS) {
            if (request.assignmentGroup() == null) {
                throw new IllegalArgumentException("Transition to WORK_IN_PROGRESS requires an assignment group");
            }
            if (request.assignee() == null) {
                throw new IllegalArgumentException("Transition to WORK_IN_PROGRESS requires an assignee");
            }
            if (request.internalComment() == null || request.internalComment().isBlank()) {
                throw new IllegalArgumentException("Transition to WORK_IN_PROGRESS requires an internal comment");
            }
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

    /**
     * Loads assignment group and assignee and ensures the assignee is a member of the group.
     * Call after {@link #validateRequiredTransitionFields} for {@link TicketStatus#ASSIGNED} or
     * {@link TicketStatus#WORK_IN_PROGRESS}.
     */
    public ResolvedAssignment resolveAssignmentOrThrow(ChangeTicketStatusRequest request) {
        AssignmentGroup group = assignmentGroupRepository.findById(request.assignmentGroup())
                .orElseThrow(() -> new IllegalArgumentException("Assignment group not found"));
        User assignee = userRepository.findById(request.assignee())
                .orElseThrow(() -> new IllegalArgumentException("Assignee user not found"));
        if (!assignmentGroupRepository.isUserMember(group.getId(), assignee.getId())) {
            throw new IllegalArgumentException(
                    "Assignee must be a member of the ticket's assignment group"
            );
        }
        return new ResolvedAssignment(group, assignee);
    }

    public record ResolvedAssignment(AssignmentGroup group, User assignee) {
    }
}
