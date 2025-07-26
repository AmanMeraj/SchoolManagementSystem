package com.school.schoolmanagement.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.databinding.ItemDayAttendanceCellBinding;

import java.util.List;

public class DayStatusAdapter extends RecyclerView.Adapter<DayStatusAdapter.DayStatusViewHolder> {

    private Context context;
    private List<Character> attendanceStatusList;

    public DayStatusAdapter(Context context, List<Character> attendanceStatusList) {
        this.context = context;
        this.attendanceStatusList = attendanceStatusList;
    }

    @NonNull
    @Override
    public DayStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemDayAttendanceCellBinding binding = ItemDayAttendanceCellBinding.inflate(inflater, parent, false);
        return new DayStatusViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DayStatusViewHolder holder, int position) {
        char status = attendanceStatusList.get(position);
        holder.binding.tvDayCode.setText(String.valueOf(status));

        // Optional: Set text color based on status
        switch (status) {
            case 'P':
                holder.binding.tvDayCode.setTextColor(Color.parseColor("#4CAF50")); // Green
                break;
            case 'L':
                holder.binding.tvDayCode.setTextColor(Color.parseColor("#FFEB3B")); // Yellow
                break;
            case 'A':
                holder.binding.tvDayCode.setTextColor(Color.parseColor("#F44336")); // Red
                break;
            case 'H':
                holder.binding.tvDayCode.setTextColor(Color.parseColor("#333333")); // Black
                break;
            case 'F':
                holder.binding.tvDayCode.setTextColor(Color.parseColor("#2196F3")); // Blue
                break;
            default:
                holder.binding.tvDayCode.setTextColor(Color.GRAY);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return attendanceStatusList.size();
    }

    static class DayStatusViewHolder extends RecyclerView.ViewHolder {
        ItemDayAttendanceCellBinding binding;

        public DayStatusViewHolder(ItemDayAttendanceCellBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

