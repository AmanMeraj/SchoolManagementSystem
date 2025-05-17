package com.school.schoolmanagement.SuperAdmin.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.school.schoolmanagement.SuperAdmin.Model.SearchSubAdminModel;
import com.school.schoolmanagement.databinding.RowSearchSubAdminBinding;

import java.util.List;

public class AdapterSearchSubAdmin extends RecyclerView.Adapter<AdapterSearchSubAdmin.ViewHolder> {

    private List<SearchSubAdminModel> subAdminList;
    private Context context;
    private SubAdminListener listener;

    // Interface for callback events
    public interface SubAdminListener {
        void onEditClicked(SearchSubAdminModel subAdmin, int position);
        void onDeleteClicked(SearchSubAdminModel subAdmin, int position);
        void onUserNameChanged(SearchSubAdminModel subAdmin, String userName, int position);
        void onPasswordChanged(SearchSubAdminModel subAdmin, String password, int position);
    }

    public AdapterSearchSubAdmin(Context context, List<SearchSubAdminModel> subAdminList, SubAdminListener listener) {
        this.context = context;
        this.subAdminList = subAdminList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowSearchSubAdminBinding binding = RowSearchSubAdminBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchSubAdminModel subAdmin = subAdminList.get(position);

        // Set data to views
        holder.binding.tvIdData.setText(subAdmin.getId());
        holder.binding.tvBNameData.setText(subAdmin.getBranchName());
        holder.binding.tvAdminData.setText(subAdmin.getAdmin());
        holder.binding.etUserName.setText(subAdmin.getUserName());
        holder.binding.etPassword.setText(subAdmin.getPassword());

        // Set up click listeners
        holder.binding.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClicked(subAdmin, holder.getAdapterPosition());
            }
        });

        holder.binding.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClicked(subAdmin, holder.getAdapterPosition());
            }
        });

        // Set up text change listeners
        holder.binding.etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null) {
                    listener.onUserNameChanged(subAdmin, s.toString(), holder.getAdapterPosition());
                }
                subAdmin.setUserName(s.toString());
            }
        });

        holder.binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null) {
                    listener.onPasswordChanged(subAdmin, s.toString(), holder.getAdapterPosition());
                }
                subAdmin.setPassword(s.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return subAdminList.size();
    }

    public void updateData(List<SearchSubAdminModel> newList) {
        this.subAdminList = newList;
        notifyDataSetChanged();
    }

    public void addItem(SearchSubAdminModel subAdmin) {
        subAdminList.add(subAdmin);
        notifyItemInserted(subAdminList.size() - 1);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < subAdminList.size()) {
            subAdminList.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RowSearchSubAdminBinding binding;

        ViewHolder(RowSearchSubAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
