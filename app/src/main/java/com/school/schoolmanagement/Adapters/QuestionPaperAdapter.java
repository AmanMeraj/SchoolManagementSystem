package com.school.schoolmanagement.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.school.schoolmanagement.Admin.Model.QuestionPaperResponse;
import com.school.schoolmanagement.databinding.RowQuestionPaperBinding;

import java.util.ArrayList;

public class QuestionPaperAdapter extends RecyclerView.Adapter<QuestionPaperAdapter.QuestionPaperViewHolder> {

    private Context context;
    private ArrayList<QuestionPaperResponse.Datum> questionPapers;
    private OnItemClickListener listener;

    // Interface for handling click events
    public interface OnItemClickListener {
        void onEditClick(QuestionPaperResponse.Datum questionPaper, int position);
        void onDeleteClick(QuestionPaperResponse.Datum questionPaper, int position);
    }

    public QuestionPaperAdapter(Context context, ArrayList<QuestionPaperResponse.Datum> questionPapers) {
        this.context = context;
        this.questionPapers = questionPapers != null ? questionPapers : new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestionPaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowQuestionPaperBinding binding = RowQuestionPaperBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new QuestionPaperViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionPaperViewHolder holder, int position) {
        QuestionPaperResponse.Datum questionPaper = questionPapers.get(position);
        holder.bind(questionPaper, position);
    }

    @Override
    public int getItemCount() {
        return questionPapers.size();
    }

    // Method to update the list
    public void updateList(ArrayList<QuestionPaperResponse.Datum> newList) {
        this.questionPapers.clear();
        if (newList != null) {
            this.questionPapers.addAll(newList);
        }
        notifyDataSetChanged();
    }

    // Method to add single item
    public void addItem(QuestionPaperResponse.Datum questionPaper) {
        questionPapers.add(questionPaper);
        notifyItemInserted(questionPapers.size() - 1);
    }

    // Method to remove item
    public void removeItem(int position) {
        if (position >= 0 && position < questionPapers.size()) {
            questionPapers.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, questionPapers.size());
        }
    }

    // Method to update single item
    public void updateItem(int position, QuestionPaperResponse.Datum questionPaper) {
        if (position >= 0 && position < questionPapers.size()) {
            questionPapers.set(position, questionPaper);
            notifyItemChanged(position);
        }
    }

    public class QuestionPaperViewHolder extends RecyclerView.ViewHolder {
        private RowQuestionPaperBinding binding;

        public QuestionPaperViewHolder(@NonNull RowQuestionPaperBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(QuestionPaperResponse.Datum questionPaper, int position) {
            // Set serial number
            binding.tvSr.setText(String.valueOf(position + 1));

            // Set title
            binding.tvTitle.setText(questionPaper.getTitle() != null ? questionPaper.getTitle() : "N/A");

            // Set class names (join multiple classes if available)
            String classText = "N/A";
            if (questionPaper.getClassNames() != null && !questionPaper.getClassNames().isEmpty()) {
                if (questionPaper.getClassNames().size() == 1) {
                    classText = questionPaper.getClassNames().get(0);
                } else {
                    classText = String.join(", ", questionPaper.getClassNames());
                }
            }
            binding.tvClass.setText(classText);

            // Set subject
            binding.tvSubject.setText(questionPaper.getSubject() != null ? questionPaper.getSubject() : "N/A");

            // Set exam date
            binding.tvExamDate.setText(questionPaper.getExamDate() != null ? questionPaper.getExamDate() : "N/A");

            // Set duration
            binding.tvDuration.setText(questionPaper.getDuration() != null ? questionPaper.getDuration() : "N/A");

            // Set click listeners for action buttons
            binding.btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(questionPaper, getAdapterPosition());
                }
            });

            binding.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(questionPaper, getAdapterPosition());
                }
            });
        }
    }
}
