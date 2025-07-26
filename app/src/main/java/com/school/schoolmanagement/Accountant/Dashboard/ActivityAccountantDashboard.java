package com.school.schoolmanagement.Accountant.Dashboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.school.schoolmanagement.Accountant.FeeInvoice.ActivityAccountantCollectFees;
import com.school.schoolmanagement.Accountant.FeeInvoice.ActivityAccountantFeesRecord;
import com.school.schoolmanagement.Accountant.FeeInvoice.ActivityGenerateFeesInvoice;
import com.school.schoolmanagement.Accountant.IncomeOrExpense.ActivityAccountantAccountStatement;
import com.school.schoolmanagement.Accountant.IncomeOrExpense.ActivityAddIncomeOrExpense;
import com.school.schoolmanagement.Accountant.Profile.ActivityAccountantProfile;
import com.school.schoolmanagement.Admin.Accounts.Expense.ActivityAddExpense;
import com.school.schoolmanagement.Admin.Accounts.Income.ActivityAddIncome;
import com.school.schoolmanagement.Admin.Accounts.Statement.ActivityAccountsStatement;
import com.school.schoolmanagement.Admin.ActivityAdminDashboard;
import com.school.schoolmanagement.Admin.Fees.CollectFees.ActivityCollectFees;
import com.school.schoolmanagement.Admin.Fees.FeesRecord.ActivityFeesRecord;
import com.school.schoolmanagement.Admin.Fees.Report.ActivityFeesReport;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Login.ActivityLogin;
import com.school.schoolmanagement.Model.EmployeeDashboardResponse;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.UpdateLoginDetails.ActivityUpdateLoginCredentials;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityAccountantDashboardBinding;

import java.text.DecimalFormat;

