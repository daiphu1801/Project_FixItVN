package com.fixit.ui.worker.orders.questionnaire;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.navigation.fragment.NavHostFragment;

import com.fixit.R;
import com.fixit.base.BaseFragment;
import com.fixit.databinding.FragmentWorkerQuestionnaireBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerQuestionnaireFragment extends BaseFragment<FragmentWorkerQuestionnaireBinding> {

    @Override
    protected FragmentWorkerQuestionnaireBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerQuestionnaireBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        binding.llWorkerTopbar.tvToolbarTitle.setText("Khảo sát sau việc");
        binding.llWorkerTopbar.btnBack.setOnClickListener(v -> {
            // Ngăn việc quay lại màn hình thanh toán
            Toast.makeText(requireContext(), "Vui lòng hoàn thành khảo sát", Toast.LENGTH_SHORT).show();
        });

        binding.btnSubmitQuestionnaire.setOnClickListener(v -> {
            float rating = binding.ratingBarCustomer.getRating();
            String comment = binding.etQuestionnaireComment.getText().toString();
            
            // Ở đây sẽ gọi API lưu kết quả khảo sát
            Toast.makeText(requireContext(), "Cảm ơn bạn đã phản hồi!", Toast.LENGTH_LONG).show();

            // Quay về màn hình chính và xóa stack các màn hình order cũ
            NavHostFragment.findNavController(this)
                    .navigate(R.id.workerHomeFragment);
        });
    }

    @Override
    protected void observeData() {
        // Không có data binding phức tạp cho màn hình này ở bản mock
    }
}
