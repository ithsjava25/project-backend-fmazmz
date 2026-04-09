package org.fmazmz.casemanager.ticket.repository;

import org.fmazmz.casemanager.ticket.domain.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    Optional<Ticket> findByNumber(String number);

    Page<Ticket> findByRequester_Id(UUID requesterId, Pageable pageable);

    Page<Ticket> findByAssignee_Id(UUID assigneeId, Pageable pageable);

    Page<Ticket> findByAssignmentGroup_Id(UUID assignmentGroupId, Pageable pageable);
}
