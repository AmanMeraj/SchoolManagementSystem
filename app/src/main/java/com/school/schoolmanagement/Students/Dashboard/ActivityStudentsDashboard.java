package com.school.schoolmanagement.Students.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

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

import com.google.android.material.navigation.NavigationView;
import com.school.schoolmanagement.Admin.Students.AdmissionLetter.ActivityAdmissionLetter;
import com.school.schoolmanagement.Login.ActivityLogin;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.Adapter.AdapterStudentExamSeekbar;
import com.school.schoolmanagement.Students.Adapter.AdapterStudentsFesPaid;
import com.school.schoolmanagement.Students.Adapter.AdapterStudentsTsetReport;
import com.school.schoolmanagement.Students.AdmissionConfirmation.ActivityStudentsAdmissionConfirmation;
import com.school.schoolmanagement.Students.Assingments.ActivityStudentsAssingments;
import com.school.schoolmanagement.Students.ExamResult.ActivityStudentsExamResult;
import com.school.schoolmanagement.Students.LastSubmittedFees.ActivityStudentsLastSubmittedFees;
import com.school.schoolmanagement.Students.Model.StudentExamReport;
import com.school.schoolmanagement.Students.Model.StudentTestReport;
import com.school.schoolmanagement.Students.Model.StudentsFeesPaidModel;
import com.school.schoolmanagement.Students.TestResult.ActivityStudentsTestResult;
import com.school.schoolmanagement.Students.TimeTable.ActivityStudentsTimetable;
import com.school.schoolmanagement.Students.UpdateLoginDetails.ActivityUpdateLoginCredentials;
import com.school.schoolmanagement.databinding.ActivityStudentsDashboardBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityStudentsDashboard extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
ActivityStudentsDashboardBinding binding;

    AdapterStudentsTsetReport adapter;
    List<StudentTestReport> reportList;

    private AdapterStudentsFesPaid adapterStudentsFesPaid;
    private List<StudentsFeesPaidModel> paymentList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private AdapterStudentExamSeekbar adapterStudentExamSeekbar;
    private List<StudentExamReport> seekbarItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityStudentsDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reportList = new ArrayList<>();
        reportList.add(new StudentTestReport("Mathematics", R.drawable.book2, 75, "100", "75", "25"));
        reportList.add(new StudentTestReport("Science", R.drawable.book2, 60, "100", "60", "20"));
        // Add more items as needed

        adapter = new AdapterStudentsTsetReport(this, reportList);
        binding.rcClassTest.setAdapter(adapter);



        setupRecyclerView();
        loadData();

        paymentList = generateSampleData();

        // Initialize adapter
        adapterStudentsFesPaid = new AdapterStudentsFesPaid(this, paymentList);
        binding.rcFees.setAdapter(adapterStudentsFesPaid);
        setupToolbarAndDrawer();


    }

    private void setupToolbarAndDrawer() {
        // Initialize toolbar
        toolbar = binding.toolbar; // Make sure you have a toolbar in your layout
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        // Initialize DrawerLayout
        drawerLayout = binding.drawerLayout; // Make sure you have a drawer layout in your layout

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
        navigationView = binding.navigationView; // Make sure you have a navigation view in your layout
        navigationView.setNavigationItemSelectedListener(this); // Set this activity as the listener for navigation item selection

        // Set the default selected item (you can change this to the relevant item ID)
        navigationView.setCheckedItem(R.id.nav_student_dashboard);
    }

    // Override onNavigationItemSelected method to handle item clicks
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
        itemActions.put(R.id.nav_student_logout, this::logout);

        // Run the corresponding method for the selected item
        if (itemActions.containsKey(item.getItemId())) {
            itemActions.get(item.getItemId()).run();
        }

        // Close the drawer after item is selected
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Example methods to open different activities or fragments
    private void openDashboard() {
        // Add your logic to open the dashboard
    }

    private void openAdmissionLetter() {
        // Add your logic to open the admission letter
        Intent admissionLetter = new Intent(this, ActivityStudentsAdmissionConfirmation.class);
        startActivity(admissionLetter);
    }

    private void openPaidFeesReceipt() {
        // Add your logic to open the paid fees receipt
        Intent lastFees = new Intent(this, ActivityStudentsLastSubmittedFees.class);
        startActivity(lastFees);
    }

    private void openTimetable() {
        // Add your logic to open the timetable
        Intent timeTable = new Intent(this, ActivityStudentsTimetable.class);
        startActivity(timeTable);
    }

    private void openReportCard() {

    }

    private void openTestResults() {
        // Add your logic to open the test results
        Intent testResult = new Intent(this, ActivityStudentsTestResult.class);
        startActivity(testResult);
    }

    private void openExamResults() {
        // Add your logic to open the exam results
        // Add your logic to open the report card
        Intent examResult = new Intent(this, ActivityStudentsExamResult.class);
        startActivity(examResult);
    }

    private void openHomeAssignments() {
        // Add your logic to open the home assignments
        // Add your logic to open the report card
        Intent studentAssignment = new Intent(this, ActivityStudentsAssingments.class);
        startActivity(studentAssignment);
    }

    private void openAccountSettings() {
        // Add your logic to open the account settings
        Intent loginCredential = new Intent(this, ActivityUpdateLoginCredentials.class);
        startActivity(loginCredential);
    }

    private void logout() {
        // Add your logic to handle logout
        Intent logout = new Intent(this, ActivityLogin.class);
        startActivity(logout);

    }

    private void setupRecyclerView() {
        seekbarItems = new ArrayList<>();
        adapterStudentExamSeekbar = new AdapterStudentExamSeekbar(this, seekbarItems);

        binding.rcExamProgress.setAdapter(adapterStudentExamSeekbar);
    }

    private void loadData() {
        // Sample data without explicit colors (colors will rotate in adapter)
        seekbarItems.add(new StudentExamReport("Examination Name (280/400)", 80));
        seekbarItems.add(new StudentExamReport("Evaluation 1 (220/400)", 40));
        seekbarItems.add(new StudentExamReport("Mid-term Evaluation (320/400)", 65));
        seekbarItems.add(new StudentExamReport("Final Term (380/400)", 90));
        seekbarItems.add(new StudentExamReport("End Sem  (320/400)", 55));

        adapter.notifyDataSetChanged();
    }

    private List<StudentsFeesPaidModel> generateSampleData() {
        List<StudentsFeesPaidModel> data = new ArrayList<>();

        // Sample data
        data.add(new StudentsFeesPaidModel("January", "Awaiting", 2600.00, "May 25",
                25000.00, 0.00, 25000.00));
        data.add(new StudentsFeesPaidModel("February", "Paid", 2600.00, "June 15",
                25000.00, 2600.00, 22400.00));
        data.add(new StudentsFeesPaidModel("March", "Awaiting", 2600.00, "July 10",
                25000.00, 2600.00, 22400.00));
        data.add(new StudentsFeesPaidModel("April", "Paid", 2600.00, "August 5",
                25000.00, 5200.00, 19800.00));

        return data;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;  // Avoid memory leaks
    }
}