public class ActivityAccountantDashboard extends Utility implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityAccountantDashboardBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ViewModel viewModel;
    private String auth;
    int employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAccountantDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize ViewModel and authentication token
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        auth = "Bearer " + pref.getPrefString(this, pref.user_token);

        setupToolbar();
        setupNavigation();
        setupClickListeners();
        fetchEmployeeDashboard();

        binding.viewProfileRel.setOnClickListener(v -> {
            Intent intent= new Intent(ActivityAccountantDashboard.this, ActivityAccountantProfile.class);
            intent.putExtra("employeeId",employeeId);
            startActivity(intent);
        });

    }

    private void setupToolbar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        drawerLayout = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.black));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupNavigation() {
        navigationView = binding.navigationView;
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupClickListeners() {

        // Chat button click
        binding.btnChat.setOnClickListener(v -> {
            Toast.makeText(this, "Chat feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Notification button click
        binding.btnNotification.setOnClickListener(v -> {
            Toast.makeText(this, "No new notifications", Toast.LENGTH_SHORT).show();
        });

        // School selection card click
        binding.cardSelectSchool.setOnClickListener(v -> {
            Toast.makeText(this, "School selection dialog will appear here", Toast.LENGTH_SHORT).show();
        });

        // View profile text click
        binding.tvViewProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ActivityAccountantProfile.class));
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        int id = item.getItemId();

        if (id == R.id.nav_accountant_dashboard) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_accountant_add_income) {
            intent = new Intent(this, ActivityAddIncome.class);
        } else if (id == R.id.nav_accountant_add_expense) {
            intent = new Intent(this, ActivityAddExpense.class);
        } else if (id == R.id.nav_accountant_profile) {
            Intent intentProfile= new Intent(ActivityAccountantDashboard.this, ActivityAccountantProfile.class);
            intentProfile.putExtra("employeeId",employeeId);
            startActivity(intentProfile);
        } else if (id == R.id.nav_accountant_collect_fees) {
            intent = new Intent(this, ActivityCollectFees.class);
        } else if (id == R.id.nav_accountant_fees_record) {
            intent = new Intent(this, ActivityFeesRecord.class);
        } else if (id == R.id.nav_accountant_fees_report) {
            intent = new Intent(this, ActivityFeesReport.class);
        } else if (id == R.id.nav_accountant_account_statement) {
            intent = new Intent(this, ActivityAccountsStatement.class);
        } else if (id == R.id.nav_accountant_generate_invoice) {
            intent = new Intent(this, ActivityGenerateFeesInvoice.class);
        } else if (id == R.id.nav_accountant_account_settings) {
           Intent intent1= new Intent(ActivityAccountantDashboard.this,ActivityUpdateLoginCredentials.class);
           intent1.putExtra("from","employee");
           startActivity(intent1);
        } else if (id == R.id.nav_accountant_logout) {
           Intent intent1= new Intent(ActivityAccountantDashboard.this,ActivityLogin.class);
           pref.clearAll(this);
           startActivity(intent1);
        }

        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Feature coming soon", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Exit Application")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void fetchEmployeeDashboard() {
        viewModel.getEmployeeDashboard(auth).observe(this, response -> {
            if (response != null && response.isSuccess && response.data != null) {
                EmployeeDashboardResponse.Data data = response.data.getData();

                // Set profile information
                binding.tvStudentsName.setText(data.getEmployee().getName());
                binding.tvRole.setText(data.getEmployee().getRole());
                binding.tvStudentId.setText("ID: " + data.getEmployee().getId());
                employeeId = data.getEmployee().getId();
                Log.d("TAG", "fetchEmployeeDashboard: " + employeeId);

                // Set school information
                binding.tvSchoolName.setText(data.getSchool().getName());
                binding.tvSchoolSlogan.setText(data.getSchool().getSlogan());

                // Set salary information
                binding.tvCurrentSalary.setText("₹" + data.getSalary().getLastSalary());
                binding.tvThisMonth.setText("₹" + data.getSalary().getMonthlySalary());
                updateAttendanceProgress(data.getAttendance());

                // Set profile image in main layout
                if (data.getEmployee().getProfileImage() != null && !data.getEmployee().getProfileImage().isEmpty()) {
                    try {
                        byte[] decodedBytes = Base64.decode(data.getEmployee().getProfileImage(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        binding.imgProfile.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Update Navigation Header
                updateNavigationHeader(data);

            } else if (response != null && response.code == 401) {
                showLoginDialog();
            } else {
                String errorMsg = response != null ? response.message : "Unknown error";
                Toast.makeText(this, "Failed to load dashboard: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateNavigationHeader(EmployeeDashboardResponse.Data data) {
        // Get the header view from navigation view
        View headerView = binding.navigationView.getHeaderView(0);

        if (headerView != null) {
            // Find header views
            ImageView navHeaderLogo = headerView.findViewById(R.id.nav_header_logo);
            TextView navHeaderAdminName = headerView.findViewById(R.id.nav_header_admin_name);
            TextView navHeaderAdminEmail = headerView.findViewById(R.id.nav_header_admin_email);
            TextView navHeaderStatus = headerView.findViewById(R.id.nav_header_status);

            // Set employee name
            if (navHeaderAdminName != null) {
                navHeaderAdminName.setText(data.getEmployee().getName());
            }

            // Set employee email (if available in your data model)
            if (navHeaderAdminEmail != null) {
                // Assuming you have email in employee data, otherwise use a default or employee ID
                String email = data.getEmployee().getRole(); // Add this if available
                if (email != null && !email.isEmpty()) {
                    navHeaderAdminEmail.setText(email);
                } else {
                    navHeaderAdminEmail.setText("ID: " + data.getEmployee().getId());
                }
            }

            // Set employee status
            if (navHeaderStatus != null) {
                // You can set this based on your business logic
                navHeaderStatus.setText("Active");
                navHeaderStatus.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            }

            // Set profile image in navigation header
            if (navHeaderLogo != null && data.getSchool().getLogo() != null &&
                    !data.getEmployee().getProfileImage().isEmpty()) {
                try {
                    byte[] decodedBytes = Base64.decode(data.getSchool().getLogo(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    navHeaderLogo.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    navHeaderLogo.setImageResource(R.drawable.logo_sm);
                }
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


    private void showLoginDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Session Expired")
                .setMessage("Please log in again to continue.")
                .setCancelable(false)
                .setPositiveButton("Login", (dialog, which) -> {
                    startActivity(new Intent(this, ActivityLogin.class));
                    finishAffinity();
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when activity resumes
        fetchEmployeeDashboard();
    }
}