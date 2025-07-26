package com.school.schoolmanagement.Adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Model.GradingItem;
import com.school.schoolmanagement.R;

import java.util.List;

public class GradingAdapter extends RecyclerView.Adapter<GradingAdapter.GradingViewHolder> {

    private List<GradingItem> gradingItems;

    public GradingAdapter(List<GradingItem> gradingItems) {
        this.gradingItems = gradingItems;
    }

    @NonNull
    @Override
    public GradingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_marks_grading, parent, false);
        return new GradingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradingViewHolder holder, int position) {
        GradingItem item = gradingItems.get(position);

        // Remove text watchers to prevent infinite loops
        holder.etGrade.removeTextChangedListener(holder.gradeWatcher);
        holder.etPercentFrom.removeTextChangedListener(holder.percentFromWatcher);
        holder.etPercentUpto.removeTextChangedListener(holder.percentUptoWatcher);
        holder.etStatus.removeTextChangedListener(holder.statusWatcher);

        // Set values
        holder.etGrade.setText(item.getGrade());
        holder.etPercentFrom.setText(item.getPercentFrom());
        holder.etPercentUpto.setText(item.getPercentUpto());
        holder.etStatus.setText(item.getStatus());

        // Add text watchers back
        holder.etGrade.addTextChangedListener(holder.gradeWatcher);
        holder.etPercentFrom.addTextChangedListener(holder.percentFromWatcher);
        holder.etPercentUpto.addTextChangedListener(holder.percentUptoWatcher);
        holder.etStatus.addTextChangedListener(holder.statusWatcher);
    }

    @Override
    public int getItemCount() {
        return gradingItems.size();
    }

    public void addItem() {
        gradingItems.add(new GradingItem("", "", "", ""));
        notifyItemInserted(gradingItems.size() - 1);
    }

    public void removeLastItem() {
        if (gradingItems.size() > 1) {
            gradingItems.remove(gradingItems.size() - 1);
            notifyItemRemoved(gradingItems.size());
        }
    }

    public List<GradingItem> getGradingItems() {
        return gradingItems;
    }

    public class GradingViewHolder extends RecyclerView.ViewHolder {
        EditText etGrade, etPercentFrom, etPercentUpto, etStatus;
        TextWatcher gradeWatcher, percentFromWatcher, percentUptoWatcher, statusWatcher;

        public GradingViewHolder(@NonNull View itemView) {
            super(itemView);

            etGrade = itemView.findViewById(R.id.et_grade);
            etPercentFrom = itemView.findViewById(R.id.et_percent_from);
            etPercentUpto = itemView.findViewById(R.id.et_percent_upto);
            etStatus = itemView.findViewById(R.id.et_status);

            // Initialize text watchers
            gradeWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        gradingItems.get(position).setGrade(s.toString());
                    }
                }
            };

            percentFromWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        gradingItems.get(position).setPercentFrom(s.toString());
                    }
                }
            };

            percentUptoWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        gradingItems.get(position).setPercentUpto(s.toString());
                    }
                }
            };

            statusWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        gradingItems.get(position).setStatus(s.toString());
                    }
                }
            };
        }
    }
}
