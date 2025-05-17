package com.school.schoolmanagement.Students.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.Model.StudentExamReport;
import com.school.schoolmanagement.databinding.RowStudentExaminationSeekBarBinding;

import java.util.List;

public class AdapterStudentExamSeekbar extends RecyclerView.Adapter<AdapterStudentExamSeekbar.ViewHolder>{



        private List<StudentExamReport> seekbarItems;
        private Context context;

        // Colors array for rotation
        private int[] colorResources = {
                R.color.light_blue,
                R.color.orange,
                R.color.green
        };

        // Drawable resources for seekbar progress
        private int[] progressDrawables = {
                R.drawable.sb_progress_drawable,
                R.drawable.sb_progress_drawable_orange,
                R.drawable.sb_progress_bar_green
        };

        // Drawable resources for seekbar thumbs
        private int[] thumbDrawables = {
                R.drawable.sb_thumb,
                R.drawable.sb_thumb_orange,
                R.drawable.sb_thumb_green
        };

        public AdapterStudentExamSeekbar(Context context, List<StudentExamReport> seekbarItems) {
            this.context = context;
            this.seekbarItems = seekbarItems;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RowStudentExaminationSeekBarBinding binding = RowStudentExaminationSeekBarBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            StudentExamReport item = seekbarItems.get(position);

            // Set title
            holder.binding.tvExaminationName.setText(item.getTitle());

            // Set progress
            holder.binding.presentStudentSeekBar.setProgress(item.getProgress());

            // Set percentage text
            holder.binding.percentTvPresentStudents.setText(item.getProgress() + "%");

            // Get color index for rotation (0, 1, 2, 0, 1, 2, ...)
            int colorIndex = position % colorResources.length;
            int colorResource = colorResources[colorIndex];

            // Set text colors
            int color = ContextCompat.getColor(context, colorResource);
            holder.binding.tvExaminationName.setTextColor(color);
            holder.binding.percentTvPresentStudents.setTextColor(color);

            // Set seekbar drawables based on position
            holder.binding.presentStudentSeekBar.setProgressDrawable(
                    ContextCompat.getDrawable(context, progressDrawables[colorIndex]));
            holder.binding.presentStudentSeekBar.setThumb(
                    ContextCompat.getDrawable(context, thumbDrawables[colorIndex]));
        }

        @Override
        public int getItemCount() {
            return seekbarItems.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            RowStudentExaminationSeekBarBinding binding;

            public ViewHolder(RowStudentExaminationSeekBarBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

}
