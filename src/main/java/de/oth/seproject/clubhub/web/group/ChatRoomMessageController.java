package de.oth.seproject.clubhub.web.group;

import de.oth.seproject.clubhub.config.ClubUserDetails;
import de.oth.seproject.clubhub.persistence.model.*;
import de.oth.seproject.clubhub.persistence.repository.*;
import de.oth.seproject.clubhub.web.dto.chat.GroupChatMessageDTO;
import de.oth.seproject.clubhub.web.dto.chat.NewGroupChatMessageDTO;
import de.oth.seproject.clubhub.web.service.NavigationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class ChatRoomMessageController {

    private final NavigationService navigationService;

    private final ChatRoomRepository chatRoomRepository;

    private final ChatRoomMessageRepository chatRoomMessageRepository;

    private final GroupRepository groupRepository;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    public ChatRoomMessageController(NavigationService navigationService, ChatRoomRepository chatRoomRepository, ChatRoomMessageRepository chatRoomMessageRepository, GroupRepository groupRepository, RoleRepository roleRepository, UserRepository userRepository) {
        this.navigationService = navigationService;
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMessageRepository = chatRoomMessageRepository;
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/group/{groupId}/room/{roomId}/chat")
    public String showChatPage(@AuthenticationPrincipal ClubUserDetails userDetails, @PathVariable("groupId") long groupId, @PathVariable("roomId") long roomId,
                               @RequestParam("size") Optional<Integer> size, Model model) {

        boolean isChatRoomMember = chatRoomRepository.existsByIdAndUsers_Id(roomId, userDetails.getUser().getId());

        // user must be a member of this chat room
        if (!isChatRoomMember) {
            return "redirect:/group/" + groupId + "/rooms";
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group Id:" + groupId));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid room Id:" + roomId));

        boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(userDetails.getUser(), group, RoleType.TRAINER);

        int maxMessages = size.orElse(50);

        final PageRequest pageRequest = PageRequest.of(0, maxMessages, Sort.by("createdOn").descending());

        Page<ChatRoomMessage> chatRoomMessagePage = chatRoomMessageRepository.findAllByChatRoom(chatRoom, pageRequest);

        model.addAttribute("chatRoom", chatRoom);
        model.addAttribute("chatRoomMessagePage", chatRoomMessagePage);

        navigationService.addNavigationAttributes(model, userDetails.getUser().getId(), group);
        return "show-group-chat";
    }

    @Transactional
    @MessageMapping("/deliver")
    @SendTo("/group-chat/new")
    public NewGroupChatMessageDTO onMessageDelivered(GroupChatMessageDTO message) throws Exception {

        boolean isChatRoomMember = chatRoomRepository.existsByIdAndUsers_Id(message.getChatRoomId(), message.getUserId());

        if (!isChatRoomMember) {
            throw new AccessDeniedException("Not a chat room member!");
        }

        User user = userRepository.findById(message.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + message.getUserId()));
        ChatRoom chatRoom = chatRoomRepository.findById(message.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid room Id:" + message.getChatRoomId()));

        boolean isTrainerInGroup = roleRepository.existsByUserAndGroupAndRoleName(user, chatRoom.getGroup(), RoleType.TRAINER);

        ChatRoomMessage chatRoomMessage = new ChatRoomMessage();
        chatRoomMessage.setUser(user);
        chatRoomMessage.setChatRoom(chatRoom);
        chatRoomMessage.setMessage(message.getMessage());
        chatRoomMessage.setCreatedOn(LocalDateTime.now());

        chatRoomMessageRepository.save(chatRoomMessage);

        return new NewGroupChatMessageDTO(chatRoom.getId(), user.getId(), HtmlUtils.htmlEscape(user.getFirstName() + " " + user.getLastName()), isTrainerInGroup, HtmlUtils.htmlEscape(message.getMessage()), chatRoomMessage.getCreatedOn().toString());
    }
}
