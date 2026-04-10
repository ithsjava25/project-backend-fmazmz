package org.fmazmz.casemanager.ticket.application.workflow;

import org.fmazmz.casemanager.assignmentgroup.domain.AssignmentGroup;
import org.fmazmz.casemanager.assignmentgroup.repository.AssignmentGroupRepository;
import org.fmazmz.casemanager.ticket.dto.ChangeTicketStatusRequest;
import org.fmazmz.casemanager.ticket.domain.TicketStatus;
import org.fmazmz.casemanager.ticket.repository.TransitionRepository;
import org.fmazmz.casemanager.user.domain.User;
import org.fmazmz.casemanager.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketWorkflowValidatorTest {

    @Mock
    private TransitionRepository transitionRepository;

    @Mock
    private AssignmentGroupRepository assignmentGroupRepository;

    @Mock
    private UserRepository userRepository;

    private TicketWorkflowValidator validator;

    private final UUID groupId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @BeforeEach
    void setUp() {
        validator = new TicketWorkflowValidator(transitionRepository, assignmentGroupRepository, userRepository);
    }

    @Nested
    @DisplayName("validateRequiredTransitionFields")
    class ValidateRequiredTransitionFields {

        @Test
        @DisplayName("ASSIGNED requires assignment group and assignee")
        void assignedRequiresGroupAndAssignee() {
            IllegalArgumentException missingGroup = assertThrows(IllegalArgumentException.class,
                    () -> validator.validateRequiredTransitionFields(TicketStatus.ASSIGNED,
                            new ChangeTicketStatusRequest(TicketStatus.ASSIGNED, null, userId, null, null, null)));
            assertEquals("Transition to ASSIGNED requires an assignment group", missingGroup.getMessage());

            IllegalArgumentException missingAssignee = assertThrows(IllegalArgumentException.class,
                    () -> validator.validateRequiredTransitionFields(TicketStatus.ASSIGNED,
                            new ChangeTicketStatusRequest(TicketStatus.ASSIGNED, groupId, null, null, null, null)));
            assertEquals("Transition to ASSIGNED requires an assignee", missingAssignee.getMessage());
        }

        @Test
        @DisplayName("ASSIGNED accepts when group and assignee are present")
        void assignedAcceptsWithGroupAndAssignee() {
            assertDoesNotThrow(() -> validator.validateRequiredTransitionFields(TicketStatus.ASSIGNED,
                    new ChangeTicketStatusRequest(TicketStatus.ASSIGNED, groupId, userId, null, null, null)));
        }

        @Test
        @DisplayName("WORK_IN_PROGRESS requires assignment group, assignee, and internal comment")
        void wipRequiresGroupAssigneeAndInternalComment() {
            assertEquals("Transition to WORK_IN_PROGRESS requires an assignment group",
                    assertThrows(IllegalArgumentException.class,
                            () -> validator.validateRequiredTransitionFields(TicketStatus.WORK_IN_PROGRESS,
                                    new ChangeTicketStatusRequest(TicketStatus.WORK_IN_PROGRESS, null, userId, null, "note", null)))
                            .getMessage());

            assertEquals("Transition to WORK_IN_PROGRESS requires an assignee",
                    assertThrows(IllegalArgumentException.class,
                            () -> validator.validateRequiredTransitionFields(TicketStatus.WORK_IN_PROGRESS,
                                    new ChangeTicketStatusRequest(TicketStatus.WORK_IN_PROGRESS, groupId, null, null, "note", null)))
                            .getMessage());

            assertEquals("Transition to WORK_IN_PROGRESS requires an internal comment",
                    assertThrows(IllegalArgumentException.class,
                            () -> validator.validateRequiredTransitionFields(TicketStatus.WORK_IN_PROGRESS,
                                    new ChangeTicketStatusRequest(TicketStatus.WORK_IN_PROGRESS, groupId, userId, null, null, null)))
                            .getMessage());

            assertEquals("Transition to WORK_IN_PROGRESS requires an internal comment",
                    assertThrows(IllegalArgumentException.class,
                            () -> validator.validateRequiredTransitionFields(TicketStatus.WORK_IN_PROGRESS,
                                    new ChangeTicketStatusRequest(TicketStatus.WORK_IN_PROGRESS, groupId, userId, null, "   ", null)))
                            .getMessage());
        }

        @Test
        @DisplayName("WORK_IN_PROGRESS accepts when all required fields are present")
        void wipAcceptsWhenComplete() {
            assertDoesNotThrow(() -> validator.validateRequiredTransitionFields(TicketStatus.WORK_IN_PROGRESS,
                    new ChangeTicketStatusRequest(TicketStatus.WORK_IN_PROGRESS, groupId, userId, null, "work note", null)));
        }
    }

    @Nested
    @DisplayName("resolveAssignmentOrThrow")
    class ResolveAssignmentOrThrow {

        @Test
        @DisplayName("throws when assignment group is not found")
        void groupNotFound() {
            when(assignmentGroupRepository.findById(eq(groupId))).thenReturn(Optional.empty());

            ChangeTicketStatusRequest req = new ChangeTicketStatusRequest(
                    TicketStatus.ASSIGNED, groupId, userId, null, null, null);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> validator.resolveAssignmentOrThrow(req));
            assertEquals("Assignment group not found", ex.getMessage());
            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("throws when assignee user is not found")
        void userNotFound() {
            AssignmentGroup group = new AssignmentGroup();
            group.setId(groupId);
            group.setName("G1");
            when(assignmentGroupRepository.findById(eq(groupId))).thenReturn(Optional.of(group));
            when(userRepository.findById(eq(userId))).thenReturn(Optional.empty());

            ChangeTicketStatusRequest req = new ChangeTicketStatusRequest(
                    TicketStatus.ASSIGNED, groupId, userId, null, null, null);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> validator.resolveAssignmentOrThrow(req));
            assertEquals("Assignee user not found", ex.getMessage());
        }

        @Test
        @DisplayName("throws when assignee is not a member of the group")
        void notMember() {
            AssignmentGroup group = new AssignmentGroup();
            group.setId(groupId);
            group.setName("G1");
            User user = new User();
            user.setId(userId);
            when(assignmentGroupRepository.findById(eq(groupId))).thenReturn(Optional.of(group));
            when(userRepository.findById(eq(userId))).thenReturn(Optional.of(user));
            when(assignmentGroupRepository.isUserMember(eq(groupId), eq(userId))).thenReturn(false);

            ChangeTicketStatusRequest req = new ChangeTicketStatusRequest(
                    TicketStatus.ASSIGNED, groupId, userId, null, null, null);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> validator.resolveAssignmentOrThrow(req));
            assertEquals("Assignee must be a member of the ticket's assignment group", ex.getMessage());
        }

        @Test
        @DisplayName("returns resolved group and assignee when valid")
        void success() {
            AssignmentGroup group = new AssignmentGroup();
            group.setId(groupId);
            group.setName("G1");
            User user = new User();
            user.setId(userId);
            when(assignmentGroupRepository.findById(eq(groupId))).thenReturn(Optional.of(group));
            when(userRepository.findById(eq(userId))).thenReturn(Optional.of(user));
            when(assignmentGroupRepository.isUserMember(eq(groupId), eq(userId))).thenReturn(true);

            ChangeTicketStatusRequest req = new ChangeTicketStatusRequest(
                    TicketStatus.ASSIGNED, groupId, userId, null, null, null);

            TicketWorkflowValidator.ResolvedAssignment resolved = validator.resolveAssignmentOrThrow(req);
            assertSame(group, resolved.group());
            assertSame(user, resolved.assignee());
        }
    }
}
