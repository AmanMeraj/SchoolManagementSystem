package com.school.schoolmanagement.Adapters;

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
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.RowStudnetPasswordBinding;

import java.util.List;


public class AdapterStudentManageLogin extends RecyclerView.Adapter<AdapterStudentManageLogin.ViewHolder> {
    private final Context context;
    private final List<StudentDetails> userList;
    private final OnStudentActionListener listener;

    public AdapterStudentManageLogin(Context context, List<StudentDetails> userList, OnStudentActionListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowStudnetPasswordBinding binding = RowStudnetPasswordBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentDetails user = userList.get(position);

        Log.d("AdapterDebug", "Binding user at position: " + position);
        Log.d("AdapterDebug", "ID: " + user.getStudentId());
        Log.d("AdapterDebug", "SName: " + user.getStudentName());
        Log.d("AdapterDebug", "Class: " + user.getClassName());
        Log.d("AdapterDebug", "UserID: " + user.getUsername());
        Log.d("AdapterDebug", "Password: " + user.getPassword());

        holder.binding.tvId.setText(String.valueOf(user.getStudentId()));
        holder.binding.tvSName.setText(user.getStudentName());
        holder.binding.tvClass.setText(user.getClassName());
        holder.binding.etUserId.setText(user.getUsername());
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

        holder.binding.save.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSaveClicked(user);
            }
        });

        holder.binding.ivMail.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMailClicked(user);
            }
        });
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

    public interface OnStudentActionListener {
        void onSaveClicked(StudentDetails student);
        void onMailClicked(StudentDetails student);
    }
}
