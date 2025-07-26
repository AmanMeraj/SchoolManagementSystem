package com.school.schoolmanagement.Students.Dashboard;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.navigation.NavigationView;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Login.ActivityLogin;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.Adapter.AdapterStudentExamSeekbar;
import com.school.schoolmanagement.Students.Adapter.AdapterStudentsFesPaid;
import com.school.schoolmanagement.Students.Adapter.AdapterStudentsTsetReport;
import com.school.schoolmanagement.Students.AdmissionConfirmation.ActivityStudentsAdmissionConfirmation;
import com.school.schoolmanagement.Students.Assingments.ActivityStudentsAssignments;
import com.school.schoolmanagement.Students.ExamResult.ActivityStudentsExamResult;
import com.school.schoolmanagement.Students.LastSubmittedFees.ActivityStudentsLastSubmittedFees;
import com.school.schoolmanagement.Students.Model.StudentDashboardApiResponse1;
import com.school.schoolmanagement.Students.Model.StudentDashboardApiResponse2;
import com.school.schoolmanagement.Students.Model.StudentDashboardApiResponse3;
import com.school.schoolmanagement.Students.Model.StudentExamReport;
import com.school.schoolmanagement.Students.Profile.ActivityStudentsProfile;
import com.school.schoolmanagement.Students.TestResult.ActivityStudentsTestResult;
import com.school.schoolmanagement.Students.TimeTable.ActivityStudentsTimetable;
import com.school.schoolmanagement.Students.UpdateLoginDetails.ActivityUpdateLoginCredentials;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityStudentsDashboardBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityStudentsDashboard extends Utility implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "StudentsDashboard";

    ActivityStudentsDashboardBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private ActivityResultLauncher<String[]> permissionLauncher;
    // Adapters
    AdapterStudentsTsetReport adapter;
    List<StudentDashboardApiResponse2.ClassTestReport> reportList;
    private AdapterStudentsFesPaid adapterStudentsFesPaid;
    private List<StudentDashboardApiResponse3.Datum> paymentList;
    private AdapterStudentExamSeekbar adapterStudentExamSeekbar;
    private List<StudentExamReport> seekbarItems;

    // Navigation components
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private Toolbar toolbar;

    // ViewModel
    private ViewModel viewModel;

    // Shared Preferences
    private SharedPreferences sharedPreferences;
    private String authToken;
    private final String[] REQUIRED_PERMISSIONS = {
            // Camera permission for profile pictures and assignments
            Manifest.permission.CAMERA,

            // Media permissions for Android 13+ (Photos and Videos in Settings)
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,

            // Storage permissions for older Android versions (if applicable)
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStudentsDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();
        setupToolbarAndDrawer();
        checkAndRequestPermissions();
        setupRecyclerViews();
        setupClickListeners();
        loadStudentDashboard();
    }

    private void initializeComponents() {
        authToken = "Bearer "+pref.getPrefString(this,pref.user_token);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        // Initialize lists
        reportList = new ArrayList<>();
        paymentList = new ArrayList<>();
        seekbarItems = new ArrayList<>();
    }

    private void setupToolbarAndDrawer() {
        // Initialize toolbar
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize DrawerLayout
        drawerLayout = binding.drawerLayout;

        // Setup ActionBarDrawerToggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // Initialize NavigationView
        navigationView = binding.navigationView;
        navigationView.setNavigationItemSelectedListener(this);

        // Set the default selected item
        navigationView.setCheckedItem(R.id.nav_student_dashboard);
    }

    private void setupRecyclerViews() {
        // Setup Test Report RecyclerView
        adapter = new AdapterStudentsTsetReport(this, reportList);
        binding.rcClassTest.setAdapter(adapter);

        // Setup Fees RecyclerView
        setupFeesRecyclerView();

        // Setup Exam Progress RecyclerView
        adapterStudentExamSeekbar = new AdapterStudentExamSeekbar(this, seekbarItems);
        binding.rcExamProgress.setAdapter(adapterStudentExamSeekbar);
    }

    private void setupClickListeners() {
        // Profile click listener
        binding.profile.setOnClickListener(v -> openAccountSettings());

        // Chat button click listener
        binding.btnChat.setOnClickListener(v -> {
            // Handle chat functionality
            Toast.makeText(this, "Chat feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Notification button click listener
        binding.btnNotification.setOnClickListener(v -> {
            // Handle notification functionality
            Toast.makeText(this, "Notifications feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // View profile click listener
        binding.viewProfileRel.setOnClickListener(v -> {
          Intent intent= new Intent(ActivityStudentsDashboard.this, ActivityStudentsProfile.class);
          startActivity(intent);
        });

        // School selection card click listener
        binding.cardSelectSchool.setOnClickListener(v -> {
            // Handle school selection functionality
            Toast.makeText(this, "School selection feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadStudentDashboard() {
        if (authToken.isEmpty()) {
            Toast.makeText(this, "Authentication token not found. Please login again.", Toast.LENGTH_LONG).show();
            logout();
            return;
        }

        // Show loading state
        showLoading(true);

        // Load all three dashboard APIs
        loadStudentDashboard1();
        loadStudentDashboard2();
        loadStudentDashboard3();
    }

    private void loadStudentDashboard1() {
        // Observe the dashboard data
        viewModel.getStudentDashboard(authToken).observe(this, apiResponse -> {
            if (apiResponse != null) {
                if (apiResponse.isSuccess && apiResponse.data != null) {
                    // Handle successful response
                    handleDashboard1Data(apiResponse.data);
                    Log.d(TAG, "Dashboard1 data loaded successfully");
                } else {
                    // Handle error response
                    String errorMessage = apiResponse.message;
                    if (errorMessage == null || errorMessage.isEmpty()) {
                        errorMessage = "Failed to load dashboard1 data";
                    }

                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Dashboard1 API Error: " + errorMessage + ", Code: " + apiResponse.code);

                    // If unauthorized, redirect to login
                    if (apiResponse.code == 401) {
                        logout();
                    }
                }
            } else {
                Toast.makeText(this, "Network error. Please check your connection.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Dashboard1 API Response is null");
            }

            // Check if all APIs are loaded (implement counter if needed)
            showLoading(false);
        });
    }

    private void loadStudentDashboard2() {
        // Observe the dashboard2 data
        viewModel.getStudentDashboard2(authToken).observe(this, apiResponse -> {
            if (apiResponse != null) {
                if (apiResponse.isSuccess && apiResponse.data != null) {
                    // Handle successful response
                    handleDashboard2Data(apiResponse.data);
                    Log.d(TAG, "Dashboard2 data loaded successfully");
                } else {
                    // Handle error response
                    String errorMessage = apiResponse.message;
                    if (errorMessage == null || errorMessage.isEmpty()) {
                        errorMessage = "Failed to load dashboard2 data";
                    }

                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Dashboard2 API Error: " + errorMessage + ", Code: " + apiResponse.code);

                    // If unauthorized, redirect to login
                    if (apiResponse.code == 401) {
                        logout();
                    }
                }
            } else {
                Toast.makeText(this, "Network error loading dashboard2. Please check your connection.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Dashboard2 API Response is null");
            }
        });
    }

    private void loadStudentDashboard3() {
        // Show loading indicator if you have one
        // progressBar.setVisibility(View.VISIBLE);

        // Observe the dashboard3 data
        viewModel.getStudentDashboard3(authToken).observe(this, apiResponse -> {
            // Hide loading indicator
            // progressBar.setVisibility(View.GONE);

            if (apiResponse != null) {
                if (apiResponse.isSuccess && apiResponse.data != null) {
                    // Handle successful response
                    handleDashboard3Data(apiResponse.data);
                    Log.d(TAG, "Dashboard3 data loaded successfully");
                } else {
                    // Handle error response
                    String errorMessage = apiResponse.message;
                    if (errorMessage == null || errorMessage.isEmpty()) {
                        errorMessage = "Failed to load dashboard3 data";
                    }

                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Dashboard3 API Error: " + errorMessage + ", Code: " + apiResponse.code);

                    // If unauthorized, redirect to login
                    if (apiResponse.code == 401) {
                        logout();
                    }
                }
            } else {
                Toast.makeText(this, "Network error loading dashboard3. Please check your connection.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Dashboard3 API Response is null");
            }
        });
    }

    private void handleDashboard3Data(Object dashboard3Data) {
        try {
            // Cast the data to your model class
            StudentDashboardApiResponse3 data = (StudentDashboardApiResponse3) dashboard3Data;

            // Update fees payment data
            if (data.getData() != null && !data.getData().isEmpty()) {
                // Clear existing data
                paymentList.clear();

                // Add new data
                paymentList.addAll(data.getData());

                // Notify adapter of data changes
                if (adapterStudentsFesPaid != null) {
                    adapterStudentsFesPaid.notifyDataSetChanged();
                }

                // Show/hide empty state views
                if (paymentList.isEmpty()) {
                    // Show empty state
                    binding.rcFees.setVisibility(View.GONE);
                    // emptyStateView.setVisibility(View.VISIBLE);
                } else {
                    // Show data
                    binding.rcFees.setVisibility(View.VISIBLE);
                    // emptyStateView.setVisibility(View.GONE);
                }

                Log.d(TAG, "Fees data updated - Total items: " + paymentList.size());
            } else {
                // Handle empty data
                paymentList.clear();
                if (adapterStudentsFesPaid != null) {
                    adapterStudentsFesPaid.notifyDataSetChanged();
                }

                binding.rcFees.setVisibility(View.GONE);
                // emptyStateView.setVisibility(View.VISIBLE);

                Log.d(TAG, "No fees data available");
            }

            Log.d(TAG, "Dashboard3 data parsed successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error parsing dashboard3 data: " + e.getMessage(), e);
            Toast.makeText(this, "Error parsing dashboard3 data", Toast.LENGTH_SHORT).show();

            // Handle error state
            paymentList.clear();
            if (adapterStudentsFesPaid != null) {
                adapterStudentsFesPaid.notifyDataSetChanged();
            }
        }
    }

    // Complete Activity Setup Method (call this in onCreate or onResume)
    private void setupFeesRecyclerView() {
        try {
            // Initialize the payment list if not already done
            if (paymentList == null) {
                paymentList = new ArrayList<>();
            }

            // Initialize the adapter
            adapterStudentsFesPaid = new AdapterStudentsFesPaid(this, paymentList);

            // Setup RecyclerView
            if (binding.rcFees != null) {
                binding.rcFees.setLayoutManager(new LinearLayoutManager(this));
                binding.rcFees.setAdapter(adapterStudentsFesPaid);

                binding.rcFees.setNestedScrollingEnabled(false);
            }

            Log.d(TAG, "Fees RecyclerView setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Error setting up fees RecyclerView: " + e.getMessage(), e);
            Toast.makeText(this, "Error setting up fees display", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDashboard1Data(Object dashboardData) {
        try {
            StudentDashboardApiResponse1 data = (StudentDashboardApiResponse1) dashboardData;

            binding.tvWelcome.setText("Welcome! " + data.data.student.getName());

            // ===== Update NavigationView Header =====
            View headerView = binding.navigationView.getHeaderView(0);
            ImageView navLogo = headerView.findViewById(R.id.nav_header_logo);
            TextView navName = headerView.findViewById(R.id.nav_header_admin_name);
            TextView navEmail = headerView.findViewById(R.id.nav_header_admin_email);
            TextView navStatus = headerView.findViewById(R.id.nav_header_status);

            // Set student name and email
            navName.setText(data.data.student.getName());
            navEmail.setText(data.data.student.getClassName() != null ? data.data.student.getClassName() : "No Class Found");

            // Set status
            navStatus.setText("Active");
            navStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light));

            // Set logo in header
            if (data.data.school.logo != null && !data.data.school.logo.isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(data.data.school.logo, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    if (decodedByte != null) {
                        navLogo.setImageBitmap(decodedByte);
                    } else {
                        navLogo.setImageResource(R.drawable.logo_sm);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Header logo decode error: " + e.getMessage());
                    navLogo.setImageResource(R.drawable.logo_sm);
                }
            } else {
                navLogo.setImageResource(R.drawable.logo_sm);
            }

            // ===== Update Main Logo =====
            if (data.data.school.logo != null && !data.data.school.logo.isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(data.data.school.logo, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    if (decodedByte != null) {
                        binding.imageLogo.setImageBitmap(decodedByte);
                    } else {
                        binding.imageLogo.setImageResource(R.drawable.logo_sm);
                    }
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Error decoding base64 logo: " + e.getMessage());
                    binding.imageLogo.setImageResource(R.drawable.logo_sm);
                }
            } else {
                binding.imageLogo.setImageResource(R.drawable.logo_sm);
            }

            // School Name and Slogan
            binding.tvSchoolName.setText(data.data.school.getName() != null ? data.data.school.getName() : "No School Name");
            binding.tvSchoolSlogan.setText(data.data.school.getSlogan() != null ? data.data.school.getSlogan() : "No School Slogan");

            // Student Profile Image
            if (data.data.student.profileImage != null && !data.data.student.profileImage.isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(data.data.student.profileImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    if (decodedByte != null) {
                        binding.profileImg.setImageBitmap(decodedByte);
                    } else {
                        binding.profileImg.setImageResource(R.drawable.avatar2);
                    }
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Error decoding base64 profile image: " + e.getMessage());
                    binding.profileImg.setImageResource(R.drawable.avatar2);
                }
            } else {
                binding.profileImg.setImageResource(R.drawable.avatar2);
            }

            // Set student info
            binding.tvStudentsName.setText(data.data.student.getName() != null ? data.data.student.getName() : "N/A");
            binding.tvFathersName.setText(data.data.student.getFatherName() != null ? "Father: " + data.data.student.getFatherName() : "Father: N/A");
            binding.tvClass.setText(data.data.student.getClassName() != null ? data.data.student.getClassName() : "N/A");
            binding.tvStudentId.setText("ID: " + data.data.student.getId());

            // Attendance
            if (data.data.attendance != null) {
                updateAttendanceData(data.data.getAttendance());
            }

            // Examinations
            if (data.data.examinations != null && !data.data.examinations.isEmpty()) {
                updateExaminationData(data.data.getExaminations());
            }

            Log.d(TAG, "Dashboard1 data parsed successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error parsing dashboard1 data: " + e.getMessage(), e);
            Toast.makeText(this, "Error parsing dashboard1 data", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateExaminationData(ArrayList<StudentDashboardApiResponse1.Examination> examinations) {
        try {
            // Convert examination data to StudentExamReport objects
            List<StudentExamReport> examReports = new ArrayList<>();

            int totalObtainedMarks = 0;
            int totalMaxMarks = 0;

            for (StudentDashboardApiResponse1.Examination exam : examinations) {
                // Calculate percentage for each exam
                int percentage = 0;
                if (exam.getTotalMarks() > 0) {
                    percentage = (int) Math.round((double) exam.getObtainedMarks() / exam.getTotalMarks() * 100);
                }

                // Create StudentExamReport object
                StudentExamReport examReport = new StudentExamReport();
                examReport.setTitle(exam.getExamName());
                examReport.setProgress(percentage);
                examReports.add(examReport);

                // Add to totals for overall calculation
                totalObtainedMarks += exam.getObtainedMarks();
                totalMaxMarks += exam.getTotalMarks();
            }

            // Set up RecyclerView for exam progress using your existing adapter
            if (binding.rcExamProgress != null) {
                AdapterStudentExamSeekbar adapter = new AdapterStudentExamSeekbar(this, examReports);
                binding.rcExamProgress.setAdapter(adapter);
                binding.rcExamProgress.setLayoutManager(new LinearLayoutManager(this));
            }

            // Update performance card
            if (binding.tvObtainedCount != null) {
                binding.tvObtainedCount.setText(String.valueOf(totalObtainedMarks));
            }

            if (binding.tvTotalCount != null) {
                binding.tvTotalCount.setText(String.valueOf(totalMaxMarks));
            }

            // Calculate and set overall percentage
            int overallPercentage = 0;
            if (totalMaxMarks > 0) {
                overallPercentage = (int) Math.round((double) totalObtainedMarks / totalMaxMarks * 100);
            }

            if (binding.tvProgressPercentage != null) {
                binding.tvProgressPercentage.setText(overallPercentage + "%");
            }

            // Set progress bar value and make it non-editable
            if (binding.progressExams != null) {
                binding.progressExams.setProgress(overallPercentage);
                binding.progressExams.setEnabled(false); // Make progress bar non-editable
                binding.progressExams.setClickable(false); // Disable clicking
                binding.progressExams.setFocusable(false); // Disable focus
            }

            Log.d(TAG, "Examination data updated successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error updating examination data: " + e.getMessage(), e);
        }
    }


    private void handleDashboard2Data(Object dashboard2Data) {
        try {
            // Cast to your actual response type
            StudentDashboardApiResponse2 data = (StudentDashboardApiResponse2) dashboard2Data;

            // Update test reports
            if (data.data.classTestReport != null && !data.data.getClassTestReport().isEmpty()) {
                reportList.clear();
                reportList.addAll(data.data.getClassTestReport());
                adapter.notifyDataSetChanged();
            } else {
                // Handle empty data case
                Toast.makeText(this, "No test reports available", Toast.LENGTH_SHORT).show();
            }

            Log.d(TAG, "Dashboard2 data parsed successfully");

        } catch (ClassCastException e) {
            Log.e(TAG, "Error casting dashboard2 data: " + e.getMessage(), e);
            Toast.makeText(this, "Error parsing dashboard2 data", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error parsing dashboard2 data: " + e.getMessage(), e);
            Toast.makeText(this, "Error parsing dashboard2 data", Toast.LENGTH_SHORT).show();
        }
    }



    private void updateAttendanceData(Object attendanceData) {
        // TODO: Update attendance UI based on your data structure
        // Example:
        StudentDashboardApiResponse1.Attendance attendance = (StudentDashboardApiResponse1.Attendance) attendanceData;

        // Set count values
        binding.tvPresentCount.setText(String.valueOf(attendance.getYearly().getPresentDays()));
        binding.tvAbsentCount.setText(String.valueOf(attendance.getYearly().getAbsentDays()));
        binding.tvLeavesCount.setText(String.valueOf(attendance.getYearly().getLeaveDays()));
        binding.tvPresentMonthCount.setText(String.valueOf(attendance.getMonthly().getPresentDays()));
        binding.tvAbsentMonthCount.setText(String.valueOf(attendance.getMonthly().getAbsentDays()));
        binding.tvLeavesMonthCount.setText(String.valueOf(attendance.getMonthly().getLeaveDays()));
        try {
            // Set current year and month using Calendar
            Calendar calendar = Calendar.getInstance();

            // Set current year
            int currentYear = calendar.get(Calendar.YEAR);
            binding.yearTv.setText(String.valueOf(currentYear));
            binding.yearTv.setEnabled(false);
            binding.yearTv.setClickable(false);
            binding.yearTv.setFocusable(false);
            binding.yearTv.setFocusableInTouchMode(false);

            // Set current month name
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
            String currentMonth = monthFormat.format(calendar.getTime());
            binding.monthTv.setText(currentMonth);
            binding.monthTv.setEnabled(false);
            binding.monthTv.setClickable(false);
            binding.monthTv.setFocusable(false);
            binding.monthTv.setFocusableInTouchMode(false);

        } catch (Exception e) {
            Log.e(TAG, "Error updating attendance data: " + e.getMessage(), e);
        }

        // Calculate yearly percentages based on working days
        int workingDays = attendance.getYearly().getWorkingDays();
        int presentDays = attendance.getYearly().getPresentDays();
        int absentDays = attendance.getYearly().getAbsentDays();
        int leaveDays = attendance.getYearly().getLeaveDays();

        // Calculate percentages (avoid division by zero)
        int presentPercentage = workingDays > 0 ? (presentDays * 100) / workingDays : 0;
        int absentPercentage = workingDays > 0 ? (absentDays * 100) / workingDays : 0;
        int leavesPercentage = workingDays > 0 ? (leaveDays * 100) / workingDays : 0;

        // Update progress bars (assuming progress bars take values 0-100)
        binding.progressPresent.setProgress(presentPercentage);
        binding.progressAbsent.setProgress(absentPercentage);
        binding.progressLeaves.setProgress(leavesPercentage);

        // Set percentage text views
        binding.tvPercentage1.setText(String.valueOf(attendance.currentYearPercent));
        binding.tvPercentage2.setText(String.valueOf(attendance.currentMonthPercent));
        binding.tvPresentPercentage.setText(presentPercentage + "%");
        binding.tvAbsentPercentage.setText(absentPercentage + "%");
        binding.tvLeavesPercentage.setText(leavesPercentage + "%");

    }

    private void showLoading(boolean show) {
        // TODO: Implement loading indicator
        // You can add a ProgressBar to your layout and show/hide it here
        if (show) {
            Log.d(TAG, "Loading dashboard data...");
            // binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Loading completed");
            // binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Create a map of item IDs to their corresponding methods
        Map<Integer, Runnable> itemActions = new HashMap<>();
        itemActions.put(R.id.nav_student_dashboard, this::openDashboard);
        itemActions.put(R.id.nav_student_admission, this::openAdmissionLetter);
        itemActions.put(R.id.nav_student_paid_fee, this::openPaidFeesReceipt);
        itemActions.put(R.id.nav_student_timetable, this::openTimetable);
        itemActions.put(R.id.nav_student_report_card, this::openReportCard);
        itemActions.put(R.id.nav_student_test_results, this::openTestResults);
        itemActions.put(R.id.nav_student_exam_results, this::openExamResults);
        itemActions.put(R.id.nav_student_home_assignments, this::openHomeAssignments);
        itemActions.put(R.id.nav_student_account_settings, this::openAccountSettings);
        itemActions.put(R.id.nav_student_logout, this::handleLogout);

        // Run the corresponding method for the selected item
        if (itemActions.containsKey(item.getItemId())) {
            itemActions.get(item.getItemId()).run();
        }

        // Close the drawer after item is selected
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleLogout() {
        // Show confirmation dialog before logout
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void performLogout() {
        try {

            // Navigate to Login Activity
            Intent loginIntent = new Intent(this, ActivityLogin.class);

            // Clear the entire activity stack and start fresh
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pref.clearAll(this);

            startActivity(loginIntent);

            // Finish current activity
            finish();

            // Optional: Show a toast message
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("Logout", "Error during logout", e);
            Toast.makeText(this, "Error during logout", Toast.LENGTH_SHORT).show();
        }
    }

    // Navigation methods
    private void openDashboard() {
        // Already on dashboard, just refresh
        loadStudentDashboard();
    }

    private void openAdmissionLetter() {
        Intent admissionLetter = new Intent(this, ActivityStudentsAdmissionConfirmation.class);
        startActivity(admissionLetter);
    }

    private void openPaidFeesReceipt() {
        Intent lastFees = new Intent(this, ActivityStudentsLastSubmittedFees.class);
        startActivity(lastFees);
    }

    private void openTimetable() {
        Intent timeTable = new Intent(this, ActivityStudentsTimetable.class);
        startActivity(timeTable);
    }

    private void openReportCard() {
        // TODO: Implement report card functionality
        Toast.makeText(this, "Report Card feature coming soon", Toast.LENGTH_SHORT).show();
    }

    private void openTestResults() {
        Intent testResult = new Intent(this, ActivityStudentsTestResult.class);
        startActivity(testResult);
    }

    private void openExamResults() {
        Intent examResult = new Intent(this, ActivityStudentsExamResult.class);
        startActivity(examResult);
    }

    private void openHomeAssignments() {
        Intent studentAssignment = new Intent(this, ActivityStudentsAssignments.class);
        startActivity(studentAssignment);
    }

    private void openAccountSettings() {
        Intent loginCredential = new Intent(this, ActivityUpdateLoginCredentials.class);
        startActivity(loginCredential);
    }

    private void logout() {
        // Navigate to login screen
        Intent logout = new Intent(this, ActivityLogin.class);
        logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pref.clearAll(this);
        startActivity(logout);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void initializePermissionLauncher() {
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (String permission : REQUIRED_PERMISSIONS) {
                        if (isPermissionValidForCurrentVersion(permission)) {
                            if (result.get(permission) != null && !result.get(permission)) {
                                deniedPermissions.add(permission);
                            }
                        }
                    }

                    if (deniedPermissions.isEmpty()) {
                        // All permissions granted
                        onAllPermissionsGranted();
                    } else {
                        // Some permissions denied
                        handleDeniedPermissions(deniedPermissions);
                    }
                }
        );
    }

    /**
     * Check if all required permissions are granted
     * @return true if all permissions are granted, false otherwise
     */
    private boolean areAllPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            // Skip permissions that don't exist on current Android version
            if (!isPermissionValidForCurrentVersion(permission)) {
                continue;
            }

            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if permission is valid for current Android version
     * @param permission The permission to check
     * @return true if permission is valid for current version
     */
    private boolean isPermissionValidForCurrentVersion(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            // For Android 13+, only request media permissions and camera
            // Storage permissions are handled automatically by the system
            return permission.equals(Manifest.permission.READ_MEDIA_IMAGES) ||
                    permission.equals(Manifest.permission.READ_MEDIA_VIDEO) ||
                    permission.equals(Manifest.permission.CAMERA);
        } else {
            // For older versions, skip new media permissions, use storage permissions
            if (permission.equals(Manifest.permission.READ_MEDIA_IMAGES) ||
                    permission.equals(Manifest.permission.READ_MEDIA_VIDEO)) {
                return false;
            }
            // Include camera and storage permissions for older versions
            return permission.equals(Manifest.permission.CAMERA) ||
                    permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    /**
     * Get list of permissions that are not granted
     * @return List of denied permissions
     */
    private List<String> getDeniedPermissions() {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : REQUIRED_PERMISSIONS) {
            if (!isPermissionValidForCurrentVersion(permission)) {
                continue;
            }

            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }

    /**
     * Request all required permissions
     */
    private void requestAllPermissions() {
        List<String> deniedPermissions = getDeniedPermissions();

        if (deniedPermissions.isEmpty()) {
            onAllPermissionsGranted();
            return;
        }

        // Show explanation dialog first
        showPermissionExplanationDialog(deniedPermissions);
    }

    /**
     * Show explanation dialog before requesting permissions
     * @param deniedPermissions List of permissions that need to be requested
     */
    private void showPermissionExplanationDialog(List<String> deniedPermissions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions Required");

        // Create a more user-friendly message based on what actually appears in Settings
        String message = "This student app needs access to:\n\n";

        boolean needsCamera = deniedPermissions.contains(Manifest.permission.CAMERA);
        boolean needsMedia = deniedPermissions.contains(Manifest.permission.READ_MEDIA_IMAGES) ||
                deniedPermissions.contains(Manifest.permission.READ_MEDIA_VIDEO) ||
                deniedPermissions.contains(Manifest.permission.READ_EXTERNAL_STORAGE);

        if (needsCamera) {
            message += "📷 Camera - To take photos for profile pictures and assignments\n\n";
        }

        if (needsMedia) {
            message += "📁 Photos and Videos - To access study materials, images, and educational content\n\n";
        }

        message += "These permissions help you get the full student experience!";

        builder.setMessage(message);

        builder.setPositiveButton("Grant Permissions", (dialog, which) -> {
            dialog.dismiss();
            requestPermissions(deniedPermissions);
        });

        builder.setNegativeButton("Skip", (dialog, which) -> {
            dialog.dismiss();
            Toast.makeText(this, "You can enable permissions later in Settings", Toast.LENGTH_LONG).show();
            onAllPermissionsGranted(); // Load dashboard anyway
        });

        builder.setCancelable(true);
        builder.show();
    }

    /**
     * Get human-readable explanation for permissions
     * @param permissions List of permissions
     * @return Formatted explanation string
     */
    private String getPermissionExplanation(List<String> permissions) {
        StringBuilder explanation = new StringBuilder();

        for (String permission : permissions) {
            switch (permission) {
                case Manifest.permission.READ_EXTERNAL_STORAGE:
                    explanation.append("• Files and Media: To access and save study materials and documents\n");
                    break;
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    explanation.append("• Files and Media: To download and save PDFs and assignments\n");
                    break;
                case Manifest.permission.READ_MEDIA_IMAGES:
                    explanation.append("• Photos and Videos: To access images for assignments and profile pictures\n");
                    break;
                case Manifest.permission.READ_MEDIA_VIDEO:
                    explanation.append("• Photos and Videos: To access educational videos and multimedia content\n");
                    break;
                case Manifest.permission.CAMERA:
                    explanation.append("• Camera: To take photos for profile pictures and assignment submissions\n");
                    break;
            }
        }

        return explanation.toString();
    }

    /**
     * Request permissions using the modern approach
     * @param deniedPermissions List of permissions to request
     */
    private void requestPermissions(List<String> deniedPermissions) {
        String[] permissionsArray = deniedPermissions.toArray(new String[0]);
        permissionLauncher.launch(permissionsArray);
    }

    /**
     * Handle the case when some permissions are denied
     * @param deniedPermissions List of denied permissions
     */
    private void handleDeniedPermissions(List<String> deniedPermissions) {
        List<String> permanentlyDenied = new ArrayList<>();
        List<String> canRequestAgain = new ArrayList<>();

        for (String permission : deniedPermissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                canRequestAgain.add(permission);
            } else {
                permanentlyDenied.add(permission);
            }
        }

        if (!permanentlyDenied.isEmpty()) {
            showPermanentlyDeniedDialog(permanentlyDenied);
        } else if (!canRequestAgain.isEmpty()) {
            // Ask again with more explanation
            showPermissionRationaleDialog(canRequestAgain);
        } else {
            // All permissions handled, continue with app
            onAllPermissionsGranted();
        }
    }

    /**
     * Show dialog for permanently denied permissions
     * @param permanentlyDenied List of permanently denied permissions
     */
    private void showPermanentlyDeniedDialog(List<String> permanentlyDenied) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enable Permissions");

        String message = "To use all features, please enable these permissions in Settings:\n\n";

        boolean needsCamera = permanentlyDenied.contains(Manifest.permission.CAMERA);
        boolean needsMedia = permanentlyDenied.contains(Manifest.permission.READ_MEDIA_IMAGES) ||
                permanentlyDenied.contains(Manifest.permission.READ_MEDIA_VIDEO) ||
                permanentlyDenied.contains(Manifest.permission.READ_EXTERNAL_STORAGE);

        if (needsCamera) {
            message += "📷 Camera - For profile photos and assignments\n";
        }

        if (needsMedia) {
            message += "📁 Photos and Videos - For study materials access\n";
        }

        message += "\n📱 Go to: Settings > Apps > " + getString(R.string.app_name) + " > Permissions";

        builder.setMessage(message);

        builder.setPositiveButton("Open Settings", (dialog, which) -> {
            dialog.dismiss();
            openAppSettings();
        });

        builder.setNegativeButton("Continue", (dialog, which) -> {
            dialog.dismiss();
            onAllPermissionsGranted(); // Continue with limited functionality
        });

        builder.setCancelable(true);
        builder.show();
    }

    /**
     * Get limitation message for denied permissions
     * @param deniedPermissions List of denied permissions
     * @return Message explaining limitations
     */
    private String getPermissionLimitationMessage(List<String> deniedPermissions) {
        StringBuilder message = new StringBuilder();

        for (String permission : deniedPermissions) {
            switch (permission) {
                case Manifest.permission.READ_EXTERNAL_STORAGE:
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    message.append("• Cannot download or save PDF files and documents\n");
                    break;
                case Manifest.permission.READ_MEDIA_IMAGES:
                    message.append("• Cannot access photos for assignments or profile pictures\n");
                    break;
                case Manifest.permission.READ_MEDIA_VIDEO:
                    message.append("• Cannot access educational videos and multimedia content\n");
                    break;
                case Manifest.permission.CAMERA:
                    message.append("• Cannot take photos for profile updates or assignment submissions\n");
                    break;
            }
        }

        return message.toString();
    }

    /**
     * Show rationale dialog for permissions that can be requested again
     * @param canRequestAgain List of permissions that can be requested again
     */
    private void showPermissionRationaleDialog(List<String> canRequestAgain) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions Needed");
        builder.setMessage("The student app needs these permissions for better functionality:\n\n" +
                getPermissionExplanation(canRequestAgain) +
                "\nWould you like to grant them now?");

        builder.setPositiveButton("Grant", (dialog, which) -> {
            dialog.dismiss();
            requestPermissions(canRequestAgain);
        });

        builder.setNegativeButton("Skip", (dialog, which) -> {
            dialog.dismiss();
            onAllPermissionsGranted(); // Continue without these permissions
        });

        builder.setCancelable(true);
        builder.show();
    }

    /**
     * Open app settings for manual permission granting
     */
    private void openAppSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(android.net.Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    /**
     * Called when all permissions are granted or app continues without them
     */
    private void onAllPermissionsGranted() {
        Log.d(TAG, "Permissions check completed. Loading dashboard...");
        // Continue with normal app initialization
        loadStudentDashboard();
    }

    /**
     * Check and request permissions - call this method from onCreate()
     */
    private void checkAndRequestPermissions() {
        // Initialize permission launcher first
        initializePermissionLauncher();

        // Check if all permissions are granted
        if (areAllPermissionsGranted()) {
            // All permissions granted, proceed normally
            onAllPermissionsGranted();
        } else {
            // Some permissions missing, request them
            requestAllPermissions();
        }
    }

    /**
     * Override onResume to handle returning from settings
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Only reload if we're not in initial setup
        if (viewModel != null) {
            loadStudentDashboard();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;  // Avoid memory leaks
    }
}