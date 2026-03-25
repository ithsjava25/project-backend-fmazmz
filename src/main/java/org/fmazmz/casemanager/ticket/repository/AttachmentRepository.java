package org.fmazmz.casemanager.ticket.repository;

import org.fmazmz.casemanager.ticket.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findAllByTicketId(UUID ticketId);
    Optional<Attachment> findByStorageKey(String storageKey);
}

