package com.school.schoolmanagement.Teachers.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.school.schoolmanagement.Admin.Adapter.CustomNavigationAdapter;
import com.school.schoolmanagement.Admin.Model.NavItem;
import com.school.schoolmanagement.Login.ActivityLogin;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.UpdateLoginDetails.ActivityUpdateLoginCredentials;
import com.school.schoolmanagement.Teachers.AddChapter.ActivityAddNewChapter;
import com.school.schoolmanagement.Teachers.Attendance.ActivityStudentsAttendancereport;
import com.school.schoolmanagement.Teachers.Exams.ActivityAddUpdateStudentsMarks;
import com.school.schoolmanagement.Teachers.Attendance.ActivityAddUpdateAttendance;
import com.school.schoolmanagement.Teachers.Attendance.ActivityClassWiseAttendanceReport;
import com.school.schoolmanagement.Teachers.Attendance.ActivitySearchStudentsAttendanceReport;
import com.school.schoolmanagement.Teachers.Homework.ActivitySearchHomework;
import com.school.schoolmanagement.Teachers.QuestionBank.ActivityAddQuestionBank;
import com.school.schoolmanagement.Teachers.QuestionBank.ActivityCreateQuestionPaper;
import com.school.schoolmanagement.Teachers.Test.ActivityCreateNewTest;
import com.school.schoolmanagement.Teachers.Timetable.ActivityTeachersTimetable;
import com.school.schoolmanagement.databinding.ActivityTeachersDashboardBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ActivityTeachersDashboard extends AppCompatActivity {
ActivityTeachersDashboardBinding binding;
    private DrawerLayout drawerLayout;
    private ExpandableListView expandableListView;
    private CustomNavigationAdapter navigationAdapter;
    private ArrayList<NavItem> navItems;
    private HashMap<NavItem, List<String>> navSubItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityTeachersDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupToolbar();
        setupNavigation();
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
                "Result card",
                "Award List"
        );
        navSubItems.put(examsNavItem, examsSubItems);

        // Class Test with subitems
        NavItem classTestNavItem = new NavItem("Class Test", R.drawable.layout);
        navItems.add(classTestNavItem);
        List<String> classTestSubItems = Arrays.asList(
                "Create Test",
                "Enter Marks",
                "View Results"
        );
        navSubItems.put(classTestNavItem, classTestSubItems);

        // Reports with subitems
        NavItem reportsNavItem = new NavItem("Report", R.drawable.layout);
        navItems.add(reportsNavItem);
        List<String> reportsSubItems = Arrays.asList(
                "Attendance Report",
                "Report Card"
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
                Intent HomeworkIntent = new Intent(this, ActivitySearchHomework.class);
                startActivity(HomeworkIntent);
                break;
            case "Time Table":
                Intent timetableIntent = new Intent(this, ActivityTeachersTimetable.class);
                startActivity(timetableIntent);
                break;
            case "Accounts Settings":
                Intent accountSettingsIntent = new Intent(this, ActivityUpdateLoginCredentials.class);
                startActivity(accountSettingsIntent);
                break;
            case "Logout":
                Intent logoutIntent = new Intent(this, ActivityLogin.class);
                startActivity(logoutIntent);
                Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
                Toast.makeText(this, itemTitle + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
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
            case "Reports":
                navigateReports(itemTitle);
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
                Intent addChapterIntent = new Intent(this, ActivityAddNewChapter.class);
                startActivity(addChapterIntent);
                break;
            case "Question Bank":
                Intent viewQuestionsIntent = new Intent(this, ActivityAddQuestionBank.class);
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
                Intent examScheduleIntent = new Intent(this, ActivityAddUpdateStudentsMarks.class);
                startActivity(examScheduleIntent);
                break;
            default:
                Toast.makeText(this, "Exams: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void navigateClassTest(String subItem) {
        switch (subItem) {
            case "Create Test":
                Intent createTestIntent = new Intent(this, ActivityCreateNewTest.class);
                startActivity(createTestIntent);
                break;
            default:
                Toast.makeText(this, "Class Test: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void navigateReports(String subItem) {
        switch (subItem) {
            case "Attendance Report":
                Intent attendanceReportIntent = new Intent(this, ActivityStudentsAttendancereport.class);
                startActivity(attendanceReportIntent);
                break;
            case "Report Card":
                Intent ReportIntent = new Intent(this, ActivityStudentsAttendancereport.class);
                startActivity(ReportIntent);
                break;
            default:
                Toast.makeText(this, "Report: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}