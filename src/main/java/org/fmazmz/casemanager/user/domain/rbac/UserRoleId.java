package org.fmazmz.casemanager.user.domain.rbac;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class UserRoleId implements Serializable {
    private UUID userId;
    private UUID roleId;
}