package com.school.schoolmanagement.Teachers.Dashboard;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Accountant.Profile.ActivityAccountantProfile;
import com.school.schoolmanagement.Admin.Adapter.CustomNavigationAdapter;
import com.school.schoolmanagement.Admin.ClassTest.ActivityClassTestResult;
import com.school.schoolmanagement.Admin.ClassTest.ActivityCreateNewClassTest;
import com.school.schoolmanagement.Admin.Exams.AddUpdateMarks.ActivityAddUpdateMarks;
import com.school.schoolmanagement.Admin.Exams.Result.ActivitySearchResult;
import com.school.schoolmanagement.Admin.Homework.homeworks.ActivityHomeworks;
import com.school.schoolmanagement.Admin.Model.AddQuestionBank;
import com.school.schoolmanagement.Admin.Model.NavItem;
import com.school.schoolmanagement.Admin.QuestionPaper.ActivityAddSubjectChapter;
import com.school.schoolmanagement.Admin.QuestionPaper.ActivityCreateQuestionPaper;
import com.school.schoolmanagement.Admin.QuestionPaper.ActivityQuestionBank;
import com.school.schoolmanagement.Admin.Report.ActivityStudentMonthlyAttendance1;
import com.school.schoolmanagement.Admin.Report.ActivityStudentsInfoReport;
import com.school.schoolmanagement.Admin.Report.ActivityStudentsReportCard;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Login.ActivityLogin;
import com.school.schoolmanagement.Model.EmployeeDashboardResponse;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.Model.StudentDashboardApiResponse1;
import com.school.schoolmanagement.Students.UpdateLoginDetails.ActivityUpdateLoginCredentials;
import com.school.schoolmanagement.Teachers.AddChapter.ActivityAddNewChapter;
import com.school.schoolmanagement.Teachers.Attendance.ActivityStudentsAttendancereport;
import com.school.schoolmanagement.Teachers.Exams.ActivityAddUpdateStudentsMarks;
import com.school.schoolmanagement.Teachers.Attendance.ActivityAddUpdateAttendance;
import com.school.schoolmanagement.Teachers.Attendance.ActivityClassWiseAttendanceReport;
import com.school.schoolmanagement.Teachers.Attendance.ActivitySearchStudentsAttendanceReport;
import com.school.schoolmanagement.Teachers.Homework.ActivitySearchHomework;
import com.school.schoolmanagement.Teachers.QuestionBank.ActivityAddQuestionBank;
import com.school.schoolmanagement.Teachers.Test.ActivityCreateNewTest;
import com.school.schoolmanagement.Teachers.Timetable.ActivityTeachersTimetable;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityTeachersDashboardBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ActivityTeachersDashboard extends Utility {
    ActivityTeachersDashboardBinding binding;
    private DrawerLayout drawerLayout;
    private ExpandableListView expandableListView;
    private CustomNavigationAdapter navigationAdapter;
    String auth;
    ViewModel viewModel;
    int employeeId;
    private ArrayList<NavItem> navItems;
    private HashMap<NavItem, List<String>> navSubItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTeachersDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();
        setupToolbar();
        setupNavigation();
        setupClickListeners();

        if (isInternetConnected(this)) {
            fetchEmployeeDashboard();
        } else {
            showToast("No internet connection");
        }
    }

    private void initializeComponents() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        auth ="Bearer "+ pref.getPrefString(this, pref.user_token);
    }

    private void setupClickListeners() {
        // Profile click listener
        binding.profile.setOnClickListener(v -> {
            // Handle profile click - maybe show profile options
            showProfileOptions();
        });

        // Chat button click listener
        binding.btnChat.setOnClickListener(v -> {
            // Handle chat functionality
            showToast("Chat feature coming soon");
        });

        // Notification button click listener
        binding.btnNotification.setOnClickListener(v -> {
            // Handle notifications
            showToast("Notifications feature coming soon");
        });

        // School selection card click listener
        binding.cardSelectSchool.setOnClickListener(v -> {
            // Handle school selection
            showToast("School selection feature coming soon");
        });

        // Profile card click listener
        binding.cardProfile.setOnClickListener(v -> {
            // Navigate to profile details
            navigateToProfile();
        });

        // View profile click listener
        binding.viewProfileRel.setOnClickListener(v -> {
            navigateToProfile();
        });
    }

    private void showProfileOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Options")
                .setItems(new String[]{"View Profile", "Edit Profile", "Settings"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            navigateToProfile();
                            break;
                        case 1:
                            // Navigate to edit profile
                            showToast("Edit Profile feature coming soon");
                            break;
                        case 2:
                            Intent settingsIntent = new Intent(this, ActivityUpdateLoginCredentials.class);
                            startActivity(settingsIntent);
                            break;
                    }
                })
                .show();
    }

    private void navigateToProfile() {
        // Navigate to profile activity
       Intent intent= new Intent(ActivityTeachersDashboard.this, ActivityAccountantProfile.class);
        intent.putExtra("employeeId",employeeId);
       startActivity(intent);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");

        drawerLayout = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.black));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupNavigation() {
        expandableListView = binding.expandableNavList;
        prepareNavigationData();

        navigationAdapter = new CustomNavigationAdapter(this, navItems, navSubItems);
        expandableListView.setAdapter(navigationAdapter);

        setupNavigationListeners();
    }

    private void prepareNavigationData() {
        navItems = new ArrayList<>();
        navSubItems = new HashMap<>();

        // Add all menu items
        navItems.add(new NavItem("Dashboard", R.drawable.layout));

        // Attendance with subitems
        NavItem attendanceNavItem = new NavItem("Attendance", R.drawable.layout);
        navItems.add(attendanceNavItem);
        List<String> attendanceSubItems = Arrays.asList(
                "Students Attendance",
                "Class wise Report",
                "Students Attendance Report"
        );
        navSubItems.put(attendanceNavItem, attendanceSubItems);

        navItems.add(new NavItem("Homework", R.drawable.layout));
        navItems.add(new NavItem("Time Table", R.drawable.layout));

        // Question Paper with subitems
        NavItem questionNavItem = new NavItem("Question Paper", R.drawable.layout);
        navItems.add(questionNavItem);
        List<String> questionSubItems = Arrays.asList(
                "Subject Chapters",
                "Question Bank",
                "Create Question Papers"
        );
        navSubItems.put(questionNavItem, questionSubItems);

        // Exams with subitems
        NavItem examsNavItem = new NavItem("Exams", R.drawable.layout);
        navItems.add(examsNavItem);
        List<String> examsSubItems = Arrays.asList(
                "Add / Update Exam Marks",
                "Result card"
        );
        navSubItems.put(examsNavItem, examsSubItems);

        // Class Test with subitems
        NavItem classTestNavItem = new NavItem("Class Test", R.drawable.layout);
        navItems.add(classTestNavItem);
        List<String> classTestSubItems = Arrays.asList(
                "Create Test",
                "View Results"
        );
        navSubItems.put(classTestNavItem, classTestSubItems);

        // Reports with subitems
        NavItem reportsNavItem = new NavItem("Report", R.drawable.layout);
        navItems.add(reportsNavItem);
        List<String> reportsSubItems = Arrays.asList(
                "Students Report Card",
                "Students Information",
                "Students Monthly Attendance Report"
        );
        navSubItems.put(reportsNavItem, reportsSubItems);

        navItems.add(new NavItem("Accounts Settings", R.drawable.layout));
        navItems.add(new NavItem("Logout", R.drawable.layout));
    }

    private void setupNavigationListeners() {
        expandableListView.setOnGroupClickListener((parent, view, groupPosition, id) -> {
            NavItem item = navItems.get(groupPosition);
            if (!navSubItems.containsKey(item)) {
                // Handle click for items without subitems
                handleNavigation(item.getTitle());
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });

        expandableListView.setOnChildClickListener((parent, view, groupPosition, childPosition, id) -> {
            NavItem group = navItems.get(groupPosition);
            String child = navSubItems.get(group).get(childPosition);
            handleSubItemNavigation(group.getTitle(), child);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        expandableListView.setOnGroupExpandListener(groupPosition -> {
            // Collapse other groups
            for (int i = 0; i < navItems.size(); i++) {
                if (i != groupPosition && expandableListView.isGroupExpanded(i)) {
                    expandableListView.collapseGroup(i);
                }
            }
        });
    }

    private void handleNavigation(String itemTitle) {
        // Handle navigation for main menu items
        switch (itemTitle) {
            case "Dashboard":
                // Already on dashboard, just close drawer
                break;
            case "Homework":
                Intent HomeworkIntent = new Intent(this, ActivityHomeworks.class);
                startActivity(HomeworkIntent);
                break;
            case "Time Table":
                Toast.makeText(this, "Soon To be Introduced", Toast.LENGTH_SHORT).show();
//                Intent timetableIntent = new Intent(this, ActivityTeachersTimetable.class);
//                startActivity(timetableIntent);
                break;
            case "Accounts Settings":
                Intent accountSettingsIntent = new Intent(this, ActivityUpdateLoginCredentials.class);
                startActivity(accountSettingsIntent);
                break;
            case "Logout":
                showLogoutDialog();
                break;
            default:
                Toast.makeText(this, itemTitle + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void performLogout() {
        // Clear preferences
        pref.clearAll(this);

        Intent logoutIntent = new Intent(this, ActivityLogin.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logoutIntent);
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void handleSubItemNavigation(String groupTitle, String itemTitle) {
        // Handle navigation for submenu items
        switch (groupTitle) {
            case "Attendance":
                navigateAttendance(itemTitle);
                break;
            case "Question Paper":
                navigateQuestionPaper(itemTitle);
                break;
            case "Exams":
                navigateExams(itemTitle);
                break;
            case "Class Test":
                navigateClassTest(itemTitle);
                break;
            case "Report":
                navigateReports(itemTitle);
                Log.d("TAG", "handleSubItemNavigation: here i am in reports");
                break;
            default:
                Toast.makeText(this, groupTitle + ": " + itemTitle + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void navigateAttendance(String subItem) {
        switch (subItem) {
            case "Students Attendance":
                Intent markAttendanceIntent = new Intent(this, ActivityAddUpdateAttendance.class);
                startActivity(markAttendanceIntent);
                break;
            case "Class wise Report":
                Intent viewAttendanceIntent = new Intent(this, ActivityClassWiseAttendanceReport.class);
                startActivity(viewAttendanceIntent);
                break;
            case "Students Attendance Report":
                Intent attendanceReportIntent = new Intent(this, ActivitySearchStudentsAttendanceReport.class);
                startActivity(attendanceReportIntent);
                break;
            default:
                Toast.makeText(this, "Attendance: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void navigateQuestionPaper(String subItem) {
        switch (subItem) {
            case "Subject Chapters":
                Intent addChapterIntent = new Intent(this, ActivityAddSubjectChapter.class);
                startActivity(addChapterIntent);
                break;
            case "Question Bank":
                Intent viewQuestionsIntent = new Intent(this, ActivityQuestionBank.class);
                startActivity(viewQuestionsIntent);
                break;
            case "Create Question Papers":
                Intent createQuestionIntent = new Intent(this, ActivityCreateQuestionPaper.class);
                startActivity(createQuestionIntent);
                break;
            default:
                Toast.makeText(this, "Question Paper: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void navigateExams(String subItem) {
        switch (subItem) {
            case "Add / Update Exam Marks":
                Intent examScheduleIntent = new Intent(this, ActivityAddUpdateMarks.class);
                startActivity(examScheduleIntent);
                break;
            case "Result Card":
                Intent examResultIntent = new Intent(this, ActivitySearchResult.class);
                startActivity(examResultIntent);
                break;
            default:
                Toast.makeText(this, "Exams: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void navigateClassTest(String subItem) {
        switch (subItem) {
            case "Create Test":
                Intent createTestIntent = new Intent(this, ActivityCreateNewClassTest.class);
                startActivity(createTestIntent);
                break;
            case "View Results":
                Intent createTestResultIntent = new Intent(this, ActivityClassTestResult.class);
                startActivity(createTestResultIntent);
                break;
            default:
                Toast.makeText(this, "Class Test: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void navigateReports(String subItem) {
        // Add debug logging to see what's being passed
        Log.d("Navigation", "Received subItem: '" + subItem + "'");

        // Trim whitespace and ensure exact matching
        String trimmedSubItem = subItem.trim();

        switch (trimmedSubItem) {
            case "Students Report Card":
                Intent attendanceReportIntent = new Intent(this, ActivityStudentsReportCard.class);
                startActivity(attendanceReportIntent);
                break;
            case "Students Information":
                Intent reportIntent = new Intent(this, ActivityStudentsInfoReport.class);
                startActivity(reportIntent);
                break;
            case "Students Monthly Attendance Report":
                Intent reportIntent2 = new Intent(this, ActivityStudentMonthlyAttendance1.class);
                startActivity(reportIntent2);
                break;
            default:
                // This will help you see what exact string is being passed
                Log.d("Navigation", "No match found for: '" + trimmedSubItem + "'");
                Toast.makeText(this, "Report: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void fetchEmployeeDashboard() {
        showLoading(true);
        Log.d("Dashboard", "Starting fetchEmployeeDashboard...");

        viewModel.getEmployeeDashboard(auth).observe(this, response -> {
            showLoading(false);

            // Comprehensive logging for debugging
            Log.d("Dashboard", "=== Response Debug Info ===");
            Log.d("Dashboard", "Response object: " + (response != null ? "Not null" : "NULL"));

            if (response != null) {
                Log.d("Dashboard", "isSuccess: " + response.isSuccess);
                Log.d("Dashboard", "Response code: " + response.code);
                Log.d("Dashboard", "Response message: " + response.message);
                Log.d("Dashboard", "Response data: " + (response.data != null ? "Not null" : "NULL"));

                if (response.data != null) {
                    try {
                        EmployeeDashboardResponse.Data innerData = response.data.getData();
                        Log.d("Dashboard", "Inner getData(): " + (innerData != null ? "Not null" : "NULL"));

                        if (innerData != null) {
                            Log.d("Dashboard", "Employee: " + (innerData.getEmployee() != null ? "Not null" : "NULL"));
                            Log.d("Dashboard", "School: " + (innerData.getSchool() != null ? "Not null" : "NULL"));
                            Log.d("Dashboard", "Salary: " + (innerData.getSalary() != null ? "Not null" : "NULL"));
                            Log.d("Dashboard", "Attendance: " + (innerData.getAttendance() != null ? "Not null" : "NULL"));
                        }
                    } catch (Exception e) {
                        Log.e("Dashboard", "Error accessing getData(): " + e.getMessage());
                    }
                }
            }
            Log.d("Dashboard", "=== End Debug Info ===");

            // Main logic with improved error handling
            if (response != null && response.isSuccess && response.data != null) {
                try {
                    EmployeeDashboardResponse.Data data = response.data.getData();

                    if (data == null) {
                        Log.e("Dashboard", "getData() returned null despite response.data being not null");
                        Toast.makeText(this, "No employee data received from server", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    binding.tvWelcome.setText("Welcome ! "+response.data.getData().getEmployee().getName());

                    // Validate essential data before proceeding
                    if (data.getEmployee() == null) {
                        Log.e("Dashboard", "Employee data is null");
                        Toast.makeText(this, "Employee information not available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Set profile information with null checks
                    if (data.getEmployee().getName() != null) {
                        binding.tvStudentsName.setText(data.getEmployee().getName());
                    } else {
                        binding.tvStudentsName.setText("N/A");
                        Log.w("Dashboard", "Employee name is null");
                    }

                    if (data.getEmployee().getRole() != null) {
                        binding.tvRole.setText(data.getEmployee().getRole());
                    } else {
                        binding.tvRole.setText("N/A");
                        Log.w("Dashboard", "Employee role is null");
                    }

                    if (data.getEmployee().getId() != -1) {
                        binding.tvStudentId.setText("ID: " + data.getEmployee().getId());
                        employeeId = data.getEmployee().getId();
                        Log.d("Dashboard", "Employee ID set: " + employeeId);
                    } else {
                        binding.tvStudentId.setText("ID: N/A");
                        Log.w("Dashboard", "Employee ID is null");
                    }

                    // Set school information with null checks
                    if (data.getSchool() != null) {
                        if (data.getSchool().getName() != null) {
                            binding.tvSchoolName.setText(data.getSchool().getName());
                        } else {
                            binding.tvSchoolName.setText("N/A");
                            Log.w("Dashboard", "School name is null");
                        }

                        if (data.getSchool().getSlogan() != null) {
                            binding.tvSchoolSlogan.setText(data.getSchool().getSlogan());
                        } else {
                            binding.tvSchoolSlogan.setText("");
                            Log.w("Dashboard", "School slogan is null");
                        }
                    } else {
                        binding.tvSchoolName.setText("N/A");
                        binding.tvSchoolSlogan.setText("");
                        Log.w("Dashboard", "School data is null");
                    }

                    // Set salary information with null checks
                    if (data.getSalary() != null) {
                        String lastSalary = data.getSalary().getLastSalary();
                        String monthlySalary = String.valueOf(data.getSalary().getMonthlySalary());

                        binding.tvCurrentSalary.setText("₹" + (lastSalary != null ? lastSalary : "0"));
                        binding.tvThisMonth.setText("₹" + (monthlySalary != null ? monthlySalary : "0"));

                        if (lastSalary == null) Log.w("Dashboard", "Last salary is null");
                        if (monthlySalary == null) Log.w("Dashboard", "Monthly salary is null");
                    } else {
                        binding.tvCurrentSalary.setText("₹0");
                        binding.tvThisMonth.setText("₹0");
                        Log.w("Dashboard", "Salary data is null");
                    }

                    // Update attendance progress with null check
                    if (data.getAttendance() != null) {
                        updateAttendanceProgress(data.getAttendance());
                    } else {
                        Log.w("Dashboard", "Attendance data is null");
                        // You might want to call updateAttendanceProgress with default values
                        // or handle this case in the updateAttendanceProgress method
                    }

                    // Set profile image in main layout
                    String profileImage = data.getEmployee().getProfileImage();
                    if (profileImage != null && !profileImage.trim().isEmpty()) {
                        setProfileImage(profileImage);
                    } else {
                        Log.w("Dashboard", "Profile image is null or empty");
                        // You might want to set a default profile image here
                    }

                    // Update Navigation Header
                    updateNavigationHeader(data);

                    Log.d("Dashboard", "Dashboard data loaded successfully");

                } catch (Exception e) {
                    Log.e("Dashboard", "Exception while processing dashboard data: " + e.getMessage(), e);
                    Toast.makeText(this, "Error processing dashboard data", Toast.LENGTH_SHORT).show();
                }

            } else if (response != null && response.code == 401) {
                Log.w("Dashboard", "Unauthorized access - showing login dialog");
                showLoginDialog();

            } else {
                // Enhanced error logging
                String errorMsg;
                if (response == null) {
                    errorMsg = "No response received from server";
                    Log.e("Dashboard", "Response is null - possible network issue");
                } else if (!response.isSuccess) {
                    errorMsg = "Server returned error: " + (response.message != null ? response.message : "Unknown error");
                    Log.e("Dashboard", "Server error - isSuccess: false, message: " + response.message);
                } else if (response.data == null) {
                    errorMsg = "No data received from server";
                    Log.e("Dashboard", "Response data is null");
                } else {
                    errorMsg = response.message != null ? response.message : "Unknown error occurred";
                    Log.e("Dashboard", "Unknown error condition - message: " + response.message);
                }

                Toast.makeText(this, "Failed to load dashboard: " + errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setProfileImage(String profileImageBase64) {
        if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(profileImageBase64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                // Assuming you have an ImageView for profile in your layout
                // binding.imgProfile.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ProfileImage", "Error decoding profile image: " + e.getMessage());
            }
        }
    }

    private void updateAttendanceProgress(EmployeeDashboardResponse.Attendance attendance) {
        // Format double to two decimal places
        DecimalFormat df = new DecimalFormat("0.0");

        // Year and month percentages (formatted)
        double yearPercentage = attendance.getCurrentYearPercentage();
        double monthPercentage = attendance.getCurrentMonthPercentage();

        binding.progressCircle1.setProgress((int) yearPercentage);
        binding.progressCircle2.setProgress((int) monthPercentage);

        binding.tvPercentage1.setText(df.format(yearPercentage) + "%");
        binding.tvPercentage2.setText(df.format(monthPercentage) + "%");

        // Total working days for the year
        int totalWorkingDays = attendance.getCurrentYearTotalWorkingDays();
        int presentDays = attendance.getCurrentYearPresentDays();
        int absentDays = attendance.getCurrentYearAbsentDays();
        int leaveDays = attendance.getCurrentYearLeaveDays();

        // Avoid division by zero
        if (totalWorkingDays > 0) {
            double presentPercent = (presentDays * 100.0) / totalWorkingDays;
            double absentPercent = (absentDays * 100.0) / totalWorkingDays;
            double leavePercent = (leaveDays * 100.0) / totalWorkingDays;

            // Set progress values
            binding.progressPresent.setProgress((int) presentPercent);
            binding.progressAbsent.setProgress((int) absentPercent);
            binding.progressLeaves.setProgress((int) leavePercent);
            // (Optional) Add: binding.progressLeave.setProgress(leaveDays); if you have one

            // Set percent text
            binding.tvPresentPercentage.setText(df.format(presentPercent) + "%");
            binding.tvAbsentPercentage.setText(df.format(absentPercent) + "%");
            binding.tvLeavesPercentage.setText(df.format(absentPercent) + "%");
            // (Optional) Add: binding.tvLeavePercent.setText(df.format(leavePercent) + "%");
        } else {
            // Set 0% if total working days is 0
            binding.progressPresent.setProgress(0);
            binding.progressAbsent.setProgress(0);
            binding.progressLeaves.setProgress(0);
            binding.tvPresentPercentage.setText("0.0%");
            binding.tvAbsentPercentage.setText("0.0%");
            binding.tvLeavesPercentage.setText("0.0%");
        }
    }

    private void updateNavigationHeader(EmployeeDashboardResponse.Data data) {
        if (data == null || binding.navHead == null) {
            return;
        }

        // Update admin email
        if (binding.navHead.navHeaderAdminEmail != null && data.getEmployee()!= null) {
            String username = data.getEmployee().getName();
            binding.navHead.navHeaderAdminEmail.setText(
                    username != null && !username.isEmpty() ? username : "No email available"
            );
        }

        // Update admin name (role)
        if (binding.navHead.navHeaderAdminName != null && data.getEmployee() != null) {
            String role = data.getEmployee().getRole();
            binding.navHead.navHeaderAdminName.setText(
                    role != null && !role.isEmpty() ? role : "Admin"
            );
        }

        // Update admin status
        if (binding.navHead.navHeaderStatus != null && data.getEmployee() != null) {
            String status ="Active";
            if (status != null && !status.isEmpty()) {
                binding.navHead.navHeaderStatus.setText(status);

                // Set status color based on status value
                int statusColor;
                switch (status.toLowerCase()) {
                    case "active":
                        statusColor = ContextCompat.getColor(this, android.R.color.holo_green_light);
                        break;
                    case "inactive":
                        statusColor = ContextCompat.getColor(this, android.R.color.holo_red_light);
                        break;
                    case "pending":
                        statusColor = ContextCompat.getColor(this, android.R.color.holo_orange_light);
                        break;
                    default:
                        statusColor = ContextCompat.getColor(this, android.R.color.darker_gray);
                        break;
                }
                binding.navHead.navHeaderStatus.setTextColor(statusColor);
            } else {
                binding.navHead.navHeaderStatus.setText("Unknown");
                binding.navHead.navHeaderStatus.setTextColor(
                        ContextCompat.getColor(this, android.R.color.darker_gray)
                );
            }
        }
    }

    private void loadSchoolLogo(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            setDefaultLogo();
            return;
        }

        try {
            // Strip data URL prefix if present
            String cleanBase64 = base64Image;
            if (base64Image.startsWith("data:image")) {
                int commaIndex = base64Image.indexOf(",");
                if (commaIndex != -1) {
                    cleanBase64 = base64Image.substring(commaIndex + 1);
                }
            }

            // Decode base64 to bitmap
            byte[] decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            if (bitmap != null) {
                BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);

                // Set image to navigation drawer header
                if (binding.navHead != null && binding.navHead.navHeaderLogo != null) {
                    binding.navHead.navHeaderLogo.setImageBitmap(bitmap);
                }

                // Set logo in toolbar
                if (binding.toolbar != null) {
                    binding.toolbar.setLogo(drawable);
                }

                // Set in ActionBar if available
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setLogo(drawable);
                }

            } else {
                Log.w("SchoolLogo", "Failed to decode bitmap from base64");
                setDefaultLogo();
            }

        } catch (IllegalArgumentException e) {
            Log.e("SchoolLogo", "Failed to decode base64 image: " + e.getMessage());
            setDefaultLogo();
        } catch (OutOfMemoryError e) {
            Log.e("SchoolLogo", "Out of memory while decoding image: " + e.getMessage());
            setDefaultLogo();
        } catch (Exception e) {
            Log.e("SchoolLogo", "Unexpected error while loading logo: " + e.getMessage());
            setDefaultLogo();
        }
    }

    private void setDefaultLogo() {
        // Set default logo in navigation header
        if (binding.navHead != null && binding.navHead.navHeaderLogo != null) {
            binding.navHead.navHeaderLogo.setImageResource(R.drawable.logo_sm);
        }

        // Set default logo in toolbar
        if (binding.toolbar != null) {
            binding.toolbar.setLogo(R.drawable.logo_sm);
        }

        // Set default logo in ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setLogo(R.drawable.logo_sm);
        }
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Session Expired")
                .setMessage("Your session has expired. Please login again.")
                .setPositiveButton("Login", (dialog, which) -> {
                    Intent loginIntent = new Intent(this, ActivityLogin.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void showLoading(boolean show) {
        // Show/hide loading indicator
        // You can implement this based on your UI requirements
        if (show) {
            // Show loading
        } else {
            // Hide loading
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when activity resumes
        if (isInternetConnected(this)) {
            fetchEmployeeDashboard();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Show exit confirmation dialog
            showExitDialog();
        }
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}