package com.school.schoolmanagement.Adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Model.GetAttendanceResponse;
import com.school.schoolmanagement.Model.GetEmployeeAttendance;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.RowMarkAttendanceBinding;

import java.util.ArrayList;
import java.util.List;

public class AdapterMarkEmployeeAttendance extends RecyclerView.Adapter<AdapterMarkEmployeeAttendance.AttendanceViewHolder> {

    private static final String TAG = "AdapterMarkEmployeeAttendance";

    // Status constants
    public static final int STATUS_NONE = 0;
    public static final int STATUS_PRESENT = 1;
    public static final int STATUS_ABSENT = 2;
    public static final int STATUS_LEAVE = 3;

    private final List<GetEmployeeAttendance.Datum> employeeList;
    private final Context context;
    private final OnAttendanceChangeListener listener;

    // Interface for attendance change callback
    public interface OnAttendanceChangeListener {
        void onAttendanceChanged(int position, int status);
    }

    public AdapterMarkEmployeeAttendance(Context context, List<GetEmployeeAttendance.Datum> employeeList, OnAttendanceChangeListener listener) {
        this.context = context;
        this.employeeList = employeeList != null ? employeeList : new ArrayList<>();
        this.listener = listener;

        Log.d(TAG, "Adapter constructor called");
        Log.d(TAG, "Initial employeeList size: " + this.employeeList.size());
        Log.d(TAG, "Context: " + (context != null ? "not null" : "null"));
        Log.d(TAG, "Listener: " + (listener != null ? "not null" : "null"));
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");

        try {
            RowMarkAttendanceBinding binding = RowMarkAttendanceBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            Log.d(TAG, "ViewHolder created successfully");
            return new AttendanceViewHolder(binding);
        } catch (Exception e) {
            Log.e(TAG, "Error creating ViewHolder: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for position: " + position);

        if (employeeList == null || employeeList.isEmpty()) {
            Log.e(TAG, "employeeList is null or empty!");
            return;
        }

        if (position >= employeeList.size()) {
            Log.e(TAG, "Position " + position + " is out of bounds. List size: " + employeeList.size());
            return;
        }

        GetEmployeeAttendance.Datum employee = employeeList.get(position);

        if (employee == null) {
            Log.e(TAG, "Employee at position " + position + " is null!");
            return;
        }

        Log.d(TAG, "Binding employee: " + employee.getEmployeeName() + " (ID: " + employee.getEmployeeId() + ")");
        Log.d(TAG, "Employee status: " + employee.getStatus());

        // Check if binding views are null
        if (holder.binding == null) {
            Log.e(TAG, "Holder binding is null!");
            return;
        }

        try {
            // Set employee data
            String employeeId = String.valueOf(employee.getEmployeeId());
            String employeeName = employee.getEmployeeName();

            Log.d(TAG, "Setting employee ID: " + employeeId);
            Log.d(TAG, "Setting employee name: " + employeeName);

            // Set employee ID and name
            if (holder.binding.tvStudentId != null) {
                holder.binding.tvStudentId.setText(employeeId);
            }
            if (holder.binding.tvStudentName != null) {
                holder.binding.tvStudentName.setText(employeeName);
            }

            Log.d(TAG, "Employee data set successfully");

            // Reset all buttons appearance
            resetButtonsAppearance(holder);

            // Highlight the current attendance status
            int currentStatus = getStatusFromString(employee.getStatus());
            Log.d(TAG, "Current status for employee " + employeeName + ": " + currentStatus);
            highlightSelectedStatus(holder, currentStatus);

            // Set click listeners for attendance options
            if (holder.binding.btnPresent != null) {
                holder.binding.btnPresent.setOnClickListener(v -> {
                    Log.d(TAG, "Present button clicked for position: " + position);
                    updateAttendanceStatus(position, STATUS_PRESENT);
                    resetButtonsAppearance(holder);
                    highlightSelectedStatus(holder, STATUS_PRESENT);
                });
            }

            if (holder.binding.btnLeave != null) {
                holder.binding.btnLeave.setOnClickListener(v -> {
                    Log.d(TAG, "Leave button clicked for position: " + position);
                    updateAttendanceStatus(position, STATUS_LEAVE);
                    resetButtonsAppearance(holder);
                    highlightSelectedStatus(holder, STATUS_LEAVE);
                });
            }

            if (holder.binding.btnAbsent != null) {
                holder.binding.btnAbsent.setOnClickListener(v -> {
                    Log.d(TAG, "Absent button clicked for position: " + position);
                    updateAttendanceStatus(position, STATUS_ABSENT);
                    resetButtonsAppearance(holder);
                    highlightSelectedStatus(holder, STATUS_ABSENT);
                });
            }

            // Handle not marked button if it exists
            if (holder.binding.btnNotMarked != null) {
                holder.binding.btnNotMarked.setOnClickListener(v -> {
                    Log.d(TAG, "Not marked button clicked for position: " + position);
                    updateAttendanceStatus(position, STATUS_NONE);
                    resetButtonsAppearance(holder);
                    highlightSelectedStatus(holder, STATUS_NONE);
                });
            }

            Log.d(TAG, "Click listeners set successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error in onBindViewHolder: " + e.getMessage(), e);
        }
    }

    private void resetButtonsAppearance(AttendanceViewHolder holder) {
        Log.d(TAG, "Resetting buttons appearance");

        try {
            // Reset Present button
            if (holder.binding.btnPresent != null) {
                GradientDrawable presentDrawable = createCircularDrawable(
                        ContextCompat.getColor(context, android.R.color.transparent),
                        ContextCompat.getColor(context, R.color.light_blue)
                );
                holder.binding.btnPresent.setBackground(presentDrawable);
                holder.binding.btnPresent.setTextColor(ContextCompat.getColor(context, R.color.light_blue));
            }

            // Reset Leave button
            if (holder.binding.btnLeave != null) {
                GradientDrawable leaveDrawable = createCircularDrawable(
                        ContextCompat.getColor(context, android.R.color.transparent),
                        ContextCompat.getColor(context, R.color.light_blue)
                );
                holder.binding.btnLeave.setBackground(leaveDrawable);
                holder.binding.btnLeave.setTextColor(ContextCompat.getColor(context, R.color.light_blue));
            }

            // Reset Absent button
            if (holder.binding.btnAbsent != null) {
                GradientDrawable absentDrawable = createCircularDrawable(
                        ContextCompat.getColor(context, android.R.color.transparent),
                        ContextCompat.getColor(context, R.color.light_red)
                );
                holder.binding.btnAbsent.setBackground(absentDrawable);
                holder.binding.btnAbsent.setTextColor(ContextCompat.getColor(context, R.color.light_red));
            }

            // Reset Not Marked button if it exists
            if (holder.binding.btnNotMarked != null) {
                GradientDrawable notMarkedDrawable = createCircularDrawable(
                        ContextCompat.getColor(context, android.R.color.transparent),
                        ContextCompat.getColor(context, R.color.light_red)
                );
                holder.binding.btnNotMarked.setBackground(notMarkedDrawable);
                holder.binding.btnNotMarked.setTextColor(ContextCompat.getColor(context, R.color.light_red));
            }

            Log.d(TAG, "Buttons appearance reset successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error resetting buttons appearance: " + e.getMessage(), e);
        }
    }

    private GradientDrawable createCircularDrawable(int fillColor, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(fillColor);
        drawable.setStroke(2, strokeColor); // 2dp stroke width
        return drawable;
    }

    private void highlightSelectedStatus(AttendanceViewHolder holder, int status) {
        Log.d(TAG, "Highlighting status: " + status);

        switch (status) {
            case STATUS_PRESENT:
                if (holder.binding.btnPresent != null) {
                    highlightButton(holder.binding.btnPresent, R.color.light_blue);
                }
                break;

            case STATUS_LEAVE:
                if (holder.binding.btnLeave != null) {
                    highlightButton(holder.binding.btnLeave, R.color.light_blue);
                }
                break;

            case STATUS_ABSENT:
                if (holder.binding.btnAbsent != null) {
                    highlightButton(holder.binding.btnAbsent, R.color.light_red);
                }
                break;

            case STATUS_NONE:
                if (holder.binding.btnNotMarked != null) {
                    highlightButton(holder.binding.btnNotMarked, R.color.light_red);
                }
                break;

            default:
                Log.d(TAG, "No status selected");
                break;
        }
    }

    private void highlightButton(TextView button, int backgroundColor) {
        Log.d(TAG, "Highlighting button with background: " + backgroundColor);

        try {
            // Create a filled circular drawable for selected state
            GradientDrawable selectedDrawable = createCircularDrawable(
                    ContextCompat.getColor(context, backgroundColor),
                    ContextCompat.getColor(context, backgroundColor)
            );

            button.setBackground(selectedDrawable);
            button.setTextColor(ContextCompat.getColor(context, android.R.color.white));

        } catch (Exception e) {
            Log.e(TAG, "Error highlighting button: " + e.getMessage(), e);
        }
    }

    private void updateAttendanceStatus(int position, int status) {
        Log.d(TAG, "Updating attendance status for position " + position + " to status: " + status);

        if (employeeList == null || position >= employeeList.size()) {
            Log.e(TAG, "Cannot update status - invalid position or null list");
            return;
        }

        GetEmployeeAttendance.Datum employee = employeeList.get(position);
        if (employee == null) {
            Log.e(TAG, "Employee at position " + position + " is null!");
            return;
        }

        int oldStatus = getStatusFromString(employee.getStatus());
        Log.d(TAG, "Old status: " + oldStatus + ", New status: " + status);

        // Toggle status if already selected
        if (oldStatus == status) {
            status = STATUS_NONE;
            Log.d(TAG, "Status toggled to NONE");
        }

        // Update the employee's status
        String statusString = getStringFromStatus(status);
        employee.setStatus(statusString);
        Log.d(TAG, "Employee status updated to: " + statusString);

        if (listener != null) {
            listener.onAttendanceChanged(position, status);
        } else {
            Log.w(TAG, "Listener is null!");
        }
    }

    private int getStatusFromString(String status) {
        Log.d(TAG, "Converting status string to int: " + status);

        if (status == null || status.trim().isEmpty()) {
            Log.d(TAG, "Status is null or empty, returning STATUS_NONE");
            return STATUS_NONE;
        }

        // Convert to lowercase and trim whitespace
        String statusLower = status.toLowerCase().trim();

        switch (statusLower) {
            // Full words
            case "present":
            case "p":  // Single character from API
                return STATUS_PRESENT;

            case "absent":
            case "a":  // Single character from API
                return STATUS_ABSENT;

            case "leave":
            case "l":  // Single character from API
                return STATUS_LEAVE;

            default:
                Log.d(TAG, "Unknown status: " + status + ", returning STATUS_NONE");
                return STATUS_NONE;
        }
    }

    private String getStringFromStatus(int status) {
        Log.d(TAG, "Converting status int to string: " + status);

        switch (status) {
            case STATUS_PRESENT:
                return "P";  // Using single character to match API format
            case STATUS_ABSENT:
                return "A";  // Using single character to match API format
            case STATUS_LEAVE:
                return "L";  // Using single character to match API format
            default:
                return null;
        }
    }

    // Helper methods for status checking
    public boolean isPresent(GetEmployeeAttendance.Datum employee) {
        return getStatusFromString(employee.getStatus()) == STATUS_PRESENT;
    }

    public boolean isAbsent(GetEmployeeAttendance.Datum employee) {
        return getStatusFromString(employee.getStatus()) == STATUS_ABSENT;
    }

    public boolean isOnLeave(GetEmployeeAttendance.Datum employee) {
        return getStatusFromString(employee.getStatus()) == STATUS_LEAVE;
    }

    public boolean hasNoStatus(GetEmployeeAttendance.Datum employee) {
        return getStatusFromString(employee.getStatus()) == STATUS_NONE;
    }

    @Override
    public int getItemCount() {
        int count = employeeList != null ? employeeList.size() : 0;
        Log.d(TAG, "getItemCount called, returning: " + count);
        return count;
    }

    // ViewHolder class
    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        private final RowMarkAttendanceBinding binding;

        public AttendanceViewHolder(RowMarkAttendanceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            Log.d(TAG, "ViewHolder created");
        }
    }

