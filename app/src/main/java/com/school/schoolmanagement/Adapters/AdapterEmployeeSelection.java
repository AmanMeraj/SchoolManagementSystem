package com.school.schoolmanagement.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.AllEmployees;
import com.school.schoolmanagement.R;

import java.util.List;

public class AdapterEmployeeSelection extends RecyclerView.Adapter<AdapterEmployeeSelection.EmployeeViewHolder> {

    private List<AllEmployees> employeeList;
    private OnEmployeeSelectedListener listener;

    public interface OnEmployeeSelectedListener {
        void onEmployeeSelected(AllEmployees employee);
    }

    public AdapterEmployeeSelection(List<AllEmployees> employeeList, OnEmployeeSelectedListener listener) {
        this.employeeList = employeeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_employee_selection, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        AllEmployees employee = employeeList.get(position);

        holder.tvEmployeeName.setText(employee.getName() != null ? employee.getName() : "N/A");
        holder.tvEmployeeId.setText("ID: " + employee.getEmployeeId());
        holder.tvEmployeeDesignation.setText(employee.getEmployeeRole() != null ? employee.getEmployeeRole() : "N/A");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEmployeeSelected(employee);
            }
        });
    }

    @Override
    public int getItemCount() {
        return employeeList != null ? employeeList.size() : 0;
    }

    public void updateList(List<AllEmployees> newList) {
        this.employeeList = newList;
        notifyDataSetChanged();
    }

    static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmployeeName, tvEmployeeId, tvEmployeeDesignation;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmployeeName = itemView.findViewById(R.id.tv_employee_name);
            tvEmployeeId = itemView.findViewById(R.id.tv_employee_id);
            tvEmployeeDesignation = itemView.findViewById(R.id.tv_employee_designation);
        }
    }
}