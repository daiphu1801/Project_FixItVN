package com.fixit.feature.customer.chat.domain.model;

/**
 * Model đại diện cho 1 preview hội thoại trong danh sách tin nhắn.
 */
public class ChatPreview {

    private final String workerId;
    private final String workerName;
    private final String lastMessage;
    private final String lastMessageTime;
    private final boolean isOnline;
    private final boolean isUnread;

    public ChatPreview(String workerId, String workerName,
                       String lastMessage, String lastMessageTime,
                       boolean isOnline, boolean isUnread) {
        this.workerId = workerId;
        this.workerName = workerName;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.isOnline = isOnline;
        this.isUnread = isUnread;
    }

    public String getWorkerId() { return workerId; }
    public String getWorkerName() { return workerName; }
    public String getLastMessage() { return lastMessage; }
    public String getLastMessageTime() { return lastMessageTime; }
    public boolean isOnline() { return isOnline; }
    public boolean isUnread() { return isUnread; }
}
