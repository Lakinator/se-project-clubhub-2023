package de.oth.seproject.clubhub.web;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.Group;
import de.oth.seproject.clubhub.persistence.model.GroupEvent;
import de.oth.seproject.clubhub.persistence.model.Role;
import de.oth.seproject.clubhub.persistence.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class GroupEventController {

    private final GroupEventRepository groupEventRepository;

    private final GroupRepository groupRepository;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final LocationRepository locationRepository;

    public GroupEventController(GroupEventRepository groupEventRepository, GroupRepository groupRepository, RoleRepository roleRepository, UserRepository userRepository, LocationRepository locationRepository) {
        this.groupEventRepository = groupEventRepository;
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    @GetMapping("/group/{groupId}/calendar")
    public String showGroupPage(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId, @RequestParam("intervalStart") Optional<LocalDate> intervalStart,
                                @RequestParam("intervalEnd") Optional<LocalDate> intervalEnd, Model model) {
        LocalDate currentIntervalStart = intervalStart.orElse(LocalDate.now().plusDays(7));
        LocalDate currentIntervalEnd = intervalEnd.orElse(LocalDate.now());

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        List<GroupEvent> groupEvents = groupEventRepository.findAllByGroupAndEventDateBetween(group, currentIntervalStart, currentIntervalEnd);

        model.addAttribute("group", group);
        model.addAttribute("groupEvents", groupEvents);
        return "show-group-calendar";
    }

}
