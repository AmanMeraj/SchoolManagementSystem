package com.school.schoolmanagement.Teachers.Adapters;

import android.view.LayoutInflater;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.school.schoolmanagement.Teachers.Model.TestMarkModel;
import com.school.schoolmanagement.databinding.RowTestResultsBinding;

import java.util.List;

public class AdapterTestMark extends RecyclerView.Adapter<AdapterTestMark.TestMarkViewHolder> {

    private List<TestMarkModel> testMarkList;

    public AdapterTestMark(List<TestMarkModel> testMarkList) {
        this.testMarkList = testMarkList;
    }

    @NonNull
    @Override
    public TestMarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowTestResultsBinding binding = RowTestResultsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TestMarkViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TestMarkViewHolder holder, int position) {
        TestMarkModel model = testMarkList.get(position);
        holder.bind(model);
    }

    @Override
    public int getItemCount() {
        return testMarkList.size();
    }

    public void updateList(List<TestMarkModel> newList) {
        this.testMarkList = newList;
        notifyDataSetChanged();
    }

    static class TestMarkViewHolder extends RecyclerView.ViewHolder {

        private final RowTestResultsBinding binding;

        public TestMarkViewHolder(RowTestResultsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TestMarkModel model) {
            binding.tvRowDate.setText(model.getDate());
            binding.tvRowId.setText(model.getStudentId());
            binding.tvRowSName.setText(model.getStudentName());
            binding.tvRowClass.setText(model.getStudentClass());
            binding.tvRowSubject.setText(model.getSubject());
            binding.tvRowTotalMarks.setText(String.valueOf(model.getTotalMarks()));
            binding.tvRowObtained.setText(String.valueOf(model.getObtainedMarks()));
        }
    }
}
