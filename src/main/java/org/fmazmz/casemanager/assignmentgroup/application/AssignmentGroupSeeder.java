package org.fmazmz.casemanager.assignmentgroup.application;

import lombok.extern.slf4j.Slf4j;
import org.fmazmz.casemanager.assignmentgroup.domain.AssignmentGroup;
import org.fmazmz.casemanager.assignmentgroup.repository.AssignmentGroupRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Order(5)
public class AssignmentGroupSeeder implements CommandLineRunner {

    private final AssignmentGroupRepository assignmentGroupRepository;

    public AssignmentGroupSeeder(AssignmentGroupRepository assignmentGroupRepository) {
        this.assignmentGroupRepository = assignmentGroupRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        ensureGroup("L1", "Level 1 — first line support");
        ensureGroup("L2", "Level 2 — advanced troubleshooting");
        ensureGroup("L3", "Level 3 — engineering / vendor escalation");
    }

    private void ensureGroup(String name, String description) {
        if (assignmentGroupRepository.findByName(name).isPresent()) {
            return;
        }
        AssignmentGroup group = new AssignmentGroup();
        group.setName(name);
        group.setDescription(description);
        assignmentGroupRepository.save(group);
        log.info("Seeded assignment group {}", name);
    }
}
