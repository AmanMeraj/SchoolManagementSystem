package com.school.schoolmanagement.Admin.Adapter;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.Subject;

import java.util.List;

public class SubjectHeaderAdapter extends RecyclerView.Adapter<SubjectHeaderAdapter.ViewHolder> {
    private List<Subject> subjects;

    public SubjectHeaderAdapter(List<Subject> subjects) {
        this.subjects = subjects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, parent.getContext().getResources().getDisplayMetrics()),
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(11);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(5, 5, 5, 5);

        // Set font if needed
        // textView.setTypeface(ResourcesCompat.getFont(parent.getContext(), R.font.plus_jakarta_sans_extrabold));

        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(subjects.get(position).getSubjectName());
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}
