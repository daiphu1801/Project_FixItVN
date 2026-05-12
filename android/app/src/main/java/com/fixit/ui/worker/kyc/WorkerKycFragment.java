package com.fixit.ui.worker.kyc;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.fixit.base.BaseFragment;
import com.fixit.databinding.FragmentWorkerKycBinding;

public class WorkerKycFragment extends BaseFragment<FragmentWorkerKycBinding> {

    @Override
    protected FragmentWorkerKycBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerKycBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Nút back trên toolbar
        binding.appBarLayout.toolbar.setTitle("Xác minh danh tính");
        binding.appBarLayout.toolbar.setNavigationOnClickListener(v -> 
                Navigation.findNavController(v).navigateUp());

        // Thiết lập dữ liệu cho dropdown loại giấy tờ
        String[] docTypes = new String[]{
                "Căn cước công dân (CCCD)",
                "Chứng minh nhân dân (CMND) cũ",
                "Bằng lái xe",
                "Hộ chiếu"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                docTypes
        );
        binding.actvDocType.setAdapter(adapter);

        // Đặt giá trị mặc định là CCCD
        binding.actvDocType.setText(docTypes[0], false);

        // Xử lý sự kiện click trên các khối ảnh (Placeholder)
        binding.cardUploadFront.setOnClickListener(v -> 
                Toast.makeText(requireContext(), "Mở camera chụp mặt trước", Toast.LENGTH_SHORT).show()
        );

        binding.cardUploadBack.setOnClickListener(v -> 
                Toast.makeText(requireContext(), "Mở camera chụp mặt sau", Toast.LENGTH_SHORT).show()
        );

        binding.cardUploadPortrait.setOnClickListener(v -> 
                Toast.makeText(requireContext(), "Mở camera chụp ảnh chân dung", Toast.LENGTH_SHORT).show()
        );

        // Xử lý nút Gửi xét duyệt -> Chuyển sang màn hình Đợi duyệt
        binding.btnSubmitKYC.setOnClickListener(v -> 
                Navigation.findNavController(v).navigate(com.fixit.R.id.workerKycPendingFragment)
        );
    }

    @Override
    protected void observeData() {
        // Fetch data nếu cần
    }
}
