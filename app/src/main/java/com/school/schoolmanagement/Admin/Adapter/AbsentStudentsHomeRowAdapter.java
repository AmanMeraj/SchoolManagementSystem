package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.AdminDashboardAbsentStudents;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.AbsentStudentsAdminRowBinding;

import java.util.List;

public class AbsentStudentsHomeRowAdapter extends RecyclerView.Adapter<AbsentStudentsHomeRowAdapter.MyViewHolder> {

    private Context context;
    private List<AdminDashboardAbsentStudents.Datum> studentList;

    public AbsentStudentsHomeRowAdapter(Context context, List<AdminDashboardAbsentStudents.Datum> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AbsentStudentsAdminRowBinding binding = AbsentStudentsAdminRowBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AdminDashboardAbsentStudents.Datum student = studentList.get(position);

        // Set name and class name
        holder.binding.nameSubject.setText(student.getName() + "\n" + student.getClassName());

        // Load image from base64 string
        if (student.getProfilePicture() != null && !student.getProfilePicture().isEmpty()) {
            try {
                // Convert base64 string to bitmap
                Bitmap bitmap = decodeBase64ToBitmap(student.getProfilePicture());
                if (bitmap != null) {
                    holder.binding.image.setImageBitmap(bitmap);
                } else {
                    // If decoding fails, use default image
                    holder.binding.image.setImageResource(R.drawable.avatar2);
                }
            } catch (Exception e) {
                // Handle any exception during base64 decoding
                e.printStackTrace();
                holder.binding.image.setImageResource(R.drawable.avatar2);
            }
        } else {
            // If no profile picture, use default image
            holder.binding.image.setImageResource(R.drawable.avatar2);
        }
    }

    @Override
    public int getItemCount() {
        return studentList != null ? studentList.size() : 0;
    }

    /**
     * Convert base64 string to bitmap
     * @param base64String The base64 encoded image string
     * @return Bitmap object or null if conversion fails
     */
    private Bitmap decodeBase64ToBitmap(String base64String) {
        try {
            // Remove data:image/jpeg;base64, or data:image/png;base64, prefix if present
            String cleanBase64 = base64String;
            if (base64String.contains(",")) {
                cleanBase64 = base64String.substring(base64String.indexOf(",") + 1);
            }

            // Decode base64 string to byte array
            byte[] decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT);

            // Convert byte array to bitmap
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        AbsentStudentsAdminRowBinding binding;

        public MyViewHolder(@NonNull AbsentStudentsAdminRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // Method to update the list
    public void updateList(List<AdminDashboardAbsentStudents.Datum> newStudentList) {
        this.studentList = newStudentList;
        notifyDataSetChanged();
    }
}