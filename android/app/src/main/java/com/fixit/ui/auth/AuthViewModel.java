package com.fixit.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fixit.data.remote.dto.AuthResponse;
import com.fixit.data.repository.AuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;

    private final MutableLiveData<AuthResponse> _authResult = new MutableLiveData<>();
    public LiveData<AuthResponse> authResult = _authResult;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    @Inject
    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void login(String phone, String password) {
        _isLoading.setValue(true);
        authRepository.login(phone, password, new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful()) {
                    _authResult.setValue(response.body());
                } else {
                    _error.setValue("Đăng nhập thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                _isLoading.setValue(false);
                _error.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void register(String phone, String password, String fullName, String role) {
        _isLoading.setValue(true);
        authRepository.register(phone, password, fullName, role, new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful()) {
                    _authResult.setValue(response.body());
                } else {
                    _error.setValue("Đăng ký thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                _isLoading.setValue(false);
                _error.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
