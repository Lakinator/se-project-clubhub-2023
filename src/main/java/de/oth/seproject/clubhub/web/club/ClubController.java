package de.oth.seproject.clubhub.web.club;


import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.*;
import de.oth.seproject.clubhub.persistence.repository.ClubRepository;
import de.oth.seproject.clubhub.persistence.repository.RoleRepository;
import de.oth.seproject.clubhub.persistence.repository.SurveyRepository;
import de.oth.seproject.clubhub.persistence.repository.UserRepository;
import de.oth.seproject.clubhub.web.service.EmailService;
import de.oth.seproject.clubhub.web.service.NavigationService;
import org.springframework.data.domain.PageImpl;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Controller
public class ClubController {

    private final NavigationService navigationService;
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final ClubRepository clubRepository;

    public ClubController(NavigationService navigationService, SurveyRepository surveyRepository, UserRepository userRepository, RoleRepository roleRepository, EmailService emailService,
                          ClubRepository clubRepository) {
        this.navigationService = navigationService;
        this.surveyRepository = surveyRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.clubRepository = clubRepository;
    }

    @GetMapping("/clubs/register")
    public String registerClubPage(Model model) {

        model.addAttribute("club", new Club());
        model.addAttribute("registerClub", true);

        return "add-club";
    }

    @PostMapping("/clubs/create")
    public String createClub(@Valid Club club, BindingResult result, Model model, @RequestParam(name = "update", required = false, defaultValue = "false") boolean update) {

        if (result.hasErrors()) {
            return "add-club";
        }

        clubRepository.save(club);

        if (update) {
            return "redirect:/user";
        }
        return "redirect:/registration";
    }

    @GetMapping("/clubs/delete")
    public String deleteClub(@AuthenticationPrincipal ClubUserDetails userDetails) {

        boolean isTrainerInClub = roleRepository.existsByUserAndRoleName(userDetails.getUser(), RoleType.TRAINER);

        // user has to be a trainer of a group in this club
        if (!isTrainerInClub) {
            return "redirect:/user";
        }

        var currentUser = userDetails.getUser();
        clubRepository.deleteById(currentUser.getClub().getId());

        return "redirect:/logout";
    }

    @GetMapping("/clubs/edit")
    public String editClub(@AuthenticationPrincipal ClubUserDetails userDetails, Model model) {

        boolean isTrainerInClub = roleRepository.existsByUserAndRoleName(userDetails.getUser(), RoleType.TRAINER);

        // user has to be a trainer of a group in this club
        if (!isTrainerInClub) {
            return "redirect:/user";
        }

        var currentUser = userDetails.getUser();
        var currentClub = currentUser.getClub();
        model.addAttribute("club", currentClub);
        model.addAttribute("prevId", currentClub.getId());
        model.addAttribute("registerClub", false);

        return "add-club";
    }
}
