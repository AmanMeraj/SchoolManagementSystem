package com.school.schoolmanagement.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Model.ClassTestResult;
import com.school.schoolmanagement.databinding.ItemTestResultBinding;

import java.util.ArrayList;

public class ClassTestResultAdapter extends RecyclerView.Adapter<ClassTestResultAdapter.ViewHolder> {

    private ArrayList<ClassTestResult.Datum> resultsList = new ArrayList<>();
    private String resultType = "";

    public ClassTestResultAdapter(ArrayList<ClassTestResult.Datum> resultsList) {
        this.resultsList = resultsList;
    }

    public void updateResults(ArrayList<ClassTestResult.Datum> newResults, String type) {
        this.resultsList = newResults;
        this.resultType = type;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClassTestResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemTestResultBinding binding = ItemTestResultBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassTestResultAdapter.ViewHolder holder, int position) {
        holder.bind(resultsList.get(position));
    }

    @Override
    public int getItemCount() {
        return resultsList != null ? resultsList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTestResultBinding binding;

        public ViewHolder(@NonNull ItemTestResultBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ClassTestResult.Datum item) {
            if (item == null) return;

            binding.tvTestDate.setText(item.getDate());
            binding.tvId.setText(String.valueOf(item.getStudentId()));
            binding.tvStudentName.setText(item.getStudentName());
            binding.tvSubject.setText(item.getSubject());
            binding.tvTotalMarks.setText(String.valueOf(item.getTotalMarks()));
            binding.tvObtainedMarks.setText(String.valueOf(item.getObtainedMarks()));
        }
    }
}
