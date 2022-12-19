package de.oth.seproject.clubhub.web.club;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.Club;
import de.oth.seproject.clubhub.persistence.repository.ClubRepository;
import de.oth.seproject.clubhub.persistence.repository.GroupRepository;
import de.oth.seproject.clubhub.persistence.repository.RoleRepository;
import de.oth.seproject.clubhub.persistence.repository.UserRepository;
import de.oth.seproject.clubhub.web.dto.ClubDTO;
import de.oth.seproject.clubhub.web.service.NavigationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class ClubExploreController {

    private final NavigationService navigationService;

    private final ClubRepository clubRepository;

    private final GroupRepository groupRepository;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    public ClubExploreController(NavigationService navigationService, ClubRepository clubRepository, GroupRepository groupRepository, RoleRepository roleRepository, UserRepository userRepository) {
        this.navigationService = navigationService;
        this.clubRepository = clubRepository;
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/explore")
    public String groupPage(@AuthenticationPrincipal ClubUserDetails userDetails, @RequestParam("page") Optional<Integer> page,
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
}
