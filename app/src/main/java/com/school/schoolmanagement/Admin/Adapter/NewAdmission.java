package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.AdminDashboardNewAdmission;
import com.school.schoolmanagement.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class NewAdmission extends RecyclerView.Adapter<NewAdmission.MyViewHolder> {

    private Context context;
    private List<AdminDashboardNewAdmission.Datum> studentList;

    public NewAdmission(Context context, List<AdminDashboardNewAdmission.Datum> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.absent_students_admin_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AdminDashboardNewAdmission.Datum student = studentList.get(position);
        holder.nameSubject.setText(student.getName() + "\n" + student.getClassName() + "\n" + student.getNumber());

        // Handle base64 encoded profile picture
        if (student.getProfilePicture() != null && !student.getProfilePicture().isEmpty()) {
            try {
                // Check if it's a base64 string (usually starts with data:image or is pure base64)
                String base64String = student.getProfilePicture();

                // Remove data:image/jpeg;base64, or data:image/png;base64, prefix if present
                if (base64String.startsWith("data:image")) {
                    base64String = base64String.substring(base64String.indexOf(",") + 1);
                }

                // Decode base64 string to byte array
                byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);

                // Convert byte array to bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                if (bitmap != null) {
                    // Use Glide to load the bitmap (for consistent image handling, caching, etc.)
                    Glide.with(context)
                            .load(bitmap)
                            .placeholder(R.drawable.avatar2)
                            .into(holder.image);
                } else {
                    // If bitmap is null, use default image
                    holder.image.setImageResource(R.drawable.avatar2);
                }

            } catch (Exception e) {
                // If there's an error decoding, try loading as regular URL
                // This handles cases where the string might be a URL instead of base64
                Glide.with(context)
                        .load(student.getProfilePicture())
                        .placeholder(R.drawable.avatar2)
                        .error(R.drawable.avatar2) // Show default image on error
                        .into(holder.image);
            }
        } else {
            // No profile picture, use default
            holder.image.setImageResource(R.drawable.avatar2);
        }
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void updateList(List<AdminDashboardNewAdmission.Datum> newList) {
        this.studentList = newList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView nameSubject;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            nameSubject = itemView.findViewById(R.id.name_subject);
        }
    }
}