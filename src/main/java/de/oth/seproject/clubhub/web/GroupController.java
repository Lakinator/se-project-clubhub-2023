package de.oth.seproject.clubhub.web;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.Group;
import de.oth.seproject.clubhub.persistence.model.Role;
import de.oth.seproject.clubhub.persistence.model.User;
import de.oth.seproject.clubhub.persistence.repository.GroupRepository;
import de.oth.seproject.clubhub.persistence.repository.RoleRepository;
import de.oth.seproject.clubhub.persistence.repository.UserRepository;
import de.oth.seproject.clubhub.web.dto.GroupDTO;
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
import java.util.List;
import java.util.Optional;

@Controller
public class GroupController {

    private final GroupRepository groupRepository;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    public GroupController(GroupRepository groupRepository, RoleRepository roleRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/groups")
    public String groupPage(@AuthenticationPrincipal ClubUserDetails userDetails, @RequestParam("page") Optional<Integer> page,
                            @RequestParam("size") Optional<Integer> size, Model model) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        final PageRequest pageRequest = PageRequest.of(currentPage - 1, pageSize);

        Page<Group> groupPage = groupRepository.findAllByClub(userDetails.getUser().getClub(), pageRequest);

        Page<GroupDTO> groupDTOPage = groupPage.map(group -> {
            Optional<Role> roleInGroup = roleRepository.findByUserAndGroup(userDetails.getUser(), group);
            final boolean isTrainer = roleInGroup.isPresent() && roleInGroup.get().getAuthority().equals("TRAINER");
            return new GroupDTO(group.getId(), group.getRoles().size(), group.getName(), roleInGroup.isPresent(), isTrainer);
        });

        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("club", userDetails.getUser().getClub());
        model.addAttribute("groupDTOPage", groupDTOPage);
        return "groups";
    }

    @GetMapping("/add-group")
    public String addGroupPage(@AuthenticationPrincipal ClubUserDetails userDetails, Model model) {
        Group group = new Group();
        model.addAttribute("group", group);
        model.addAttribute("clubName", userDetails.getUser().getClub().getName());
        return "add-group";
    }

    @PostMapping("/create-group")
    public String createGroup(@AuthenticationPrincipal ClubUserDetails userDetails, @Valid Group group, BindingResult result, Model model) {

        // TODO: validation

        // need to retrieve user again because user from principal is a detached reference
        Optional<User> optionalUser = userRepository.findById(userDetails.getUser().getId());

        optionalUser.ifPresent(user -> {
            group.setClub(user.getClub());

            groupRepository.save(group);

            Role role = new Role();
            role.setName("TRAINER");
            role.setUser(user);
            role.setGroup(group);

            roleRepository.save(role);
        });

        if (optionalUser.isEmpty()) {
            // user doesn't exist
            return "redirect:/logout";
        }

        return "redirect:/groups";
    }

    @GetMapping("/join-group/{id}")
    public String joinGroup(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, Model model) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + id));

        // need to retrieve user again because user from principal is a detached reference
        Optional<User> optionalUser = userRepository.findById(userDetails.getUser().getId());

        optionalUser.ifPresent(user -> {

            // check if user is already a member of this group
            if (!roleRepository.existsByUserAndGroup(user, group)) {
                Role role = new Role();
                role.setName("MEMBER");
                role.setUser(user);
                role.setGroup(group);

                roleRepository.save(role);
            }

        });

        if (optionalUser.isEmpty()) {
            // user doesn't exist
            return "redirect:/logout";
        }

        return "redirect:/groups";
    }

    @GetMapping("/show-group/{id}")
    public String showGroupPage(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, @RequestParam("page") Optional<Integer> page,
                                @RequestParam("size") Optional<Integer> size, Model model) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + id));

        Optional<Role> roleInGroup = roleRepository.findByUserAndGroup(userDetails.getUser(), group);
        final boolean isTrainer = roleInGroup.isPresent() && roleInGroup.get().getAuthority().equals("TRAINER");

        if (isTrainer) {
            return "redirect:/edit-group/" + id;
        }

        final PageRequest pageRequest = PageRequest.of(currentPage - 1, pageSize);

        Page<Role> rolePage = roleRepository.findAllByGroup(group, pageRequest);

        model.addAttribute("group", group);
        model.addAttribute("rolePage", rolePage);
        return "show-group";
    }

    @GetMapping("/edit-group/{id}")
    public String editGroupPage(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, Model model) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + id));

        Optional<Role> roleInGroup = roleRepository.findByUserAndGroup(userDetails.getUser(), group);
        final boolean isTrainer = roleInGroup.isPresent() && roleInGroup.get().getAuthority().equals("TRAINER");

        if (!isTrainer) {
            return "redirect:/show-group/" + id;
        }

        model.addAttribute("group", group);
        model.addAttribute("roleNames", List.of("TRAINER", "MEMBER"));
        return "edit-group";
    }

    @PostMapping("/update-group/{id}")
    public String updateGroup(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, @Valid Group group,
                              BindingResult result, Model model) {

        Optional<Role> roleInGroup = roleRepository.findByUserAndGroup(userDetails.getUser(), group);
        final boolean isTrainer = roleInGroup.isPresent() && roleInGroup.get().getAuthority().equals("TRAINER");

        if (!isTrainer) {
            return "redirect:/show-group/" + id;
        }

        if (!result.hasErrors()) {
            group.getRoles().forEach(role -> {
                Optional<Role> persistedRole = roleRepository.findById(role.getId());

                persistedRole.ifPresent(r -> {
                    r.setName(role.getName());
                    roleRepository.save(r);
                });
            });
        }

        return "redirect:/show-group/" + id;
    }

    @GetMapping("/delete-group/{id}")
    public String deleteGroup(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, Model model) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + id));

        Optional<Role> roleInGroup = roleRepository.findByUserAndGroup(userDetails.getUser(), group);
        final boolean isTrainer = roleInGroup.isPresent() && roleInGroup.get().getAuthority().equals("TRAINER");

        if (isTrainer) {
            groupRepository.delete(group);
        }

        return "redirect:/groups";
    }
}
