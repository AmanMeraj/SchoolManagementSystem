package com.school.schoolmanagement.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.school.schoolmanagement.Model.TimeSlotModel;
import com.school.schoolmanagement.databinding.RowTimeslotBinding;

import java.util.List;

public class AdapterTimeSlot extends RecyclerView.Adapter<AdapterTimeSlot.TimeSlotViewHolder> {

    private final List<TimeSlotModel> list;
    private final Context context;

    public AdapterTimeSlot(Context context, List<TimeSlotModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RowTimeslotBinding binding = RowTimeslotBinding.inflate(inflater, parent, false);
        return new TimeSlotViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlotModel model = list.get(position);
        if (model.isLeft()) {
            holder.binding.leftCard.setVisibility(View.VISIBLE);
            holder.binding.rightCard.setVisibility(View.GONE);
            holder.binding.tvLeftTitle.setText(model.getTitle());
            holder.binding.tvLeftTime.setText(model.getTime());
        } else {
            holder.binding.leftCard.setVisibility(View.GONE);
            holder.binding.rightCard.setVisibility(View.VISIBLE);
            holder.binding.tvRightTitle.setText(model.getTitle());
            holder.binding.tvRightTime.setText(model.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        RowTimeslotBinding binding;

        public TimeSlotViewHolder(@NonNull RowTimeslotBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

