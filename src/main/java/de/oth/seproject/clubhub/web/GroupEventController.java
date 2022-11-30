package de.oth.seproject.clubhub.web;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.*;
import de.oth.seproject.clubhub.persistence.repository.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
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
        LocalDate currentIntervalStart = intervalStart.orElse(LocalDate.now().minusDays(7));
        LocalDate currentIntervalEnd = intervalEnd.orElse(LocalDate.now().plusDays(7));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        List<GroupEvent> groupEvents = groupEventRepository.findAllByGroupAndEventDateBetweenOrderByEventStartDesc(group, currentIntervalStart, currentIntervalEnd);

        model.addAttribute("group", group);
        model.addAttribute("groupEvents", groupEvents);
        return "show-group-calendar";
    }

    @GetMapping("/group/{groupId}/add-event")
    public String addGroupEventPage(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        List<Location> locations = locationRepository.findAll();

        GroupEvent groupEvent = new GroupEvent();
        model.addAttribute("groupEvent", groupEvent);
        model.addAttribute("group", group);
        model.addAttribute("eventTypes", EventType.values());
        model.addAttribute("locations", locations);
        return "add-group-event";
    }

    @PostMapping("/group/{groupId}/create-event")
    public String createGroupEvent(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId, @Valid GroupEvent groupEvent, BindingResult result, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        // TODO: validation

        Optional<Role> roleInGroup = roleRepository.findByUserAndGroup(userDetails.getUser(), group);
        final boolean isTrainerInGroup = roleInGroup.isPresent() && roleInGroup.get().getAuthority().equals(RoleType.TRAINER.name());

        if (isTrainerInGroup) {
            groupEvent.setGroup(group);
            groupEvent.setUser(roleInGroup.get().getUser());

            groupEventRepository.save(groupEvent);
        }


        return "redirect:/group/" + groupId + "/calendar";
    }

}
