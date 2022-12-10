package de.oth.seproject.clubhub.web.dto.chat;

/**
 * This dto will be sent by the websocket clients.
 */
public class InboundGroupChatMessageDTO {
    private String message;

    private long userId;

    private long chatRoomId;

    public InboundGroupChatMessageDTO() {
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
