package de.oth.seproject.clubhub.web;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.Announcement;
import de.oth.seproject.clubhub.persistence.model.User;
import de.oth.seproject.clubhub.persistence.repository.AnnouncementRepository;
import de.oth.seproject.clubhub.persistence.repository.UserRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class AnnouncementController {

    private final AnnouncementRepository announcementRepository;

    private final UserRepository userRepository;

    public AnnouncementController(AnnouncementRepository announcementRepository, UserRepository userRepository) {
        this.announcementRepository = announcementRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/announcements")
    public String announcementPage(@AuthenticationPrincipal ClubUserDetails userDetails, @RequestParam("page") Optional<Integer> page,
                                   @RequestParam("size") Optional<Integer> size, Model model) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        final PageRequest pageRequest = PageRequest.of(currentPage - 1, pageSize);

        // TODO: sort by createdOn/updatedOn
        Page<Announcement> announcementPage = announcementRepository.findAllByClub(userDetails.getUser().getClub(), pageRequest);

        int totalPages = announcementPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("club", userDetails.getUser().getClub());
        model.addAttribute("announcementPage", announcementPage);
        return "announcements";
    }

    @GetMapping("/add-announcement")
    public String addAnnouncementPage(@AuthenticationPrincipal ClubUserDetails userDetails, Model model) {
        Announcement announcement = new Announcement();
        model.addAttribute("announcement", announcement);
        model.addAttribute("clubName", userDetails.getUser().getClub().getName());
        return "add-announcement";
    }

    @PostMapping("/create-announcement")
    public String createAnnouncement(@AuthenticationPrincipal ClubUserDetails userDetails, @Valid Announcement announcement, BindingResult result, Model model) {

        // TODO: validation

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

    @GetMapping("/edit-announcement/{id}")
    public String editAnnouncementPage(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, Model model) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid announcement Id:" + id));

        model.addAttribute("announcement", announcement);
        return "edit-announcement";
    }

    @PostMapping("/update-announcement/{id}")
    public String updateAnnouncement(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, @Valid Announcement announcement,
                                     BindingResult result, Model model) {

        if (!result.hasErrors()) {
            announcementRepository.findById(id).ifPresent(persistedAnnouncement -> {
                persistedAnnouncement.setMessage(announcement.getMessage());
                persistedAnnouncement.setUpdatedOn(LocalDateTime.now());
                announcementRepository.save(persistedAnnouncement);
            });
        }

        return "redirect:/announcements";
    }

    @GetMapping("/delete-announcement/{id}")
    public String deleteAnnouncement(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, Model model) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid announcement Id:" + id));
        announcementRepository.delete(announcement);
        return "redirect:/announcements";
    }


}
