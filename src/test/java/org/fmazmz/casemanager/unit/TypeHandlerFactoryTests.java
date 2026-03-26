package org.fmazmz.casemanager.unit;

import org.fmazmz.casemanager.ticket.model.TicketType;
import org.fmazmz.casemanager.ticket.orchestration.typehandlers.TypeHandler;
import org.fmazmz.casemanager.ticket.orchestration.typehandlers.TypeHandlerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

    @Nested
    @DisplayName("TypeHandlerFactory#resolve")
    class ResolveHandlerFromType {
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
}
