package de.oth.seproject.clubhub.web.group;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.ChatRoom;
import de.oth.seproject.clubhub.persistence.model.Group;
import de.oth.seproject.clubhub.persistence.model.RoleType;
import de.oth.seproject.clubhub.persistence.model.User;
import de.oth.seproject.clubhub.persistence.repository.*;
import de.oth.seproject.clubhub.web.dto.ChatRoomDTO;
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
import java.util.Optional;

@Controller
public class ChatRoomController {

    private final NavigationService navigationService;

    private final ChatRoomRepository chatRoomRepository;

    private final ChatRoomMessageRepository chatRoomMessageRepository;

    private final GroupRepository groupRepository;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    public ChatRoomController(NavigationService navigationService, ChatRoomRepository chatRoomRepository, ChatRoomMessageRepository chatRoomMessageRepository, GroupRepository groupRepository, RoleRepository roleRepository, UserRepository userRepository) {
        this.navigationService = navigationService;
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMessageRepository = chatRoomMessageRepository;
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/group/{id}/rooms")
    public String chatRoomsPage(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long groupId, @RequestParam("page") Optional<Integer> page,
                                @RequestParam("size") Optional<Integer> size, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        boolean isInGroup = roleRepository.existsByUserAndGroup(userDetails.getUser(), group);

        // user has to be a member of this group
        if (!isInGroup) {
            return "redirect:/groups";
        }

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        final PageRequest pageRequest = PageRequest.of(currentPage - 1, pageSize);

        Page<ChatRoom> chatRoomPage = chatRoomRepository.findAllByGroup(group, pageRequest);

        // mapping to another object for easier processing with thymeleaf
        Page<ChatRoomDTO> chatRoomDTOPage = chatRoomPage.map(chatRoom -> {
            boolean isChatRoomMember = chatRoomRepository.existsByIdAndUsers_Id(chatRoom.getId(), userDetails.getUser().getId());
            boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

            // TODO: include last message timestamp
            return new ChatRoomDTO(chatRoom.getId(), chatRoom.getUsers().size(), chatRoom.getChatRoomMessages().size(), chatRoom.getName(), null, isChatRoomMember, isTrainerInGroup);
        });

        model.addAttribute("chatRoomDTOPage", chatRoomDTOPage);

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId(), group);
        return "group-chat-rooms";
    }

    @GetMapping("/group/{id}/rooms/add")
    public String addChatRoomPage(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long groupId, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        final boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

        // user has to be a trainer of this group
        if (!isTrainerInGroup) {
            return "redirect:/group/" + groupId + "/rooms";
        }

        ChatRoom chatRoom = new ChatRoom();
        model.addAttribute("chatRoom", chatRoom);

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId(), group);
        return "add-group-chat-room";
    }

    @PostMapping("/group/{id}/room/create")
    public String createChatRoom(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("id") long groupId, @Valid ChatRoom chatRoom, BindingResult result, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));

        if (result.hasErrors()) {
            navigationService.addNavigationAttributes(model, userDetails.getUser().getId(), group);
            return "add-group-chat-room";
        }

        // need to retrieve user again because user from principal is a detached reference
        Optional<User> optionalUser = userRepository.findById(userDetails.getUser().getId());

        optionalUser.ifPresent(user -> {
            ChatRoom newChatRoom = new ChatRoom();
            newChatRoom.addUser(user);
            newChatRoom.setGroup(group);
            newChatRoom.setName(chatRoom.getName());

            chatRoomRepository.save(newChatRoom);
        });

        if (optionalUser.isEmpty()) {
            // user doesn't exist
            return "redirect:/logout";
        }

        return "redirect:/group/" + groupId + "/rooms";
    }

    @GetMapping("/group/{groupId}/room/{roomId}/leave")
    public String leaveChatRoom(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId, @PathVariable("roomId") long roomId, Model model) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid room Id:" + roomId));

        // need to retrieve user again because user from principal is a detached reference
        Optional<User> optionalUser = userRepository.findById(userDetails.getUser().getId());

        optionalUser.ifPresent(user -> {
            boolean isChatRoomMember = chatRoomRepository.existsByIdAndUsers_Id(roomId, user.getId());

            if (isChatRoomMember) {
                chatRoom.removeUser(user);

                chatRoomRepository.save(chatRoom);

                // check if there are any members remaining
                if (chatRoom.getUsers().isEmpty()) {
                    chatRoomRepository.delete(chatRoom); // TODO: make sure all messages are removed as well
                }
            }
        });

        if (optionalUser.isEmpty()) {
            // user doesn't exist
            return "redirect:/logout";
        }

        return "redirect:/group/" + groupId + "/rooms";
    }

}
