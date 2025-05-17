package com.school.schoolmanagement.Teachers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Teachers.Model.ModelQuestionPaper;

import java.util.List;

public class AdapterQuestionPaper extends RecyclerView.Adapter<AdapterQuestionPaper.QuestionPaperViewHolder> {

    private Context context;
    private List<ModelQuestionPaper> questionPaperList;
    private OnItemClickListener listener;

    // Interface for handling item clicks
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AdapterQuestionPaper(Context context, List<ModelQuestionPaper> questionPaperList) {
        this.context = context;
        this.questionPaperList = questionPaperList;
    }

    @NonNull
    @Override
    public QuestionPaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_create_question_paper, parent, false);
        return new QuestionPaperViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionPaperViewHolder holder, int position) {
        ModelQuestionPaper questionPaper = questionPaperList.get(position);

        holder.tvSr.setText(String.valueOf(position + 1));
        holder.tvTitle.setText(questionPaper.getTitle());
        holder.tvClass.setText(questionPaper.getClassName());
        holder.tvSubject.setText(questionPaper.getSubject());
        holder.tvExamDate.setText(questionPaper.getExamDate());
        holder.tvDuration.setText(questionPaper.getDuration());
    }

    @Override
    public int getItemCount() {
        return questionPaperList.size();
    }

    // Method to update the list data
    public void updateList(List<ModelQuestionPaper> newList) {
        this.questionPaperList = newList;
        notifyDataSetChanged();
    }

    public static class QuestionPaperViewHolder extends RecyclerView.ViewHolder {
        TextView tvSr, tvTitle, tvClass, tvSubject, tvExamDate, tvDuration;
        View editView, deleteView;

        public QuestionPaperViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            tvSr = itemView.findViewById(R.id.tv_row_sr);
            tvTitle = itemView.findViewById(R.id.tv_row_title);
            tvClass = itemView.findViewById(R.id.tv_row_class);
            tvSubject = itemView.findViewById(R.id.tv_row_subject);
            tvExamDate = itemView.findViewById(R.id.tv_row_exam_date);
            tvDuration = itemView.findViewById(R.id.tv_row_duration);

            // Edit and Delete buttons should be added to your row layout
            editView = itemView.findViewById(R.id.iv_edit);
            deleteView = itemView.findViewById(R.id.iv_delete);

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            if (editView != null) {
                editView.setOnClickListener(v -> {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onEditClick(position);
                        }
                    }
                });
            }

            if (deleteView != null) {
                deleteView.setOnClickListener(v -> {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                });
            }
        }
    }
}