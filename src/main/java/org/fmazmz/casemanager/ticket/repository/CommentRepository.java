package org.fmazmz.casemanager.ticket.repository;

import org.fmazmz.casemanager.ticket.model.Comment;
import org.fmazmz.casemanager.ticket.model.CommentVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findAllByUserId(UUID userId);

    List<Comment> findAllByTicketId(UUID ticketId);
    List<Comment> findByTicketIdAndVisiblity(UUID ticketId, CommentVisibility visibility);
}
