package de.oth.seproject.clubhub.web.club;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.*;
import de.oth.seproject.clubhub.persistence.repository.*;
import de.oth.seproject.clubhub.web.dto.ClubDTO;
import de.oth.seproject.clubhub.web.dto.EventExtraDTO;
import de.oth.seproject.clubhub.web.dto.GroupDTO;
import de.oth.seproject.clubhub.web.service.NavigationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
public class ClubExploreController {

    private final NavigationService navigationService;

    private final ClubRepository clubRepository;

    private final GroupRepository groupRepository;

    private final GroupEventRequestRepository groupEventRequestRepository;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final LocationRepository locationRepository;

    public ClubExploreController(NavigationService navigationService, ClubRepository clubRepository, GroupRepository groupRepository, GroupEventRequestRepository groupEventRequestRepository, RoleRepository roleRepository, UserRepository userRepository, LocationRepository locationRepository) {
        this.navigationService = navigationService;
        this.clubRepository = clubRepository;
        this.groupRepository = groupRepository;
        this.groupEventRequestRepository = groupEventRequestRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    @GetMapping("/explore")
    public String explorePage(@AuthenticationPrincipal ClubUserDetails userDetails, @RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size, Model model) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        final PageRequest pageRequest = PageRequest.of(currentPage - 1, pageSize);

        Page<Club> clubPage = clubRepository.findAll(pageRequest);

        // mapping to another object for easier processing with thymeleaf
        Page<ClubDTO> clubDTOPage = clubPage.map(club -> {
            boolean isMember = userDetails.getUser().getClub().getId() == club.getId();

            return new ClubDTO(club.getId(), club.getUsers().size(), club.getGroups().size(), club.getName(), isMember);
        });

        model.addAttribute("clubDTOPage", clubDTOPage);

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
        return "explore-clubs";
    }

    @GetMapping("/explore/club/{clubId}/groups")
    public String exploreGroupPage(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("clubId") long clubId, @RequestParam("page") Optional<Integer> page,
                                   @RequestParam("size") Optional<Integer> size, Model model) {
        Club exploredClub = clubRepository.findById(clubId).orElse(userDetails.getUser().getClub());

        if (exploredClub.getId() == userDetails.getUser().getClub().getId()) {
            return "redirect:/groups";
        }

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        final PageRequest pageRequest = PageRequest.of(currentPage - 1, pageSize);

        Page<Group> groupPage = groupRepository.findAllByClub(exploredClub, pageRequest);

        // mapping to another object for easier processing with thymeleaf
        Page<GroupDTO> groupDTOPage = groupPage.map(group -> {
            return new GroupDTO(group.getId(), group.getRoles().size(), group.getName(), false, false);
        });

        boolean isTrainer = roleRepository.existsByUserAndRoleName(userDetails.getUser(), RoleType.TRAINER);

        model.addAttribute("exploredClub", exploredClub);
        model.addAttribute("groupDTOPage", groupDTOPage);
        model.addAttribute("isTrainer", isTrainer);

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
        return "explore-club-groups";
    }

    @GetMapping("/explore/club/{clubId}/group/{groupId}/request")
    public String addGroupEventRequestPage(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("clubId") long clubId, @PathVariable("groupId") long groupId, Model model) {
        Club exploredClub = clubRepository.findById(clubId).orElse(userDetails.getUser().getClub());

        if (exploredClub.getId() == userDetails.getUser().getClub().getId()) {
            return "redirect:/groups";
        }

        Group requestedGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        final boolean isTrainer = roleRepository.existsByUserAndRoleName(userDetails.getUser(), RoleType.TRAINER);

        // user has to be a trainer in his club
        if (!isTrainer) {
            return "redirect:/groups/";
        }

        List<Location> locations = locationRepository.findAll();
        List<Group> creatorGroups = roleRepository.findAllByUserAndRoleName(userDetails.getUser(), RoleType.TRAINER).stream().map(Role::getGroup).toList();

        GroupEventRequest groupEventRequest = new GroupEventRequest();
        model.addAttribute("groupEventRequest", groupEventRequest);
        model.addAttribute("eventTypes", EventType.values());
        model.addAttribute("locations", locations);
        model.addAttribute("exploredClub", exploredClub);
        model.addAttribute("requestedGroup", requestedGroup);
        model.addAttribute("creatorGroups", creatorGroups);

        model.addAttribute("eventExtraDTO", new EventExtraDTO());

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
        return "add-group-event-request";
    }

    @PostMapping("/explore/club/{clubId}/group/{groupId}/request/create")
    public String createGroupEventRequest(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("clubId") long clubId, @PathVariable("groupId") long groupId,
                                          @Valid GroupEventRequest groupEventRequest, BindingResult result, EventExtraDTO eventExtraDTO, Model model) {
        Club exploredClub = clubRepository.findById(clubId).orElse(userDetails.getUser().getClub());

        if (exploredClub.getId() == userDetails.getUser().getClub().getId()) {
            return "redirect:/groups";
        }

        Group requestedGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        User user = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id"));

        final boolean isTrainer = roleRepository.existsByUserAndRoleName(user, RoleType.TRAINER);

        // user has to be a trainer in his club
        if (!isTrainer) {
            return "redirect:/groups/";
        }

        if (result.hasErrors()) {
            List<Location> locations = locationRepository.findAll();
            List<Group> creatorGroups = roleRepository.findAllByUserAndRoleName(userDetails.getUser(), RoleType.TRAINER).stream().map(Role::getGroup).toList();
            model.addAttribute("eventTypes", EventType.values());
            model.addAttribute("locations", locations);
            model.addAttribute("exploredClub", exploredClub);
            model.addAttribute("requestedGroup", requestedGroup);
            model.addAttribute("creatorGroups", creatorGroups);
            model.addAttribute("eventExtraDTO", new EventExtraDTO());
            navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
            return "add-group-event-request";
        }

        groupEventRequest.setRequestedGroup(requestedGroup);
        groupEventRequest.setUser(user);

        if (eventExtraDTO.getWholeDay()) {
            groupEventRequest.setEventStart(LocalTime.MIN);
            groupEventRequest.setEventEnd(LocalTime.MAX);
        } else {
            if (groupEventRequest.getEventStart() == null) {
                groupEventRequest.setEventStart(LocalTime.MIN);
            }
            if (groupEventRequest.getEventEnd() == null) {
                groupEventRequest.setEventEnd(LocalTime.MAX);
            }
        }

        groupEventRequestRepository.save(groupEventRequest);

        return "redirect:/group/" + groupId + "/calendar";
    }
}
