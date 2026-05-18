package com.fixit.feature.customer.booking.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentNoteInputBinding;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NoteInputFragment extends BaseFragment<FragmentNoteInputBinding> {

    public static final String REQUEST_KEY = "note_request";
    public static final String BUNDLE_KEY = "note_text";
    public static final String TITLE_KEY = "title_text";
    public static final String TYPE_KEY = "input_type";

    @NonNull
    @Override
    protected FragmentNoteInputBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentNoteInputBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Lấy dữ liệu truyền vào
        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString(TITLE_KEY);
            String initialText = args.getString(BUNDLE_KEY);
            String type = args.getString(TYPE_KEY);

            if (title != null) binding.tvTitle.setText(title);
            if (initialText != null) binding.etDescription.setText(initialText);
        }

        // Nút Quay lại
        binding.btnBack.setOnClickListener(v -> {
            if (navController != null) navController.popBackStack();
        });

        // Nút Xong
        binding.btnDone.setOnClickListener(v -> {
            String note = binding.etDescription.getText() != null ? binding.etDescription.getText().toString() : "";
            Bundle result = new Bundle();
            result.putString(BUNDLE_KEY, note);
            if (args != null) result.putString(TYPE_KEY, args.getString(TYPE_KEY));
            
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            
            if (navController != null) navController.popBackStack();
        });
    }

    @Override
    protected void observeData() {
    }
}
