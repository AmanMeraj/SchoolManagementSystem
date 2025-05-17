package com.school.schoolmanagement.SuperAdmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.SuperAdmin.Model.ModelAllBranches;
import com.school.schoolmanagement.databinding.RowBranchesBinding;

import java.util.List;

public class AdapterAllBranches extends RecyclerView.Adapter<AdapterAllBranches.BranchViewHolder> {

    private Context context;
    private List<ModelAllBranches> branchesList;
    private BranchClickListener listener;

    public AdapterAllBranches(Context context, List<ModelAllBranches> branchesList) {
        this.context = context;
        this.branchesList = branchesList;
    }

    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowBranchesBinding binding = RowBranchesBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new BranchViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchViewHolder holder, int position) {
        ModelAllBranches model = branchesList.get(position);

        // Set data to views
        holder.binding.tvName.setText(model.getSchoolName());
        holder.binding.studentsText.setText("No. of Students: " + model.getNumberOfStudents());
        holder.binding.adminText.setText("Admin: " + model.getAdminName());

        // Button click listener
        holder.binding.btnViewBranch.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBranchClick(model, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return branchesList != null ? branchesList.size() : 0;
    }

    // Update the data in the adapter
    public void updateData(List<ModelAllBranches> newList) {
        this.branchesList = newList;
        notifyDataSetChanged();
    }

    // Set click listener
    public void setOnBranchClickListener(BranchClickListener listener) {
        this.listener = listener;
    }

    // ViewHolder class
    static class BranchViewHolder extends RecyclerView.ViewHolder {
        RowBranchesBinding binding;

        public BranchViewHolder(RowBranchesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // Interface for click events
    public interface BranchClickListener {
        void onBranchClick(ModelAllBranches branch, int position);
    }
}