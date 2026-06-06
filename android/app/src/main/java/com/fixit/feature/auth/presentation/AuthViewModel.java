package com.fixit.feature.auth.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fixit.core.common.Result;
import com.fixit.feature.auth.domain.model.Session;
import com.fixit.feature.auth.domain.usecase.GetCurrentSessionUseCase;
import com.fixit.feature.auth.domain.usecase.LoginUseCase;
import com.fixit.feature.auth.domain.usecase.RegisterUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final GetCurrentSessionUseCase getCurrentSessionUseCase;

    private final MutableLiveData<AuthUiState> _uiState = new MutableLiveData<>(AuthUiState.idle());
    public LiveData<AuthUiState> uiState = _uiState;

    private final MutableLiveData<AuthEvent> _event = new MutableLiveData<>();
    public LiveData<AuthEvent> event = _event;

    @Inject
    public AuthViewModel(LoginUseCase loginUseCase, RegisterUseCase registerUseCase,
                         GetCurrentSessionUseCase getCurrentSessionUseCase) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
        this.getCurrentSessionUseCase = getCurrentSessionUseCase;
    }

    /**
     * Kiểm tra session đã lưu khi khởi động app.
     * Nếu có session hợp lệ → phát event navigate để bỏ qua màn login.
     * Nếu không → không làm gì, app hiển thị màn login bình thường.
     */
    public void checkExistingSession() {
        Session session = getCurrentSessionUseCase.execute();
        if (session != null) {
            _event.setValue(AuthEvent.navigate(session));
        }
    }

    public void login(String phone, String password, String role) {
        _uiState.setValue(AuthUiState.loading());
        loginUseCase.execute(phone, password, role, this::handleLoginResult);
    }

    public void register(String phone, String email, String password, String fullName, String role) {
        _uiState.setValue(AuthUiState.loading());
        registerUseCase.execute(phone, email, password, fullName, role, this::handleRegisterResult);
    }

    private void handleLoginResult(Result<Session> result) {
        if (result.isSuccess()) {
            _uiState.setValue(AuthUiState.success());
            _event.setValue(AuthEvent.navigate(result.getData()));
        } else {
            _uiState.setValue(AuthUiState.error(result.getError().getMessage()));
        }
    }

    private void handleRegisterResult(Result<Session> result) {
        if (result.isSuccess()) {
            _uiState.setValue(AuthUiState.success());
            _event.setValue(AuthEvent.registerSuccess(result.getData()));
        } else {
            _uiState.setValue(AuthUiState.error(result.getError().getMessage()));
        }
    }
}
