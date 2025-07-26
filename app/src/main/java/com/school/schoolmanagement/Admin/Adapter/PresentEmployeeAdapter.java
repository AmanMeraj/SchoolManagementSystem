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

import com.school.schoolmanagement.Admin.Model.AdminDashboardPresentEmployee;
import com.school.schoolmanagement.R;

import java.util.List;

public class PresentEmployeeAdapter extends RecyclerView.Adapter<PresentEmployeeAdapter.MyViewHolder> {

    private Context context;
    private List<AdminDashboardPresentEmployee.Datum> employeeList;

    public PresentEmployeeAdapter(Context context, List<AdminDashboardPresentEmployee.Datum> employeeList) {
        this.context = context;
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.absent_students_admin_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AdminDashboardPresentEmployee.Datum employee = employeeList.get(position);

        // Set name and number
        holder.nameSubject.setText(employee.getName() + "\n" + employee.getNumber());

        // Handle Base64 image
        if (employee.getProfilePicture() != null && !employee.getProfilePicture().isEmpty()) {
            try {
                // Remove data:image/png;base64, prefix if present
                String base64String = employee.getProfilePicture();
                if (base64String.contains(",")) {
                    base64String = base64String.split(",")[1];
                }

                // Decode base64 to bitmap
                byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                if (bitmap != null) {
                    holder.image.setImageBitmap(bitmap);
                } else {
                    // Set default image if bitmap is null
                    holder.image.setImageResource(R.drawable.avatar2);
                }
            } catch (Exception e) {
                // Set default image if decoding fails
                holder.image.setImageResource(R.drawable.avatar2);
            }
        } else {
            // Set default image if no profile picture
            holder.image.setImageResource(R.drawable.avatar2);
        }
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public void updateList(List<AdminDashboardPresentEmployee.Datum> newEmployeeList) {
        this.employeeList = newEmployeeList;
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