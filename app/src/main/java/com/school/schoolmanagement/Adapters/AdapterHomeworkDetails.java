package com.school.schoolmanagement.Adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Model.HomeworkDetails;
import com.school.schoolmanagement.databinding.ItemHomeworkRowBinding;

import java.util.ArrayList;

public class AdapterHomeworkDetails extends RecyclerView.Adapter<AdapterHomeworkDetails.HomeworkViewHolder> {

    Context context;
    ArrayList<HomeworkDetails.Datum> list;

    public AdapterHomeworkDetails(Context context, ArrayList<HomeworkDetails.Datum> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public HomeworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHomeworkRowBinding binding = ItemHomeworkRowBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HomeworkViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeworkViewHolder holder, int position) {
        HomeworkDetails.Datum item = list.get(position);
        holder.binding.tvSr.setText(String.valueOf(position+1));
        holder.binding.tvTeacherName.setText( item.getTeacherName());
        holder.binding.tvClass.setText( item.getClassName());
        holder.binding.tvSubject.setText( item.getSubjectName());
        holder.binding.tvDate.setText(item.getHomeworkDate());

        // Clear previous views to avoid duplication on reuse
        holder.binding.layoutHomework.removeAllViews();

        // Dynamically add homework detail lines
        for (String hw : item.getHomeworkDetails()) {
            TextView textView = new TextView(context);
            textView.setText("- " + hw);
            textView.setTextSize(12);
            textView.setWidth(1200);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(8, 4, 8, 4);
            holder.binding.layoutHomework.addView(textView);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HomeworkViewHolder extends RecyclerView.ViewHolder {
        ItemHomeworkRowBinding binding;

        public HomeworkViewHolder(@NonNull ItemHomeworkRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

