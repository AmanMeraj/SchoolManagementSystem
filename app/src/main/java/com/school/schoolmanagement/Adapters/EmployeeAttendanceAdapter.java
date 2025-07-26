package com.school.schoolmanagement.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.school.schoolmanagement.Model.EmployeeAttendanceReport;
import com.school.schoolmanagement.Model.StudentsAttendanceReport;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.RowEmployeeAttendanceReportBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class EmployeeAttendanceAdapter extends RecyclerView.Adapter<EmployeeAttendanceAdapter.ViewHolder> {

    private Context context;
    private ArrayList<EmployeeAttendanceReport.Datum> attendanceList;
    private OnItemClickListener onItemClickListener;

    // Constructor
    public EmployeeAttendanceAdapter(Context context, ArrayList<EmployeeAttendanceReport.Datum> attendanceList) {
        this.context = context;
        this.attendanceList = attendanceList;
        sortByDate();
    }
    private void sortByDate() {
        Collections.sort(attendanceList, new Comparator<EmployeeAttendanceReport.Datum>() {
            @Override
            public int compare(EmployeeAttendanceReport.Datum o1, EmployeeAttendanceReport.Datum o2) {
                return compareDates(o2.getDate(), o1.getDate()); // Swap o1 and o2
            }
        });
    }

    /**
     * Compare two date strings
     * Assumes date format is "dd/MM/yyyy" or "yyyy-MM-dd"
     * Returns negative if date1 is after date2 (for descending order)
     */
    private int compareDates(String date1, String date2) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date d1 = parseDate(date1, sdf1, sdf2);
            Date d2 = parseDate(date2, sdf1, sdf2);

            if (d1 != null && d2 != null) {
                // Return negative for descending order (most recent first)
                return d2.compareTo(d1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fallback to string comparison if date parsing fails
        return date2.compareTo(date1);
    }

    /**
     * Try to parse date with different formats
     */
    private Date parseDate(String dateStr, SimpleDateFormat... formats) {
        for (SimpleDateFormat format : formats) {
            try {
                return format.parse(dateStr);
            } catch (ParseException e) {
                // Continue to next format
            }
        }
        return null;
    }
    public void sortAndRefresh() {
        sortByDate();
        notifyDataSetChanged();
    }

    // Interface for item click handling
    public interface OnItemClickListener {
        void onItemClick(EmployeeAttendanceReport.Datum datum, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowEmployeeAttendanceReportBinding binding = RowEmployeeAttendanceReportBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmployeeAttendanceReport.Datum datum = attendanceList.get(position);
        holder.bind(datum, position);
    }

    @Override
    public int getItemCount() {
        return attendanceList != null ? attendanceList.size() : 0;
    }

    // Method to update the data
    public void updateData(ArrayList<EmployeeAttendanceReport.Datum> newAttendanceList) {
        this.attendanceList = newAttendanceList;
        notifyDataSetChanged();
    }

    // Method to add new item
    public void addItem(EmployeeAttendanceReport.Datum datum) {
        if (attendanceList == null) {
            attendanceList = new ArrayList<>();
        }
        attendanceList.add(datum);
        notifyItemInserted(attendanceList.size() - 1);
    }

    // Method to remove item
    public void removeItem(int position) {
        if (attendanceList != null && position >= 0 && position < attendanceList.size()) {
            attendanceList.remove(position);
            notifyItemRemoved(position);
        }
    }

    // Method to clear all data
    public void clearData() {
        if (attendanceList != null) {
            attendanceList.clear();
            notifyDataSetChanged();
        }
    }

    // ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        private RowEmployeeAttendanceReportBinding binding;

        public ViewHolder(@NonNull RowEmployeeAttendanceReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(EmployeeAttendanceReport.Datum datum, int position) {
            // Set the data to views
            binding.tvDateValue.setText(datum.getDate() != null ? datum.getDate() : "");
            binding.tvDayValue.setText(datum.getDay() != null ? datum.getDay() : "");
            binding.tvIdValue.setText(datum.getEmployeeId() != null ? datum.getEmployeeId() : "");
            binding.tvStudentValue.setText(datum.getEmployeeName() != null ? datum.getEmployeeName() : "");
            binding.tvStatusValue.setText(datum.getStatus() != null ? datum.getStatus() : "");

            // Set status color based on attendance status
            setStatusColor(datum.getStatus());

            // Set click listener
            binding.getRoot().setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(datum, position);
                }
            });
        }


        /**
         * Public method to sort and refresh the adapter
         */


        private void setStatusColor(String status) {
            if (status != null) {
                switch (status.toUpperCase()) {
                    case "P":
                    case "PRESENT":
                        binding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.green));
                        break;
                    case "A":
                    case "ABSENT":
                        binding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.dark_red));
                        break;
                    case "L":
                    case "LATE":
                        binding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.orange));
                        break;
                    case "H":
                    case "HOLIDAY":
                        binding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.light_blue));
                        break;
                    default:
                        binding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.black));
                        break;
                }
            }
        }

    }
}
