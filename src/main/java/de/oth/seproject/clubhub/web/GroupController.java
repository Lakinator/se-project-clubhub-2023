package de.oth.seproject.clubhub.web;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.Group;
import de.oth.seproject.clubhub.persistence.model.Role;
import de.oth.seproject.clubhub.persistence.model.RoleType;
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

        // mapping to another object for easier processing with thymeleaf
        Page<GroupDTO> groupDTOPage = groupPage.map(group -> {
            boolean hasJoined = roleRepository.existsByUserAndGroup(userDetails.getUser(), group);
            boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

            return new GroupDTO(group.getId(), group.getRoles().size(), group.getName(), hasJoined, isTrainerInGroup);
        });

        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("club", userDetails.getUser().getClub());
        model.addAttribute("groupDTOPage", groupDTOPage);
        return "groups";
    }

    @GetMapping("/groups/add")
    public String addGroupPage(@AuthenticationPrincipal ClubUserDetails userDetails, Model model) {
        Group group = new Group();
        model.addAttribute("group", group);
        model.addAttribute("clubName", userDetails.getUser().getClub().getName());
        return "add-group";
    }

    @PostMapping("/group/create")
    public String createGroup(@AuthenticationPrincipal ClubUserDetails userDetails, @Valid Group group, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("clubName", userDetails.getUser().getClub().getName());
            return "add-group";
        }

        // need to retrieve user again because user from principal is a detached reference
        Optional<User> optionalUser = userRepository.findById(userDetails.getUser().getId());

        optionalUser.ifPresent(user -> {
            group.setClub(user.getClub());

            groupRepository.save(group);

            Role role = new Role();
            role.setRoleName(RoleType.TRAINER);
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

    @GetMapping("/group/{id}/join")
    public String joinGroup(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, Model model) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + id));

        // need to retrieve user again because user from principal is a detached reference
        Optional<User> optionalUser = userRepository.findById(userDetails.getUser().getId());

        optionalUser.ifPresent(user -> {

            // check if user is in this club
            if (group.getClub().getId() != user.getClub().getId()) {
                throw new IllegalArgumentException("This group is from a different club!");
            }

            // check if user is already a member of this group
            if (!roleRepository.existsByUserAndGroup(user, group)) {
                Role role = new Role();
                role.setRoleName(RoleType.MEMBER);
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

    @GetMapping("/group/{id}/leave")
    public String leaveGroup(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, Model model) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + id));

        Optional<Role> roleInGroup = roleRepository.findByUserAndGroup(userDetails.getUser(), group);

        roleInGroup.ifPresent(role -> {
            roleRepository.delete(role);

            // check if there are any members remaining
            if (!roleRepository.existsAllByGroup(group)) {
                groupRepository.delete(group);
            }
        });

        return "redirect:/groups";
    }

    @GetMapping("/group/{id}/show")
    public String showGroupPage(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, @RequestParam("page") Optional<Integer> page,
                                @RequestParam("size") Optional<Integer> size, Model model) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + id));

        boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

        // if user is a trainer of this group, automatically redirect him to the edit page
        if (isTrainerInGroup) {
            return "redirect:/group/" + id + "/edit";
        }

        final PageRequest pageRequest = PageRequest.of(currentPage - 1, pageSize);

        Page<Role> rolePage = roleRepository.findAllByGroup(group, pageRequest);

        model.addAttribute("group", group);
        model.addAttribute("rolePage", rolePage);
        return "show-group";
    }

    @GetMapping("/group/{id}/edit")
    public String editGroupPage(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, Model model) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + id));

        Optional<Role> roleInGroup = roleRepository.findByUserAndGroup(userDetails.getUser(), group);
        final boolean isTrainerInGroup = roleInGroup.isPresent() && roleInGroup.get().getRoleName().equals(RoleType.TRAINER);

        // if user is not a trainer of this group, automatically redirect him to the show page
        if (!isTrainerInGroup) {
            return "redirect:/group/" + id + "/show";
        }

        model.addAttribute("activeRole", roleInGroup.get());
        model.addAttribute("group", group);
        model.addAttribute("roleNames", RoleType.values());
        return "edit-group";
    }

    @PostMapping("/group/{id}/update")
    public String updateGroup(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, @Valid Group group,
                              BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "redirect:/group/" + id + "/show";
        }

        boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

        // if user is not a trainer of this group, automatically redirect him to the show page
        if (!isTrainerInGroup) {
            return "redirect:/group/" + id + "/show";
        }

        group.getRoles().forEach(role -> {
            Optional<Role> persistedRole = roleRepository.findById(role.getId());

            persistedRole.ifPresent(r -> {

                // trainer can't change its own role
                if (!r.getUser().getId().equals(userDetails.getUser().getId())) {
                    r.setRoleName(role.getRoleName());
                    roleRepository.save(r);
                }

            });
        });

        groupRepository.findById(id).ifPresent(g -> {
            g.setName(group.getName());
            groupRepository.save(g);
        });

        return "redirect:/group/" + id + "/show";
    }

    @GetMapping("/groups/{groupId}/kick/{userId}")
    public String kickMemberFromGroup(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId, @PathVariable("userId") long userId, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

        if (isTrainerInGroup) {
            Optional<User> kickedUser = userRepository.findById(userId);

            if (kickedUser.isPresent()) {
                Optional<Role> kickedUserRoleInGroup = roleRepository.findByUserAndGroup(kickedUser.get(), group);

                kickedUserRoleInGroup.ifPresent(role -> {
                    roleRepository.delete(role);

                    // check if there are any members remaining
                    if (!roleRepository.existsAllByGroup(group)) {
                        groupRepository.delete(group);
                    }
                });
            }

        }

        return "redirect:/group/" + groupId + "/edit";
    }

    @GetMapping("/group/{id}/delete")
    public String deleteGroup(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long id, Model model) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + id));

        boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

        if (isTrainerInGroup) {
            groupRepository.delete(group);
        }

        return "redirect:/groups";
    }
}
