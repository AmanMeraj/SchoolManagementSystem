package com.school.schoolmanagement.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.school.schoolmanagement.Admin.Model.Entry;
import com.school.schoolmanagement.databinding.RowQuestionBank2Binding;

import java.util.ArrayList;

public class QuestionBankAdapter extends RecyclerView.Adapter<QuestionBankAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Entry> questionList;
    private OnQuestionBankClickListener clickListener;

    // ✅ Updated interface name and method names
    public interface OnQuestionBankClickListener {
        void onQuestionEditClick(Entry entry, int position);
        void onQuestionDeleteClick(Entry entry, int position);
    }

    // Constructor
    public QuestionBankAdapter(Context context, ArrayList<Entry> questionList, OnQuestionBankClickListener clickListener) {
        this.context = context;
        this.questionList = questionList != null ? questionList : new ArrayList<>();
        this.clickListener = clickListener;
    }

    // Optional setter (if constructor not used for listener)
    public void setOnQuestionBankClickListener(OnQuestionBankClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowQuestionBank2Binding binding = RowQuestionBank2Binding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Entry entry = questionList.get(position);
        holder.bind(entry, position);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public void updateList(ArrayList<Entry> newList) {
        this.questionList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addItems(ArrayList<Entry> newItems) {
        if (newItems != null && !newItems.isEmpty()) {
            int startPosition = questionList.size();
            questionList.addAll(newItems);
            notifyItemRangeInserted(startPosition, newItems.size());
        }
    }

    public void removeItem(int position) {
        if (position >= 0 && position < questionList.size()) {
            questionList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, questionList.size());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RowQuestionBank2Binding binding;

        public ViewHolder(RowQuestionBank2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Entry entry, int position) {
            binding.tvSr.setText(String.valueOf(position + 1));
            binding.tvTitle.setText(entry.getExamTitle() != null ? entry.getExamTitle() : "N/A");
            binding.tvClass.setText(entry.getClassName() != null ? entry.getClassName() : "N/A");
            binding.tvSubject.setText(entry.getSubject() != null ? entry.getSubject() : "N/A");
            binding.tvQType.setText(entry.getQuestionType() != null ? entry.getQuestionType() : "N/A");
            binding.tvMarks.setText(entry.getMark() != null ? entry.getMark() : "0");

            String question = entry.getQuestion() != null ? entry.getQuestion() : "No question";
            if (question.length() > 50) {
                question = question.substring(0, 50) + "...";
            }
            binding.tvQuestion.setText(question);

            binding.btnEdit.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onQuestionEditClick(entry, getAdapterPosition());
                }
            });

            binding.btnDelete.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onQuestionDeleteClick(entry, getAdapterPosition());
                }
            });

            // Optional: Row click can be added here if needed
        }
    }

    public Entry getItem(int position) {
        if (position >= 0 && position < questionList.size()) {
            return questionList.get(position);
        }
        return null;
    }

    public ArrayList<Entry> getAllItems() {
        return new ArrayList<>(questionList);
    }

    public void clearAll() {
        questionList.clear();
        notifyDataSetChanged();
    }
}
