package org.fmazmz.casemanager.user.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class UserRoleId implements Serializable {
    private UUID userId;
    private UUID roleId;
}