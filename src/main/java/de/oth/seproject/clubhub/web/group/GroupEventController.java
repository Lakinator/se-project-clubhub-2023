package de.oth.seproject.clubhub.web.group;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.*;
import de.oth.seproject.clubhub.persistence.repository.*;
import de.oth.seproject.clubhub.web.dto.EventExtraDTO;
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
import java.time.LocalTime;
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
    
    private final GroupEventAttendanceRepository groupEventAttendanceRepository;

    public GroupEventController(NavigationService navigationService, GroupEventRepository groupEventRepository, GroupRepository groupRepository, RoleRepository roleRepository, LocationRepository locationRepository, GroupEventAttendanceRepository groupEventAttendanceRepository) {
        this.navigationService = navigationService;
        this.groupEventRepository = groupEventRepository;
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.locationRepository = locationRepository;
        this.groupEventAttendanceRepository = groupEventAttendanceRepository;
    }

    /**
     * Shows the calendar for the given group
     */
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

        List<GroupEvent> groupEvents = groupEventRepository.findAllByGroupAndEventDateBetweenOrderByEventDateAscEventStartAsc(group, selectedIntervalStart, selectedIntervalEnd);

        model.addAttribute("groupEvents", groupEvents);
        model.addAttribute("lastMonth", selectedMonth.minus(1).getValue());
        model.addAttribute("nextMonth", selectedMonth.plus(1).getValue());
        model.addAttribute("currentMonth", LocalDate.now().getMonth().getValue());
        model.addAttribute("lastYear", selectedIntervalStart.minusMonths(1).getYear());
        model.addAttribute("nextYear", selectedIntervalStart.plusMonths(1).getYear());
        model.addAttribute("selectedIntervalStart", selectedIntervalStart);
        model.addAttribute("selectedIntervalEnd", selectedIntervalEnd);

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId(), group);
        return "show-group-calendar";
    }

    /**
     * Show the page to add a new event, which can only be added by a trainer of the given group.
     */
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

        model.addAttribute("eventExtraDTO", new EventExtraDTO());

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId(), groupId);
        return "add-group-event";
    }

    /**
     * Creates a new event in the database with the given data and validates it.
     */
    @PostMapping("/group/{groupId}/event/create")
    public String createGroupEvent(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId, @Valid GroupEvent groupEvent, BindingResult result, EventExtraDTO eventExtraDTO, Model model) {
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
            model.addAttribute("eventExtraDTO", new EventExtraDTO());
            navigationService.addNavigationAttributes(model, userDetails.getUser().getId(), group);
            return "add-group-event";
        }

        groupEvent.setGroup(group);
        groupEvent.setUser(roleInGroup.get().getUser());
        groupEvent.setTeamIsFinal(false);

        if (eventExtraDTO.getWholeDay()) {
            groupEvent.setEventStart(LocalTime.MIN);
            groupEvent.setEventEnd(LocalTime.MAX);
        } else {
            if (groupEvent.getEventStart() == null) {
                groupEvent.setEventStart(LocalTime.MIN);
            }
            if (groupEvent.getEventEnd() == null) {
                groupEvent.setEventEnd(LocalTime.MAX);
            }
        }

        List<Role> roles = roleRepository.findAllByGroup(group);
        for (Role role : roles) {
            GroupEventAttendance attendance = new GroupEventAttendance();
            attendance.setUser(role.getUser());
            attendance.setGroupEvent(groupEvent);
            attendance.setStatus(AttendanceStatus.UNCLEAR);
            attendance.setIsNotRemoved(true);
            
            groupEventAttendanceRepository.save(attendance);
        }
        
        groupEventRepository.save(groupEvent);        

        return "redirect:/group/" + groupId + "/calendar";
    }

    /**
     * Shows the page for editing a given event.
     */
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

        model.addAttribute("eventExtraDTO", new EventExtraDTO(groupEvent.isWholeDay()));

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId(), group);
        return "edit-group-event";
    }

    /**
     * Persists/Updates the given event with validation.
     */
    @PostMapping("/group/{groupId}/event/{eventId}/update")
    public String updateGroupEvent(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId, @PathVariable("eventId") long eventId, @Valid GroupEvent groupEvent,
                                   BindingResult result,
                                   EventExtraDTO eventExtraDTO, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

        // user has to be a trainer of this group
        if (!isTrainerInGroup) {
            return "redirect:/group/" + groupId + "/calendar";
        }

        if (result.hasErrors()) {
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

            if (eventExtraDTO.getWholeDay()) {
                e.setEventStart(LocalTime.MIN);
                e.setEventEnd(LocalTime.MAX);
            } else {
                if (groupEvent.getEventStart() == null) {
                    e.setEventStart(LocalTime.MIN);
                } else {
                    e.setEventStart(groupEvent.getEventStart());
                }

                if (groupEvent.getEventEnd() == null) {
                    e.setEventEnd(LocalTime.MAX);
                } else {
                    e.setEventEnd(groupEvent.getEventEnd());
                }
            }

            e.setTitle(groupEvent.getTitle());
            e.setDescription(groupEvent.getDescription());
            e.setLocation(groupEvent.getLocation());

            groupEventRepository.save(e);
        });

        return "redirect:/group/" + groupId + "/calendar";
    }

    /**
     * Deletes a given event if the user is a trainer of the group.
     */
    @GetMapping("/group/{groupId}/event/{eventId}/delete")
    public String deleteGroupEvent(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId, @PathVariable("eventId") long eventId, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        GroupEvent groupEvent = groupEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group event Id:" + eventId));

        boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

        // user has to be a trainer of this group
        if (isTrainerInGroup) {
            List<GroupEventAttendance> persistedAttendances = groupEventAttendanceRepository.findAllByGroupEvent(groupEvent);
            for (GroupEventAttendance attendance : persistedAttendances) {
                groupEventAttendanceRepository.delete(attendance);
            }
        	
            groupEventRepository.delete(groupEvent);
        }

        return "redirect:/group/" + groupId + "/calendar";
    }
    
    /**
     * Adds required attributes to the model and shows the show attendance html page
     *
     * @param userDetails User details of the current user
     * @param groupId Received from the path
     * @param eventId Received from the path
     * @param model Model for the requested html page
     * @return Name of the show attendance html page
     */
    @GetMapping("/group/{groupId}/event/{eventId}/attendance")
    public String showEventAttendance(@AuthenticationPrincipal ClubUserDetails userDetails,
                                      @PathVariable("groupId") long groupId, @PathVariable("eventId") long eventId, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));
        
        GroupEvent activeEvent = groupEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event Id:" + eventId));
    	
        List<GroupEventAttendance> attendances = groupEventAttendanceRepository.findAllByGroupEvent(activeEvent);

        model.addAttribute("activeEvent", activeEvent);
        model.addAttribute("attendances", attendances);

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId(), group);
        return "show-attendance";
    }

    /**
     * Changes the attendance status of the current user and redirects to /group/{groupId}/event/{eventId}/attendance
     *
     * @param userDetails User details of the current user
     * @param groupId Received from the path
     * @param eventId Received from the path
     * @param type Received as a request parameter
     * @return Name of the redirected show attendance html page
     */
    @GetMapping("/group/{groupId}/event/{eventId}/attendance/update")
    public String updateEventAttendance(@AuthenticationPrincipal ClubUserDetails userDetails,
                                        @PathVariable("groupId") long groupId, @PathVariable("eventId") long eventId,
                                        @RequestParam("type") Optional<Integer> type) {
        GroupEvent groupEvent = groupEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event Id:" + eventId));
        
        if (groupEvent.isTeamIsFinal()) {
            return "redirect:/group/" + groupId + "/event/" + eventId + "/attendance";
        }
    	
        type.ifPresent(status -> {
            Optional<GroupEventAttendance> persistedAttendance = groupEventAttendanceRepository.findByUserAndGroupEvent(userDetails.getUser(), groupEvent);
        
            persistedAttendance.ifPresent(attendance -> {
                if (status == 1) attendance.setStatus(AttendanceStatus.IN);
                else attendance.setStatus(AttendanceStatus.OUT);

                groupEventAttendanceRepository.save(attendance);
            });
        });
        return "redirect:/group/" + groupId + "/event/" + eventId + "/attendance";
    }
    
    /**
     * Marks an user from a group in an event as removed and redirects to /group/{groupId}/event/{eventId}/attendance
     *
     * @param userDetails User details of the current user
     * @param groupId Received from the path
     * @param eventId Received from the path
     * @param attendanceId Received from the path
     * @return Name of the redirected show attendance html page
     */
    @GetMapping("/group/{groupId}/event/{eventId}/attendance/{attendanceId}/remove")
    public String removeEventAttendance(@AuthenticationPrincipal ClubUserDetails userDetails,
                                        @PathVariable("groupId") long groupId, @PathVariable("eventId") long eventId, 
                                        @PathVariable("attendanceId") long attendanceId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));
        
        GroupEvent groupEvent = groupEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event Id:" + eventId));

        boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

        if (!isTrainerInGroup || groupEvent.isTeamIsFinal()) {
            return "redirect:/group/" + groupId + "/event/" + eventId + "/attendance";
        }
    	
        Optional<GroupEventAttendance> persistedAttendance = groupEventAttendanceRepository.findById(attendanceId);
    	
        persistedAttendance.ifPresent(attendance -> {
            attendance.setIsNotRemoved(false);

            groupEventAttendanceRepository.save(attendance);
        });

        return "redirect:/group/" + groupId + "/event/" + eventId + "/attendance";
    }
    
    /**
     * Removes removed mark from all users and redirects to /group/{groupId}/event/{eventId}/attendance
     *
     * @param userDetails User details of the current user
     * @param groupId Received from the path
     * @param eventId Received from the path
     * @return Name of the redirected show attendance html page
     */
    @GetMapping("/group/{groupId}/event/{eventId}/attendance/reset")
    public String resetEventAttendance(@AuthenticationPrincipal ClubUserDetails userDetails,
                                       @PathVariable("groupId") long groupId, @PathVariable("eventId") long eventId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));
        
        GroupEvent groupEvent = groupEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event Id:" + eventId));

        boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

        if (!isTrainerInGroup || groupEvent.isTeamIsFinal()) {
            return "redirect:/group/" + groupId + "/event/" + eventId + "/attendance";
        }

        List<GroupEventAttendance> attendances = groupEventAttendanceRepository.findAllByGroupEvent(groupEvent);
        for (GroupEventAttendance attendance : attendances) {
            attendance.setIsNotRemoved(true);

            groupEventAttendanceRepository.save(attendance);    		
        }

        return "redirect:/group/" + groupId + "/event/" + eventId + "/attendance";
    }
    
    /**
     * Changes the attendance of an event to final and deletes marked users and redirects to /group/{groupId}/calendar
     *
     * @param userDetails User details of the current user
     * @param groupId Received from the path
     * @param eventId Received from the path
     * @param model Model for the requested html page
     * @return Name of the redirected show group calendar html page
     */
    @GetMapping("/group/{groupId}/event/{eventId}/attendance/finalize")
    public String finalizeEventAttendance(@AuthenticationPrincipal ClubUserDetails userDetails,
                                          @PathVariable("groupId") long groupId, @PathVariable("eventId") long eventId, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));
        
        GroupEvent groupEvent = groupEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group event Id:" + eventId));        

        boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

        if (!isTrainerInGroup || groupEvent.isTeamIsFinal()) {
            return "redirect:/group/" + groupId + "/event/" + eventId + "/attendance";
        }
        
        List<GroupEventAttendance> persistedAttendances = groupEventAttendanceRepository.findAllByGroupEvent(groupEvent);
        for (GroupEventAttendance attendance : persistedAttendances) {
            if (!attendance.getIsNotRemoved()) groupEventAttendanceRepository.delete(attendance);
        }
        
        groupEvent.setTeamIsFinal(true);
        groupEventRepository.save(groupEvent);

        return "redirect:/group/" + groupId + "/calendar";
    }
}
