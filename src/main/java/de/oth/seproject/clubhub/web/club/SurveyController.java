package de.oth.seproject.clubhub.web.club;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.*;
import de.oth.seproject.clubhub.persistence.repository.RoleRepository;
import de.oth.seproject.clubhub.persistence.repository.SurveyRepository;
import de.oth.seproject.clubhub.persistence.repository.UserRepository;
import de.oth.seproject.clubhub.web.service.EmailService;
import de.oth.seproject.clubhub.web.service.NavigationService;
import org.springframework.data.domain.Page;
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
public class SurveyController {

    private final NavigationService navigationService;
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    public SurveyController(NavigationService navigationService, SurveyRepository surveyRepository, UserRepository userRepository, RoleRepository roleRepository, EmailService emailService) {
        this.navigationService = navigationService;
        this.surveyRepository = surveyRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
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
        int fromIndex = pageRequest.getPageNumber() * pageRequest.getPageSize();
        int toIndex = Math.min(fromIndex + pageRequest.getPageSize(), surveys.size());
        var surveyPage = new PageImpl<>(surveys.subList(fromIndex, toIndex), pageRequest, surveys.size());

        List<List<String>> options = new LinkedList<>();
        List<List<Integer>> optionVotes = new LinkedList<>();

        for (var survey : surveys) {
            List<String> currentSurveyOptions = new LinkedList<>();
            List<Integer> currentSurveyOptionVotes = new LinkedList<>();
            for (var option : survey.getOptions().split("\\|")) {
                var optionItems = option.split("=");
                currentSurveyOptions.add(optionItems[0]);
                currentSurveyOptionVotes.add(Integer.parseInt(optionItems[1]));
            }
            options.add(currentSurveyOptions);
            optionVotes.add(currentSurveyOptionVotes);
        }

        var optionsPage = new PageImpl<>(options.subList(fromIndex, toIndex), pageRequest, options.size());
        model.addAttribute("options", optionsPage);
        var optionVotesPage = new PageImpl<>(optionVotes.subList(fromIndex, toIndex), pageRequest, optionVotes.size());
        model.addAttribute("optionVotes", optionVotesPage);

        model.addAttribute("isTrainerInClub", roleRepository.existsByUserAndRoleName(userDetails.getUser(), RoleType.TRAINER));
        model.addAttribute("surveyPage", surveyPage);

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
        return "surveys";
    }

    @GetMapping("/surveys/add")
    public String addSurveyPage(@AuthenticationPrincipal ClubUserDetails userDetails, Model model) {

        boolean isTrainerInClub = roleRepository.existsByUserAndRoleName(userDetails.getUser(), RoleType.TRAINER);

        // user has to be a trainer of a group in this club
        if (!isTrainerInClub) {
            return "redirect:/surveys";
        }

        var newSurvey = new Survey();
        model.addAttribute("survey", newSurvey);

        // retrieve all groups for the current user
        var currentUser = userDetails.getUser();
        var currentUserRoles = roleRepository.findAllByUser(currentUser);
        List<Group> groups = new LinkedList<>();
        for (var role: currentUserRoles) {
            groups.add(role.getGroup());
        }

        model.addAttribute("groups", groups);

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
        return "add-survey";
    }

    @PostMapping("/surveys/create")
    public String createSurvey(@AuthenticationPrincipal ClubUserDetails userDetails, @Valid Survey survey, BindingResult result, Model model) {

        boolean isTrainerInClub = roleRepository.existsByUserAndRoleName(userDetails.getUser(), RoleType.TRAINER);

        // user has to be a trainer of a group in this club
        if (!isTrainerInClub) {
            return "redirect:/surveys";
        }

        if (result.hasErrors()) {
            navigationService.addNavigationAttributes(model, userDetails.getUser().getId());
            return "add-survey";
        }

        // need to retrieve user again because user from principal is a detached reference
        Optional<User> optionalUser = userRepository.findById(userDetails.getUser().getId());

        optionalUser.ifPresent(user -> {
            survey.setUser(user);
            survey.setCreatedOn(LocalDateTime.now());

            var formattedOptions = new LinkedList<String>();
            for (var option : survey.getOptions().split(",")) {
                formattedOptions.add(option + "=0");
            }
            survey.setOptions(String.join("|", formattedOptions));

            surveyRepository.save(survey);
        });

        if (optionalUser.isEmpty()) {
            // user doesn't exist
            return "redirect:/logout";
        }

        // send email to all users in the group where the survey has been posted
        var group = survey.getGroup();
        for (var role : roleRepository.findAllByGroup(group)) {
            var userToMessage = role.getUser();
            emailService.sendEmail(userToMessage.getEmail(), "A new survey has been created for one of your clubs!", "New ClubHub Survey!");
        }

        return "redirect:/surveys";
    }

    @PostMapping("/surveys/vote")
    public String voteOnSurvey(@AuthenticationPrincipal ClubUserDetails userDetails, @RequestParam(name = "survey", required = true) long survey, @RequestParam(name = "option", required = true) int option) {
        Survey persistedSurvey = surveyRepository.findById(survey)
                .orElseThrow(() -> new IllegalArgumentException("Invalid survey Id:" + survey));

        List<String> nameItems = new LinkedList<>();
        List<Integer> voteItems = new LinkedList<>();
        for (var options : persistedSurvey.getOptions().split("\\|")) {
            var item = options.split("=");
            nameItems.add(item[0]);
            voteItems.add(Integer.parseInt(item[1]));
        }

        voteItems.set(option, voteItems.get(option) + 1);

        List<String> optionItems = new LinkedList<>();
        for (var i = 0; i < nameItems.size(); i += 1) {
            optionItems.add(nameItems.get(i) + "=" + voteItems.get(i));
        }
        var optionStr = String.join("|", optionItems);


        persistedSurvey.setOptions(optionStr);
        persistedSurvey.setUpdatedOn(LocalDateTime.now());
        surveyRepository.save(persistedSurvey);

        return "redirect:/surveys";
    }

    @GetMapping("/surveys/{id}/delete")
    public String deleteSurvey(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, Model model) {
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
