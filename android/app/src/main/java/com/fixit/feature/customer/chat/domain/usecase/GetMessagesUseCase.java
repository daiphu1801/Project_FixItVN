package com.fixit.feature.customer.chat.domain.usecase;

import com.fixit.feature.customer.chat.domain.repository.ChatRepository;
import com.google.firebase.firestore.ListenerRegistration;

import javax.inject.Inject;

public class GetMessagesUseCase {
    private final ChatRepository chatRepository;

    @Inject
    public GetMessagesUseCase(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public ListenerRegistration execute(String conversationId, String currentUserId, ChatRepository.MessagesListener listener) {
        return chatRepository.listenMessages(conversationId, currentUserId, listener);
    }
}
