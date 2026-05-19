package com.fixit.feature.customer.chat.domain.model;

/**
 * Model đại diện cho 1 tin nhắn trong cuộc hội thoại.
 */
public class ChatMessage {

    private final String messageId;
    private final String content;
    private final String timestamp;
    private final boolean isSentByMe;

    public ChatMessage(String messageId, String content,
                       String timestamp, boolean isSentByMe) {
        this.messageId = messageId;
        this.content = content;
        this.timestamp = timestamp;
        this.isSentByMe = isSentByMe;
    }

    public String getMessageId() { return messageId; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
    public boolean isSentByMe() { return isSentByMe; }
}
