package com.school.schoolmanagement.Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.school.schoolmanagement.Admin.Model.StudentInfoReport;
import com.school.schoolmanagement.R;

import java.util.ArrayList;
import java.util.Calendar;

public class StudentInfoAdapter extends RecyclerView.Adapter<StudentInfoAdapter.StudentViewHolder> {

    private Context context;
    private ArrayList<StudentInfoReport.Datum> studentList;

    public StudentInfoAdapter(Context context, ArrayList<StudentInfoReport.Datum> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_student_info_report, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentInfoReport.Datum student = studentList.get(position);

        // Serial Number (position + 1)
        holder.tvSerialNumber.setText(String.valueOf(position + 1));

        // Student ID
        holder.tvStudentId.setText(String.valueOf(student.getStudentId()));

        // Student Name
        holder.tvStudentName.setText(student.getStudentName() != null ? student.getStudentName() : "N/A");

        // Father Name
        String fatherName = "N/A";
        if (student.getParent() != null && student.getParent().getFatherName() != null) {
            fatherName = student.getParent().getFatherName();
        }
        holder.tvFatherName.setText(fatherName);

        // Class
        holder.tvClass.setText(student.getClassName() != null ? student.getClassName() : "N/A");

        // Discount in Fees (placeholder - not available in model)
        holder.tvDiscountFees.setText("0");

        // Admission Date
        holder.tvAdmissionDate.setText(student.dateOfAdmission);

        // Date of Birth
        holder.tvDob.setText(student.dateOfBirth);

        // Age (calculate from DOB)
        String age = calculateAge(student.getDateOfBirth());
        holder.tvAge.setText(age);

        // Gender
        holder.tvGender.setText(student.getGender() != null ? student.getGender() : "N/A");

        // Student Birth ID (using registration number)
        holder.tvBirthId.setText(student.getRegistrationNumber() != null ?
                student.getRegistrationNumber() : "N/A");

        // Religion (placeholder - not available in model)
        holder.tvReligion.setText("N/A");

        // Cast (placeholder - not available in model)
        holder.tvCast.setText("N/A");

        // Status (placeholder - assuming active)
        holder.tvStatus.setText("Active");
    }

    @Override
    public int getItemCount() {
        return studentList != null ? studentList.size() : 0;
    }

    // Helper method to format date
    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "N/A";
        }

        // Assuming date comes in yyyy-MM-dd format, convert to dd/MM/yyyy
        try {
            String[] parts = dateString.split("-");
            if (parts.length == 3) {
                return parts[2] + "/" + parts[1] + "/" + parts[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateString; // Return original if formatting fails
    }

    // Helper method to calculate age from date of birth
    private String calculateAge(String dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.isEmpty()) {
            return "N/A";
        }

        try {
            String[] parts;
            int year, month, day;

            // Handle different date formats
            if (dateOfBirth.contains("-")) {
                parts = dateOfBirth.split("-");
                year = Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1]);
                day = Integer.parseInt(parts[2]);
            } else if (dateOfBirth.contains("/")) {
                parts = dateOfBirth.split("/");
                // Assuming dd/MM/yyyy format
                day = Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1]);
                year = Integer.parseInt(parts[2]);
            } else {
                return "N/A";
            }

            Calendar today = Calendar.getInstance();
            Calendar birthDate = Calendar.getInstance();
            birthDate.set(year, month - 1, day); // Month is 0-based in Calendar

            int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

            // Check if birthday hasn't occurred this year
            if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return String.valueOf(age);
        } catch (Exception e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    // Method to update the adapter data
    public void updateData(ArrayList<StudentInfoReport.Datum> newStudentList) {
        this.studentList = newStudentList;
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvSerialNumber, tvStudentId, tvStudentName, tvFatherName, tvClass,
                tvDiscountFees, tvAdmissionDate, tvDob, tvAge, tvGender, tvBirthId,
                tvReligion, tvCast, tvStatus;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSerialNumber = itemView.findViewById(R.id.tv_serial_number);
            tvStudentId = itemView.findViewById(R.id.tv_student_id);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvFatherName = itemView.findViewById(R.id.tv_father_name);
            tvClass = itemView.findViewById(R.id.tv_class);
            tvDiscountFees = itemView.findViewById(R.id.tv_discount_fees);
            tvAdmissionDate = itemView.findViewById(R.id.tv_admission_date);
            tvDob = itemView.findViewById(R.id.tv_dob);
            tvAge = itemView.findViewById(R.id.tv_age);
            tvGender = itemView.findViewById(R.id.tv_gender);
            tvBirthId = itemView.findViewById(R.id.tv_birth_id);
            tvReligion = itemView.findViewById(R.id.tv_religion);
            tvCast = itemView.findViewById(R.id.tv_cast);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}
