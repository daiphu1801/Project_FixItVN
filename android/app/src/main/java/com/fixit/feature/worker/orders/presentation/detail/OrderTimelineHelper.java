package com.fixit.feature.worker.orders.presentation.detail;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import com.fixit.databinding.LayoutOrderTimelineCardBinding;
import com.fixit.feature.worker.orders.domain.model.JobStatus;

public class OrderTimelineHelper {
    private final LayoutOrderTimelineCardBinding binding;

    public OrderTimelineHelper(LayoutOrderTimelineCardBinding binding) {
        this.binding = binding;
    }

    public void updateTimelineUI(JobStatus status) {
        int currentStep = status.getStep();
        updateStep(1, currentStep);
        updateStep(2, currentStep);
        updateStep(3, currentStep);
        updateStep(4, currentStep);
        updateStep(5, currentStep);
    }

    private void updateStep(int stepIndex, int currentStepIndex) {
        int colorActive = Color.parseColor("#2563eb");
        int colorDone = Color.parseColor("#2563eb");
        int colorPending = Color.parseColor("#e2e8f0");
        int textActive = Color.parseColor("#2563eb");
        int textDone = Color.parseColor("#0f172a");
        int textPending = Color.parseColor("#64748b");

        if (stepIndex < currentStepIndex) {
            setStepState(stepIndex, true, false, colorDone, textDone);
        } else if (stepIndex == currentStepIndex) {
            setStepState(stepIndex, false, true, colorActive, textActive);
        } else {
            setStepState(stepIndex, false, false, colorPending, textPending);
        }
    }

    private void setStepState(int stepIndex, boolean isDone, boolean isActive, int color, int textColor) {
        float density = binding.getRoot().getContext().getResources().getDisplayMetrics().density;
        int padding = isDone ? 0 : (int) (5 * density);

        switch (stepIndex) {
            case 1:
                binding.step1Pulse.setVisibility(isActive ? View.VISIBLE : View.GONE);
                binding.step1Icon.setPadding(padding, padding, padding, padding);
                binding.step1Icon.setImageResource(
                        isDone ? com.fixit.R.drawable.ic_lucide_check_circle : com.fixit.R.drawable.circle_background);
                binding.step1Icon.setImageTintList(ColorStateList.valueOf(color));
                binding.step1Line.setBackgroundColor(isDone ? color : Color.parseColor("#e2e8f0"));
                binding.step1Title.setTextColor(textColor);
                break;
            case 2:
                binding.step2Pulse.setVisibility(isActive ? View.VISIBLE : View.GONE);
                binding.step2Icon.setPadding(padding, padding, padding, padding);
                binding.step2Icon.setImageResource(
                        isDone ? com.fixit.R.drawable.ic_lucide_check_circle : com.fixit.R.drawable.circle_background);
                binding.step2Icon.setImageTintList(ColorStateList.valueOf(color));
                binding.step2Line.setBackgroundColor(isDone ? color : Color.parseColor("#e2e8f0"));
                binding.step2Title.setTextColor(textColor);
                break;
            case 3:
                binding.step3Pulse.setVisibility(isActive ? View.VISIBLE : View.GONE);
                binding.step3Icon.setPadding(padding, padding, padding, padding);
                binding.step3Icon.setImageResource(
                        isDone ? com.fixit.R.drawable.ic_lucide_check_circle : com.fixit.R.drawable.circle_background);
                binding.step3Icon.setImageTintList(ColorStateList.valueOf(color));
                binding.step3Line.setBackgroundColor(isDone ? color : Color.parseColor("#e2e8f0"));
                binding.step3Title.setTextColor(textColor);
                break;
            case 4:
                binding.step4Pulse.setVisibility(isActive ? View.VISIBLE : View.GONE);
                binding.step4Icon.setPadding(padding, padding, padding, padding);
                binding.step4Icon.setImageResource(
                        isDone ? com.fixit.R.drawable.ic_lucide_check_circle : com.fixit.R.drawable.circle_background);
                binding.step4Icon.setImageTintList(ColorStateList.valueOf(color));
                binding.step4Line.setBackgroundColor(isDone ? color : Color.parseColor("#e2e8f0"));
                binding.step4Title.setTextColor(textColor);
                break;
            case 5:
                binding.step5Pulse.setVisibility(isActive ? View.VISIBLE : View.GONE);
                binding.step5Icon.setPadding(padding, padding, padding, padding);
                binding.step5Icon.setImageResource(
                        isDone ? com.fixit.R.drawable.ic_lucide_check_circle : com.fixit.R.drawable.circle_background);
                binding.step5Icon.setImageTintList(ColorStateList.valueOf(color));
                binding.step5Title.setTextColor(textColor);
                break;
        }
    }
}
