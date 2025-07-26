package com.school.schoolmanagement.Teachers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Model.StudentsAttendanceReport;
import com.school.schoolmanagement.databinding.RowStudentsAttendanceReportBinding;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private final List<StudentsAttendanceReport.Datum> attendanceList;
    private final Context context;

    public AttendanceAdapter(Context context, List<StudentsAttendanceReport.Datum> attendanceList) {
        this.context = context;
        this.attendanceList = attendanceList;

        // Sort the list by date when adapter is created
        sortByDate();
    }

    /**
     * Sort attendance records by date in ascending order (oldest first)
     */
    private void sortByDate() {
        Collections.sort(attendanceList, new Comparator<StudentsAttendanceReport.Datum>() {
            @Override
            public int compare(StudentsAttendanceReport.Datum o1, StudentsAttendanceReport.Datum o2) {
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

    /**
     * Public method to sort and refresh the adapter
     */
    public void sortAndRefresh() {
        sortByDate();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowStudentsAttendanceReportBinding binding = RowStudentsAttendanceReportBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new AttendanceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        StudentsAttendanceReport.Datum record = attendanceList.get(position);

        // Set data to views using the model class fields
        holder.binding.tvDateValue.setText(record.getDate());
        holder.binding.tvDayValue.setText(record.getDay());
        holder.binding.tvIdValue.setText(record.getStudentId());
        holder.binding.tvStudentValue.setText(record.getStudentName());
        holder.binding.tvClassValue.setText(record.getMyclass());
        holder.binding.tvStatusValue.setText(record.getStatus());

        // Set status color based on attendance status
        String status = record.getStatus();
        if (status != null) {
            switch (status.toUpperCase()) {
                case "P":
                case "PRESENT":
                    holder.binding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.light_green3));
                    break;
                case "A":
                case "ABSENT":
                    holder.binding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.dark_red));
                    break;
                case "L":
                case "LATE":
                    holder.binding.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.light_blue));
                    break;
                default:
                    holder.binding.tvStatusValue.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        private final RowStudentsAttendanceReportBinding binding;

        public AttendanceViewHolder(RowStudentsAttendanceReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}