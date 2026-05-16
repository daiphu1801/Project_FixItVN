package com.fixit.feature.worker.home.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.feature.worker.home.domain.model.Appointment;
import com.fixit.databinding.ItemAppointmentBinding;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private final List<Appointment> items;

    public AppointmentAdapter(List<Appointment> items) {
        this.items = new ArrayList<>(items);
    }

    public void submitList(List<Appointment> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAppointmentBinding binding = ItemAppointmentBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment item = items.get(position);
        holder.binding.tvAppointmentTime.setText(item.getTime());
        holder.binding.tvAppointmentTitle.setText(item.getServiceTitle());
        holder.binding.tvAppointmentAddress.setText(item.getAddress());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemAppointmentBinding binding;

        ViewHolder(ItemAppointmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
