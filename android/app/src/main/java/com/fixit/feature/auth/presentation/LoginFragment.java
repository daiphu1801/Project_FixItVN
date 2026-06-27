package com.fixit.feature.auth.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.fixit.R;
import com.fixit.databinding.FragmentLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel viewModel;
    private String selectedRole = "CUSTOMER";

    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    private String googleIdToken = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        // Dùng Activity scope để chia sẻ cùng ViewModel với AuthActivity
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        if (getArguments() != null) {
            selectedRole = getArguments().getString("role", "CUSTOMER");
        }

        // Configure Google Sign-In
        String webClientId = getString(R.string.google_web_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Register ActivityResultLauncher for Google Sign-In
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null) {
                                googleIdToken = account.getIdToken();
                                String email = account.getEmail();

                                // Điền email tự động và chuyển sang bước bổ sung thông tin
                                binding.etGoogleEmail.setText(email);
                                showGoogleExtraStep();
                            }
                        } catch (ApiException e) {
                            android.util.Log.e("FixIt_LoginFragment", "Google Sign-In failed", e);
                            String errorMsg = "Google Sign-In thất bại (mã " + e.getStatusCode() + "). ";
                            if (e.getStatusCode() == 10) {
                                errorMsg += "Vui lòng kiểm tra lại Web Client ID trong local.properties hoặc SHA-1 trong Firebase.";
                            }
                            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> {
            if (binding.layoutLoginGoogleExtra.getVisibility() == View.VISIBLE) {
                showMainLogin();
            } else {
                requireActivity().onBackPressed();
            }
        });

        binding.btnLoginGG.setOnClickListener(v -> {
            // Đăng xuất trước để đảm bảo hiển thị hộp thoại chọn tài khoản Google mọi lúc
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                googleSignInLauncher.launch(signInIntent);
            });
        });

        binding.btnConfirmGoogle.setOnClickListener(v -> {
            if (googleIdToken == null) {
                Toast.makeText(getContext(), "Không lấy được token Google. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.loginWithGoogle(googleIdToken, selectedRole);
        });

        binding.tvRegister.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment, getArguments()));

        binding.tvForgotPassword.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_forgotPasswordFragment, getArguments()));

        binding.btnLogin.setOnClickListener(v -> {
            String phone = binding.etPhone.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.login(phone, password, selectedRole);
        });

        // Chỉ observe uiState để hiển thị loading/error trong Fragment.
        // Việc navigate được xử lý tập trung tại AuthActivity observer.
        viewModel.uiState.observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            boolean isLoading = state.isLoading();
            binding.btnLogin.setEnabled(!isLoading);
            binding.btnLogin.setText(isLoading ? "Đang xử lý..." : "Đăng nhập");
            binding.btnLoginGG.setEnabled(!isLoading);
            binding.btnConfirmGoogle.setEnabled(!isLoading);
            binding.btnConfirmGoogle.setText(isLoading ? "Đang xử lý..." : "Tiếp tục");
            
            if (state.getErrorMessage() != null) {
                Toast.makeText(getContext(), state.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void showMainLogin() {
        binding.layoutLoginMain.setVisibility(View.VISIBLE);
        binding.layoutLoginGoogleExtra.setVisibility(View.GONE);
    }

    private void showGoogleExtraStep() {
        binding.layoutLoginMain.setVisibility(View.GONE);
        binding.layoutLoginGoogleExtra.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

