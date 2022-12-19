package de.oth.seproject.clubhub.web.group;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.GroupEvent;
import de.oth.seproject.clubhub.persistence.model.GroupEventRequest;
import de.oth.seproject.clubhub.persistence.model.RequestStatus;
import de.oth.seproject.clubhub.persistence.model.RoleType;
import de.oth.seproject.clubhub.persistence.repository.*;
import de.oth.seproject.clubhub.web.dto.GroupEventRequestDTO;
import de.oth.seproject.clubhub.web.service.NavigationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
public class EventRequestController {

    private final NavigationService navigationService;

    private final ClubRepository clubRepository;

    private final GroupRepository groupRepository;

    private final GroupEventRepository groupEventRepository;

    private final GroupEventRequestRepository groupEventRequestRepository;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final LocationRepository locationRepository;

    public EventRequestController(NavigationService navigationService, ClubRepository clubRepository, GroupRepository groupRepository, GroupEventRepository groupEventRepository, GroupEventRequestRepository groupEventRequestRepository, RoleRepository roleRepository, UserRepository userRepository, LocationRepository locationRepository) {
        this.navigationService = navigationService;
        this.clubRepository = clubRepository;
        this.groupRepository = groupRepository;
        this.groupEventRepository = groupEventRepository;
        this.groupEventRequestRepository = groupEventRequestRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    @GetMapping("/requests")
    public String showRequestsPage(@AuthenticationPrincipal ClubUserDetails userDetails,
                                @RequestParam("year") Optional<Integer> year,
                                @RequestParam("month") Optional<Month> month, Model model) {

        // calendar pagination
        int selectedYear = year.orElse(LocalDate.now().getYear());
        Month selectedMonth = month.orElse(LocalDate.now().getMonth());

        LocalDate selectedIntervalStart = LocalDate.now()
                .withYear(selectedYear)
                .withMonth(selectedMonth.getValue())
                .with(TemporalAdjusters.firstDayOfMonth());
        LocalDate selectedIntervalEnd = selectedIntervalStart.with(TemporalAdjusters.lastDayOfMonth());

        // get all outgoing and received group event requests
        List<GroupEventRequest> creatorGroupEventRequests = groupEventRequestRepository.findAllByCreatorGroup_ClubAndEventDateBetweenOrderByEventDateAscEventStartAsc(userDetails.getUser().getClub(), selectedIntervalStart, selectedIntervalEnd);
        List<GroupEventRequest> receivedGroupEventRequests = groupEventRequestRepository.findAllByRequestedGroup_ClubAndEventDateBetweenOrderByEventDateAscEventStartAsc(userDetails.getUser().getClub(), selectedIntervalStart, selectedIntervalEnd);

        // mapping to another object for easier processing with thymeleaf
        List<GroupEventRequestDTO> groupEventRequestDTOs = new ArrayList<>();

        groupEventRequestDTOs.addAll(creatorGroupEventRequests.stream().map(groupEventRequest -> {
            boolean isTrainerInCreatorGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), groupEventRequest.getCreatorGroup(), RoleType.TRAINER);
            return new GroupEventRequestDTO(groupEventRequest, isTrainerInCreatorGroup, false);
        }).toList());

        groupEventRequestDTOs.addAll(receivedGroupEventRequests.stream().map(groupEventRequest -> {
            boolean isTrainerInRequestedGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), groupEventRequest.getRequestedGroup(), RoleType.TRAINER);
            return new GroupEventRequestDTO(groupEventRequest, false, isTrainerInRequestedGroup);
        }).toList());

        // sort by event date after merging
        groupEventRequestDTOs.sort(Comparator.comparing(o -> o.groupEventRequest().getEventDate()));

        model.addAttribute("groupEventRequestDTOs", groupEventRequestDTOs);
        model.addAttribute("lastMonth", selectedMonth.minus(1).getValue());
        model.addAttribute("nextMonth", selectedMonth.plus(1).getValue());
        model.addAttribute("currentMonth", LocalDate.now().getMonth().getValue());
        model.addAttribute("lastYear", selectedIntervalStart.minusMonths(1).getYear());
        model.addAttribute("nextYear", selectedIntervalStart.plusMonths(1).getYear());
        model.addAttribute("selectedIntervalStart", selectedIntervalStart);
        model.addAttribute("selectedIntervalEnd", selectedIntervalEnd);

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
        return "show-requests";
    }

    @GetMapping("/request/{requestId}/accept")
    public String acceptGroupEventRequest(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("requestId") long requestId, Model model) {
        GroupEventRequest groupEventRequest = groupEventRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group event request Id:" + requestId));

        boolean isTrainer = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), groupEventRequest.getRequestedGroup(), RoleType.TRAINER);

        // trainer needs to be a trainer in the group which received the request
        if (isTrainer) {
            // create group event for the creator
            GroupEvent groupEvent = new GroupEvent();
            groupEvent.setUser(groupEventRequest.getUser());
            groupEvent.setGroup(groupEventRequest.getCreatorGroup());
            groupEvent.setEventType(groupEventRequest.getEventType());
            groupEvent.setTitle(groupEventRequest.getTitle());
            groupEvent.setDescription(groupEventRequest.getDescription());
            groupEvent.setEventDate(groupEventRequest.getEventDate());
            groupEvent.setEventStart(groupEventRequest.getEventStart());
            groupEvent.setEventEnd(groupEventRequest.getEventEnd());
            groupEvent.setLocation(groupEventRequest.getLocation());

            groupEventRepository.save(groupEvent);

            // create group event for the one which accepted the request
            groupEvent = new GroupEvent();
            groupEvent.setUser(groupEventRequest.getUser());
            groupEvent.setGroup(groupEventRequest.getRequestedGroup());
            groupEvent.setEventType(groupEventRequest.getEventType());
            groupEvent.setTitle(groupEventRequest.getTitle());
            groupEvent.setDescription(groupEventRequest.getDescription());
            groupEvent.setEventDate(groupEventRequest.getEventDate());
            groupEvent.setEventStart(groupEventRequest.getEventStart());
            groupEvent.setEventEnd(groupEventRequest.getEventEnd());
            groupEvent.setLocation(groupEventRequest.getLocation());

            groupEventRepository.save(groupEvent);

            // save status of request
            groupEventRequest.setRequestStatus(RequestStatus.ACCEPTED);
            groupEventRequestRepository.save(groupEventRequest);
        }

        return "redirect:/requests";
    }

    @GetMapping("/request/{requestId}/deny")
    public String denyGroupEventRequest(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("requestId") long requestId, Model model) {
        GroupEventRequest groupEventRequest = groupEventRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group event request Id:" + requestId));

        boolean isTrainer = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), groupEventRequest.getRequestedGroup(), RoleType.TRAINER);

        // trainer needs to be a trainer in the group which received the request
        if (isTrainer) {
            groupEventRequest.setRequestStatus(RequestStatus.DENIED);
            groupEventRequestRepository.save(groupEventRequest);
        }

        return "redirect:/requests";
    }

    @GetMapping("/request/{requestId}/delete")
    public String deleteGroupEventRequest(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("requestId") long requestId, Model model) {
        GroupEventRequest groupEventRequest = groupEventRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group event request Id:" + requestId));

        boolean isTrainer = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), groupEventRequest.getCreatorGroup(), RoleType.TRAINER);

        // trainer needs to be a trainer in the group which created the request
        if (isTrainer) {
            groupEventRequestRepository.delete(groupEventRequest);
        }

        return "redirect:/requests";
    }
}
