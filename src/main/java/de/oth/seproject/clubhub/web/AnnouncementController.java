package de.oth.seproject.clubhub.web;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.Announcement;
import de.oth.seproject.clubhub.persistence.repository.AnnouncementRepository;
import de.oth.seproject.clubhub.persistence.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AnnouncementController {

    private final AnnouncementRepository announcementRepository;

    private final UserRepository userRepository;

    public AnnouncementController(AnnouncementRepository announcementRepository, UserRepository userRepository) {
        this.announcementRepository = announcementRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/announcements")
    public String announcementPage(@AuthenticationPrincipal ClubUserDetails userDetails, Model model) {
        // TODO: pagination https://www.baeldung.com/spring-thymeleaf-pagination
        List<Announcement> announcementList = announcementRepository.findAllByClub(userDetails.getUser().getClub());
        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("club", userDetails.getUser().getClub());
        model.addAttribute("announcements", announcementList);
        return "announcements";
    }

    @GetMapping("/add-announcement")
    public String addAnnouncementPage(@AuthenticationPrincipal ClubUserDetails userDetails, Model model) {
        Announcement announcement = new Announcement();
        model.addAttribute("announcement", announcement);
        model.addAttribute("clubName", userDetails.getUser().getClub().getName());
        return "add-announcement";
    }

    @PostMapping("/add-announcement")
    public String addAnnouncementPage(@AuthenticationPrincipal ClubUserDetails userDetails, @Valid Announcement announcement, BindingResult result, Model model) {

        // TODO: validation

        announcement.setCreatedOn(LocalDateTime.now());

//        announcementRepository.save(announcement); // TODO: doesn't work, org.hibernate.PersistentObjectException: detached entity passed to persist: de.oth.seproject.clubhub.persistence.model.Club

        return "redirect:/announcements";
    }


}
