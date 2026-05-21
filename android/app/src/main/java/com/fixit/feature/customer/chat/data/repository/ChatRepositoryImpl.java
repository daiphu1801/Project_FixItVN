package com.fixit.feature.customer.chat.data.repository;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.chat.domain.model.ChatMessage;
import com.fixit.feature.customer.chat.domain.model.ChatPreview;
import com.fixit.feature.customer.chat.domain.repository.ChatRepository;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ChatRepositoryImpl implements ChatRepository {

    @Inject
    public ChatRepositoryImpl() {
    }

    @Override
    public void sendMessage(String conversationId, String senderId, String receiverId,
                             String senderName, String receiverName, String content,
                             ResultCallback<Void> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tách conversationId để xác định customerId và workerId
        // Định dạng conversationId là customerId_workerId
        String[] parts = conversationId.split("_");
        if (parts.length < 2) {
            callback.onResult(Result.error(new AppError("ID hội thoại không hợp lệ")));
            return;
        }
        String customerId = parts[0];
        String workerId = parts[1];

        // Cập nhật thông tin phòng hội thoại chính
        Map<String, Object> conversationUpdates = new HashMap<>();
        conversationUpdates.put("id", conversationId);
        conversationUpdates.put("lastMessage", content);
        conversationUpdates.put("lastMessageTime", FieldValue.serverTimestamp());
        conversationUpdates.put("senderId", senderId);
        conversationUpdates.put("customerId", customerId);
        conversationUpdates.put("workerId", workerId);

        // Xác định ai là thợ, ai là khách để gán tên tương ứng
        String customerNameValue = senderId.equals(customerId) ? senderName : receiverName;
        String workerNameValue = senderId.equals(workerId) ? senderName : receiverName;

        conversationUpdates.put("customerName", customerNameValue);
        conversationUpdates.put("workerName", workerNameValue);
        conversationUpdates.put("participants", Arrays.asList(customerId, workerId));

        // Trạng thái chưa đọc (unread) cho người nhận
        Map<String, Boolean> unreadFor = new HashMap<>();
        unreadFor.put(senderId, false);
        unreadFor.put(receiverId, true);
        conversationUpdates.put("unreadFor", unreadFor);

        WriteBatch batch = db.batch();

        DocumentReference convRef = db.collection("conversations").document(conversationId);
        batch.set(convRef, conversationUpdates, SetOptions.merge());

        // Tạo tin nhắn mới trong subcollection
        DocumentReference msgRef = convRef.collection("messages").document();
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("messageId", msgRef.getId());
        messageData.put("senderId", senderId);
        messageData.put("receiverId", receiverId);
        messageData.put("content", content);
        messageData.put("timestamp", FieldValue.serverTimestamp());

        batch.set(msgRef, messageData);

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onResult(Result.success(null));
            } else {
                callback.onResult(Result.error(new AppError(
                        task.getException() != null ? task.getException().getMessage() : "Lỗi gửi tin nhắn",
                        task.getException()
                )));
            }
        });
    }

    @Override
    public ListenerRegistration listenMessages(String conversationId, String currentUserId, MessagesListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        listener.onError(error);
                        return;
                    }
                    List<ChatMessage> list = new ArrayList<>();
                    if (value != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            String msgId = doc.getId();
                            String content = doc.getString("content");
                            String senderId = doc.getString("senderId");
                            com.google.firebase.Timestamp ts = doc.getTimestamp("timestamp");
                            String formattedTime;
                            if (ts != null) {
                                formattedTime = sdf.format(ts.toDate());
                            } else {
                                formattedTime = sdf.format(new Date()); // Dự phòng khi timestamp đang ghi trên server
                            }
                            boolean isSentByMe = senderId != null && senderId.equals(currentUserId);
                            list.add(new ChatMessage(msgId, content, formattedTime, isSentByMe));
                        }
                    }
                    listener.onMessagesUpdated(list);
                });
    }

    @Override
    public ListenerRegistration listenConversations(String userId, String userRole, ConversationsListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("conversations")
                .whereArrayContains("participants", userId)
                .orderBy("lastMessageTime", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        listener.onError(error);
                        return;
                    }
                    List<ChatPreview> list = new ArrayList<>();
                    if (value != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            String customerId = doc.getString("customerId");
                            String workerId = doc.getString("workerId");
                            String customerName = doc.getString("customerName");
                            String workerName = doc.getString("workerName");

                            // Nếu vai trò hiện tại là CUSTOMER, thì người đối diện là thợ (worker)
                            // Ngược lại, nếu vai trò là WORKER, người đối diện là khách (customer)
                            boolean isCustomer = "CUSTOMER".equalsIgnoreCase(userRole);
                            String otherUserId = isCustomer ? workerId : customerId;
                            String otherUserName = isCustomer ? workerName : customerName;

                            String lastMessage = doc.getString("lastMessage");
                            com.google.firebase.Timestamp ts = doc.getTimestamp("lastMessageTime");
                            String lastMessageTime = "";
                            if (ts != null) {
                                lastMessageTime = sdf.format(ts.toDate());
                            }

                            // Đọc trạng thái chưa đọc
                            Map<String, Object> unreadFor = (Map<String, Object>) doc.get("unreadFor");
                            boolean isUnread = false;
                            if (unreadFor != null && unreadFor.containsKey(userId)) {
                                isUnread = Boolean.TRUE.equals(unreadFor.get(userId));
                            }

                            // Giả định online dựa trên hoạt động gần nhất (hoặc mặc định online)
                            boolean isOnline = true;

                            list.add(new ChatPreview(otherUserId, otherUserName, lastMessage, lastMessageTime, isOnline, isUnread));
                        }
                    }
                    listener.onConversationsUpdated(list);
                });
    }

    @Override
    public void markAsRead(String conversationId, String currentUserId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("conversations")
                .document(conversationId)
                .update("unreadFor." + currentUserId, false);
    }
}
