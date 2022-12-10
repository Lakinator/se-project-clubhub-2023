package de.oth.seproject.clubhub.web.dto.chat;

/**
 * This dto will be sent to the listening websocket clients.
 */
public class OutboundGroupChatMessageDTO {

    private long chatMessageId;

    private long chatRoomId;

    private long userId;

    private String userName;

    private boolean isTrainerInGroup;

    private String content;

    private String timestamp;

    public OutboundGroupChatMessageDTO() {
        // -- //
    }

    public OutboundGroupChatMessageDTO(long chatMessageId, long chatRoomId, long userId, String userName, boolean isTrainerInGroup, String content, String timestamp) {
        this.chatMessageId = chatMessageId;
        this.chatRoomId = chatRoomId;
        this.userId = userId;
        this.userName = userName;
        this.isTrainerInGroup = isTrainerInGroup;
        this.content = content;
        this.timestamp = timestamp;
    }

    public long getChatMessageId() {
        return chatMessageId;
    }

    public void setChatMessageId(long chatMessageId) {
        this.chatMessageId = chatMessageId;
    }

    public long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isTrainerInGroup() {
        return isTrainerInGroup;
    }

    public void setTrainerInGroup(boolean trainerInGroup) {
        isTrainerInGroup = trainerInGroup;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
