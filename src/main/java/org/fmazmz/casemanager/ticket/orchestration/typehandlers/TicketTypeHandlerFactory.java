package org.fmazmz.casemanager.ticket.orchestration.typehandlers;

import lombok.extern.slf4j.Slf4j;
import org.fmazmz.casemanager.ticket.model.TicketType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TicketTypeHandlerFactory {
    private final Map<TicketType, TicketTypeHandler> handlers;

    public TicketTypeHandlerFactory(List<TicketTypeHandler> handlerList) {
        this.handlers = handlerList.stream()
            .collect(Collectors.toMap(TicketTypeHandler::supports, h -> h));
    }

    public TicketTypeHandler resolve(TicketType type) {
        log.info("Resolving handler for type {}", type);

        TicketTypeHandler handler = handlers.get(type);
        if (handler == null) {
            log.warn("Could not resolve a handler for TicketType: {}", type);
            throw new IllegalStateException("No handler configured for type: " + type);
        }
        return handler;
    }
}