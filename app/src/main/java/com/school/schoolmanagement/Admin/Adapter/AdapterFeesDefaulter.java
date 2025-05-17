package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.FeesDefaulterModel;
import com.school.schoolmanagement.databinding.RowFeesDefaultersBinding;

import java.util.List;

public class AdapterFeesDefaulter extends RecyclerView.Adapter<AdapterFeesDefaulter.ViewHolder> {
    private Context context;
    private List<FeesDefaulterModel> defaulterList;

    public AdapterFeesDefaulter(Context context, List<FeesDefaulterModel> defaulterList) {
        this.context = context;
        this.defaulterList = defaulterList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowFeesDefaultersBinding binding = RowFeesDefaultersBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeesDefaulterModel model = defaulterList.get(position);

        holder.binding.numberTv.setText(model.getNumber());
        holder.binding.nameTv.setText(model.getName());

        // Call button click - Opens dial pad with the number
        holder.binding.callBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + model.getNumber()));
            context.startActivity(intent);
        });

        // Submit button click - Shows a toast message
        holder.binding.btnSubmit.setOnClickListener(v -> {
            Toast.makeText(context, "Submitted for " + model.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return defaulterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RowFeesDefaultersBinding binding;

        public ViewHolder(@NonNull RowFeesDefaultersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

