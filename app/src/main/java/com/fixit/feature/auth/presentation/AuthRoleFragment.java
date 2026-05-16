package com.fixit.feature.auth.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.fixit.R;
import com.fixit.databinding.FragmentAuthRoleBinding;

public class AuthRoleFragment extends Fragment {

    private FragmentAuthRoleBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthRoleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.cardCustomer.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("role", "CUSTOMER");
            Navigation.findNavController(view).navigate(R.id.action_authRoleFragment_to_loginFragment, bundle);
        });

        binding.cardWorker.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("role", "WORKER");
            Navigation.findNavController(view).navigate(R.id.action_authRoleFragment_to_loginFragment, bundle);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
