package de.oth.seproject.clubhub.web.dto;

import java.time.LocalDateTime;

public record ChatRoomDTO(long id, int userCount, int messageCount, String name, LocalDateTime lastMessageTimestamp, boolean isChatRoomMember, boolean isTrainerInGroup) {
}
