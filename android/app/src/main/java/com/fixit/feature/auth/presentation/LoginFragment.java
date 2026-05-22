package com.fixit.feature.auth.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.fixit.R;
import com.fixit.databinding.FragmentLoginBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel viewModel;
    private String selectedRole = "CUSTOMER";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        if (getArguments() != null) {
            selectedRole = getArguments().getString("role", "CUSTOMER");
        }

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

        binding.btnLoginGG.setOnClickListener(v -> showGoogleExtraStep());

        binding.btnConfirmGoogle.setOnClickListener(v -> {
            // TODO: Complete Google login flow.
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

        viewModel.event.observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }

            String fullName = event.getSession() != null && event.getSession().getUser() != null
                    ? event.getSession().getUser().getFullName()
                    : "";
            Toast.makeText(getContext(), "Đăng nhập thành công! Chào " + fullName, Toast.LENGTH_SHORT).show();

            Class<?> destination = event.getType() == AuthEvent.Type.NAVIGATE_TO_WORKER
                    ? com.fixit.feature.worker.presentation.WorkerActivity.class
                    : com.fixit.feature.customer.presentation.CustomerActivity.class;

            android.content.Intent intent = new android.content.Intent(getActivity(), destination);
            startActivity(intent);
            requireActivity().finish();
        });

        viewModel.uiState.observe(getViewLifecycleOwner(), state -> {
            if (state == null) {
                return;
            }
            binding.btnLogin.setEnabled(!state.isLoading());
            binding.btnLogin.setText(state.isLoading() ? "Đang xử lý..." : "Đăng nhập");
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
