package com.fixit.feature.auth.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fixit.core.common.Result;
import com.fixit.feature.auth.domain.model.Session;
import com.fixit.feature.auth.domain.usecase.GetCurrentSessionUseCase;
import com.fixit.feature.auth.domain.usecase.LoginUseCase;
import com.fixit.feature.auth.domain.usecase.LoginWithGoogleUseCase;
import com.fixit.feature.auth.domain.usecase.RegisterUseCase;
import com.fixit.feature.auth.domain.usecase.ForgotPasswordUseCase;
import com.fixit.feature.auth.domain.usecase.ResetPasswordUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final GetCurrentSessionUseCase getCurrentSessionUseCase;
    private final LoginWithGoogleUseCase loginWithGoogleUseCase;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;

    private final MutableLiveData<AuthUiState> _uiState = new MutableLiveData<>(AuthUiState.idle());
    public LiveData<AuthUiState> uiState = _uiState;

    private final MutableLiveData<AuthEvent> _event = new MutableLiveData<>();
    public LiveData<AuthEvent> event = _event;

    @Inject
    public AuthViewModel(LoginUseCase loginUseCase, RegisterUseCase registerUseCase,
                         GetCurrentSessionUseCase getCurrentSessionUseCase,
                         LoginWithGoogleUseCase loginWithGoogleUseCase,
                         ForgotPasswordUseCase forgotPasswordUseCase,
                         ResetPasswordUseCase resetPasswordUseCase) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
        this.getCurrentSessionUseCase = getCurrentSessionUseCase;
        this.loginWithGoogleUseCase = loginWithGoogleUseCase;
        this.forgotPasswordUseCase = forgotPasswordUseCase;
        this.resetPasswordUseCase = resetPasswordUseCase;
    }

    /**
     * Kiểm tra session đã lưu khi khởi động app.
     * Nếu có session hợp lệ → phát event navigate để bỏ qua màn login.
     * Nếu không → không làm gì, app hiển thị màn login bình thường.
     */
    public void checkExistingSession() {
        Session session = getCurrentSessionUseCase.execute();
        android.util.Log.d("FixIt_AuthViewModel", "checkExistingSession: session = " + session);
        if (session != null) {
            android.util.Log.d("FixIt_AuthViewModel", "checkExistingSession: session is NOT null! Navigating automatically to role: " + 
                (session.getUser() != null ? session.getUser().getRole() : "null"));
            _event.setValue(AuthEvent.navigate(session));
        } else {
            android.util.Log.d("FixIt_AuthViewModel", "checkExistingSession: session is null. Showing login screen.");
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

    public void loginWithGoogle(String idToken, String role) {
        _uiState.setValue(AuthUiState.loading());
        loginWithGoogleUseCase.execute(idToken, role, this::handleLoginResult);
    }

    public void forgotPassword(String email, String phone) {
        _uiState.setValue(AuthUiState.loading());
        forgotPasswordUseCase.execute(email, phone, result -> {
            if (result.isSuccess()) {
                _uiState.setValue(AuthUiState.success());
                _event.setValue(AuthEvent.forgotPasswordSuccess());
            } else {
                _uiState.setValue(AuthUiState.error(result.getError().getMessage()));
            }
        });
    }

    public void resetPassword(String email, String phone, String otpCode, String newPassword) {
        _uiState.setValue(AuthUiState.loading());
        resetPasswordUseCase.execute(email, phone, otpCode, newPassword, result -> {
            if (result.isSuccess()) {
                _uiState.setValue(AuthUiState.success());
                _event.setValue(AuthEvent.resetPasswordSuccess());
            } else {
                _uiState.setValue(AuthUiState.error(result.getError().getMessage()));
            }
        });
    }

    /** Gọi sau khi Fragment đã xử lý xong lỗi để reset state về idle */
    public void resetState() {
        _uiState.setValue(AuthUiState.idle());
    }

    /** Gọi sau khi Fragment đã consume event để tránh phát lại khi re-observe */
    public void consumeEvent() {
        _event.setValue(null);
    }
}
