package org.fmazmz.casemanager.ticket.workflow;

import org.fmazmz.casemanager.ticket.model.TicketAction;
import org.fmazmz.casemanager.ticket.model.TicketStatus;
import org.fmazmz.casemanager.ticket.model.TicketTransition;
import org.fmazmz.casemanager.ticket.repository.TransitionRepository;
import org.fmazmz.casemanager.user.model.rbac.Permission;
import org.fmazmz.casemanager.user.repository.PermissionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(20)
public class TicketTransitionSeeder implements CommandLineRunner {

    private final TransitionRepository transitionRepository;
    private final PermissionRepository permissionRepository;

    public TicketTransitionSeeder(
            TransitionRepository transitionRepository,
            PermissionRepository permissionRepository
    ) {
        this.transitionRepository = transitionRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        for (TransitionSeed seed : defaultTransitions()) {
            if (transitionRepository.existsByFromStatusAndToStatus(seed.from(), seed.to())) {
                continue;
            }

            String permissionName = seed.requiredAction().permissionName();
            Permission permission = permissionRepository
                    .findByName(permissionName)
                    .orElseThrow(() -> new IllegalStateException(
                            "Missing permission for transition seed (run RbacSeeder first): " + permissionName
                    ));

            TicketTransition transition = new TicketTransition();
            transition.setFromStatus(seed.from());
            transition.setToStatus(seed.to());
            transition.setRequiredPermission(permission);
            transitionRepository.save(transition);
        }
    }

    private static List<TransitionSeed> defaultTransitions() {
        List<TransitionSeed> transitions = new ArrayList<>();
        transitions.add(new TransitionSeed(TicketStatus.OPEN, TicketStatus.ASSIGNED, TicketAction.ASSIGN));
        transitions.add(new TransitionSeed(TicketStatus.OPEN, TicketStatus.WORK_IN_PROGRESS, TicketAction.CHANGE_STATUS));
        transitions.add(new TransitionSeed(TicketStatus.ASSIGNED, TicketStatus.WORK_IN_PROGRESS, TicketAction.CHANGE_STATUS));
        transitions.add(new TransitionSeed(TicketStatus.WORK_IN_PROGRESS, TicketStatus.AWAITING_USER_INFO, TicketAction.CHANGE_STATUS));
        transitions.add(new TransitionSeed(TicketStatus.AWAITING_USER_INFO, TicketStatus.WORK_IN_PROGRESS, TicketAction.CHANGE_STATUS));
        transitions.add(new TransitionSeed(TicketStatus.WORK_IN_PROGRESS, TicketStatus.RESOLVED, TicketAction.RESOLVE));
        transitions.add(new TransitionSeed(TicketStatus.RESOLVED, TicketStatus.CLOSED, TicketAction.CHANGE_STATUS));
        transitions.add(new TransitionSeed(TicketStatus.RESOLVED, TicketStatus.WORK_IN_PROGRESS, TicketAction.REOPEN));
        return transitions;
    }

    private record TransitionSeed(TicketStatus from, TicketStatus to, TicketAction requiredAction) {
    }
}
