package com.school.schoolmanagement.SuperAdmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.SuperAdmin.Model.AddChapterModel;
import com.school.schoolmanagement.databinding.RowAddChapterBinding;

import java.util.List;

public class AdapterAddChapter extends RecyclerView.Adapter<AdapterAddChapter.SubjectViewHolder> {

    private final Context context;
    private final List<AddChapterModel> subjectList;
    private final OnSubjectClickListener listener;

    public interface OnSubjectClickListener {
        void onEditClick(AddChapterModel subject, int position);
        void onDeleteClick(AddChapterModel subject, int position);
    }

    public AdapterAddChapter(Context context, List<AddChapterModel> subjectList, OnSubjectClickListener listener) {
        this.context = context;
        this.subjectList = subjectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowAddChapterBinding binding = RowAddChapterBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new SubjectViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        AddChapterModel subject = subjectList.get(position);
        holder.binding.tvSerialNumber.setText(String.valueOf(position + 1));
        holder.binding.tvSubjectName.setText(subject.getName());
        holder.binding.tvSubjectPosition.setText(subject.getPosition());

        holder.binding.ivEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(subject, position);
            }
        });

        holder.binding.ivDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(subject, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        RowAddChapterBinding binding;

        public SubjectViewHolder(RowAddChapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<AddChapterModel> updatedList) {
        this.subjectList.clear();
        this.subjectList.addAll(updatedList);
        notifyDataSetChanged();
    }
}