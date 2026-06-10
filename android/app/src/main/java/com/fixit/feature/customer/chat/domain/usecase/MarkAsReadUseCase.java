package com.fixit.feature.customer.chat.domain.usecase;

import com.fixit.feature.customer.chat.domain.repository.ChatRepository;

import javax.inject.Inject;

public class MarkAsReadUseCase {
    private final ChatRepository chatRepository;

    @Inject
    public MarkAsReadUseCase(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public void execute(String conversationId, String currentUserId) {
        chatRepository.markAsRead(conversationId, currentUserId);
    }
}
