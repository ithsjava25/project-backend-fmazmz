package org.fmazmz.casemanager.ticket.application.typehandlers;

import org.fmazmz.casemanager.assignmentgroup.domain.AssignmentGroup;
import org.fmazmz.casemanager.assignmentgroup.repository.AssignmentGroupRepository;
import org.fmazmz.casemanager.ticket.domain.Priority;
import org.fmazmz.casemanager.ticket.domain.Ticket;
import org.fmazmz.casemanager.ticket.domain.TicketType;
import org.fmazmz.casemanager.ticket.application.typehandlers.IncidentHandler;
import org.fmazmz.casemanager.ticket.application.typehandlers.RequestHandler;
import org.fmazmz.casemanager.ticket.application.typehandlers.TypeHandler;
import org.fmazmz.casemanager.ticket.application.typehandlers.TypeHandlerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TypeHandlersTests {

    @Nested
    class TypeHandlerFactoryTests {
        @Mock
        private TypeHandler incidentHandler;
        @Mock
        private TypeHandler requestHandler;
        private TypeHandlerFactory handlerFactory;

        @BeforeEach
        void setUp() {
            when(incidentHandler.supports()).thenReturn(TicketType.INCIDENT);
            when(requestHandler.supports()).thenReturn(TicketType.REQUEST);

            handlerFactory = new TypeHandlerFactory(List.of(incidentHandler, requestHandler));
        }

        @Test
        @DisplayName("returns handler for INCIDENT")
        void returnsIncidentHandler() {
            TypeHandler resolved = handlerFactory.resolve(TicketType.INCIDENT);
            assertSame(incidentHandler, resolved);
        }

        @Test
        @DisplayName("returns handler for REQUEST")
        void returnsRequestHandler() {
            TypeHandler resolved = handlerFactory.resolve(TicketType.REQUEST);
            assertSame(requestHandler, resolved);
        }

        @Test
        @DisplayName("throws when no handler is configured")
        void throwsWhenMissingHandler() {
            TypeHandlerFactory incompleteFactory =
                    new TypeHandlerFactory(List.of(incidentHandler));

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> incompleteFactory.resolve(TicketType.REQUEST)
            );
            assertTrue(ex.getMessage().contains("REQUEST"));
        }
    }

    @Nested
    class IncidentHandlerTests {
        @Mock
        private AssignmentGroupRepository assignmentGroupRepository;
        private IncidentHandler incidentHandler;

        @BeforeEach
        void setUp() {
            incidentHandler = new IncidentHandler(assignmentGroupRepository);
        }

        @Test
        @DisplayName("returns INCIDENT as the correct supported TicketType")
        void returnsCorrectSupportedType() {
            assertEquals(TicketType.INCIDENT, incidentHandler.supports());
        }

        @Test
        @DisplayName("applies P3 as default priority")
        void appliesDefaultPriority() {
            Ticket ticket = new Ticket();
            ticket.setPriority(null);
            when(assignmentGroupRepository.findByName("L1")).thenReturn(Optional.empty());

            incidentHandler.applyDefaults(ticket);

            assertEquals(Priority.P3, ticket.getPriority());
        }

        @Test
        @DisplayName("applies L1 as default assignment group when none provided")
        void appliesDefaultL1Group() {
            Ticket ticket = new Ticket();
            AssignmentGroup l1Group = new AssignmentGroup();
            l1Group.setName("L1");
            when(assignmentGroupRepository.findByName("L1")).thenReturn(Optional.of(l1Group));

            incidentHandler.applyDefaults(ticket);

            assertSame(l1Group, ticket.getAssignmentGroup());
        }
    }

    @Nested
    class RequestHandlerTests {
        RequestHandler requestHandler = new RequestHandler();

        @Test
        @DisplayName("returns REQUEST as the correct supported TicketType")
        void returnsCorrectSupportedType() {
            assert(requestHandler.supports()).equals(TicketType.REQUEST);
        }

        @Test
        @DisplayName("applies P5 as default priority")
        void appliesDefaultPriority() {
            Ticket ticket = new Ticket();
            ticket.setPriority(null);

            requestHandler.applyDefaults(ticket);

            assert(ticket.getPriority()).equals(Priority.P5);
        }
    }
}
