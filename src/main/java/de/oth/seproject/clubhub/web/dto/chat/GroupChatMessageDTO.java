package de.oth.seproject.clubhub.web.dto.chat;

/**
 * This dto will be received from the websocket clients.
 */
public class GroupChatMessageDTO {
    private String message;

    private long userId;

    private long chatRoomId;

    public GroupChatMessageDTO() {
        // -- //
    }

    public String getMessage() {
        return message;
    }

    public void setName(String name) {
        this.message = name;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}
