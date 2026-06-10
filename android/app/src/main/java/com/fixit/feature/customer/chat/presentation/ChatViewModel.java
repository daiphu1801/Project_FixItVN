package com.fixit.feature.customer.chat.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.storage.SessionStorage;
import com.fixit.feature.auth.domain.model.Session;
import com.fixit.feature.customer.chat.domain.model.ChatMessage;
import com.fixit.feature.customer.chat.domain.repository.ChatRepository;
import com.fixit.feature.customer.chat.domain.usecase.GetMessagesUseCase;
import com.fixit.feature.customer.chat.domain.usecase.MarkAsReadUseCase;
import com.fixit.feature.customer.chat.domain.usecase.SendMessageUseCase;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatViewModel extends ViewModel {

    private final GetMessagesUseCase getMessagesUseCase;
    private final SendMessageUseCase sendMessageUseCase;
    private final MarkAsReadUseCase markAsReadUseCase;
    private final SessionStorage sessionStorage;

    private final MutableLiveData<List<ChatMessage>> _messages = new MutableLiveData<>();
    public final LiveData<List<ChatMessage>> messages = _messages;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;

    private ListenerRegistration listenerRegistration;
    private String conversationId;
    private String currentUserId;
    private String currentUserName;
    private String otherUserId;
    private String otherUserName;

    @Inject
    public ChatViewModel(GetMessagesUseCase getMessagesUseCase,
                         SendMessageUseCase sendMessageUseCase,
                         MarkAsReadUseCase markAsReadUseCase,
                         SessionStorage sessionStorage) {
        this.getMessagesUseCase = getMessagesUseCase;
        this.sendMessageUseCase = sendMessageUseCase;
        this.markAsReadUseCase = markAsReadUseCase;
        this.sessionStorage = sessionStorage;
    }

    public void init(String otherUserId, String otherUserName) {
        this.otherUserId = otherUserId;
        this.otherUserName = otherUserName;

        Session session = sessionStorage.getSession();
        if (session == null || session.getUser() == null) {
            _error.setValue("Bạn chưa đăng nhập");
            return;
        }

        this.currentUserId = session.getUser().getId();
        // Lấy tên người dùng hiện tại (nếu trống thì gán mặc định tùy vai trò)
        String fullName = session.getUser().getFullName();
        if (fullName == null || fullName.isEmpty()) {
            fullName = "CUSTOMER".equalsIgnoreCase(session.getUser().getRole().name()) ? "Khách hàng" : "Thợ";
        }
        this.currentUserName = fullName;

        // Định dạng conversationId: customerId_workerId
        if ("CUSTOMER".equalsIgnoreCase(session.getUser().getRole().name())) {
            this.conversationId = currentUserId + "_" + otherUserId;
        } else {
            this.conversationId = otherUserId + "_" + currentUserId;
        }

        // Đánh dấu đã đọc khi mở phòng chat
        markAsRead();

        // Đăng ký lắng nghe tin nhắn thời gian thực
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }

        listenerRegistration = getMessagesUseCase.execute(conversationId, currentUserId, new ChatRepository.MessagesListener() {
            @Override
            public void onMessagesUpdated(List<ChatMessage> list) {
                _messages.setValue(list);
            }

            @Override
            public void onError(Exception e) {
                _error.setValue(e.getMessage());
            }
        });
    }

    public void sendMessage(String content) {
        if (content == null || content.trim().isEmpty() || conversationId == null) {
            return;
        }

        sendMessageUseCase.execute(
                conversationId,
                currentUserId,
                otherUserId,
                currentUserName,
                otherUserName,
                content.trim(),
                new ResultCallback<Void>() {
                    @Override
                    public void onResult(Result<Void> result) {
                        if (!result.isSuccess()) {
                            _error.setValue(result.getError().getMessage());
                        }
                    }
                }
        );
    }

    public void markAsRead() {
        if (conversationId != null && currentUserId != null) {
            markAsReadUseCase.execute(conversationId, currentUserId);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }
}
