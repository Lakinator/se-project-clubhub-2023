package de.oth.seproject.clubhub.web.dto.chat;

public class NewGroupChatMessageDTO {

    private long chatRoomId;

    private long userId;

    private String userName;

    private boolean isTrainerInGroup;

    private String content;

    private String timestamp;

    public NewGroupChatMessageDTO() {
        // -- //
    }

    public NewGroupChatMessageDTO(long chatRoomId, long userId, String userName, boolean isTrainerInGroup, String content, String timestamp) {
        this.chatRoomId = chatRoomId;
        this.userId = userId;
        this.userName = userName;
        this.isTrainerInGroup = isTrainerInGroup;
        this.content = content;
        this.timestamp = timestamp;
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
