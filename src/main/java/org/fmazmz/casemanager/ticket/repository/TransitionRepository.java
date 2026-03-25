package org.fmazmz.casemanager.ticket.repository;

import org.fmazmz.casemanager.ticket.model.TicketStatus;
import org.fmazmz.casemanager.ticket.model.TicketTransition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransitionRepository extends JpaRepository<TicketTransition, UUID> {
    Optional<TicketTransition> findByFromStatusAndToStatus(TicketStatus fromStatus, TicketStatus toStatus);
}
