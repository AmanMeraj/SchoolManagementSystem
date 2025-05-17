package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.ImageUtils;

import java.util.List;

public class AdapterAllEmployees extends RecyclerView.Adapter<AdapterAllEmployees.EmployeeViewHolder> {

    private final Context context;
    private final List<AllEmployees> employeeList;
    private final OnEmployeeClickListener listener;

    public AdapterAllEmployees(Context context, List<AllEmployees> employeeList, OnEmployeeClickListener listener) {
        this.context = context;
        this.employeeList = employeeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_all_students, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        AllEmployees employee = employeeList.get(position);

        // Set employee name
        holder.tvName.setText(employee.name);

        // Set employee role
        holder.tvRole.setText(employee.employeeRole);

        // Load and set the profile image
        if (employee.profilePicture != null && !employee.profilePicture.isEmpty()) {
            // Compress image before loading with Glide
            String compressedImage = ImageUtils.compressBase64Image(employee.profilePicture);

            Glide.with(context)
                    .load(ImageUtils.base64ToByteArray(compressedImage))
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.avatar2)
                            .error(R.drawable.avatar2)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .circleCrop())
                    .into(holder.ivProfile);
        } else {
            // Load default image if no profile picture
            Glide.with(context)
                    .load(R.drawable.avatar2)
                    .apply(new RequestOptions().circleCrop())
                    .into(holder.ivProfile);
        }

        // Set click listeners
        holder.cardView.setOnClickListener(v -> listener.onViewClick(employee));
        holder.ivEdit.setOnClickListener(v -> listener.onEditClick(employee));
        holder.ivDelete.setOnClickListener(v -> listener.onDeleteClick(employee));
        holder.ivEye.setOnClickListener(v -> listener.onViewClick(employee));
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivProfile;
        TextView tvName;
        TextView tvRole;
        ImageView ivEdit,ivEye;
        ImageView ivDelete;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            ivEye=itemView.findViewById(R.id.eye);
            ivProfile = itemView.findViewById(R.id.image_profile_picture);
            tvName = itemView.findViewById(R.id.name_tv);
            tvRole = itemView.findViewById(R.id.number_tv);
            ivEdit = itemView.findViewById(R.id.edit_sm);
            ivDelete = itemView.findViewById(R.id.delete_sm);
        }
    }

    public interface OnEmployeeClickListener {
        void onViewClick(AllEmployees employee);
        void onEditClick(AllEmployees employee);
        void onDeleteClick(AllEmployees employee);
        void onDViewClick(AllEmployees employee);
    }
}