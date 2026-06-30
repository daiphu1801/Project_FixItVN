package com.fixit.feature.customer.chat.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fixit.core.storage.SessionStorage;
import com.fixit.feature.auth.domain.model.Session;
import com.fixit.feature.customer.chat.domain.model.ChatPreview;
import com.fixit.feature.customer.chat.domain.repository.ChatRepository;
import com.fixit.feature.customer.chat.domain.usecase.GetConversationsUseCase;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ConversationsViewModel extends ViewModel {

    private final GetConversationsUseCase getConversationsUseCase;
    private final SessionStorage sessionStorage;

    private final MutableLiveData<List<ChatPreview>> _conversations = new MutableLiveData<>();
    public final LiveData<List<ChatPreview>> conversations = _conversations;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;

    private ListenerRegistration listenerRegistration;

    @Inject
    public ConversationsViewModel(GetConversationsUseCase getConversationsUseCase,
                                  SessionStorage sessionStorage) {
        this.getConversationsUseCase = getConversationsUseCase;
        this.sessionStorage = sessionStorage;
    }

    public void startListening() {
        Session session = sessionStorage.getSession();
        if (session == null || session.getUser() == null) {
            _error.setValue("Bạn chưa đăng nhập");
            return;
        }

        String userId = session.getUser().getId();
        String userRole = session.getUser().getRole().name();

        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }

        listenerRegistration = getConversationsUseCase.execute(userId, userRole, new ChatRepository.ConversationsListener() {
            @Override
            public void onConversationsUpdated(List<ChatPreview> list) {
                _conversations.setValue(list);
            }

            @Override
            public void onError(Exception e) {
                _error.setValue(e.getMessage());
            }
        });
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
