package org.fmazmz.casemanager.ticket.api;

import org.fmazmz.casemanager.ticket.audit.LogQueryFacade;
import org.fmazmz.casemanager.ticket.dto.LogJournal;
import org.fmazmz.casemanager.user.auth.AuthenticatedUserResolver;
import org.fmazmz.casemanager.user.model.User;
import org.fmazmz.casemanager.utils.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class AuditLogController implements AuditLogApi {
    private final LogQueryFacade lcf;
    private final AuthenticatedUserResolver authenticatedUserResolver;

    public AuditLogController(LogQueryFacade lcf, AuthenticatedUserResolver authenticatedUserResolver) {
        this.lcf = lcf;
        this.authenticatedUserResolver = authenticatedUserResolver;
    }

    @GetMapping
    @Override
    public ResponseEntity<ApiResponseWrapper<List<LogJournal>>> getAll(OAuth2AuthenticationToken authentication) {
        User actor = authenticatedUserResolver.requireUser(authentication);

        return ResponseEntity.ok(new ApiResponseWrapper<>(lcf.getAllTicketLogJournals(actor.getId())));
    }

    @GetMapping("{ticketId}")
    @Override
    public ResponseEntity<ApiResponseWrapper<List<LogJournal>>> getByTicketId(
            OAuth2AuthenticationToken authentication,
            @PathVariable UUID ticketId) {
        User actor = authenticatedUserResolver.requireUser(authentication);

        return ResponseEntity.ok(new ApiResponseWrapper<>(lcf.getAllLogJournalsByTicketId(actor.getId(), ticketId)));
    }
}
