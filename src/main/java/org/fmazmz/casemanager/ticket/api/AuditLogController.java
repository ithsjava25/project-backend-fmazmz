package org.fmazmz.casemanager.ticket.api;

import org.fmazmz.casemanager.ticket.audit.LogQueryFacade;
import org.fmazmz.casemanager.ticket.dto.LogJournal;
import org.fmazmz.casemanager.user.auth.CurrentUser;
import org.fmazmz.casemanager.user.model.User;
import org.fmazmz.casemanager.utils.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class AuditLogController implements AuditLogApi {

    private final LogQueryFacade lcf;

    public AuditLogController(LogQueryFacade lcf) {
        this.lcf = lcf;
    }

    @GetMapping
    @Override
    public ResponseEntity<ApiResponseWrapper<List<LogJournal>>> getAll(@CurrentUser User actor) {
        return ResponseEntity.ok(new ApiResponseWrapper<>(lcf.getAllTicketLogJournals(actor.getId())));
    }

    @GetMapping("{ticketId}")
    @Override
    public ResponseEntity<ApiResponseWrapper<List<LogJournal>>> getByTicketId(
            @CurrentUser User actor,
            @PathVariable UUID ticketId) {

        return ResponseEntity.ok(new ApiResponseWrapper<>(lcf.getAllLogJournalsByTicketId(actor.getId(), ticketId)));
    }
}
