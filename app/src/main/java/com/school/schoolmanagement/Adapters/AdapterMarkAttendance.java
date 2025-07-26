package com.school.schoolmanagement.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Model.GetAttendanceResponse;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.RowMarkAttendanceBinding;

import java.util.ArrayList;
import java.util.List;

public class AdapterMarkAttendance extends RecyclerView.Adapter<AdapterMarkAttendance.AttendanceViewHolder> {

    private static final String TAG = "AdapterMarkAttendance";

    // Status constants
    public static final int STATUS_NONE = 0;
    public static final int STATUS_PRESENT = 1;
    public static final int STATUS_ABSENT = 2;
    public static final int STATUS_LEAVE = 3;

    private final List<GetAttendanceResponse.Datum> studentList;
    private final Context context;
    private final OnAttendanceChangeListener listener;

    // Interface for attendance change callback
    public interface OnAttendanceChangeListener {
        void onAttendanceChanged(int position, int status);
    }

    public AdapterMarkAttendance(Context context, List<GetAttendanceResponse.Datum> studentList, OnAttendanceChangeListener listener) {
        this.context = context;
        this.studentList = studentList;
        this.listener = listener;

        Log.d(TAG, "Adapter constructor called");
        Log.d(TAG, "Initial studentList size: " + (studentList != null ? studentList.size() : "null"));
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

        if (studentList == null) {
            Log.e(TAG, "studentList is null!");
            return;
        }

        if (position >= studentList.size()) {
            Log.e(TAG, "Position " + position + " is out of bounds. List size: " + studentList.size());
            return;
        }

        GetAttendanceResponse.Datum student = studentList.get(position);

        if (student == null) {
            Log.e(TAG, "Student at position " + position + " is null!");
            return;
        }

        Log.d(TAG, "Binding student: " + student.getStudentName() + " (ID: " + student.getStudentId() + ")");
        Log.d(TAG, "Student status: " + student.getStatus());

        // Check if binding views are null
        if (holder.binding == null) {
            Log.e(TAG, "Holder binding is null!");
            return;
        }

        if (holder.binding.tvStudentId == null) {
            Log.e(TAG, "tvStudentId is null!");
            return;
        }

        if (holder.binding.tvStudentName == null) {
            Log.e(TAG, "tvStudentName is null!");
            return;
        }

        try {
            // Set student data
            String studentId = String.valueOf(student.getStudentId());
            String studentName = student.getStudentName();

            Log.d(TAG, "Setting student ID: " + studentId);
            Log.d(TAG, "Setting student name: " + studentName);

            holder.binding.tvStudentId.setText(studentId);
            holder.binding.tvStudentName.setText(studentName);

            Log.d(TAG, "Student data set successfully");

            // Reset all buttons appearance
            resetButtonsAppearance(holder);

            // Highlight the current attendance status
            int currentStatus = getStatusFromString(student.getStatus());
            Log.d(TAG, "Current status for student " + studentName + ": " + currentStatus);
            highlightSelectedStatus(holder, currentStatus);

            // Set click listeners for attendance options
            holder.binding.btnPresent.setOnClickListener(v -> {
                Log.d(TAG, "Present button clicked for position: " + position);
                updateAttendanceStatus(position, STATUS_PRESENT);
                resetButtonsAppearance(holder);
                highlightSelectedStatus(holder, STATUS_PRESENT);
            });

            holder.binding.btnLeave.setOnClickListener(v -> {
                Log.d(TAG, "Leave button clicked for position: " + position);
                updateAttendanceStatus(position, STATUS_LEAVE);
                resetButtonsAppearance(holder);
                highlightSelectedStatus(holder, STATUS_LEAVE);
            });

            holder.binding.btnAbsent.setOnClickListener(v -> {
                Log.d(TAG, "Absent button clicked for position: " + position);
                updateAttendanceStatus(position, STATUS_ABSENT);
                resetButtonsAppearance(holder);
                highlightSelectedStatus(holder, STATUS_ABSENT);
            });

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
            // Check if buttons exist
            if (holder.binding.btnPresent == null) {
                Log.e(TAG, "btnPresent is null!");
                return;
            }
            if (holder.binding.btnLeave == null) {
                Log.e(TAG, "btnLeave is null!");
                return;
            }
            if (holder.binding.btnAbsent == null) {
                Log.e(TAG, "btnAbsent is null!");
                return;
            }

            // Reset Present button - create circular drawable
            GradientDrawable presentDrawable = createCircularDrawable(
                    ContextCompat.getColor(context, android.R.color.transparent),
                    ContextCompat.getColor(context, R.color.light_blue)
            );
            holder.binding.btnPresent.setBackground(presentDrawable);
            holder.binding.btnPresent.setTextColor(ContextCompat.getColor(context, R.color.light_blue));

            // Reset Leave button - create circular drawable
            GradientDrawable leaveDrawable = createCircularDrawable(
                    ContextCompat.getColor(context, android.R.color.transparent),
                    ContextCompat.getColor(context, R.color.light_blue)
            );
            holder.binding.btnLeave.setBackground(leaveDrawable);
            holder.binding.btnLeave.setTextColor(ContextCompat.getColor(context, R.color.light_blue));

            // Reset Absent button - create circular drawable
            GradientDrawable absentDrawable = createCircularDrawable(
                    ContextCompat.getColor(context, android.R.color.transparent),
                    ContextCompat.getColor(context, R.color.light_red)
            );
            holder.binding.btnAbsent.setBackground(absentDrawable);
            holder.binding.btnAbsent.setTextColor(ContextCompat.getColor(context, R.color.light_red));

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
                highlightButton(holder.binding.btnPresent, R.color.light_blue);
                break;

            case STATUS_LEAVE:
                highlightButton(holder.binding.btnLeave, R.color.light_blue);
                break;

            case STATUS_ABSENT:
                highlightButton(holder.binding.btnAbsent, R.color.light_red);
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

        if (studentList == null || position >= studentList.size()) {
            Log.e(TAG, "Cannot update status - invalid position or null list");
            return;
        }

        GetAttendanceResponse.Datum student = studentList.get(position);
        if (student == null) {
            Log.e(TAG, "Student at position " + position + " is null!");
            return;
        }

        int oldStatus = getStatusFromString(student.getStatus());
        Log.d(TAG, "Old status: " + oldStatus + ", New status: " + status);

        // Toggle status if already selected
        if (oldStatus == status) {
            status = STATUS_NONE;
            Log.d(TAG, "Status toggled to NONE");
        }

        // Update the student's status
        String statusString = getStringFromStatus(status);
        student.setStatus(statusString);
        Log.d(TAG, "Student status updated to: " + statusString);

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

    // Also update the reverse method to handle API format
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
    public boolean isPresent(GetAttendanceResponse.Datum student) {
        return getStatusFromString(student.getStatus()) == STATUS_PRESENT;
    }

    public boolean isAbsent(GetAttendanceResponse.Datum student) {
        return getStatusFromString(student.getStatus()) == STATUS_ABSENT;
    }

    public boolean isOnLeave(GetAttendanceResponse.Datum student) {
        return getStatusFromString(student.getStatus()) == STATUS_LEAVE;
    }

    public boolean hasNoStatus(GetAttendanceResponse.Datum student) {
        return getStatusFromString(student.getStatus()) == STATUS_NONE;
    }

    @Override
    public int getItemCount() {
        int count = studentList != null ? studentList.size() : 0;
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
    public void updateStudentList(List<GetAttendanceResponse.Datum> newList) {
        Log.d(TAG, "updateStudentList called");
        Log.d(TAG, "Current list size: " + (this.studentList != null ? this.studentList.size() : "null"));
        Log.d(TAG, "New list size: " + (newList != null ? newList.size() : "null"));

        if (this.studentList == null) {
            Log.e(TAG, "studentList is null! Cannot update.");
            return;
        }

        if (newList == null) {
            Log.w(TAG, "New list is null, clearing current list");
            this.studentList.clear();
            Log.d(TAG, "Final list size: " + this.studentList.size());
            Log.d(TAG, "Calling notifyDataSetChanged()");
            notifyDataSetChanged();
            return;
        }

        // Create a copy of the new list to avoid ConcurrentModificationException
        List<GetAttendanceResponse.Datum> tempList = new ArrayList<>();

        try {
            // First, copy all valid items to temporary list
            for (GetAttendanceResponse.Datum student : newList) {
                if (student != null) {
                    tempList.add(student);
                    Log.d(TAG, "Copied student: " + student.getStudentName() + " (ID: " + student.getStudentId() + ", Status: " + student.getStatus() + ")");
                } else {
                    Log.w(TAG, "Skipping null student in new list");
                }
            }

            Log.d(TAG, "All students copied to temp list. Temp list size: " + tempList.size());

            // Now clear the current list and add all items from temp list
            this.studentList.clear();
            Log.d(TAG, "Current list cleared, size: " + this.studentList.size());

            this.studentList.addAll(tempList);
            Log.d(TAG, "All students added from temp list successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error updating student list: " + e.getMessage(), e);
            // If there's an error, at least clear the list to avoid inconsistent state
            this.studentList.clear();
        }

        Log.d(TAG, "Final list size: " + this.studentList.size());
        Log.d(TAG, "Calling notifyDataSetChanged()");
        notifyDataSetChanged();
    }
}