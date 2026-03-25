package org.fmazmz.casemanager.ticket.repository;

import org.fmazmz.casemanager.ticket.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findAllByUser_Id(UUID userId);

    List<Comment> findAllByTicket_Id(UUID ticketId);
}
