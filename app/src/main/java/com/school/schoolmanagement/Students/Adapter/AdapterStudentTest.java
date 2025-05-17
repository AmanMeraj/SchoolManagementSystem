package com.school.schoolmanagement.Students.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.school.schoolmanagement.Students.Model.StudentTestModel;
import com.school.schoolmanagement.databinding.RowStudentTestDataBinding;

import java.util.List;

public class AdapterStudentTest extends RecyclerView.Adapter<AdapterStudentTest.ViewHolder> {

    private Context context;
    private List<StudentTestModel> list;

    public AdapterStudentTest(Context context, List<StudentTestModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RowStudentTestDataBinding binding = RowStudentTestDataBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentTestModel model = list.get(position);
        holder.binding.tvDate.setText(model.getDate());
        holder.binding.tvClass.setText(model.getClassName());
        holder.binding.tvSubject.setText(model.getSubject());
        holder.binding.tvTotalMark.setText(model.getTotalMark());
        holder.binding.tvObtained.setText(model.getObtainedMark());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RowStudentTestDataBinding binding;

        public ViewHolder(@NonNull RowStudentTestDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

