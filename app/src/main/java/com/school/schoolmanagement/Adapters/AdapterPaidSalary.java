package com.school.schoolmanagement.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.school.schoolmanagement.Model.SalaryPaidResponse;
import com.school.schoolmanagement.R;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdapterPaidSalary extends RecyclerView.Adapter<AdapterPaidSalary.ViewHolder> {

    private static final String TAG = "AdapterPaidSalary";
    private Context context;
    private ArrayList<SalaryPaidResponse.Datum> salaryList;
    private OnItemClickListener onItemClickListener;

    // Interface for handling item clicks
    public interface OnItemClickListener {
        void onItemClick(SalaryPaidResponse.Datum salaryData, int position);
    }

    public AdapterPaidSalary(Context context, ArrayList<SalaryPaidResponse.Datum> salaryList) {
        this.context = context;
        this.salaryList = salaryList != null ? salaryList : new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_payment_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            // Check if position is valid
            if (position < 0 || position >= salaryList.size()) {
                Log.e(TAG, "Invalid position: " + position + ", list size: " + salaryList.size());
                return;
            }

            SalaryPaidResponse.Datum salaryData = salaryList.get(position);

            // Check if salaryData is null
            if (salaryData == null) {
                Log.e(TAG, "Salary data is null at position: " + position);
                return;
            }

            // Check if TextViews are null and set employee name
            if (holder.tvEmployeeName != null) {
                String employeeName = salaryData.getEmployeeName();
                holder.tvEmployeeName.setText(employeeName != null ? employeeName : "N/A");
            } else {
                Log.e(TAG, "tvEmployeeName is null - check your layout file");
            }

            // Set salary month
            if (holder.tvSalaryMonth != null) {
                String salaryMonth = salaryData.getSalaryMonth();
                holder.tvSalaryMonth.setText(salaryMonth != null ? salaryMonth : "N/A");
            } else {
                Log.e(TAG, "tvSalaryMonth is null - check your layout file");
            }

            // Format and set the amount (using netPaid as the main amount)
            if (holder.tvAmount != null) {
                try {
                    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
                    double netPaid = salaryData.getNetPaid();
                    String formattedAmount = currencyFormatter.format(netPaid);
                    holder.tvAmount.setText(formattedAmount);
                } catch (Exception e) {
                    Log.e(TAG, "Error formatting amount: " + e.getMessage());
                    holder.tvAmount.setText("$0.00");
                }
            } else {
                Log.e(TAG, "tvAmount is null - check your layout file");
            }

            // Handle item click
            if (holder.itemView != null) {
                holder.itemView.setOnClickListener(v -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(salaryData, position);
                    }
                });
            }

            // Handle arrow click
            if (holder.arrow != null) {
                holder.arrow.setOnClickListener(v -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(salaryData, position);
                    }
                });
            } else {
                Log.w(TAG, "Arrow ImageView is null - check your layout file");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in onBindViewHolder at position " + position + ": " + e.getMessage(), e);
        }
    }

    @Override
    public int getItemCount() {
        return salaryList != null ? salaryList.size() : 0;
    }

    // Method to update the data
    public void updateData(ArrayList<SalaryPaidResponse.Datum> newSalaryList) {
        this.salaryList = newSalaryList != null ? newSalaryList : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Method to add new data
    public void addData(ArrayList<SalaryPaidResponse.Datum> newSalaryList) {
        if (newSalaryList == null || newSalaryList.isEmpty()) {
            return;
        }

        if (this.salaryList == null) {
            this.salaryList = new ArrayList<>();
        }

        int startPosition = this.salaryList.size();
        this.salaryList.addAll(newSalaryList);
        notifyItemRangeInserted(startPosition, newSalaryList.size());
    }

    // Method to clear data
    public void clearData() {
        if (salaryList != null) {
            salaryList.clear();
            notifyDataSetChanged();
        }
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmployeeName, tvSalaryMonth, tvAmount;
        ImageView arrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            try {
                tvEmployeeName = itemView.findViewById(R.id.tv_employee_name);
                tvSalaryMonth = itemView.findViewById(R.id.tv_salary_month);
                tvAmount = itemView.findViewById(R.id.tv_amount);
                arrow = itemView.findViewById(R.id.arrow);

                // Log warnings if any views are null
                if (tvEmployeeName == null) {
                    Log.e(TAG, "Could not find R.id.tv_employee_name in layout");
                }
                if (tvSalaryMonth == null) {
                    Log.e(TAG, "Could not find R.id.tv_salary_month in layout");
                }
                if (tvAmount == null) {
                    Log.e(TAG, "Could not find R.id.tv_amount in layout");
                }
                if (arrow == null) {
                    Log.w(TAG, "Could not find R.id.arrow in layout");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error initializing ViewHolder: " + e.getMessage(), e);
            }
        }
    }
}