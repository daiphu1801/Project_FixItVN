package com.fixit.feature.customer.profile.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerAccountInfoBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.card.MaterialCardView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import dagger.hilt.android.AndroidEntryPoint;

import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;

@AndroidEntryPoint
public class CustomerAccountInfoFragment extends BaseFragment<FragmentCustomerAccountInfoBinding> {

    private CustomerProfileViewModel viewModel;

    @NonNull
    @Override
    protected FragmentCustomerAccountInfoBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerAccountInfoBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        binding.ivBack.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });

        // Chọn giới tính
        binding.boxGender.setOnClickListener(v -> showGenderSelectionDialog());

        // Chọn ngày sinh
        binding.boxDob.setOnClickListener(v -> showDatePicker());

        binding.btnSave.setOnClickListener(v -> {
            String fullName = binding.etFullName.getText().toString().trim();
            if (fullName.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
                return;
            }
            if (viewModel != null) {
                viewModel.updateProfile(fullName);
            }
        });
    }

    private void showGenderSelectionDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.Theme_FixIt_BottomSheetDialog);
        dialog.setContentView(R.layout.dialog_gender_selection);

        MaterialCardView btnMale = dialog.findViewById(R.id.btnMale);
        MaterialCardView btnFemale = dialog.findViewById(R.id.btnFemale);
        MaterialCardView btnOther = dialog.findViewById(R.id.btnOther);
        RadioButton rbMale = dialog.findViewById(R.id.rbMale);
        RadioButton rbFemale = dialog.findViewById(R.id.rbFemale);
        RadioButton rbOther = dialog.findViewById(R.id.rbOther);

        String currentGender = binding.tvGenderValue.getText().toString();
        if (rbMale != null) rbMale.setChecked(currentGender.equals("Nam"));
        if (rbFemale != null) rbFemale.setChecked(currentGender.equals("Nữ"));
        if (rbOther != null) rbOther.setChecked(currentGender.equals("Khác"));

        if (btnMale != null) {
            btnMale.setOnClickListener(v -> {
                binding.tvGenderValue.setText("Nam");
                dialog.dismiss();
            });
        }
        if (btnFemale != null) {
            btnFemale.setOnClickListener(v -> {
                binding.tvGenderValue.setText("Nữ");
                dialog.dismiss();
            });
        }
        if (btnOther != null) {
            btnOther.setOnClickListener(v -> {
                binding.tvGenderValue.setText("Khác");
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày sinh")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            binding.tvDobValue.setText(sdf.format(calendar.getTime()));
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);

        viewModel.getProfileData().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                binding.tvUserNameDisplay.setText(profile.getFullName());
                if (binding.etFullName.getText().toString().isEmpty()) {
                    binding.etFullName.setText(profile.getFullName());
                }
            }
        });

        viewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                if (navController != null) {
                    navController.popBackStack();
                }
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.loadProfile();
    }
}
