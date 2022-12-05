package de.oth.seproject.clubhub.web.service;

import de.oth.seproject.clubhub.persistence.model.Group;
import de.oth.seproject.clubhub.persistence.model.Role;
import de.oth.seproject.clubhub.persistence.model.RoleType;
import de.oth.seproject.clubhub.persistence.model.User;
import de.oth.seproject.clubhub.persistence.repository.GroupRepository;
import de.oth.seproject.clubhub.persistence.repository.RoleRepository;
import de.oth.seproject.clubhub.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;

/**
 * Defines attributes used by the main nav fragment.
 * The needed attributes need to be passed from the child view to its parent using th:with.
 */
@Service
public class NavigationService {

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    private final RoleRepository roleRepository;

    public NavigationService(UserRepository userRepository, GroupRepository groupRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
    }

    public void addNavigationAttributes(Model model, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id:" + userId));

        this.addNavigationAttributes(model, user);
    }

    private void addNavigationAttributes(Model model, User user) {
        model.addAttribute("activeUser", user);
    }

    public void addNavigationAttributes(Model model, long userId, long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group id:" + groupId));

        this.addNavigationAttributes(model, userId, group);
    }

    public void addNavigationAttributes(Model model, long userId, Group group) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id:" + userId));

        this.addNavigationAttributes(model, user, group);
    }

    private void addNavigationAttributes(Model model, User user, Group group) {
        this.addNavigationAttributes(model, user);

        Optional<Role> roleInGroup = roleRepository.findByUserAndGroup(user, group);

        model.addAttribute("activeGroup", group);
        model.addAttribute("roleInActiveGroup", roleInGroup.orElse(null));
        model.addAttribute("isTrainerInActiveGroup", roleInGroup.isPresent() && roleInGroup.get().getRoleName().equals(RoleType.TRAINER));
    }

}
