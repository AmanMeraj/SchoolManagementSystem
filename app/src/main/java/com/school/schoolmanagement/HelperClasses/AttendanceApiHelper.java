package com.school.schoolmanagement.HelperClasses;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.AttendanceEntries;
import com.school.schoolmanagement.Model.StudentsAttendance;
import com.school.schoolmanagement.Model.AttendanceReport;
import com.school.schoolmanagement.Utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceApiHelper extends Utility {

    private ViewModel viewModel;
    private Context context;
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_LIMIT = 100;

    public AttendanceApiHelper(Context context) {
        this.context = context;
        if (context instanceof ViewModelStoreOwner) {
            this.viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ViewModel.class);
        }
    }

    // Interface for attendance callback
    public interface AttendanceCallback {
        void onSuccess(List<AttendanceEntries> attendanceList);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }

    // Interface for attendance report callback
    public interface AttendanceReportCallback {
        void onSuccess(AttendanceReport attendanceReport);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }

    // Interface for attendance statistics callback
    public interface AttendanceStatsCallback {
        void onSuccess(AttendanceStats stats);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }

    // Interface for employee attendance callback
    public interface EmployeeAttendanceCallback {
        void onSuccess(List<AttendanceEntries> employeeAttendanceList);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }

    // Interface for employee attendance report callback
    public interface EmployeeAttendanceReportCallback {
        void onSuccess(AttendanceReport employeeAttendanceReport);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }

    // Interface for employee attendance statistics callback
    public interface EmployeeAttendanceStatsCallback {
        void onSuccess(AttendanceStats stats);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }

    // Method to fetch monthly attendance data
    public void fetchMonthlyAttendance(int classId, String month, String year, AttendanceCallback callback) {
        fetchMonthlyAttendance(classId, month, year, DEFAULT_PAGE, DEFAULT_LIMIT, callback);
    }

    // Method to fetch monthly attendance data with pagination
    public void fetchMonthlyAttendance(int classId, String month, String year, int page, int limit, AttendanceCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        callback.onLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner && viewModel != null) {
            viewModel.getStudentsMonthlyAttendance(auth, month, year, page, limit, classId)
                    .observe((LifecycleOwner) context, response -> {
                        callback.onLoading(false);
                        if (response != null && response.isSuccess && response.data != null) {
                            AttendanceReport report = response.data;
                            if (report.getData() != null &&
                                    report.getData().getStudentsAttendance() != null &&
                                    report.getData().getStudentsAttendance().getEntries() != null &&
                                    !report.getData().getStudentsAttendance().getEntries().isEmpty()) {
                                callback.onSuccess(report.getData().getStudentsAttendance().getEntries());
                            } else {
                                callback.onError("No attendance data found for the specified period");
                            }
                        } else {
                            String errorMessage = response != null && response.message != null ?
                                    response.message : "Failed to fetch attendance data";
                            callback.onError(errorMessage);
                        }
                    });
        } else {
            callback.onLoading(false);
            callback.onError("Context is not a LifecycleOwner or ViewModel is not available");
        }
    }

    // Method to fetch complete attendance report
    public void fetchMonthlyAttendanceReport(int classId, String month, String year, AttendanceReportCallback callback) {
        fetchMonthlyAttendanceReport(classId, month, year, DEFAULT_PAGE, DEFAULT_LIMIT, callback);
    }

    // Method to fetch complete attendance report with pagination
    public void fetchMonthlyAttendanceReport(int classId, String month, String year, int page, int limit, AttendanceReportCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        callback.onLoading(true);
        Log.d("TAG", "fetchAttendanceStatistics: "+month+" "+year);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner && viewModel != null) {
            viewModel.getStudentsMonthlyAttendance(auth, month, year, page, limit, classId)
                    .observe((LifecycleOwner) context, response -> {
                        callback.onLoading(false);
                        if (response != null && response.isSuccess && response.data != null) {
                            callback.onSuccess(response.data);
                        } else {
                            String errorMessage = response != null && response.message != null ?
                                    response.message : "Failed to fetch attendance report";
                            callback.onError(errorMessage);
                        }
                    });
        } else {
            callback.onLoading(false);
            callback.onError("Context is not a LifecycleOwner or ViewModel is not available");
        }
    }

    // Method to fetch attendance statistics
    public void fetchAttendanceStatistics(int classId, String month, String year, AttendanceStatsCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        callback.onLoading(true);

        // Fetch the attendance report first and then calculate statistics
        fetchMonthlyAttendanceReport(classId, month, year, new AttendanceReportCallback() {
            @Override
            public void onSuccess(AttendanceReport attendanceReport) {
                try {
                    AttendanceStats stats = calculateAttendanceStats(attendanceReport, month, year);
                    callback.onLoading(false);
                    callback.onSuccess(stats);
                } catch (Exception e) {
                    callback.onLoading(false);
                    callback.onError("Error calculating statistics: " + e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onLoading(false);
                callback.onError(errorMessage);
            }

            @Override
            public void onLoading(boolean isLoading) {
                // Loading state is handled by the parent method
            }
        });
    }

    // ================ EMPLOYEE ATTENDANCE METHODS ================

    // Method to fetch monthly employee attendance data
    public void fetchEmployeeMonthlyAttendance(String month, String year, String role, EmployeeAttendanceCallback callback) {
        fetchEmployeeMonthlyAttendance(month, year, role, DEFAULT_PAGE, DEFAULT_LIMIT, callback);
    }

    // Method to fetch monthly employee attendance data with pagination
    public void fetchEmployeeMonthlyAttendance(String month, String year, String role, int page, int limit, EmployeeAttendanceCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        callback.onLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner && viewModel != null) {
            viewModel.getEmployeeMonthlyAttendance(auth, month, year, page, limit, role)
                    .observe((LifecycleOwner) context, response -> {
                        callback.onLoading(false);
                        if (response != null && response.isSuccess && response.data != null) {
                            AttendanceReport report = response.data;
                            if (report.getData() != null &&
                                    report.getData().getStudentsAttendance() != null &&
                                    report.getData().getStudentsAttendance().getEntries() != null &&
                                    !report.getData().getStudentsAttendance().getEntries().isEmpty()) {
                                callback.onSuccess(report.getData().getStudentsAttendance().getEntries());
                            } else {
                                callback.onError("No employee attendance data found for the specified period");
                            }
                        } else {
                            String errorMessage = response != null && response.message != null ?
                                    response.message : "Failed to fetch employee attendance data";
                            callback.onError(errorMessage);
                        }
                    });
        } else {
            callback.onLoading(false);
            callback.onError("Context is not a LifecycleOwner or ViewModel is not available");
        }
    }

    // Method to fetch complete employee attendance report
    public void fetchEmployeeMonthlyAttendanceReport(String month, String year, String role, EmployeeAttendanceReportCallback callback) {
        fetchEmployeeMonthlyAttendanceReport(month, year, role, DEFAULT_PAGE, DEFAULT_LIMIT, callback);
    }

    // Method to fetch complete employee attendance report with pagination
    public void fetchEmployeeMonthlyAttendanceReport(String month, String year, String role, int page, int limit, EmployeeAttendanceReportCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        callback.onLoading(true);
        Log.d("TAG", "fetchEmployeeAttendanceReport: " + month + " " + year + " " + role);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner && viewModel != null) {
            viewModel.getEmployeeMonthlyAttendance(auth, month, year, page, limit, role)
                    .observe((LifecycleOwner) context, response -> {
                        callback.onLoading(false);
                        if (response != null && response.isSuccess && response.data != null) {
                            callback.onSuccess(response.data);
                        } else {
                            String errorMessage = response != null && response.message != null ?
                                    response.message : "Failed to fetch employee attendance report";
                            callback.onError(errorMessage);
                        }
                    });
        } else {
            callback.onLoading(false);
            callback.onError("Context is not a LifecycleOwner or ViewModel is not available");
        }
    }

    // Method to fetch employee attendance statistics
    public void fetchEmployeeAttendanceStatistics(String month, String year, String role, EmployeeAttendanceStatsCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        callback.onLoading(true);

        // Fetch the employee attendance report first and then calculate statistics
        fetchEmployeeMonthlyAttendanceReport(month, year, role, new EmployeeAttendanceReportCallback() {
            @Override
            public void onSuccess(AttendanceReport attendanceReport) {
                try {
                    AttendanceStats stats = calculateAttendanceStats(attendanceReport, month, year);
                    callback.onLoading(false);
                    callback.onSuccess(stats);
                } catch (Exception e) {
                    callback.onLoading(false);
                    callback.onError("Error calculating employee statistics: " + e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onLoading(false);
                callback.onError(errorMessage);
            }

            @Override
            public void onLoading(boolean isLoading) {
                // Loading state is handled by the parent method
            }
        });
    }

    // Method to get employee attendance data for a specific employee by ID
    public void fetchSpecificEmployeeAttendance(int employeeId, String month, String year, String role, EmployeeAttendanceCallback callback) {
        fetchEmployeeMonthlyAttendance(month, year, role, new EmployeeAttendanceCallback() {
            @Override
            public void onSuccess(List<AttendanceEntries> attendanceList) {
                List<AttendanceEntries> employeeAttendance = new ArrayList<>();
                for (AttendanceEntries employee : attendanceList) {
                    if (employee.getStudentId() == employeeId) { // Assuming employeeId maps to studentId field
                        employeeAttendance.add(employee);
                        break;
                    }
                }

                if (!employeeAttendance.isEmpty()) {
                    callback.onSuccess(employeeAttendance);
                } else {
                    callback.onError("No attendance data found for employee ID: " + employeeId);
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }

            @Override
            public void onLoading(boolean isLoading) {
                callback.onLoading(isLoading);
            }
        });
    }

    // Method to get employees with low attendance (below threshold)
    public List<AttendanceEntries> getEmployeesWithLowAttendance(List<AttendanceEntries> employeeAttendanceList, double threshold) {
        List<AttendanceEntries> lowAttendanceEmployees = new ArrayList<>();

        if (employeeAttendanceList != null) {
            for (AttendanceEntries employee : employeeAttendanceList) {
                if (employee.getAttendanceSummary() != null &&
                        employee.getAttendanceSummary().getAttendancePercentage() < threshold) {
                    lowAttendanceEmployees.add(employee);
                }
            }
        }

        return lowAttendanceEmployees;
    }

    // Method to get employees with perfect attendance
    public List<AttendanceEntries> getEmployeesWithPerfectAttendance(List<AttendanceEntries> employeeAttendanceList) {
        List<AttendanceEntries> perfectAttendanceEmployees = new ArrayList<>();

        if (employeeAttendanceList != null) {
            for (AttendanceEntries employee : employeeAttendanceList) {
                if (employee.getAttendanceSummary() != null &&
                        employee.getAttendanceSummary().getAttendancePercentage() == 100.0) {
                    perfectAttendanceEmployees.add(employee);
                }
            }
        }

        return perfectAttendanceEmployees;
    }

    // Method to calculate overall employee attendance percentage
    public float calculateEmployeeAttendancePercentage(List<AttendanceEntries> employeeAttendanceList) {
        if (employeeAttendanceList == null || employeeAttendanceList.isEmpty()) {
            return 0.0f;
        }

        double totalPercentage = 0.0;
        int validEmployeeCount = 0;

        for (AttendanceEntries employee : employeeAttendanceList) {
            try {
                if (employee.getAttendanceSummary() != null) {
                    double employeePercentage = employee.getAttendanceSummary().getAttendancePercentage();
                    if (employeePercentage >= 0) {
                        totalPercentage += employeePercentage;
                        validEmployeeCount++;
                    }
                }
            } catch (Exception e) {
                // Skip invalid employee data
                continue;
            }
        }

        return validEmployeeCount > 0 ? (float)(totalPercentage / validEmployeeCount) : 0.0f;
    }

    // Method to refresh employee attendance data
    public void refreshEmployeeAttendanceData(String month, String year, String role, EmployeeAttendanceCallback callback) {
        // Clear any cached data if you have caching mechanism
        fetchEmployeeMonthlyAttendance(month, year, role, callback);
    }

    // ================ END EMPLOYEE ATTENDANCE METHODS ================

    // Method to update attendance (if your API supports it)
    public void updateAttendance(int studentId, String date, String status, AttendanceCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        callback.onLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        // TODO: Implement actual update API call when endpoint is available
        // This would require a separate API endpoint for updating attendance
        callback.onLoading(false);
        callback.onError("Update attendance functionality not implemented yet");
    }

    // Method to calculate attendance statistics from report
    private AttendanceStats calculateAttendanceStats(AttendanceReport report, String month, String year) {
        if (report == null || report.getData() == null ||
                report.getData().getStudentsAttendance() == null ||
                report.getData().getStudentsAttendance().getEntries() == null ||
                report.getData().getStudentsAttendance().getEntries().isEmpty()) {
            return new AttendanceStats(0, 0, 0.0f, 0, 0);
        }

        List<AttendanceEntries> attendanceList = report.getData().getStudentsAttendance().getEntries();
        int totalDays = getTotalDaysInMonth(month, year);
        int totalStudents = attendanceList.size();

        // Use class statistics if available, otherwise calculate from entries
        if (report.getData().getClassStatistics() != null) {
            return new AttendanceStats(
                    totalDays,
                    totalStudents,
                    (float) report.getData().getClassStatistics().getAverageAttendancePercentage(),
                    report.getData().getClassStatistics().getTotalPresentDays() / totalStudents,
                    report.getData().getClassStatistics().getTotalAbsentDays() / totalStudents
            );
        }

        // Calculate from individual entries
        double totalAttendancePercentage = 0.0;
        int totalPresentDays = 0;
        int totalAbsentDays = 0;
        int validStudentCount = 0;

        for (AttendanceEntries student : attendanceList) {
            try {
                if (student.getAttendanceSummary() != null) {
                    double percentage = student.getAttendanceSummary().getAttendancePercentage();
                    int present = student.getAttendanceSummary().getPresentDays();
                    int absent = student.getAttendanceSummary().getAbsentDays();

                    if (percentage >= 0) {
                        totalAttendancePercentage += percentage;
                        totalPresentDays += present;
                        totalAbsentDays += absent;
                        validStudentCount++;
                    }
                }
            } catch (Exception e) {
                // Skip invalid student data
                continue;
            }
        }

        float averageAttendancePercentage = validStudentCount > 0 ? (float)(totalAttendancePercentage / validStudentCount) : 0.0f;
        int averagePresentDays = validStudentCount > 0 ? totalPresentDays / validStudentCount : 0;
        int averageAbsentDays = validStudentCount > 0 ? totalAbsentDays / validStudentCount : 0;

        return new AttendanceStats(totalDays, totalStudents, averageAttendancePercentage, averagePresentDays, averageAbsentDays);
    }

    // Helper methods - Now properly implemented based on the JSON structure
    private float calculateStudentAttendancePercentage(AttendanceEntries student, int totalDays) {
        if (student.getAttendanceSummary() != null) {
            return (float) student.getAttendanceSummary().getAttendancePercentage();
        }
        return 0.0f;
    }

    private int getStudentPresentDays(AttendanceEntries student) {
        if (student.getAttendanceSummary() != null) {
            return student.getAttendanceSummary().getPresentDays();
        }
        return 0;
    }

    private int getStudentAbsentDays(AttendanceEntries student) {
        if (student.getAttendanceSummary() != null) {
            return student.getAttendanceSummary().getAbsentDays();
        }
        return 0;
    }

    // Utility method to get total days in month
    private int getTotalDaysInMonth(String month, String year) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);
            return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } catch (NumberFormatException e) {
            return 30; // Default fallback
        }
    }

    // Method to validate attendance data
    public boolean validateAttendanceData(List<AttendanceEntries> attendanceList) {
        if (attendanceList == null || attendanceList.isEmpty()) {
            return false;
        }

        for (AttendanceEntries student : attendanceList) {
            if (student == null || student.getStudentId() <= 0 ||
                    student.getStudentName() == null || student.getStudentName().trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    // Method to calculate overall class attendance percentage
    public float calculateClassAttendancePercentage(List<AttendanceEntries> attendanceList) {
        if (attendanceList == null || attendanceList.isEmpty()) {
            return 0.0f;
        }

        double totalPercentage = 0.0;
        int validStudentCount = 0;

        for (AttendanceEntries student : attendanceList) {
            try {
                if (student.getAttendanceSummary() != null) {
                    double studentPercentage = student.getAttendanceSummary().getAttendancePercentage();
                    if (studentPercentage >= 0) {
                        totalPercentage += studentPercentage;
                        validStudentCount++;
                    }
                }
            } catch (Exception e) {
                // Skip invalid student data
                continue;
            }
        }

        return validStudentCount > 0 ? (float)(totalPercentage / validStudentCount) : 0.0f;
    }

    // Method to get attendance data for a specific student
    public void fetchStudentAttendance(int studentId, int classId, String month, String year, AttendanceCallback callback) {
        fetchMonthlyAttendance(classId, month, year, new AttendanceCallback() {
            @Override
            public void onSuccess(List<AttendanceEntries> attendanceList) {
                List<AttendanceEntries> studentAttendance = new ArrayList<>();
                for (AttendanceEntries student : attendanceList) {
                    if (student.getStudentId() == studentId) {
                        studentAttendance.add(student);
                        break;
                    }
                }

                if (!studentAttendance.isEmpty()) {
                    callback.onSuccess(studentAttendance);
                } else {
                    callback.onError("No attendance data found for student ID: " + studentId);
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }

            @Override
            public void onLoading(boolean isLoading) {
                callback.onLoading(isLoading);
            }
        });
    }

    // Method to get students with low attendance (below threshold)
    public List<AttendanceEntries> getStudentsWithLowAttendance(List<AttendanceEntries> attendanceList, double threshold) {
        List<AttendanceEntries> lowAttendanceStudents = new ArrayList<>();

        if (attendanceList != null) {
            for (AttendanceEntries student : attendanceList) {
                if (student.getAttendanceSummary() != null &&
                        student.getAttendanceSummary().getAttendancePercentage() < threshold) {
                    lowAttendanceStudents.add(student);
                }
            }
        }

        return lowAttendanceStudents;
    }

    // Method to get students with perfect attendance
    public List<AttendanceEntries> getStudentsWithPerfectAttendance(List<AttendanceEntries> attendanceList) {
        List<AttendanceEntries> perfectAttendanceStudents = new ArrayList<>();

        if (attendanceList != null) {
            for (AttendanceEntries student : attendanceList) {
                if (student.getAttendanceSummary() != null &&
                        student.getAttendanceSummary().getAttendancePercentage() == 100.0) {
                    perfectAttendanceStudents.add(student);
                }
            }
        }

        return perfectAttendanceStudents;
    }

    // Data class for attendance statistics
    public static class AttendanceStats {
        private int totalDays;
        private int totalStudents;
        private float averageAttendancePercentage;
        private int averagePresentDays;
        private int averageAbsentDays;

        public AttendanceStats(int totalDays, int totalStudents, float averageAttendancePercentage,
                               int averagePresentDays, int averageAbsentDays) {
            this.totalDays = totalDays;
            this.totalStudents = totalStudents;
            this.averageAttendancePercentage = averageAttendancePercentage;
            this.averagePresentDays = averagePresentDays;
            this.averageAbsentDays = averageAbsentDays;
        }

        // Getters
        public int getTotalDays() { return totalDays; }
        public int getTotalStudents() { return totalStudents; }
        public float getAverageAttendancePercentage() { return averageAttendancePercentage; }
        public int getAveragePresentDays() { return averagePresentDays; }
        public int getAverageAbsentDays() { return averageAbsentDays; }

        // Setters
        public void setTotalDays(int totalDays) { this.totalDays = totalDays; }
        public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
        public void setAverageAttendancePercentage(float averageAttendancePercentage) {
            this.averageAttendancePercentage = averageAttendancePercentage;
        }
        public void setAveragePresentDays(int averagePresentDays) { this.averagePresentDays = averagePresentDays; }
        public void setAverageAbsentDays(int averageAbsentDays) { this.averageAbsentDays = averageAbsentDays; }

        @Override
        public String toString() {
            return "AttendanceStats{" +
                    "totalDays=" + totalDays +
                    ", totalStudents=" + totalStudents +
                    ", averageAttendancePercentage=" + averageAttendancePercentage +
                    ", averagePresentDays=" + averagePresentDays +
                    ", averageAbsentDays=" + averageAbsentDays +
                    '}';
        }
    }

    // Method to check if user token is valid
    public boolean isUserTokenValid() {
        String token = pref.getPrefString(context, pref.user_token);
        return token != null && !token.trim().isEmpty() && !token.equals("null");
    }

    // Method to refresh attendance data
    public void refreshAttendanceData(int classId, String month, String year, AttendanceCallback callback) {
        // Clear any cached data if you have caching mechanism
        fetchMonthlyAttendance(classId, month, year, callback);
    }
}