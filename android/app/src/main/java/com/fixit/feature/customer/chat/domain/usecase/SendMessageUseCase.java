package com.fixit.feature.customer.chat.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.chat.domain.repository.ChatRepository;

import javax.inject.Inject;

public class SendMessageUseCase {
    private final ChatRepository chatRepository;

    @Inject
    public SendMessageUseCase(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public void execute(String conversationId, String senderId, String receiverId,
                        String senderName, String receiverName, String content,
                        ResultCallback<Void> callback) {
        chatRepository.sendMessage(conversationId, senderId, receiverId, senderName, receiverName, content, callback);
    }
}
