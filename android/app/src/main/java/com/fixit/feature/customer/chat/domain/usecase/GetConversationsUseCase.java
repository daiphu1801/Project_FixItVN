package com.fixit.feature.customer.chat.domain.usecase;

import com.fixit.feature.customer.chat.domain.repository.ChatRepository;
import com.google.firebase.firestore.ListenerRegistration;

import javax.inject.Inject;

public class GetConversationsUseCase {
    private final ChatRepository chatRepository;

    @Inject
    public GetConversationsUseCase(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public ListenerRegistration execute(String userId, String userRole, ChatRepository.ConversationsListener listener) {
        return chatRepository.listenConversations(userId, userRole, listener);
    }
}
