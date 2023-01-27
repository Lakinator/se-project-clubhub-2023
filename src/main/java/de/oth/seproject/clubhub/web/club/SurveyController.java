package de.oth.seproject.clubhub.web.club;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.Announcement;
import de.oth.seproject.clubhub.persistence.model.RoleType;
import de.oth.seproject.clubhub.persistence.model.User;
import de.oth.seproject.clubhub.persistence.repository.AnnouncementRepository;
import de.oth.seproject.clubhub.persistence.repository.RoleRepository;
import de.oth.seproject.clubhub.persistence.repository.UserRepository;
import de.oth.seproject.clubhub.web.service.NavigationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class SurveyController {

    private final NavigationService navigationService;
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public SurveyController(NavigationService navigationService, SurveyRepository surveyRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.navigationService = navigationService;
        this.surveyRepository = surveyRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/surveys")
    public String surveyPage(@AuthenticationPrincipal ClubUserDetails userDetails, @RequestParam("page") Optional<Integer> page,
                                   @RequestParam("size") Optional<Integer> size, Model model) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        final PageRequest pageRequest = PageRequest.of(currentPage - 1, pageSize, Sort.by("createdOn").descending());

        // retrieve all surveys for the current user
        var currentUser = userDetails.getUser();
        var currentUserRoles = roleRepository.findAllByUser(currentUser);
        List<Survey> surveys = new LinkedList<>();
        for (var role: currentUserRoles) {
            var surveysPerGroup = surveyRepository.findAllByGroup(role.getGroup());
            surveys.addAll(surveysPerGroup);
        }
        // create page from all surveys
        var surveyPage = new PageImpl<>(surveys, pageRequest, surveys.size());

        model.addAttribute("isTrainerInClub", roleRepository.existsByUserAndRoleName(userDetails.getUser(), RoleType.TRAINER));
        model.addAttribute("surveyPage", surveyPage);

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
        return "surveys";
    }

    @GetMapping("/surveys/add")
    public String addAnnouncementPage(@AuthenticationPrincipal ClubUserDetails userDetails, Model model) {

        boolean isTrainerInClub = roleRepository.existsByUserAndRoleName(userDetails.getUser(), RoleType.TRAINER);

        // user has to be a trainer of a group in this club
        if (!isTrainerInClub) {
            return "redirect:/announcements";
        }

        Announcement announcement = new Announcement();
        model.addAttribute("announcement", announcement);

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
        return "add-announcement";
    }

    @PostMapping("/surveys/create")
    public String createAnnouncement(@AuthenticationPrincipal ClubUserDetails userDetails, @Valid Announcement announcement, BindingResult result, Model model) {

        boolean isTrainerInClub = roleRepository.existsByUserAndRoleName(userDetails.getUser(), RoleType.TRAINER);

        // user has to be a trainer of a group in this club
        if (!isTrainerInClub) {
            return "redirect:/announcements";
        }

        if (result.hasErrors()) {
            navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
            return "add-announcement";
        }

        // need to retrieve user again because user from principal is a detached reference
        Optional<User> optionalUser = userRepository.findById(userDetails.getUser().getId());

        optionalUser.ifPresent(user -> {
            announcement.setUser(user);
            announcement.setClub(user.getClub());
            announcement.setCreatedOn(LocalDateTime.now());

            announcementRepository.save(announcement);
        });

        if (optionalUser.isEmpty()) {
            // user doesn't exist
            return "redirect:/logout";
        }

        return "redirect:/announcements";
    }

    @GetMapping("/surveys/{id}/edit")
    public String editAnnouncementPage(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, Model model) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid announcement Id:" + id));

        boolean isUserOwnerOfAnnouncement = announcement.getUser().getId().equals(userDetails.getUser().getId());

        // user has to be the creator of this announcement
        if (!isUserOwnerOfAnnouncement) {
            return "redirect:/announcements";
        }

        model.addAttribute("announcement", announcement);

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
        return "edit-announcement";
    }

    @PostMapping("/surveys/{id}/update")
    public String updateAnnouncement(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, @Valid Announcement announcement,
                                     BindingResult result, Model model) {
        Announcement persistedAnnouncement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid announcement Id:" + id));

        boolean isUserOwnerOfAnnouncement = persistedAnnouncement.getUser().getId().equals(userDetails.getUser().getId());

        // user has to be the creator of this announcement
        if (!isUserOwnerOfAnnouncement) {
            return "redirect:/announcements";
        }

        if (result.hasErrors()) {
            navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
            return "edit-announcement";
        }

        persistedAnnouncement.setMessage(announcement.getMessage());
        persistedAnnouncement.setUpdatedOn(LocalDateTime.now());
        announcementRepository.save(persistedAnnouncement);

        return "redirect:/announcements";
    }

    @GetMapping("/surveys/{id}/delete")
    public String deleteAnnouncement(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, Model model) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid survey id:" + id));

        boolean isTrainerInClub = roleRepository.existsByUserAndRoleName(userDetails.getUser(), RoleType.TRAINER);

        // user has to be a trainer of a group in this club
        if (!isTrainerInClub) {
            return "redirect:/surveys";
        }

        surveyRepository.delete(survey);

        return "redirect:/surveys";
    }
}
