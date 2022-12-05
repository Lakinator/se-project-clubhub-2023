package de.oth.seproject.clubhub.web.group;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.*;
import de.oth.seproject.clubhub.persistence.repository.GroupEventRepository;
import de.oth.seproject.clubhub.persistence.repository.GroupRepository;
import de.oth.seproject.clubhub.persistence.repository.LocationRepository;
import de.oth.seproject.clubhub.persistence.repository.RoleRepository;
import de.oth.seproject.clubhub.web.service.NavigationService;
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
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Controller
public class GroupEventController {

    private final NavigationService navigationService;

    private final GroupEventRepository groupEventRepository;

    private final GroupRepository groupRepository;

    private final RoleRepository roleRepository;

    private final LocationRepository locationRepository;

    public GroupEventController(NavigationService navigationService, GroupEventRepository groupEventRepository, GroupRepository groupRepository, RoleRepository roleRepository, LocationRepository locationRepository) {
        this.navigationService = navigationService;
        this.groupEventRepository = groupEventRepository;
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.locationRepository = locationRepository;
    }

    @GetMapping("/group/{groupId}/calendar")
    public String showGroupPage(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId,
                                @RequestParam("year") Optional<Integer> year,
                                @RequestParam("month") Optional<Month> month, Model model) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        // calendar pagination
        int selectedYear = year.orElse(LocalDate.now().getYear());
        Month selectedMonth = month.orElse(LocalDate.now().getMonth());

        LocalDate selectedIntervalStart = LocalDate.now()
                .withYear(selectedYear)
                .withMonth(selectedMonth.getValue())
                .with(TemporalAdjusters.firstDayOfMonth());
        LocalDate selectedIntervalEnd = selectedIntervalStart.with(TemporalAdjusters.lastDayOfMonth());

        List<GroupEvent> groupEvents = groupEventRepository.findAllByGroupAndEventDateBetweenOrderByEventStartAsc(group, selectedIntervalStart, selectedIntervalEnd);

        model.addAttribute("groupEvents", groupEvents);
        model.addAttribute("lastMonth", selectedMonth.minus(1).getValue());
        model.addAttribute("lastYear", selectedIntervalStart.minusYears(1).getYear());
        model.addAttribute("nextMonth", selectedMonth.plus(1).getValue());
        model.addAttribute("nextYear", selectedIntervalStart.plusYears(1).getYear());
        model.addAttribute("currentMonth", LocalDate.now().getMonth().getValue());
        model.addAttribute("selectedIntervalStart", selectedIntervalStart);
        model.addAttribute("selectedIntervalEnd", selectedIntervalEnd);

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId(), group);
        return "show-group-calendar";
    }

    @GetMapping("/group/{groupId}/events/add")
    public String addGroupEventPage(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        final boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

        // user has to be a trainer of this group
        if (!isTrainerInGroup) {
            return "redirect:/group/" + groupId + "/calendar";
        }

        List<Location> locations = locationRepository.findAll();

        GroupEvent groupEvent = new GroupEvent();
        model.addAttribute("groupEvent", groupEvent);
        model.addAttribute("eventTypes", EventType.values());
        model.addAttribute("locations", locations);

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId(), groupId);
        return "add-group-event";
    }

    @PostMapping("/group/{groupId}/event/create")
    public String createGroupEvent(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId, @Valid GroupEvent groupEvent, BindingResult result, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        Optional<Role> roleInGroup = roleRepository.findByUserAndGroup(userDetails.getUser(), group);
        final boolean isTrainerInGroup = roleInGroup.isPresent() && roleInGroup.get().getRoleName().equals(RoleType.TRAINER);

        // user has to be a trainer of this group
        if (!isTrainerInGroup) {
            return "redirect:/group/" + groupId + "/calendar";
        }

        if (result.hasErrors()) {
            List<Location> locations = locationRepository.findAll();
            model.addAttribute("eventTypes", EventType.values());
            model.addAttribute("locations", locations);
            navigationService.addNavigationAttributes(model, userDetails.getUser().getId(), group);
            return "add-group-event";
        }

        groupEvent.setGroup(group);
        groupEvent.setUser(roleInGroup.get().getUser());

        groupEventRepository.save(groupEvent);

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

        model.addAttribute("groupEventId", eventId);
        model.addAttribute("groupEvent", groupEvent);
        model.addAttribute("eventTypes", EventType.values());
        model.addAttribute("locations", locations);

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId(), group);
        return "edit-group-event";
    }

    @PostMapping("/group/{groupId}/event/{eventId}/update")
    public String updateGroupEvent(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId, @PathVariable("eventId") long eventId, @Valid GroupEvent groupEvent,
                                   BindingResult result, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

        // user has to be a trainer of this group
        if (!isTrainerInGroup) {
            return "redirect:/group/" + groupId + "/calendar";
        }

        if (result.hasErrors()) {
            GroupEvent persistedGroupEvent = groupEventRepository.findById(eventId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid group event Id:" + eventId));

            List<Location> locations = locationRepository.findAll();
            model.addAttribute("groupEventId", eventId);
            model.addAttribute("eventTypes", EventType.values());
            model.addAttribute("locations", locations);
            navigationService.addNavigationAttributes(model, userDetails.getUser().getId(), group);
            return "edit-group-event";
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
