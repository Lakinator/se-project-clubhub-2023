package de.oth.seproject.clubhub.web;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.*;
import de.oth.seproject.clubhub.persistence.repository.*;
import org.springframework.format.annotation.DateTimeFormat;
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
    public String showGroupPage(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId, @RequestParam("intervalStart") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> intervalStart,
                                @RequestParam("intervalEnd") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> intervalEnd, Model model) {
        LocalDate currentIntervalStart = intervalStart.orElse(LocalDate.now().minusDays(7));
        LocalDate currentIntervalEnd = intervalEnd.orElse(LocalDate.now().plusDays(7));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        Optional<Role> optionalRole = roleRepository.findByUserAndGroup(userDetails.getUser(), group);

        List<GroupEvent> groupEvents = groupEventRepository.findAllByGroupAndEventDateBetweenOrderByEventStartDesc(group, currentIntervalStart, currentIntervalEnd);

        model.addAttribute("isTrainer", optionalRole.isPresent() && optionalRole.get().getRoleName().equals(RoleType.TRAINER));
        model.addAttribute("group", group);
        model.addAttribute("groupEvents", groupEvents);
        return "show-group-calendar";
    }

    @GetMapping("/group/{groupId}/events/add")
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

    @PostMapping("/group/{groupId}/event/create")
    public String createGroupEvent(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId, @Valid GroupEvent groupEvent, BindingResult result, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        if (result.hasErrors()) {
            List<Location> locations = locationRepository.findAll();
            model.addAttribute("group", group);
            model.addAttribute("eventTypes", EventType.values());
            model.addAttribute("locations", locations);
            return "add-group-event";
        }

        Optional<Role> roleInGroup = roleRepository.findByUserAndGroup(userDetails.getUser(), group);
        final boolean isTrainerInGroup = roleInGroup.isPresent() && roleInGroup.get().getRoleName().equals(RoleType.TRAINER);

        if (isTrainerInGroup) {
            groupEvent.setGroup(group);
            groupEvent.setUser(roleInGroup.get().getUser());

            groupEventRepository.save(groupEvent);
        }

        return "redirect:/group/" + groupId + "/calendar";
    }

    @GetMapping("/group/{groupId}/event/{eventId}/edit")
    public String editGroupEventPage(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId, @PathVariable("eventId") long eventId, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        GroupEvent groupEvent = groupEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group event Id:" + eventId));

        boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

        // user has to be a trainer of this group
        if (!isTrainerInGroup) {
            return "redirect:/group/" + groupId + "/calendar";
        }

        List<Location> locations = locationRepository.findAll();

        model.addAttribute("group", group);
        model.addAttribute("groupEvent", groupEvent);
        model.addAttribute("eventTypes", EventType.values());
        model.addAttribute("locations", locations);
        return "edit-group-event";
    }

    @PostMapping("/group/{groupId}/event/{eventId}/update")
    public String updateGroupEvent(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId, @PathVariable("eventId") long eventId, @Valid GroupEvent groupEvent,
                                   BindingResult result, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        if (result.hasErrors()) {
            List<Location> locations = locationRepository.findAll();
            model.addAttribute("group", group);
            model.addAttribute("eventTypes", EventType.values());
            model.addAttribute("locations", locations);
            return "edit-group-event";
        }

        boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

        // user has to be a trainer of this group
        if (!isTrainerInGroup) {
            return "redirect:/group/" + groupId + "/calendar";
        }

        Optional<GroupEvent> persistedGroupEvent = groupEventRepository.findById(eventId);

        persistedGroupEvent.ifPresent(e -> {
            e.setEventType(groupEvent.getEventType());
            e.setEventDate(groupEvent.getEventDate());
            e.setEventStart(groupEvent.getEventStart());
            e.setEventEnd(groupEvent.getEventEnd());
            e.setTitle(groupEvent.getTitle());
            e.setDescription(groupEvent.getDescription());
            e.setLocation(groupEvent.getLocation());

            groupEventRepository.save(e);
        });

        return "redirect:/group/" + groupId + "/calendar";
    }

    @GetMapping("/group/{groupId}/event/{eventId}/delete")
    public String deleteGroupEvent(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId, @PathVariable("eventId") long eventId, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        GroupEvent groupEvent = groupEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group event Id:" + eventId));

        boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

        // user has to be a trainer of this group
        if (isTrainerInGroup) {
            groupEventRepository.delete(groupEvent);
        }

        return "redirect:/group/" + groupId + "/calendar";
    }

}