    // Method to update the list
    public void updateEmployeeList(List<GetEmployeeAttendance.Datum> newList) {
        Log.d(TAG, "updateEmployeeList called");
        Log.d(TAG, "Current list size: " + (this.employeeList != null ? this.employeeList.size() : "null"));
        Log.d(TAG, "New list size: " + (newList != null ? newList.size() : "null"));

        if (this.employeeList == null) {
            Log.e(TAG, "employeeList is null! Cannot update.");
            return;
        }

        if (newList == null) {
            Log.w(TAG, "New list is null, clearing current list");
            this.employeeList.clear();
            Log.d(TAG, "Final list size: " + this.employeeList.size());
            Log.d(TAG, "Calling notifyDataSetChanged()");
            notifyDataSetChanged();
            return;
        }

        // Create a copy of the new list to avoid ConcurrentModificationException
        List<GetEmployeeAttendance.Datum> tempList = new ArrayList<>();

        try {
            // First, copy all valid items to temporary list
            for (GetEmployeeAttendance.Datum employee : newList) {
                if (employee != null) {
                    tempList.add(employee);
                    Log.d(TAG, "Copied employee: " + employee.getEmployeeName() + " (ID: " + employee.getEmployeeId() + ", Status: " + employee.getStatus() + ")");
                } else {
                    Log.w(TAG, "Skipping null employee in new list");
                }
            }

            Log.d(TAG, "All employees copied to temp list. Temp list size: " + tempList.size());

            // Now clear the current list and add all items from temp list
            this.employeeList.clear();
            Log.d(TAG, "Current list cleared, size: " + this.employeeList.size());

            this.employeeList.addAll(tempList);
            Log.d(TAG, "All employees added from temp list successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error updating employee list: " + e.getMessage(), e);
            // If there's an error, at least clear the list to avoid inconsistent state
            this.employeeList.clear();
        }

        Log.d(TAG, "Final list size: " + this.employeeList.size());
        Log.d(TAG, "Calling notifyDataSetChanged()");
        notifyDataSetChanged();
    }

    // Method to get all employees with their current status
    public List<GetEmployeeAttendance.Datum> getEmployeeList() {
        return new ArrayList<>(employeeList);
    }

    // Method to get count of employees by status
    public int getPresentCount() {
        int count = 0;
        for (GetEmployeeAttendance.Datum employee : employeeList) {
            if (isPresent(employee)) {
                count++;
            }
        }
        return count;
    }

    public int getAbsentCount() {
        int count = 0;
        for (GetEmployeeAttendance.Datum employee : employeeList) {
            if (isAbsent(employee)) {
                count++;
            }
        }
        return count;
    }

    public int getLeaveCount() {
        int count = 0;
        for (GetEmployeeAttendance.Datum employee : employeeList) {
            if (isOnLeave(employee)) {
                count++;
            }
        }
        return count;
    }

    public int getNotMarkedCount() {
        int count = 0;
        for (GetEmployeeAttendance.Datum employee : employeeList) {
            if (hasNoStatus(employee)) {
                count++;
            }
        }
        return count;
    }
}