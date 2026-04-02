package org.fmazmz.casemanager.ticket.api;

import org.fmazmz.casemanager.ticket.audit.LogQueryFacade;
import org.fmazmz.casemanager.ticket.dto.LogJournal;
import org.fmazmz.casemanager.user.auth.CurrentUser;
import org.fmazmz.casemanager.user.model.User;
import org.fmazmz.casemanager.utils.ApiResponseWrapper;
import org.fmazmz.casemanager.utils.PagedResult;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class AuditLogController implements AuditLogApi {

    private final LogQueryFacade lcf;

    public AuditLogController(LogQueryFacade lcf) {
        this.lcf = lcf;
    }

    @GetMapping
    @Override
    public ResponseEntity<ApiResponseWrapper<PagedResult<LogJournal>>> getAll(
            @CurrentUser User actor,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(new ApiResponseWrapper<>(lcf.getAllTicketLogJournals(actor.getId(), pageable)));
    }

    @GetMapping("{ticketId}")
    @Override
    public ResponseEntity<ApiResponseWrapper<PagedResult<LogJournal>>> getByTicketId(
            @CurrentUser User actor,
            @PathVariable UUID ticketId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(new ApiResponseWrapper<>(lcf.getAllLogJournalsByTicketId(actor.getId(), ticketId, pageable)));
    }
}
