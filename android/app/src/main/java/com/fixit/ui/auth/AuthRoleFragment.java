package com.fixit.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fixit.databinding.FragmentAuthRoleBinding;
import com.fixit.ui.customer.CustomerActivity;
import com.fixit.ui.main.MainActivity;

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
            // Chuyển sang màn hình chính của Customer
            Intent intent = new Intent(requireActivity(), CustomerActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        binding.cardWorker.setOnClickListener(v -> {
            // Chuyển sang màn hình chính của Worker (MainActivity)
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
