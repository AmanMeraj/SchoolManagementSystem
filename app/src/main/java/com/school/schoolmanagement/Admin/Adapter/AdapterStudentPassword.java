package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.StudentPassword;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.RowStudnetPasswordBinding;

import java.util.List;

public class AdapterStudentPassword extends RecyclerView.Adapter<AdapterStudentPassword.ViewHolder> {
    private final Context context;
    private final List<StudentPassword> userList;

    public AdapterStudentPassword(Context context, List<StudentPassword> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowStudnetPasswordBinding binding = RowStudnetPasswordBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentPassword user = userList.get(position);

        Log.d("AdapterDebug", "Binding user at position: " + position);
        Log.d("AdapterDebug", "ID: " + user.getId());
        Log.d("AdapterDebug", "SName: " + user.getSName());
        Log.d("AdapterDebug", "Class: " + user.getClassName());
        Log.d("AdapterDebug", "UserID: " + user.getUserId());
        Log.d("AdapterDebug", "Password: " + user.getPassword());

        holder.binding.tvId.setText(user.getId());
        holder.binding.tvSName.setText(user.getSName());
        holder.binding.tvClass.setText(user.getClassName());
        holder.binding.etUserId.setText(user.getUserId());
        holder.binding.etPassword.setText(user.getPassword());

        // Handle password visibility toggle
        holder.binding.ivEye.setOnClickListener(v -> {
            if (holder.isPasswordVisible) {
                // Hide password
                holder.binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                holder.binding.ivEye.setImageResource(R.drawable.ic_eye_closed);
            } else {
                // Show password
                holder.binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                holder.binding.ivEye.setImageResource(R.drawable.open_eye);
            }
            holder.isPasswordVisible = !holder.isPasswordVisible;
            // Move cursor to the end
            holder.binding.etPassword.setSelection(holder.binding.etPassword.getText().length());
        });

        holder.binding.save.setOnClickListener(v ->
                Toast.makeText(context, "Saved: " + user.getUserId(), Toast.LENGTH_SHORT).show()
        );

        holder.binding.ivMail.setOnClickListener(v ->
                Toast.makeText(context, "Mail Sent to: " + user.getUserId(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RowStudnetPasswordBinding binding;
        private boolean isPasswordVisible = false; // Track visibility state

        public ViewHolder(RowStudnetPasswordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
