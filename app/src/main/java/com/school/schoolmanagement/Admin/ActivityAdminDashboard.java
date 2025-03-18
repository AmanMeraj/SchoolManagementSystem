package com.school.schoolmanagement.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.school.schoolmanagement.Admin.Adapter.AbsentStudentsHomeRowAdapter;
import com.school.schoolmanagement.Admin.Adapter.AbsentTeachers;
import com.school.schoolmanagement.Admin.Adapter.CustomNavigationAdapter;
import com.school.schoolmanagement.Admin.Adapter.NewAdmission;
import com.school.schoolmanagement.Admin.Classes.AllClasses.ActivityAllClass;
import com.school.schoolmanagement.Admin.Classes.NewClass.ActivityNewClass;
import com.school.schoolmanagement.Admin.Employee.AllEmployee.ActivityAllEmployee;
import com.school.schoolmanagement.Admin.Employee.AdmissionForm.ActivityAddEmployee;
import com.school.schoolmanagement.Admin.Employee.JobLetter.ActivityJobLetter;
import com.school.schoolmanagement.Admin.Employee.ManageLogin.ActivityStaffLogin;
import com.school.schoolmanagement.Admin.Employee.StaffIdCard.ActivityStaffIdCard;
import com.school.schoolmanagement.Admin.GeneralSettings.AccountFeesInvoice.ActivityAccountFeesIinvoice;
import com.school.schoolmanagement.Admin.GeneralSettings.AccountSettings.ActivityAccountSettings;
import com.school.schoolmanagement.Admin.GeneralSettings.InstituteProfile.ActivityInstituteProfile;
import com.school.schoolmanagement.Admin.GeneralSettings.RulesAndregulations.ActivityRulesAndRegulations;
import com.school.schoolmanagement.Admin.Model.NavItem;
import com.school.schoolmanagement.Admin.Model.NewAdmissonModel;
import com.school.schoolmanagement.Admin.Model.Student;
import com.school.schoolmanagement.Admin.Model.Teachers;
import com.school.schoolmanagement.Admin.Students.AllStudents.ActivityAllStudents;
import com.school.schoolmanagement.Admin.Students.AdmissionForm.ActivityAddStudents;
import com.school.schoolmanagement.Admin.Students.AdmissionLetter.ActivityAdmissionLetter;
import com.school.schoolmanagement.Admin.Students.BasicList.ActivityBasicList;
import com.school.schoolmanagement.Admin.Students.Managelogin.ActivityManageStudentsLogin;
import com.school.schoolmanagement.Admin.Students.PromoteStudents.ActivityPromoteStudents;
import com.school.schoolmanagement.Admin.Students.StudentsIdCard.ActivityStudentsIdCards;
import com.school.schoolmanagement.Admin.Subjects.ClassesWithSubjects.ActivityClassesWithSubjects;
import com.school.schoolmanagement.Admin.Subjects.CreateSubject.ActivityCreateSubjects;
import com.school.schoolmanagement.Login.ActivityLogin;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityAdminDashboardBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityAdminDashboard extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ExpandableListView expandableListView;
    private CustomNavigationAdapter navigationAdapter;
    private List<NavItem> navItems;
    private Map<NavItem, List<String>> navSubItems;
    ActivityAdminDashboardBinding binding;
    private AbsentStudentsHomeRowAdapter adapter;
    private AbsentTeachers adapter2;
    private NewAdmission adapter3;
    private List<Student> studentList;
    private List<Teachers> teachersList;
    private List<NewAdmissonModel> newAdmissonModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupNavigation();
        initializeCharts();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        drawerLayout = findViewById(R.id.drawer_layout);
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
        expandableListView = findViewById(R.id.expandable_nav_list);
        prepareNavigationData();

        navigationAdapter = new CustomNavigationAdapter(this, navItems, navSubItems);
        expandableListView.setAdapter(navigationAdapter);

        setupNavigationListeners();
        studentList = new ArrayList<>();
        studentList.add(new Student("Aman", "Android", R.drawable.admin_sm));
        studentList.add(new Student("Riya", "Java", R.drawable.student_sm));
        studentList.add(new Student("Rahul", "Python", R.drawable.employee_sm));
        adapter = new AbsentStudentsHomeRowAdapter(this, studentList);
        binding.rcAbsentStudents.setAdapter(adapter);

        teachersList = new ArrayList<>();
        teachersList.add(new Teachers("Aman", "Teacher", R.drawable.admin_sm));
        teachersList.add(new Teachers("Riya", "Java Teacher", R.drawable.admin_sm));
        teachersList.add(new Teachers("Rahul", "Python Teacher", R.drawable.admin_sm));

        adapter2 = new AbsentTeachers(this, teachersList);
        binding.rcAbsentTeachers.setAdapter(adapter2);
        newAdmissonModelList = new ArrayList<>();
        newAdmissonModelList.add(new NewAdmissonModel("Aman", "Teacher","1234", R.drawable.admin_sm));
        newAdmissonModelList.add(new NewAdmissonModel("Riya", "Java Teacher","1235", R.drawable.admin_sm));
        newAdmissonModelList.add(new NewAdmissonModel("Rahul", "Python Teacher","1236", R.drawable.admin_sm));

        adapter3 = new NewAdmission(this, newAdmissonModelList);
        binding.rcNewAdmissions.setAdapter(adapter3);
    }

    private void prepareNavigationData() {
        navItems = new ArrayList<>();
        navSubItems = new HashMap<>();

        // Add all menu items
        navItems.add(new NavItem("Dashboard", R.drawable.layout));
        NavItem generalSettings = new NavItem("General Settings", R.drawable.settings);
        navItems.add(generalSettings);
        List<String> generalSubItem=Arrays.asList(
                "Institute Profile","Fees Particulars","Accounts for Fees","Rules & Regulations","Marks Grading","Account Settings","Logout"
        );
        navSubItems.put(generalSettings,generalSubItem);


        NavItem classItems = new NavItem("Classes",R.drawable.classmates);
        navItems.add(classItems);
        List<String> classSubItem=Arrays.asList(
                "All Classes","New Classes"
        );
        navSubItems.put(classItems, classSubItem);


        NavItem subjectsItems = new NavItem("Subjects",R.drawable.book);
        navItems.add(subjectsItems);
        List<String> subjects=Arrays.asList(
                "Classes With Subjects","Assign Subjects"
        );
        navSubItems.put(subjectsItems, subjects);

        // Add Students with subitems
        NavItem studentsItem = new NavItem("Students", R.drawable.student_sm);
        navItems.add(studentsItem);
        List<String> studentSubItems = Arrays.asList(
                "All Students",
                "Add New",
                "Manage Families",
                "Active / Inactive",
                "Admission Letter",
                "Student ID Cards",
                "Print Basic List",
                "Manage Login",
                "Promote Students"
        );
        navSubItems.put(studentsItem, studentSubItems);

        // Add remaining items
        NavItem employeesNavItem = new NavItem("Employees", R.drawable.employees);
        navItems.add(employeesNavItem);

        List<String> employeeSubItems = Arrays.asList(
                "All Employees",
                "Add New",
                "Staff ID Cards",
                "Job Letter",
                "Manage Login"
        );

        navSubItems.put(employeesNavItem, employeeSubItems);

        NavItem accountsNavItem = new NavItem("Accounts", R.drawable.employees);
        navItems.add(accountsNavItem);

        List<String> accountsSubItems = Arrays.asList(
                "Chart Of Accounts",
                "Add Income",
                "Add Expense",
                "Account Statement"
        );

        navSubItems.put(accountsNavItem, accountsSubItems);

        navItems.add(new NavItem("Fees", R.drawable.receipt));
        navItems.add(new NavItem("Salary", R.drawable.money));
        navItems.add(new NavItem("Attendance", R.drawable.attendance));
        navItems.add(new NavItem("Timetable", R.drawable.time_table));
        navItems.add(new NavItem("Homework", R.drawable.homework));
        navItems.add(new NavItem("WhatsApp", R.drawable.whatsapp));
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
                // You're already on dashboard, just close drawer
                break;
            case "Fees":
                // Example of navigating to a specific activity
                // Intent feesIntent = new Intent(this, FeesActivity.class);
                // startActivity(feesIntent);
                Toast.makeText(this, "Navigating to " + itemTitle + " (Activity not created yet)", Toast.LENGTH_SHORT).show();
                break;
            case "Salary":
                // Intent salaryIntent = new Intent(this, SalaryActivity.class);
                // startActivity(salaryIntent);
                Toast.makeText(this, "Navigating to " + itemTitle + " (Activity not created yet)", Toast.LENGTH_SHORT).show();
                break;
            case "Attendance":
                // Intent attendanceIntent = new Intent(this, AttendanceActivity.class);
                // startActivity(attendanceIntent);
                Toast.makeText(this, "Navigating to " + itemTitle + " (Activity not created yet)", Toast.LENGTH_SHORT).show();
                break;
            case "Timetable":
                // Intent timetableIntent = new Intent(this, TimetableActivity.class);
                // startActivity(timetableIntent);
                Toast.makeText(this, "Navigating to " + itemTitle + " (Activity not created yet)", Toast.LENGTH_SHORT).show();
                break;
            case "Homework":
                // Intent homeworkIntent = new Intent(this, HomeworkActivity.class);
                // startActivity(homeworkIntent);
                Toast.makeText(this, "Navigating to " + itemTitle + " (Activity not created yet)", Toast.LENGTH_SHORT).show();
                break;
            case "WhatsApp":
                // Intent whatsappIntent = new Intent(this, WhatsAppActivity.class);
                // startActivity(whatsappIntent);
                Toast.makeText(this, "Navigating to " + itemTitle + " (Activity not created yet)", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, itemTitle + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void handleSubItemNavigation(String groupTitle, String itemTitle) {
        // Handle navigation for submenu items
        switch (groupTitle) {
            case "General Settings":
                navigateGeneralSettings(itemTitle);
                break;
            case "Classes":
                navigateClasses(itemTitle);
                break;
            case "Subjects":
                navigateSubjects(itemTitle);
                break;
            case "Students":
                navigateStudents(itemTitle);
                break;
            case "Employees":
                navigateEmployees(itemTitle);
                break;
            case "Accounts":
                navigateAccounts(itemTitle);
                break;
            default:
                Toast.makeText(this, groupTitle + ": " + itemTitle + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // Handle navigation for General Settings subitems
    private void navigateGeneralSettings(String subItem) {
        switch (subItem) {
            case "Institute Profile":
                Intent instituteProfileIntent = new Intent(this, ActivityInstituteProfile.class);
                startActivity(instituteProfileIntent);
                break;
            case "Fees Particulars":
                Toast.makeText(this, "General Settings: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
            case "Accounts for Fees":
                Intent AccountFeesIntent = new Intent(this, ActivityAccountFeesIinvoice.class);
                startActivity(AccountFeesIntent);
                break;
            case "Rules & Regulations":
                Intent RulesAndRegulations = new Intent(this, ActivityRulesAndRegulations.class);
                startActivity(RulesAndRegulations);
                break;
            case "Marks Grading":
                Toast.makeText(this, "General Settings: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
            case "Account Settings":
                Intent AccountSettings = new Intent(this, ActivityAccountSettings.class);
                startActivity(AccountSettings);
                break;
            case "Logout":
                Intent Logout = new Intent(this, ActivityLogin.class);
                startActivity(Logout);
                Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
                Toast.makeText(this, "General Settings: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // Handle navigation for Classes subitems
    private void navigateClasses(String subItem) {
        switch (subItem) {
            case "All Classes":
                Intent allClasses = new Intent(ActivityAdminDashboard.this, ActivityAllClass.class);
                startActivity(allClasses);
                break;
            case "New Classes":
                // Navigate to appropriate activity
                Intent intent= new Intent(ActivityAdminDashboard.this, ActivityNewClass.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(this, "Classes: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // Handle navigation for Subjects subitems
    private void navigateSubjects(String subItem) {
        switch (subItem) {
            case "Classes With Subjects":
                Intent intent= new Intent(ActivityAdminDashboard.this, ActivityClassesWithSubjects.class);
                startActivity(intent);
                break;
            case "Assign Subjects":
                // Navigate to appropriate activity
                Intent intent1= new Intent(ActivityAdminDashboard.this, ActivityCreateSubjects.class);
                startActivity(intent1);
                break;
            default:
                Toast.makeText(this, "Subjects: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // Handle navigation for Students subitems
    private void navigateStudents(String subItem) {
        switch (subItem) {
            case "All Students":
                Intent allStudents= new Intent(ActivityAdminDashboard.this, ActivityAllStudents.class);
                startActivity(allStudents);
                break;
            case "Add New":
                Intent addStudent= new Intent(ActivityAdminDashboard.this, ActivityAddStudents.class);
                startActivity(addStudent);
                break;
            case "Manage Families":
            case "Active / Inactive":
            case "Admission Letter":
                Intent admissionLetter= new Intent(ActivityAdminDashboard.this, ActivityAdmissionLetter.class);
                startActivity(admissionLetter);
                break;
            case "Student ID Cards":
                Intent idCard= new Intent(ActivityAdminDashboard.this, ActivityStudentsIdCards.class);
                startActivity(idCard);
                break;
            case "Print Basic List":
                Intent basicList= new Intent(ActivityAdminDashboard.this, ActivityBasicList.class);
                startActivity(basicList);
                break;
            case "Manage Login":
                Intent studentPassword= new Intent(ActivityAdminDashboard.this, ActivityManageStudentsLogin.class);
                startActivity(studentPassword);
                break;
            case "Promote Students":
                Intent promoteStudents= new Intent(ActivityAdminDashboard.this, ActivityPromoteStudents.class);
                startActivity(promoteStudents);
                break;
            default:
                Toast.makeText(this, "Students: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // Handle navigation for Employees subitems
    private void navigateEmployees(String subItem) {
        switch (subItem) {
            case "All Employees":
                Intent allEmployees= new Intent(ActivityAdminDashboard.this, ActivityAllEmployee.class);
                startActivity(allEmployees);
                break;
            case "Add New":
                Intent addEmployees= new Intent(ActivityAdminDashboard.this, ActivityAddEmployee.class);
                startActivity(addEmployees);
                break;
            case "Staff ID Cards":
                Intent idCard= new Intent(ActivityAdminDashboard.this, ActivityStaffIdCard.class);
                startActivity(idCard);
                break;
            case "Job Letter":
                Intent jobLetter= new Intent(ActivityAdminDashboard.this, ActivityJobLetter.class);
                startActivity(jobLetter);
                break;
            case "Manage Login":
                Intent staffLogin= new Intent(ActivityAdminDashboard.this, ActivityStaffLogin.class);
                startActivity(staffLogin);
                break;
            default:
                Toast.makeText(this, "Employees: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // Handle navigation for Accounts subitems
    private void navigateAccounts(String subItem) {
        switch (subItem) {
            case "Chart Of Accounts":
            case "Add Income":
            case "Add Expense":
            case "Account Statement":
                // Navigate to appropriate activity
                Toast.makeText(this, "Accounts: " + subItem + " (Activity not created yet)", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Accounts: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void initializeCharts() {
        setupLineChart();
        setupBarChart();
    }

    private void setupLineChart() {
        List<Entry> lineEntries = new ArrayList<>();
        lineEntries.add(new Entry(1, 5000));
        lineEntries.add(new Entry(2, 7000));
        lineEntries.add(new Entry(3, 4000));
        lineEntries.add(new Entry(4, 8000));
        lineEntries.add(new Entry(5, 6000));

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Monthly Revenue");
        lineDataSet.setColor(R.color.light_blue);
        lineDataSet.setValueTextColor(getResources().getColor(R.color.light_blue));
        lineDataSet.setLineWidth(2f);

        LineData lineData = new LineData(lineDataSet);
        binding.chartAccountsOverview.setData(lineData);

        // Customize chart
        binding.chartAccountsOverview.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.chartAccountsOverview.getXAxis().setTextColor(getResources().getColor(R.color.light_blue));
        binding.chartAccountsOverview.getAxisLeft().setTextColor(getResources().getColor(R.color.light_blue));
        binding.chartAccountsOverview.getAxisRight().setTextColor(getResources().getColor(R.color.light_blue));
        binding.chartAccountsOverview.getLegend().setTextColor(getResources().getColor(R.color.light_blue));
        binding.chartAccountsOverview.getDescription().setEnabled(false);

        // **Set Animation**
        binding.chartAccountsOverview.animateX(1500); // 1.5 sec horizontal animation

        binding.chartAccountsOverview.invalidate();
    }

    private void setupBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 45)); // Class 10 - 45 students
        entries.add(new BarEntry(1f, 60)); // Class 11 - 60 students
        entries.add(new BarEntry(2f, 50)); // Class 12 - 50 students

        BarDataSet dataSet = new BarDataSet(entries, "Students per Class");
        dataSet.setColor(getResources().getColor(R.color.light_blue)); // Use getResources() for color
        dataSet.setValueTextColor(getResources().getColor(R.color.light_blue));
        dataSet.setValueTextSize(14f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f); // Adjust bar width to fit properly

        binding.chartClassStrength.setData(barData);
        binding.chartClassStrength.setFitBars(true);
        binding.chartClassStrength.getDescription().setEnabled(false);
        binding.chartClassStrength.getLegend().setTextColor(getResources().getColor(R.color.light_blue));

        // Customizing X-Axis (Class Labels)
        XAxis xAxis = binding.chartClassStrength.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String[] classes = {"Class 10", "Class 11", "Class 12"};
                if (value >= 0 && value < classes.length) {
                    return classes[(int) value]; // Ensure valid indexing
                }
                return "";
            }
        });
        xAxis.setGranularity(1f); // Ensure unique values for labels
        xAxis.setLabelCount(entries.size()); // Set correct label count
        xAxis.setTextColor(getResources().getColor(R.color.light_blue));
        xAxis.setTextSize(14f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        // Customizing Y-Axis
        YAxis leftAxis = binding.chartClassStrength.getAxisLeft();
        leftAxis.setTextColor(getResources().getColor(R.color.light_blue));
        leftAxis.setGranularity(1f);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = binding.chartClassStrength.getAxisRight();
        rightAxis.setEnabled(false);

        // **Set Animation**
        binding.chartClassStrength.animateY(2000); // 2 sec vertical animation

        binding.chartClassStrength.invalidate(); // Refresh chart
    }



}