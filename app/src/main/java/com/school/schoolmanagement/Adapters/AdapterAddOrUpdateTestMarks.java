package com.school.schoolmanagement.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.TestMarkModel;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.databinding.RowAddOrUpdateMarksBinding;

import java.util.ArrayList;
import java.util.List;

public class AdapterAddOrUpdateTestMarks extends RecyclerView.Adapter<AdapterAddOrUpdateTestMarks.ViewHolder> {

    private final List<TestMarkModel> list;
    private final Context context;

    // Solution 1: Use a single constructor with a factory method pattern
    private AdapterAddOrUpdateTestMarks(Context context, List<TestMarkModel> testMarksList) {
        this.context = context;
        this.list = new ArrayList<>(testMarksList);
    }

    // Factory method for creating adapter from StudentDetails
    public static AdapterAddOrUpdateTestMarks fromStudentDetails(Context context, List<StudentDetails> studentList) {
        List<TestMarkModel> testMarksList = new ArrayList<>();

        // Map StudentDetails to TestMarkModel
        for (StudentDetails student : studentList) {
            TestMarkModel testMark = new TestMarkModel(
                    student.getStudentId(),
                    student.getStudentName(),
                    0 // Default obtained marks
            );
            testMarksList.add(testMark);
        }

        return new AdapterAddOrUpdateTestMarks(context, testMarksList);
    }

    // Factory method for creating adapter from TestMarkModel list
    public static AdapterAddOrUpdateTestMarks fromTestMarks(Context context, List<TestMarkModel> testMarksList) {
        return new AdapterAddOrUpdateTestMarks(context, testMarksList);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RowAddOrUpdateMarksBinding binding;

        public ViewHolder(RowAddOrUpdateMarksBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowAddOrUpdateMarksBinding binding = RowAddOrUpdateMarksBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestMarkModel model = list.get(position);

        holder.binding.tvStudentId.setText(String.valueOf(model.getStudentId()));
        holder.binding.tvStudentName.setText(model.getStudentName());

        // Clear existing TextWatcher before setting new text
        if (holder.binding.etObtainedMarks.getTag() != null) {
            holder.binding.etObtainedMarks.removeTextChangedListener(
                    (TextWatcher) holder.binding.etObtainedMarks.getTag()
            );
        }

        // Set the text - convert int to String properly
        holder.binding.etObtainedMarks.setText(String.valueOf(model.getObtainedMarks()));

        // Create new TextWatcher for this position
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString().trim();
                try {
                    if (value.isEmpty()) {
                        model.setObtainedMarks(0);
                    } else {
                        int marks = Integer.parseInt(value);
                        // Optional: Add validation for maximum marks
                        model.setObtainedMarks(marks);
                    }
                } catch (NumberFormatException e) {
                    // Handle invalid input - set to 0
                    model.setObtainedMarks(0);
                }
            }
        };

        holder.binding.etObtainedMarks.addTextChangedListener(textWatcher);
        holder.binding.etObtainedMarks.setTag(textWatcher);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        // Clean up TextWatcher when view is recycled
        if (holder.binding.etObtainedMarks.getTag() != null) {
            holder.binding.etObtainedMarks.removeTextChangedListener(
                    (TextWatcher) holder.binding.etObtainedMarks.getTag()
            );
            holder.binding.etObtainedMarks.setTag(null);
        }
        super.onViewRecycled(holder);
    }

    public void validateAllMarks() {
        for (TestMarkModel model : list) {
            // Ensure no negative marks and handle any invalid states
            if (model.getObtainedMarks() < 0) {
                model.setObtainedMarks(0);
            }
        }
        notifyDataSetChanged();
    }

    public List<TestMarkModel> getUpdatedList() {
        return new ArrayList<>(list); // Return a copy to prevent external modifications
    }

    // Method to update total marks for all items
    public void updateTotalMarks(int totalMarks) {
        for (TestMarkModel model : list) {
            model.setTotal(totalMarks);
        }
        notifyDataSetChanged();
    }

    // Method to validate marks against total marks
    public boolean validateMarksRange(int totalMarks) {
        for (TestMarkModel model : list) {
            if (model.getObtainedMarks() < 0 || model.getObtainedMarks() > totalMarks) {
                return false;
            }
        }
        return true;
    }

    // Method to get list of students with invalid marks
    public List<String> getStudentsWithInvalidMarks(int totalMarks) {
        List<String> invalidStudents = new ArrayList<>();
        for (TestMarkModel model : list) {
            if (model.getObtainedMarks() < 0 || model.getObtainedMarks() > totalMarks) {
                invalidStudents.add(model.getStudentName() + " (" + model.getObtainedMarks() + ")");
            }
        }
        return invalidStudents;
    }

    // Method to clear all marks
    public void clearAllMarks() {
        for (TestMarkModel model : list) {
            model.setObtainedMarks(0);
        }
        notifyDataSetChanged();
    }

    // Method to set marks for a specific student
    public void setMarksForStudent(int studentId, int marks) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStudentId() == studentId) {
                list.get(i).setObtainedMarks(marks);
                notifyItemChanged(i);
                break;
            }
        }
    }

    // Method to get marks summary
    public String getMarksSummary(int totalMarks) {
        if (list.isEmpty()) return "No students";

        int totalStudents = list.size();
        int studentsWithMarks = 0;
        int totalObtainedMarks = 0;
        int passingMarks = (int) (totalMarks * 0.4); // 40% passing
        int passedStudents = 0;

        for (TestMarkModel model : list) {
            int obtainedMarks = model.getObtainedMarks();
            if (obtainedMarks > 0) {
                studentsWithMarks++;
            }
            totalObtainedMarks += obtainedMarks;
            if (obtainedMarks >= passingMarks) {
                passedStudents++;
            }
        }

        double average = totalStudents > 0 ? (double) totalObtainedMarks / totalStudents : 0;
        double passPercentage = totalStudents > 0 ? ((double) passedStudents / totalStudents) * 100 : 0;

        return String.format("Total: %d | Entered: %d | Passed: %d (%.1f%%) | Avg: %.1f",
                totalStudents, studentsWithMarks, passedStudents, passPercentage, average);
    }

    // Method to convert back to StudentDetails if needed
    public List<StudentDetails> getUpdatedStudentDetails() {
        List<StudentDetails> studentList = new ArrayList<>();
        for (TestMarkModel model : list) {
            StudentDetails student = new StudentDetails();
            student.setStudentId(model.getStudentId());
            student.setStudentName(model.getStudentName());
            // Set obtained marks if StudentDetails has this field
            // student.setObtainedMarks(model.getObtainedMarks());
            studentList.add(student);
        }
        return studentList;
    }
}