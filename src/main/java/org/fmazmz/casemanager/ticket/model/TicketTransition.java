package org.fmazmz.casemanager.ticket.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fmazmz.casemanager.user.model.Permission;

import java.util.UUID;

@Entity
@Table(name = "ticket_transitions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"from_status", "to_status"})
})
@NoArgsConstructor
@Getter
@Setter
public class TicketTransition {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "from_status")
    private TicketStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "to_status")
    private TicketStatus toStatus;

    @ManyToOne(optional = false)
    @JoinColumn(name = "required_permission_id", nullable = false)
    private Permission requiredPermission;
}
