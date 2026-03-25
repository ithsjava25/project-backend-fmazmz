package org.fmazmz.casemanager.ticket;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fmazmz.casemanager.user.model.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@NoArgsConstructor
@Getter
@Setter
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketAction action;

    @Column(name = "field_name")
    private String field;

    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Column(columnDefinition = "TEXT")
    private String newValue;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
