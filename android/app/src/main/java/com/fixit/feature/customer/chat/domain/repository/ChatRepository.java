package com.fixit.feature.customer.chat.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.chat.domain.model.ChatMessage;
import com.fixit.feature.customer.chat.domain.model.ChatPreview;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public interface ChatRepository {

    interface MessagesListener {
        void onMessagesUpdated(List<ChatMessage> messages);
        void onError(Exception e);
    }

    interface ConversationsListener {
        void onConversationsUpdated(List<ChatPreview> conversations);
        void onError(Exception e);
    }

    void sendMessage(String conversationId, String senderId, String receiverId,
                     String senderName, String receiverName, String content,
                     ResultCallback<Void> callback);

    ListenerRegistration listenMessages(String conversationId, String currentUserId, MessagesListener listener);

    ListenerRegistration listenConversations(String userId, String userRole, ConversationsListener listener);

    void markAsRead(String conversationId, String currentUserId);
}
