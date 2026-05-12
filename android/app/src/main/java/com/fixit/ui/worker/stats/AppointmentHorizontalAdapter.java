package com.fixit.ui.worker.stats;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.data.model.Appointment;
import com.fixit.databinding.ItemAppointmentHorizontalBinding;

import java.util.List;

public class AppointmentHorizontalAdapter extends RecyclerView.Adapter<AppointmentHorizontalAdapter.ViewHolder> {

    private final List<Appointment> appointments;

    public AppointmentHorizontalAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAppointmentHorizontalBinding binding = ItemAppointmentHorizontalBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.binding.tvAppointTime.setText(appointment.getTime());
        holder.binding.tvAppointService.setText(appointment.getServiceTitle());
        holder.binding.tvAppointAddress.setText(appointment.getAddress());
        
        // Mock countdown logic
        if (position == 0) {
            holder.binding.tvAppointCountdown.setText("Còn 2 giờ nữa");
        } else {
            holder.binding.tvAppointCountdown.setText("Còn " + (position * 2 + 3) + " giờ nữa");
        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemAppointmentHorizontalBinding binding;

        public ViewHolder(ItemAppointmentHorizontalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
