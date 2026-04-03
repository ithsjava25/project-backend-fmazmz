package org.fmazmz.casemanager.ticket.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fmazmz.casemanager.ticket.domain.TicketType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketNumberGenerator {

    private static final int NUMBER_PADDING = 7;

    private final JdbcTemplate jdbcTemplate;

    public String generate(TicketType type) {
        String sequenceName = getSequenceName(type);
        Long nextVal = jdbcTemplate.queryForObject(
                "SELECT nextval('" + sequenceName + "')", Long.class);

        if (nextVal == null) {
            log.error("Failed to get next sequence value for type: {}", type);
            throw new IllegalStateException("Sequence returned null for: " + sequenceName);
        }

        String number = formatNumber(type.getLabel(), nextVal);
        log.info("Generated ticket number: {} for type: {}", number, type);
        return number;
    }

    String getSequenceName(TicketType type) {
        return "ticket_seq_" + type.name().toLowerCase();
    }

    String formatNumber(String prefix, Long sequence) {
        return String.format("%s%0" + NUMBER_PADDING + "d", prefix, sequence);
    }
}
