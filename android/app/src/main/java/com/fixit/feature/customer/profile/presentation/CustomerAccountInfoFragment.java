package com.fixit.feature.customer.profile.presentation;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.fixit.R;
import com.fixit.core.storage.SessionStorage;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerAccountInfoBinding;
import com.fixit.feature.auth.domain.model.Session;
import com.fixit.feature.auth.domain.model.User;
import com.fixit.feature.customer.profile.domain.model.CustomerProfile;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;

@AndroidEntryPoint
public class CustomerAccountInfoFragment extends BaseFragment<FragmentCustomerAccountInfoBinding> {

    @Inject
    SessionStorage sessionStorage;

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

        // Xóa text placeholder cứng trong XML, chờ dữ liệu thật từ server
        binding.tvUserNameDisplay.setText("");
        binding.etFullName.setText("");
        binding.etEmail.setText("");
        binding.tvPhone.setText("");
        binding.tvGenderValue.setText("Chưa cập nhật");
        binding.tvDobValue.setText("Chưa cập nhật");

        // Chọn giới tính
        binding.boxGender.setOnClickListener(v -> showGenderSelectionDialog());

        // Chọn ngày sinh
        binding.boxDob.setOnClickListener(v -> showDatePicker());

        binding.btnSave.setOnClickListener(v -> {
            String fullName = binding.etFullName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String gender = binding.tvGenderValue.getText().toString().trim();
            String dob = binding.tvDobValue.getText().toString().trim();

            if (fullName.isEmpty()) {
                showToast("Vui lòng nhập họ và tên");
                return;
            }

            // Chuẩn hóa giá trị "Chưa cập nhật" thành chuỗi rỗng trước khi gửi
            String genderToSend = gender.equals("Chưa cập nhật") ? "" : gender;
            String dobToSend = dob.equals("Chưa cập nhật") ? "" : dob;
            String emailToSend = email.isEmpty() ? null : email;

            viewModel.updateProfile(fullName, emailToSend, genderToSend, dobToSend);
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
        long selection = MaterialDatePicker.todayInUtcMilliseconds();
        String currentDob = binding.tvDobValue.getText().toString().trim();
        if (!currentDob.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(currentDob));
                selection = cal.getTimeInMillis();
            } catch (Exception ignored) {}
        }

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày sinh")
                .setSelection(selection)
                .build();

        datePicker.addOnPositiveButtonClickListener(selectionTime -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selectionTime);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            binding.tvDobValue.setText(sdf.format(calendar.getTime()));
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);

        // Theo dõi trạng thái tải (Loading)
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.btnSave.setEnabled(!isLoading);
            binding.btnSave.setText(isLoading ? "Đang lưu..." : "Lưu thay đổi");
        });

        // Theo dõi thông tin người dùng được tải về
        viewModel.getProfileData().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                // Tên
                binding.tvUserNameDisplay.setText(profile.getFullName() != null ? profile.getFullName() : "");
                binding.etFullName.setText(profile.getFullName() != null ? profile.getFullName() : "");

                // Email
                binding.etEmail.setText(profile.getEmail() != null ? profile.getEmail() : "");

                // Số điện thoại
                String phone = profile.getPhoneNumber();
                binding.tvPhone.setText(phone != null && !phone.isEmpty() ? phone : "Chưa cập nhật");

                // Giới tính
                String gender = profile.getGender();
                binding.tvGenderValue.setText(gender != null && !gender.isEmpty() ? gender : "Chưa cập nhật");

                // Ngày sinh
                String dob = profile.getDob();
                binding.tvDobValue.setText(dob != null && !dob.isEmpty() ? dob : "Chưa cập nhật");
            }
        });

        // Theo dõi thông báo lỗi
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showToast(error);
            }
        });

        // Theo dõi kết quả lưu (Update)
        viewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
                    showToast("Cập nhật thông tin thành công");
                    
                    // Cập nhật thông tin trong SessionStorage
                    CustomerProfile updatedProfile = viewModel.getProfileData().getValue();
                    if (updatedProfile != null && sessionStorage != null) {
                        Session currentSession = sessionStorage.getSession();
                        if (currentSession != null && currentSession.getUser() != null) {
                            User updatedUser = new User(
                                    currentSession.getUser().getId(),
                                    currentSession.getUser().getPhone(),
                                    updatedProfile.getFullName(),
                                    currentSession.getUser().getRole()
                            );
                            Session newSession = new Session(
                                    currentSession.getAccessToken(),
                                    currentSession.getRefreshToken(),
                                    updatedUser
                            );
                            sessionStorage.saveSession(newSession);
                        }
                    }

                    // Phát tín hiệu cập nhật để Fragment cha cập nhật lại giao diện ngay lập tức
                    try {
                        Intent intent = new Intent("com.fixit.PROFILE_UPDATE");
                        requireContext().sendBroadcast(intent);
                    } catch (Exception ignored) {}

                    // Quay lại trang trước
                    if (navController != null) {
                        navController.popBackStack();
                    }
                } else {
                    showToast("Cập nhật thông tin thất bại");
                }
                viewModel.clearUpdateSuccess();
            }
        });

        // Tải dữ liệu ban đầu
        viewModel.loadProfile();
    }
}
