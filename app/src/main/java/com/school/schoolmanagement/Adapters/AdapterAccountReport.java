package com.school.schoolmanagement.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.Record;
import com.school.schoolmanagement.Model.FeesCollectionEntries;
import com.school.schoolmanagement.databinding.RowAccountReportBinding;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdapterAccountReport extends RecyclerView.Adapter<AdapterAccountReport.ViewHolder> {

    private Context context;
    private ArrayList<Record> recordList;
    private SimpleDateFormat inputDateFormat;
    private SimpleDateFormat outputDateFormat;
    private NumberFormat currencyFormat;

    public AdapterAccountReport(Context context, ArrayList<Record> recordList) {
        this.context = context;
        this.recordList = recordList != null ? recordList : new ArrayList<>();

        // Initialize date formatters
        inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        outputDateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());

        // Initialize currency formatter
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowAccountReportBinding binding = RowAccountReportBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = recordList.get(position);

        // Set serial number (position + 1)
        holder.binding.tvSr.setText(String.valueOf(position + 1));

        // Set ID
        holder.binding.tvId.setText(String.valueOf(record.getId()));

        // Set description (using as name since Record doesn't have name field)
        holder.binding.tvDescription.setText(record.getDescription());

        // Format and set date
        String formattedDate = formatDate(record.getDate());
        holder.binding.tvDate.setText(formattedDate);

        // Format and set amount
        String formattedAmount = currencyFormat.format(record.getAmount());
        holder.binding.tvPaid.setText(formattedAmount);
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    // Method to update the list
    public void updateList(ArrayList<Record> newRecordList) {
        this.recordList = newRecordList != null ? newRecordList : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Method to add new records
    public void addRecords(ArrayList<Record> newRecords) {
        if (newRecords != null && !newRecords.isEmpty()) {
            int startPosition = recordList.size();
            recordList.addAll(newRecords);
            notifyItemRangeInserted(startPosition, newRecords.size());
        }
    }

    // Method to clear all records
    public void clearRecords() {
        int size = recordList.size();
        recordList.clear();
        notifyItemRangeRemoved(0, size);
    }

    // Helper method to format date
    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "N/A";
        }

        try {
            Date date = inputDateFormat.parse(dateString);
            if (date != null) {
                return outputDateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // If parsing fails, try to extract day and month from the string
            if (dateString.length() >= 10) {
                try {
                    String[] parts = dateString.split("-");
                    if (parts.length >= 3) {
                        int day = Integer.parseInt(parts[2]);
                        int month = Integer.parseInt(parts[1]);
                        String[] months = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                        if (month >= 1 && month <= 12) {
                            return String.format("%02d %s", day, months[month]);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return dateString; // Return original string if formatting fails
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        RowAccountReportBinding binding;

        public ViewHolder(@NonNull RowAccountReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
