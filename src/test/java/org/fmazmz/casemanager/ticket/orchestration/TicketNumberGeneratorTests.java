package org.fmazmz.casemanager.ticket.orchestration;

import org.fmazmz.casemanager.ticket.application.TicketNumberGenerator;
import org.fmazmz.casemanager.ticket.domain.TicketType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketNumberGeneratorTests {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private TicketNumberGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new TicketNumberGenerator(jdbcTemplate);
    }

    @Nested
    @DisplayName("formatNumber")
    class FormatNumberTests {

        @Test
        @DisplayName("formats number with correct prefix and padding")
        void formatsWithPrefixAndPadding() {
            String result = generator.formatNumber("INC", 1L);
            assertEquals("INC0000001", result);
        }

        @Test
        @DisplayName("pads to 7 digits for small numbers")
        void padsSmallNumbers() {
            String result = generator.formatNumber("REQ", 42L);
            assertEquals("REQ0000042", result);
        }

        @Test
        @DisplayName("handles large numbers without truncation")
        void handlesLargeNumbers() {
            String result = generator.formatNumber("INC", 9999999L);
            assertEquals("INC9999999", result);
        }

        @Test
        @DisplayName("handles numbers exceeding 7 digits")
        void handlesOverflow() {
            String result = generator.formatNumber("INC", 12345678L);
            assertEquals("INC12345678", result);
        }

        @ParameterizedTest
        @CsvSource({
                "INC, 1, INC0000001",
                "INC, 999, INC0000999",
                "REQ, 1, REQ0000001",
                "REQ, 123456, REQ0123456"
        })
        @DisplayName("formats various prefix and number combinations correctly")
        void formatsVariousCombinations(String prefix, Long sequence, String expected) {
            String result = generator.formatNumber(prefix, sequence);
            assertEquals(expected, result);
        }
    }

    @Nested
    @DisplayName("getSequenceName")
    class GetSequenceNameTests {

        @Test
        @DisplayName("returns correct sequence name for INCIDENT")
        void returnsIncidentSequenceName() {
            String result = generator.getSequenceName(TicketType.INCIDENT);
            assertEquals("ticket_seq_incident", result);
        }

        @Test
        @DisplayName("returns correct sequence name for REQUEST")
        void returnsRequestSequenceName() {
            String result = generator.getSequenceName(TicketType.REQUEST);
            assertEquals("ticket_seq_request", result);
        }
    }

    @Nested
    @DisplayName("generate")
    class GenerateTests {

        @Test
        @DisplayName("generates ticket number for INCIDENT type")
        void generatesIncidentNumber() {
            when(jdbcTemplate.queryForObject(
                    eq("SELECT nextval('ticket_seq_incident')"),
                    eq(Long.class)))
                    .thenReturn(1L);

            String result = generator.generate(TicketType.INCIDENT);

            assertEquals("INC0000001", result);
        }

        @Test
        @DisplayName("generates ticket number for REQUEST type")
        void generatesRequestNumber() {
            when(jdbcTemplate.queryForObject(
                    eq("SELECT nextval('ticket_seq_request')"),
                    eq(Long.class)))
                    .thenReturn(5L);

            String result = generator.generate(TicketType.REQUEST);

            assertEquals("REQ0000005", result);
        }

        @Test
        @DisplayName("throws exception when sequence returns null")
        void throwsOnNullSequence() {
            when(jdbcTemplate.queryForObject(anyString(), eq(Long.class)))
                    .thenReturn(null);

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> generator.generate(TicketType.INCIDENT)
            );

            assertTrue(ex.getMessage().contains("ticket_seq_incident"));
        }
    }
}
