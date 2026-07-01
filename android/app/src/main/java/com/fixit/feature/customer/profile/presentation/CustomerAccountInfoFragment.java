package com.fixit.feature.customer.profile.presentation;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.fixit.R;
import com.fixit.core.storage.SessionStorage;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerAccountInfoBinding;
import com.fixit.feature.auth.domain.model.Session;
import com.fixit.feature.auth.domain.model.User;
import com.fixit.feature.customer.profile.domain.model.CustomerProfile;
import com.fixit.feature.upload.domain.model.UploadPurpose;
import com.fixit.feature.upload.domain.model.UploadTargetType;
import com.fixit.feature.upload.presentation.UploadViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerAccountInfoFragment extends BaseFragment<FragmentCustomerAccountInfoBinding> {

    @Inject
    SessionStorage sessionStorage;

    private CustomerProfileViewModel viewModel;
    private UploadViewModel uploadViewModel;
    private com.fixit.core.common.AutoRefreshHelper autoRefreshHelper;

    // Image picker cho avatar khách hàng
    private final ActivityResultLauncher<String> pickAvatarLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    // Hiển thị ảnh mới ngay lập tức trên CircleImageView
                    Glide.with(this).load(uri).circleCrop().into(binding.ivUserAvatar);
                    // Upload lên server
                    uploadViewModel.upload(
                            requireContext(),
                            uri,
                            UploadPurpose.AVATAR,
                            UploadTargetType.USER_AVATAR,
                            null,
                            null,
                            "avatar",
                            null
                    );
                }
            }
    );

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

        // Click vào avatar -> mở gallery chọn ảnh đại diện
        binding.flAvatar.setOnClickListener(v -> pickAvatarLauncher.launch("image/*"));

        binding.btnSave.setOnClickListener(v -> {
            String fullName = binding.etFullName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim().toLowerCase();
            String gender = binding.tvGenderValue.getText().toString().trim();
            String dob = binding.tvDobValue.getText().toString().trim();

            if (fullName.isEmpty()) {
                showError("Vui lòng nhập họ và tên");
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
        uploadViewModel = new ViewModelProvider(this).get(UploadViewModel.class);

        // Theo dõi trạng thái tải (Loading) của cả thông tin profile và upload avatar
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Boolean uploadingVal = uploadViewModel.isUploading.getValue();
            boolean uploading = uploadingVal != null && uploadingVal;
            binding.btnSave.setEnabled(!isLoading && !uploading);
            binding.btnSave.setText(isLoading ? "Đang lưu..." : "Lưu thay đổi");
        });

        uploadViewModel.isUploading.observe(getViewLifecycleOwner(), isUploading -> {
            Boolean loadingVal = viewModel.getIsLoading().getValue();
            boolean loading = loadingVal != null && loadingVal;
            binding.btnSave.setEnabled(!isUploading && !loading);
            if (isUploading != null && isUploading) {
                binding.btnSave.setText("Đang tải ảnh lên...");
            } else {
                binding.btnSave.setText(loading ? "Đang lưu..." : "Lưu thay đổi");
            }
        });

        // Observe kết quả upload avatar
        uploadViewModel.uploadResult.observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.isSuccess()) {
                Toast.makeText(requireContext(), "Ảnh đại diện đã cập nhật", Toast.LENGTH_SHORT).show();
                if (result.getConfirmedUpload() != null) {
                    String fileUrl = result.getConfirmedUpload().getFileUrl();
                    if (fileUrl != null && !fileUrl.isEmpty()) {
                        requireContext().getSharedPreferences(com.fixit.core.common.Constants.PREF_NAME, android.content.Context.MODE_PRIVATE)
                                .edit()
                                .putString("user_avatar", fileUrl)
                                .apply();
                        Glide.with(this).load(fileUrl).circleCrop().into(binding.ivUserAvatar);
                    }

                    try {
                        Intent intent = new Intent("com.fixit.PROFILE_UPDATE");
                        requireContext().sendBroadcast(intent);
                    } catch (Exception ignored) {}
                }
            } else {
                Toast.makeText(requireContext(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
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

                // Ảnh đại diện
                String avatarUrl = profile.getAvatarUrl();
                if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
                    Glide.with(this)
                            .load(avatarUrl)
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .circleCrop()
                            .into(binding.ivUserAvatar);
                } else {
                    binding.ivUserAvatar.setImageResource(R.drawable.ic_person);
                }
            }
        });

        // Theo dõi thông báo lỗi
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showError(error);
            }
        });

        // Theo dõi kết quả lưu (Update)
        viewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
                    showSuccess("Cập nhật thông tin thành công");
                    
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
                    showError("Cập nhật thông tin thất bại");
                }
                viewModel.clearUpdateSuccess();
            }
        });

        // Tải dữ liệu ban đầu
        viewModel.loadProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (autoRefreshHelper == null) {
            autoRefreshHelper = new com.fixit.core.common.AutoRefreshHelper(
                    requireContext(),
                    0L,
                    () -> {
                        if (viewModel != null) {
                            viewModel.loadProfile();
                        }
                    },
                    "com.fixit.PROFILE_UPDATE"
            );
        }
        autoRefreshHelper.start();
    }

    @Override
    public void onPause() {
        if (autoRefreshHelper != null) {
            autoRefreshHelper.stop();
        }
        super.onPause();
    }
}
