package org.fmazmz.casemanager.assignmentgroup.repository;

import org.fmazmz.casemanager.assignmentgroup.domain.AssignmentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssignmentGroupRepository extends JpaRepository<AssignmentGroup, UUID> {

    Optional<AssignmentGroup> findByName(String name);

    @Query("""
            select case when count(m) > 0 then true else false end
            from AssignmentGroup g join g.members m
            where g.id = :groupId and m.id = :userId
            """)
    boolean isUserMember(@Param("groupId") UUID groupId, @Param("userId") UUID userId);
}
