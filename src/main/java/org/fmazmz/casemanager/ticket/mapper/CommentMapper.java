package org.fmazmz.casemanager.ticket.mapper;

import org.fmazmz.casemanager.ticket.dto.TicketCommentRequest;
import org.fmazmz.casemanager.ticket.model.Comment;
import org.fmazmz.casemanager.ticket.model.Ticket;
import org.fmazmz.casemanager.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public static Comment toComment(TicketCommentRequest dto, User actor, Ticket ticket) {
        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setUser(actor);
        comment.setVisibility(dto.visibility());
        comment.setMessage(dto.comment());

        return comment;
    }
}
