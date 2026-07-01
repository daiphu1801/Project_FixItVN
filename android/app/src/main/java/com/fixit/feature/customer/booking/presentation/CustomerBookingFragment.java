package com.fixit.feature.customer.booking.presentation;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import androidx.lifecycle.ViewModelProvider;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerBookingBinding;
import com.fixit.feature.customer.order.presentation.CustomerOrderViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.android.material.card.MaterialCardView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.widget.Toast;
import com.fixit.feature.upload.domain.model.UploadPurpose;
import com.fixit.feature.upload.presentation.UploadViewModel;
import com.fixit.feature.customer.profile.presentation.AddressViewModel;
import com.fixit.feature.customer.profile.domain.model.CustomerAddress;
import dagger.hilt.android.AndroidEntryPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

@AndroidEntryPoint
public class CustomerBookingFragment extends BaseFragment<FragmentCustomerBookingBinding> {

    private String problemDescription = "";
    private String specialNote = "";
    private String currentAddress = "207/17B Bùi Xương Trạch, Khương Đình, Thanh Xuân, Hà Nội";
    private double currentLatitude = 21.0285;
    private double currentLongitude = 105.8542;
    private String selectedTimeText = "Ngay bây giờ";
    private final List<Uri> selectedImages = new ArrayList<>();
    private UploadViewModel uploadViewModel;
    private AddressViewModel addressViewModel;
    private List<CustomerAddress> addressesList = new ArrayList<>();
    private String currentAddressId = null;
    private String currentLabel = "TỰ ĐỘNG";
    private boolean isAddressUserSelected = false;
    private int serviceId = 1;
    private String serviceName = "Dịch vụ sửa chữa";

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImages.add(uri);
                    addImageToUI(uri);
                    // Upload ảnh vấn đề lên server
                    uploadViewModel.upload(requireContext(), uri, UploadPurpose.BOOKING_ISSUE_IMAGE);
                }
            });

    @NonNull
    @Override
    protected FragmentCustomerBookingBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerBookingBinding.inflate(inflater, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uploadViewModel = new ViewModelProvider(this).get(UploadViewModel.class);
        addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);
        addressViewModel.loadAddresses();

        if (getArguments() != null) {
            serviceId = getArguments().getInt("serviceId", 1);
            serviceName = getArguments().getString("serviceName", "Dịch vụ sửa chữa");
            String subServiceName = getArguments().getString("subServiceName", "");
            if (!subServiceName.isEmpty()) {
                problemDescription = subServiceName;
            }
        }
        getParentFragmentManager().setFragmentResultListener(
                NoteInputFragment.REQUEST_KEY,
                this,
                (requestKey, bundle) -> {
                    String text = bundle.getString(NoteInputFragment.BUNDLE_KEY);
                    String type = bundle.getString(NoteInputFragment.TYPE_KEY);

                    if (text != null && type != null) {
                        if (type.equals("problem")) {
                            problemDescription = text;
                            updateProblemUI();
                        } else if (type.equals("note")) {
                            specialNote = text;
                            updateNoteUI();
                        }
                    }
                });

        getParentFragmentManager().setFragmentResultListener(
                CustomerLocationPickerFragment.REQUEST_KEY,
                this,
                (requestKey, bundle) -> {
                    String address = bundle.getString(CustomerLocationPickerFragment.ADDRESS_KEY);
                    if (address != null) {
                        currentAddress = address;
                        currentLatitude = bundle.getDouble(CustomerLocationPickerFragment.LATITUDE_KEY, 21.0285);
                        currentLongitude = bundle.getDouble(CustomerLocationPickerFragment.LONGITUDE_KEY, 105.8542);
                        currentAddressId = null;
                        currentLabel = "Bản đồ";
                        isAddressUserSelected = true;
                        updateLocationUI();
                    }
                });
    }

    @Override
    protected void setupViews() {
        CustomerOrderViewModel orderViewModel = new ViewModelProvider(requireActivity())
                .get(CustomerOrderViewModel.class);

        // Chặn người dùng nếu đang có đơn hàng hoạt động (trạng thái khác 0)
        if (orderViewModel.orderStatus.getValue() != null && orderViewModel.orderStatus.getValue() != 0) {
            Toast.makeText(requireContext(), "Bạn đang có đơn hàng hoạt động!", Toast.LENGTH_SHORT).show();
            if (navController != null) {
                navController.navigate(R.id.nav_customer_order);
            }
            return;
        }

        binding.tvHeaderTitle.setText(serviceName);

        binding.btnBack.setOnClickListener(v -> {
            if (navController != null)
                navController.popBackStack();
        });

        // Cấu hình osmdroid MapView
        Configuration.getInstance().load(requireContext(),
                requireContext().getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE));
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        binding.mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        binding.mapView.setMultiTouchControls(false); // Vô hiệu hoá chạm đa điểm để vuốt trang mượt mà

        // Nhấp vào bản đồ để chọn địa chỉ tương tự click vào cardLocation
        binding.mapView.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                v.performClick();
                showAddressSelectionBottomSheet();
            }
            return true;
        });

        binding.cardLocation.setOnClickListener(v -> showAddressSelectionBottomSheet());
        binding.tvLocationEdit.setOnClickListener(v -> showAddressSelectionBottomSheet());
        binding.cardMap.setOnClickListener(v -> showAddressSelectionBottomSheet());

        binding.cardProblem.setOnClickListener(v -> navigateToInput("problem", "Vấn đề của bạn", problemDescription));
        binding.cardNote.setOnClickListener(v -> navigateToInput("note", "Ghi chú", specialNote));

        binding.btnAddImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // Gắn sự kiện chọn thời gian
        binding.cardTime.setOnClickListener(v -> showTimeSelectionDialog());

        binding.btnFindWorker.setOnClickListener(v -> {
            // Lấy dữ liệu từ UI
            java.math.BigDecimal lat = new java.math.BigDecimal(String.valueOf(currentLatitude));
            java.math.BigDecimal lng = new java.math.BigDecimal(String.valueOf(currentLongitude));
            String desc = problemDescription + (specialNote.isEmpty() ? "" : "\nGhi chú: " + specialNote);

            // Phương thức thanh toán mặc định là CASH - khách sẽ chọn lại ở bước nghiệm thu
            // cuối
            String paymentMethod = "CASH";

            // 1. Cập nhật trạng thái: Đang tìm thợ & gọi API tạo đơn
            orderViewModel.createBooking(serviceId, currentAddress, lat, lng, desc, paymentMethod);

            // 2. Chuyển sang Tab Đơn hàng
            if (navController != null) {
                navController.navigate(R.id.nav_customer_order);
            }
        });

        updateProblemUI();
        updateNoteUI();
        updateLocationUI();
        updateTimeUI();

        for (Uri uri : selectedImages) {
            addImageToUI(uri);
        }
    }

    private void showTimeSelectionDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.Theme_FixIt_BottomSheetDialog);
        dialog.setContentView(R.layout.dialog_time_selection);

        MaterialCardView btnNow = dialog.findViewById(R.id.btnNow);
        MaterialCardView btnSchedule = dialog.findViewById(R.id.btnSchedule);
        RadioButton rbNow = dialog.findViewById(R.id.rbNow);
        RadioButton rbSchedule = dialog.findViewById(R.id.rbSchedule);

        if (rbNow != null && rbSchedule != null) {
            rbNow.setChecked(selectedTimeText.equals("Ngay bây giờ"));
            rbSchedule.setChecked(!selectedTimeText.equals("Ngay bây giờ"));
        }

        if (btnNow != null) {
            btnNow.setOnClickListener(v -> {
                selectedTimeText = "Ngay bây giờ";
                updateTimeUI();
                dialog.dismiss();
            });
        }

        if (btnSchedule != null) {
            btnSchedule.setOnClickListener(v -> {
                dialog.dismiss();
                showDateTimePicker();
            });
        }

        dialog.show();
    }

    private void showDateTimePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);

            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                    .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
                    .setTitleText("Chọn giờ")
                    .build();

            timePicker.addOnPositiveButtonClickListener(v -> {
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
                selectedTimeText = sdf.format(calendar.getTime());
                updateTimeUI();
            });

            timePicker.show(getParentFragmentManager(), "TIME_PICKER");
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void updateTimeUI() {
        binding.tvTimeValue.setText(selectedTimeText);
    }

    private void addImageToUI(Uri uri) {
        MaterialCardView cardView = new MaterialCardView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(80), dpToPx(80));
        params.setMargins(dpToPx(12), 0, 0, 0);
        cardView.setLayoutParams(params);
        cardView.setRadius(dpToPx(8));
        cardView.setCardElevation(0);
        cardView.setStrokeWidth(dpToPx(1));
        cardView.setStrokeColor(Color.parseColor("#E2E8F0"));

        ImageView imageView = new ImageView(requireContext());
        imageView.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        cardView.addView(imageView);

        Glide.with(this).load(uri).into(imageView);
        binding.layoutImageContainer.addView(cardView);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void navigateToInput(String type, String title, String currentText) {
        if (navController != null) {
            Bundle args = new Bundle();
            args.putString(NoteInputFragment.TYPE_KEY, type);
            args.putString(NoteInputFragment.TITLE_KEY, title);
            args.putString(NoteInputFragment.BUNDLE_KEY, currentText);
            navController.navigate(R.id.nav_customer_note_input, args);
        }
    }

    private void selectAddress(CustomerAddress address) {
        if (address == null)
            return;
        currentAddress = address.getAddress();
        currentLatitude = address.getLatitude() != null ? address.getLatitude() : 21.0285;
        currentLongitude = address.getLongitude() != null ? address.getLongitude() : 105.8542;
        currentAddressId = address.getId();
        currentLabel = address.getLabel() != null ? address.getLabel() : "ĐỊA CHỈ";
        isAddressUserSelected = true;
        updateLocationUI();
    }

    private void showAddressSelectionBottomSheet() {
        CustomerAddressSelectionBottomSheet bottomSheet = CustomerAddressSelectionBottomSheet.newInstance(addressesList,
                currentAddressId);
        bottomSheet.setListener(new CustomerAddressSelectionBottomSheet.OnAddressSelectedListener() {
            @Override
            public void onAddressSelected(CustomerAddress address) {
                selectAddress(address);
            }

            @Override
            public void onSelectFromMapSelected() {
                if (navController != null) {
                    navController.navigate(R.id.nav_customer_location_picker);
                }
            }

            @Override
            public void onAddNewAddressSelected() {
                if (navController != null) {
                    navController.navigate(R.id.nav_customer_address_add_edit);
                }
            }
        });
        bottomSheet.show(getParentFragmentManager(), "ADDRESS_SELECTION");
    }

    private void updateLocationUI() {
        binding.tvLocationAddress.setText(currentAddress);
        if (currentLabel != null && !currentLabel.isEmpty()) {
            binding.tvLocationBadge.setText(currentLabel.toUpperCase());
            binding.tvLocationBadge.setVisibility(android.view.View.VISIBLE);
        } else {
            binding.tvLocationBadge.setVisibility(android.view.View.GONE);
        }
        updateMapLocation();
    }

    private void updateMapLocation() {
        if (binding == null || binding.mapView == null)
            return;
        GeoPoint geoPoint = new GeoPoint(currentLatitude, currentLongitude);
        binding.mapView.getController().setZoom(16.5);
        binding.mapView.getController().setCenter(geoPoint);

        binding.mapView.getOverlays().clear();
        Marker marker = new Marker(binding.mapView);
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Vị trí của bạn");
        binding.mapView.getOverlays().add(marker);
        binding.mapView.invalidate();
    }

    private void updateProblemUI() {
        if (!problemDescription.isEmpty()) {
            binding.tvProblemDesc.setText(problemDescription);
            binding.tvProblemDesc.setTextColor(Color.parseColor("#111827"));
        } else {
            binding.tvProblemDesc.setText("Mô tả chi tiết vấn đề đang gặp");
            binding.tvProblemDesc.setTextColor(Color.parseColor("#9CA3AF"));
        }
    }

    private void updateNoteUI() {
        if (!specialNote.isEmpty()) {
            binding.tvNoteDesc.setText(specialNote);
            binding.tvNoteDesc.setTextColor(Color.parseColor("#111827"));
        } else {
            binding.tvNoteDesc.setText("Thêm yêu cầu đặc biệt...");
            binding.tvNoteDesc.setTextColor(Color.parseColor("#9CA3AF"));
        }
    }

    @Override
    protected void observeData() {
        CustomerOrderViewModel orderViewModel = new ViewModelProvider(requireActivity())
                .get(CustomerOrderViewModel.class);

        // Theo dõi trạng thái đơn hàng để tự động chuyển hướng khi có đơn hàng hoạt động
        orderViewModel.orderStatus.observe(getViewLifecycleOwner(), status -> {
            if (status != null && status != 0) {
                if (navController != null && navController.getCurrentDestination() != null
                        && navController.getCurrentDestination().getId() == R.id.nav_customer_booking) {
                    Toast.makeText(requireContext(), "Bạn đang có đơn hàng hoạt động!", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.nav_customer_order);
                }
            }
        });

        // Observe danh sách sổ địa chỉ
        addressViewModel.getAddressesData().observe(getViewLifecycleOwner(), addresses -> {
            if (addresses == null)
                return;
            addressesList = addresses;
            if (!isAddressUserSelected && !addresses.isEmpty()) {
                CustomerAddress defaultAddr = null;
                for (CustomerAddress addr : addresses) {
                    if (addr.getDefaultAddress() != null && addr.getDefaultAddress()) {
                        defaultAddr = addr;
                        break;
                    }
                }
                if (defaultAddr == null) {
                    defaultAddr = addresses.get(0);
                }
                selectAddress(defaultAddr);
            }
        });

        // Observe kết quả upload ảnh vấn đề
        uploadViewModel.uploadResult.observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            if (result.isSuccess()) {
                Toast.makeText(requireContext(), "Ảnh đã tải lên thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        uploadViewModel.isUploading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && binding.layoutLoading != null) {
                binding.layoutLoading.getRoot()
                        .setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null && binding.mapView != null) {
            binding.mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding != null && binding.mapView != null) {
            binding.mapView.onPause();
        }
    }
}
