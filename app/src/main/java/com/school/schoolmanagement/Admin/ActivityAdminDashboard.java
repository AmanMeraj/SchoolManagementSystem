package com.school.schoolmanagement.Admin;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.school.schoolmanagement.Admin.Accounts.Chart.ActivityChartOfAccounts;
import com.school.schoolmanagement.Admin.Accounts.Expense.ActivityAddExpense;
import com.school.schoolmanagement.Admin.Accounts.Income.ActivityAddIncome;
import com.school.schoolmanagement.Admin.Accounts.Statement.ActivityAccountsStatement;
import com.school.schoolmanagement.Admin.Adapter.AbsentStudentsHomeRowAdapter;
import com.school.schoolmanagement.Admin.Adapter.CustomNavigationAdapter;
import com.school.schoolmanagement.Admin.Adapter.NewAdmission;
import com.school.schoolmanagement.Admin.Adapter.PresentEmployeeAdapter;
import com.school.schoolmanagement.Admin.Certificate.ActivityCertificateDataSelection;
import com.school.schoolmanagement.Admin.ClassTest.ActivityClassTestResult;
import com.school.schoolmanagement.Admin.ClassTest.ActivityCreateNewClassTest;
import com.school.schoolmanagement.Admin.Classes.AllClasses.ActivityAllClass;
import com.school.schoolmanagement.Admin.Classes.NewClass.ActivityNewClass;
import com.school.schoolmanagement.Admin.Employee.AllEmployee.ActivityAllEmployee;
import com.school.schoolmanagement.Admin.Employee.AdmissionForm.ActivityAddEmployee;
import com.school.schoolmanagement.Admin.Employee.JobLetter.ActivityJobLetter;
import com.school.schoolmanagement.Admin.Employee.ManageLogin.ActivityStaffLogin;
import com.school.schoolmanagement.Admin.Employee.StaffIdCard.ActivityStaffIdCard;
import com.school.schoolmanagement.Admin.Exams.AddExam.ActivityAddNewExamination;
import com.school.schoolmanagement.Admin.Exams.AddUpdateMarks.ActivityAddUpdateMarks;
import com.school.schoolmanagement.Admin.Exams.EditOrDelete.ActivityEditOrDeleteExam;
import com.school.schoolmanagement.Admin.Exams.UpdateInfo.ActivityInsertMarks;
import com.school.schoolmanagement.Admin.Fees.CollectFees.ActivityCollectFees;
import com.school.schoolmanagement.Admin.Fees.Defaulters.ActivityFeesDefaulters;
import com.school.schoolmanagement.Admin.Fees.FeesInvoice.ActivityFeesInvoice;
import com.school.schoolmanagement.Admin.Fees.FeesRecord.ActivityFeesRecord;
import com.school.schoolmanagement.Admin.Fees.FeespaidReceipt.ActivityFeesPaidReceipt;
import com.school.schoolmanagement.Admin.Fees.Report.ActivityFeesReport;
import com.school.schoolmanagement.Admin.GeneralSettings.AccountFeesInvoice.ActivityAccountFeesInvoice;
import com.school.schoolmanagement.Admin.GeneralSettings.AccountSettings.ActivityAccountSettings;
import com.school.schoolmanagement.Admin.GeneralSettings.ActivityExamGrading;
import com.school.schoolmanagement.Admin.GeneralSettings.ActivityFeespaticulars;
import com.school.schoolmanagement.Admin.GeneralSettings.InstituteProfile.ActivityInstituteProfile;
import com.school.schoolmanagement.Admin.GeneralSettings.RulesAndregulations.ActivityRulesAndRegulations;
import com.school.schoolmanagement.Admin.Homework.homeworks.ActivityHomeworks;
import com.school.schoolmanagement.Admin.Model.AdminDashBoardClassGraph;
import com.school.schoolmanagement.Admin.Model.AdminDashboardAbsentStudents;
import com.school.schoolmanagement.Admin.Model.AdminDashboardAccountsOverview;
import com.school.schoolmanagement.Admin.Model.AdminDashboardNewAdmission;
import com.school.schoolmanagement.Admin.Model.AdminDashboardPresentEmployee;
import com.school.schoolmanagement.Admin.Model.AdminDashboardPresentPercent;
import com.school.schoolmanagement.Admin.Model.DashboardApiEstimatedFees;
import com.school.schoolmanagement.Admin.Model.NavItem;
import com.school.schoolmanagement.Admin.Model.NewAdmissonModel;
import com.school.schoolmanagement.Admin.Model.Student;
import com.school.schoolmanagement.Admin.Model.Teachers;
import com.school.schoolmanagement.Admin.QuestionPaper.ActivityAddSubjectChapter;
import com.school.schoolmanagement.Admin.QuestionPaper.ActivityCreateQuestionPaper;
import com.school.schoolmanagement.Admin.QuestionPaper.ActivityQuestionBank;
import com.school.schoolmanagement.Admin.Report.ActivityAccountReport;
import com.school.schoolmanagement.Admin.Report.ActivityEmployeeMonthlyAttendance1;
import com.school.schoolmanagement.Admin.Report.ActivityFeesCollectionReport1;
import com.school.schoolmanagement.Admin.Report.ActivityStudentMonthlyAttendance1;
import com.school.schoolmanagement.Admin.Report.ActivityStudentsAttendanceReport;
import com.school.schoolmanagement.Admin.Report.ActivityStudentsInfoReport;
import com.school.schoolmanagement.Admin.Report.ActivityStudentsReportCard;
import com.school.schoolmanagement.Admin.Salary.PaidReceipt.ActivitySalaryReceipt;
import com.school.schoolmanagement.Admin.Salary.PaySalary.ActivityPaySalary;
import com.school.schoolmanagement.Admin.Salary.Report.ActivitySalaryReport;
import com.school.schoolmanagement.Admin.Students.AllStudents.ActivityAllStudents;
import com.school.schoolmanagement.Admin.Students.AdmissionForm.ActivityAddStudents;
import com.school.schoolmanagement.Admin.Students.AdmissionLetter.ActivityAdmissionLetter;
import com.school.schoolmanagement.Admin.Students.BasicList.ActivityBasicList;
import com.school.schoolmanagement.Admin.Students.Managelogin.ActivityManageStudentsLogin;
import com.school.schoolmanagement.Admin.Students.PromoteStudents.ActivityPromoteStudents;
import com.school.schoolmanagement.Admin.Students.StudentsIdCard.ActivityStudentsIdCards;
import com.school.schoolmanagement.Admin.Subjects.ClassesWithSubjects.ActivityClassesWithSubjects;
import com.school.schoolmanagement.Admin.Subjects.CreateSubject.ActivityCreateSubjects;
import com.school.schoolmanagement.GlobalRepository.GlobalRepository;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Login.ActivityLogin;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Teachers.Attendance.ActivityAddUpdateAttendance;
import com.school.schoolmanagement.Teachers.Attendance.ActivityClassWiseAttendanceReport;
import com.school.schoolmanagement.Teachers.Attendance.ActivitySearchStudentsAttendanceReport;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityAddUpdateMarksBinding;
import com.school.schoolmanagement.databinding.ActivityAdminDashboardBinding;
import com.school.schoolmanagement.databinding.ActivityStudentMonthlyAttendance1Binding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class ActivityAdminDashboard extends Utility {
    private DrawerLayout drawerLayout;
    private ExpandableListView expandableListView;
    private CustomNavigationAdapter navigationAdapter;
    private List<NavItem> navItems;
    private Map<NavItem, List<String>> navSubItems;
    ActivityAdminDashboardBinding binding;
    private AbsentStudentsHomeRowAdapter adapter;
    private PresentEmployeeAdapter adapter2;
    private NewAdmission adapter3;

    private List<AdminDashboardPresentEmployee> teachersList;
    ViewModel viewModel;
    String auth;
    private List<NewAdmissonModel> newAdmissonModelList;
    private boolean isSessionExpiredDialogShown = false;
    private AtomicInteger pendingRequests = new AtomicInteger(0);
    private AtomicInteger unauthorizedResponses = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel= new ViewModelProvider(this).get(ViewModel.class);
        auth="Bearer "+pref.getPrefString(this,pref.user_token);

        setupToolbar();
        setupNavigation();
        setupRecyclerView();
        if(isInternetConnected(this)){
            // Reset counters for new batch of requests
            pendingRequests.set(14); // Total number of API calls
            unauthorizedResponses.set(0);
            isSessionExpiredDialogShown = false;

            showLoader("Loading dashboard data...");

            getClassInfo(auth);
            getSubjectInfo(auth);
            getEmployeeInfo(auth);
            getStudentsInfo(auth);
            getExpenseInfo(auth);
            getFeesInfo(auth);
            observeAbsentStudents(auth);
            observePresentEmployees(auth);
            observeNewAdmission(auth);
            getSchoolInfo(auth);
            observeClassGraphData(auth);
            loadAccountsOverview(auth);
            observePresentPercent(auth);
            observeEstimatedFees(auth);
        }else{
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        binding.addAttendanceStudentBtn.setOnClickListener(v->{
            Intent intent= new Intent(ActivityAdminDashboard.this, ActivityAddUpdateAttendance.class);
            intent.putExtra("from","STUDENT");
            startActivity(intent);
        });
        binding.addAttendanceStudentBtn2.setOnClickListener(v->{
            Intent intent= new Intent(ActivityAdminDashboard.this, ActivityAddUpdateAttendance.class);
            intent.putExtra("from","STUDENT");
            startActivity(intent);
        });
        binding.addEmployeeAttendanceRel.setOnClickListener(v->{
            Intent intent= new Intent(ActivityAdminDashboard.this, ActivityAddUpdateAttendance.class);
            intent.putExtra("from","EMPLOYEE");
            startActivity(intent);
        });
        binding.addEmployeeAttendanceRel2.setOnClickListener(v->{
            Intent intent= new Intent(ActivityAdminDashboard.this, ActivityAddUpdateAttendance.class);
            intent.putExtra("from","EMPLOYEE");
            startActivity(intent);
        });
        binding.addNewAdmissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(ActivityAdminDashboard.this, ActivityAddStudents.class);
                startActivity(intent);
            }
        });
        binding.addNewAdmissionBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(ActivityAdminDashboard.this, ActivityAddStudents.class);
                startActivity(intent);
            }
        });

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
    }

    private void prepareNavigationData() {
        navItems = new ArrayList<>();
        navSubItems = new HashMap<>();

        // Add all menu items
        navItems.add(new NavItem("Dashboard", R.drawable.layout));

        // General Settings
        NavItem generalSettings = new NavItem("General Settings", R.drawable.settings);
        navItems.add(generalSettings);
        List<String> generalSubItem = Arrays.asList(
                "Fees Particulars","Accounts for Fees","Rules & Regulations","Marks Grading","Account Settings","Logout"
        );
        navSubItems.put(generalSettings, generalSubItem);

        // Classes
        NavItem classItems = new NavItem("Classes", R.drawable.classmates);
        navItems.add(classItems);
        List<String> classSubItem = Arrays.asList(
                "All Classes","New Classes"
        );
        navSubItems.put(classItems, classSubItem);

        // Subjects
        NavItem subjectsItems = new NavItem("Subjects", R.drawable.book);
        navItems.add(subjectsItems);
        List<String> subjects = Arrays.asList(
                "Classes With Subjects","Assign Subjects"
        );
        navSubItems.put(subjectsItems, subjects);

        // Students
        NavItem studentsItem = new NavItem("Students", R.drawable.student_sm);
        navItems.add(studentsItem);
        List<String> studentSubItems = Arrays.asList(
                "All Students",
                "Add New",
                "Admission Letter",
                "Student ID Cards",
                "Print Basic List",
                "Manage Login",
                "Promote Students"
        );
        navSubItems.put(studentsItem, studentSubItems);

        // Employees
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

        // Accounts
        NavItem accountsNavItem = new NavItem("Accounts", R.drawable.employees);
        navItems.add(accountsNavItem);
        List<String> accountsSubItems = Arrays.asList(
                "Chart Of Accounts",
                "Add Income",
                "Add Expense",
                "Account Statement"
        );
        navSubItems.put(accountsNavItem, accountsSubItems);

        // Fees - Make sure this is correct
        NavItem feesNavItem = new NavItem("Fees", R.drawable.employees);
        navItems.add(feesNavItem);
        List<String> feesNavSubItem = Arrays.asList(
                "Generate Fees Invoice",
                "Collect Fees",
                "Fees Paid Slip",
                "Fees Defaulters",
                "Fees Report"
        );
        navSubItems.put(feesNavItem, feesNavSubItem);

        // Salary - This one might be missing or improperly configured
        NavItem salaryNavItem = new NavItem("Salary", R.drawable.employees);
        navItems.add(salaryNavItem);
        List<String> salaryNavSubItem = Arrays.asList(
                "Pay salary",
                "Salary Paid Slip",
                "Salary Report"
        );
        navSubItems.put(salaryNavItem, salaryNavSubItem);
        NavItem reportsNavItem = new NavItem("Reports", R.drawable.employees);
        navItems.add(reportsNavItem);
        List<String> reportsNavSubItem = Arrays.asList(
                "Students Report Card",
                "Students Information",
                "Students Monthly Attendance Report",
                "Employees Monthly Attendance Report",
                "Fees Collection Report",
                "Account Report"
        );
        navSubItems.put(reportsNavItem, reportsNavSubItem);

        navItems.add(new NavItem("Homework", R.drawable.homework));

        NavItem attendanceNavItem = new NavItem("Attendance", R.drawable.employees);
        navItems.add(attendanceNavItem);
        List<String> attendanceSubItems = Arrays.asList(
                "Students Attendance",
                "Employees Attendance",
                "Class wise Report",
                "Students Attendance Report",
                "Employees Attendance Report"
        );
        navSubItems.put(attendanceNavItem, attendanceSubItems);
        NavItem questionPaperNavItem = new NavItem("Question Paper", R.drawable.employees);
        navItems.add(questionPaperNavItem);
        List<String> questionPaperSubItems = Arrays.asList(
                "Subject Chapters",
                "Question Bank",
                "Create Question Paper"
        );
        navSubItems.put(questionPaperNavItem, questionPaperSubItems);

        NavItem examNavItem = new NavItem("Exams", R.drawable.employees);
        navItems.add(examNavItem);
        List<String> examNavSubItem = Arrays.asList(
                "Create new Exam",
                "Add / Update Exam Marks",
                "Edit / Delete Exam",
                "Result Card"
        );
        navSubItems.put(examNavItem, examNavSubItem);
        NavItem classTestNavItem = new NavItem("Class Test", R.drawable.employees);
        navItems.add(classTestNavItem);
        List<String> classTestNavSubItem = Arrays.asList(
                "Manage Test Marks",
                "Test Result"
        );
        navSubItems.put(classTestNavItem, classTestNavSubItem);
        navSubItems.put(examNavItem, examNavSubItem);

        NavItem certificateNavItem = new NavItem("Certificate", R.drawable.employees);
        navItems.add(certificateNavItem);
        List<String> certificateNavSubItem = Arrays.asList(
                "Leave Certificate",
                "Character Certificate"
        );
        navSubItems.put(certificateNavItem, certificateNavSubItem);

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
            case "Homework":
                 Intent homeworkIntent = new Intent(this, ActivityHomeworks.class);
                 startActivity(homeworkIntent);
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
            case "Fees":
                navigateFees(itemTitle);
                break;
            case "Attendance":
                navigateAttendance(itemTitle);
                break;
            case "Salary":
                navigateSalary(itemTitle);
                break;
            case "Exams":
                navigateExams(itemTitle);
                break;
            case "Reports":
                navigateReports(itemTitle);
                break;
            case "Class Test":
                navigateClassTest(itemTitle);
                break;
            case "Question Paper":
                navigateQuestionPaper(itemTitle);
                break;
            case "Certificate":
                navigateCertificate(itemTitle);
                break;
            default:
                Toast.makeText(this, groupTitle + ": " + itemTitle + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void navigateQuestionPaper(String itemTitle) {
        switch (itemTitle){
            case "Subject Chapters":
                Intent addSubjectIntent = new Intent(this, ActivityAddSubjectChapter.class);
                startActivity(addSubjectIntent);
                break;
            case "Question Bank":
                Intent addQBResultIntent = new Intent(this, ActivityQuestionBank.class);
                startActivity(addQBResultIntent);
                break;
            case "Create Question Paper":
                Intent createQPIntent = new Intent(this, ActivityCreateQuestionPaper.class);
                startActivity(createQPIntent);
                break;
            default:
                Toast.makeText(this, "General Settings: " + itemTitle + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void navigateCertificate(String itemTitle) {
        switch (itemTitle){
            case "Leave Certificate":
                Intent leaveCertificate = new Intent(this, ActivityCertificateDataSelection.class);
                leaveCertificate.putExtra("from","leave");
                startActivity(leaveCertificate);
                break;
            case "Character Certificate":
                Intent characterCertificate = new Intent(this, ActivityCertificateDataSelection.class);
                characterCertificate.putExtra("from","character");
                startActivity(characterCertificate);
                break;
            default:
                Toast.makeText(this, "General Settings: " + itemTitle + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void navigateClassTest(String itemTitle) {
        switch (itemTitle){
            case "Manage Test Marks":
                Intent classTestIntent = new Intent(this, ActivityCreateNewClassTest.class);
                startActivity(classTestIntent);
                break;
            case "Test Result":
                Intent classTestResultIntent = new Intent(this, ActivityClassTestResult.class);
                startActivity(classTestResultIntent);
                break;
            default:
                Toast.makeText(this, "General Settings: " + itemTitle + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void navigateGeneralSettings(String subItem) {
        switch (subItem) {
            case "Institute Profile":
                Intent instituteProfileIntent = new Intent(this, ActivityInstituteProfile.class);
                startActivity(instituteProfileIntent);
                break;
            case "Fees Particulars":
                Intent feesParticularIntent = new Intent(this, ActivityFeespaticulars.class);
                startActivity(feesParticularIntent);
                break;
            case "Accounts for Fees":
                Intent AccountFeesIntent = new Intent(this, ActivityAccountFeesInvoice.class);
                startActivity(AccountFeesIntent);
                break;
            case "Rules & Regulations":
                Intent RulesAndRegulations = new Intent(this, ActivityRulesAndRegulations.class);
                startActivity(RulesAndRegulations);
                break;
            case "Marks Grading":
                Intent marksGrading = new Intent(this, ActivityExamGrading.class);
                startActivity(marksGrading);
                break;
            case "Account Settings":
                Intent AccountSettings = new Intent(this, ActivityAccountSettings.class);
                startActivity(AccountSettings);
                break;
            case "Logout":
                Intent Logout = new Intent(this, ActivityLogin.class);
                pref.clearAll(this);
                startActivity(Logout);
                Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
                Toast.makeText(this, "General Settings: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }
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
    private void navigateAccounts(String subItem) {
        switch (subItem) {
            case "Chart Of Accounts":
                Intent chart= new Intent(ActivityAdminDashboard.this, ActivityChartOfAccounts.class);
                startActivity(chart);
                break;
            case "Add Income":
                Intent AddIncome= new Intent(ActivityAdminDashboard.this, ActivityAddIncome.class);
                startActivity(AddIncome);
                break;
            case "Add Expense":
                Intent AddExpense= new Intent(ActivityAdminDashboard.this, ActivityAddExpense.class);
                startActivity(AddExpense);
                break;
            case "Account Statement":
                Intent Statement= new Intent(ActivityAdminDashboard.this, ActivityAccountsStatement.class);
                startActivity(Statement);
                break;
            default:
                Toast.makeText(this, "Accounts: " + subItem + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void navigateFees(String itemTitle) {
        switch (itemTitle){
            case "Generate Fees Invoice":
                Intent generateFee= new Intent(ActivityAdminDashboard.this, ActivityFeesInvoice.class);
                startActivity(generateFee);
                break;
            case "Collect Fees":
                Intent collectFees= new Intent(ActivityAdminDashboard.this, ActivityCollectFees.class);
                startActivity(collectFees);
                break;
            case  "Fees Paid Slip":
                Intent paidSlip= new Intent(ActivityAdminDashboard.this, ActivityFeesRecord.class);
                startActivity(paidSlip);
                break;
            case "Fees Defaulters":
                Intent feesDefaulters= new Intent(ActivityAdminDashboard.this, ActivityFeesDefaulters.class);
                startActivity(feesDefaulters);
                break;
            case "Fees Report":
                Intent feesReport= new Intent(ActivityAdminDashboard.this, ActivityFeesReport.class);
                startActivity(feesReport);
                break;
            default:
                Toast.makeText(this, "Accounts: " + itemTitle + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void navigateAttendance(String itemTitle) {
        switch (itemTitle){
            case "Students Attendance":
                Intent addUpdateAttendance= new Intent(ActivityAdminDashboard.this, ActivityAddUpdateAttendance.class);
                addUpdateAttendance.putExtra("from","STUDENT");
                startActivity(addUpdateAttendance);
                break;
            case "Employees Attendance":
                Intent addUpdateEmployeeAttendance= new Intent(ActivityAdminDashboard.this, ActivityAddUpdateAttendance.class);
                addUpdateEmployeeAttendance.putExtra("from","EMPLOYEE");
                startActivity(addUpdateEmployeeAttendance);
                break;
            case  "Class wise Report":
                Intent classWiseReport= new Intent(ActivityAdminDashboard.this, ActivityClassWiseAttendanceReport.class);
                startActivity(classWiseReport);
                break;
            case "Students Attendance Report":
                Intent feesDefaulters= new Intent(ActivityAdminDashboard.this, ActivitySearchStudentsAttendanceReport.class);
                startActivity(feesDefaulters);
                break;
            case "Employees Attendance Report":
                Intent feesReport= new Intent(ActivityAdminDashboard.this, ActivityFeesReport.class);
                startActivity(feesReport);
                break;
            default:
                Toast.makeText(this, "Accounts: " + itemTitle + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void navigateSalary(String itemTitle) {
        switch (itemTitle){
            case "Pay salary":
                Intent generateFee= new Intent(ActivityAdminDashboard.this, ActivityPaySalary.class);
                startActivity(generateFee);
                break;
            case "Salary Paid Slip":
                Intent collectFees= new Intent(ActivityAdminDashboard.this, ActivitySalaryReceipt.class);
                startActivity(collectFees);
                break;
            case "Salary Report":
                Intent feesDefaulters= new Intent(ActivityAdminDashboard.this, ActivitySalaryReport.class);
                startActivity(feesDefaulters);
                break;
            default:
                Toast.makeText(this, "Accounts: " + itemTitle + " clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void navigateExams(String itemTitle) {
     switch ((itemTitle)){

         case "Create new Exam":
             Intent createExamsIntent= new Intent(ActivityAdminDashboard.this, ActivityAddNewExamination.class);
             startActivity(createExamsIntent);
             break;
         case "Add / Update Exam Marks" :
             Intent addupdateIntent= new Intent(ActivityAdminDashboard.this, ActivityAddUpdateMarks.class);
             startActivity(addupdateIntent);
             break;
         case "Edit / Delete Exam" :
             Intent editDeleteIntent= new Intent(ActivityAdminDashboard.this, ActivityEditOrDeleteExam.class);
             startActivity(editDeleteIntent);
             break;
         case "Result Card":
             Intent resultIntent= new Intent(ActivityAdminDashboard.this, ActivityInsertMarks.class);
             startActivity(resultIntent);
             break;
     }
    }
    private void navigateReports(String itemTitle) {
        switch (itemTitle) {
            case "Students Report Card":
                Intent studentsReportCardIntent = new Intent(ActivityAdminDashboard.this, ActivityStudentsReportCard.class);
                startActivity(studentsReportCardIntent);
                break;

            case "Students Information":
                Intent studentsInfoIntent = new Intent(ActivityAdminDashboard.this, ActivityStudentsInfoReport.class);
                startActivity(studentsInfoIntent);
                break;

            case "Students Monthly Attendance Report":
                Intent sAttendanceReport = new Intent(ActivityAdminDashboard.this, ActivityStudentMonthlyAttendance1.class);
                startActivity(sAttendanceReport);
                break;

            case "Employees Monthly Attendance Report":
                Intent eAttendanceReport = new Intent(ActivityAdminDashboard.this, ActivityEmployeeMonthlyAttendance1.class);
                startActivity(eAttendanceReport);
                break;

            case "Fees Collection Report":
                Intent feesCollectionReport = new Intent(ActivityAdminDashboard.this, ActivityFeesCollectionReport1.class);
                startActivity(feesCollectionReport);
                break;

            case "Account Report":
                Intent accountReport = new Intent(ActivityAdminDashboard.this, ActivityAccountReport.class);
                startActivity(accountReport);
                break;

            default:
                Toast.makeText(this, "Report not found: " + itemTitle, Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private void getClassInfo(String auth){
        viewModel.getClassInfo(auth).observe(this,response->{
            handleResponse(response, () -> {
                binding.classesNumberTv.setText(String.valueOf(response.data.getTotalClassCount()));
                binding.thisMonthClasses.setText(String.valueOf(response.data.getAddedThisMonth()));
            });
        });
    }

    private void getStudentsInfo(String auth){
        viewModel.getStudentInfo(auth).observe(this,response->{
            handleResponse(response, () -> {
                binding.studentsNumbersTv.setText(String.valueOf(response.data.getTotalStudentCount()));
                binding.thisMonthStudents.setText(String.valueOf(response.data.getAddedThisMonth()));
            });
        });
    }

    private void getEmployeeInfo(String auth){
        viewModel.getEmployeeInfo(auth).observe(this,response->{
            handleResponse(response, () -> {
                binding.staffsNumbersTv.setText(String.valueOf(response.data.getTotalEmployees()));
                binding.thisMonthStaffs.setText(String.valueOf(response.data.getAddedThisMonth()));
            });
        });
    }

    private void getExpenseInfo(String auth){
        viewModel.getExpenseInfo(auth).observe(this,response->{
            handleResponse(response, () -> {
                binding.expensesNumbersTv.setText(String.valueOf(response.data.getTotalExpenses()));
                binding.thisMonthExpenses.setText(String.valueOf(response.data.getExpensesThisMonth()));
            });
        });
    }
    private void getSchoolInfo(String auth) {
        viewModel.getSchoolInfo(auth).observe(this, response -> {
            handleResponse(response, () -> {
                binding.navHead.navHeaderAdminEmail.setText(response.data.data.admin.getUsername());
                binding.navHead.navHeaderAdminName.setText(response.data.data.admin.getRole());
                binding.navHead.navHeaderStatus.setText(response.data.data.admin.getStatus());

                String base64Image = response.data.data.getLogo(); // or school logo

                if (base64Image != null && !base64Image.isEmpty()) {
                    try {
                        // Strip prefix if present
                        if (base64Image.startsWith("data:image")) {
                            base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
                        }

                        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);

                        // Set image to navigation drawer header
                        binding.navHead.navHeaderLogo.setImageBitmap(bitmap);

                        // Set logo in toolbar using binding
                        binding.toolbar.setLogo(drawable);

                        // Optional: also set in ActionBar (if needed)
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setLogo(drawable);
                        }

                    } catch (IllegalArgumentException e) {
                        Log.e("Base64", "Failed to decode base64 image: " + e.getMessage());
                    }
                }
            });
        });
    }



    private void getFeesInfo(String auth){
        viewModel.getFeesInfo(auth).observe(this,response->{
            handleResponse(response, () -> {
                binding.feesNumbersTv.setText(String.valueOf(response.data.getTotalReceived()));
                binding.thisMonthFees.setText(String.valueOf(response.data.getReceivedThisMonth()));
            });
        });
    }

    private void getSubjectInfo(String auth){
        viewModel.getSubjectInfo(auth).observe(this,response->{
            handleResponse(response, () -> {
                binding.subjectsNumbersTv.setText(String.valueOf(response.data.getTotalSubjects()));
                binding.thisMonthSubjects.setText(String.valueOf(response.data.getAddedThisMonth()));
            });
        });
    }

    private void handleResponse(GlobalRepository.ApiResponse response, Runnable onSuccess) {
        if (response.code == 401) {
            unauthorizedResponses.incrementAndGet();
            checkAndShowSessionExpiredDialog();
        } else if (response.isSuccess && response.code == 200) {
            onSuccess.run();
        } else {
            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
        }

        // Decrement pending requests and hide loader when all requests complete
        int remainingRequests = pendingRequests.decrementAndGet();
        if (remainingRequests <= 0) {
            hideLoader();
        }
    }

    private synchronized void checkAndShowSessionExpiredDialog() {
        // Show dialog if not already shown and we have unauthorized responses
        if (!isSessionExpiredDialogShown && unauthorizedResponses.get() > 0) {
            isSessionExpiredDialogShown = true;
            hideLoader(); // Hide loader before showing dialog
            showSessionExpiredDialog();
        }
    }

    private void showSessionExpiredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Session Expired")
                .setMessage("Your session has expired. Please log in again.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    navigateToLogin();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void navigateToLogin() {
        // Clear any stored auth tokens/user data
        clearUserSession();

        // Navigate to login activity
        Intent intent = new Intent(this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void clearUserSession() {
        pref.clearAll(this);
    }

    private void showLoader(String message){
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void hideLoader(){
        binding.loader.rlLoader.setVisibility(View.GONE);
    }
    private void setupRecyclerView() {
        adapter = new AbsentStudentsHomeRowAdapter(this, new ArrayList<>());
        binding.rcAbsentStudents.setAdapter(adapter);
        adapter2 = new PresentEmployeeAdapter(this, new ArrayList<>());
        binding.rcAbsentTeachers.setAdapter(adapter2);
        adapter3 = new NewAdmission(this, new ArrayList<>());
        binding.rcNewAdmissions.setAdapter(adapter3);
    }

    private void observeAbsentStudents(String auth) {
        viewModel.getAbsentStudents(auth).observe(this, response -> {
            if (response.isSuccess) {
                AdminDashboardAbsentStudents absentStudentsData = response.data;
                if (absentStudentsData != null && absentStudentsData.getData() != null && !absentStudentsData.getData().isEmpty()) {
                    // Data is available - show recycler view and hide no item view
                    binding.noItemAbsentStudents.setVisibility(View.GONE);
                    binding.rcAbsentStudents.setVisibility(View.VISIBLE);

                    adapter.updateList(absentStudentsData.getData());
                    binding.addAttendanceStudentBtn.setVisibility(View.GONE);
                    binding.addAttendanceStudentBtn2.setVisibility(View.VISIBLE);

                } else {
                    // No data available - show no item view and hide recycler view
                    binding.noItemAbsentStudents.setVisibility(View.VISIBLE);
                    binding.rcAbsentStudents.setVisibility(View.GONE);
                    binding.addAttendanceStudentBtn.setVisibility(View.VISIBLE);
                    binding.addAttendanceStudentBtn2.setVisibility(View.GONE);
                }
            } else {
                // Handle error - show no item view and hide recycler view
                binding.addAttendanceStudentBtn2.setVisibility(View.GONE);
                binding.noItemAbsentStudents.setVisibility(View.VISIBLE);
                binding.rcAbsentStudents.setVisibility(View.GONE);
                binding.addAttendanceStudentBtn.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Error: " + response.message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void observePresentEmployees(String auth) {
        viewModel.getPresentEmployee(auth).observe(this, response -> {
            if (response.isSuccess) {
                AdminDashboardPresentEmployee presentEmployeeData = response.data;
                if (presentEmployeeData != null && presentEmployeeData.getData() != null && !presentEmployeeData.getData().isEmpty()) {
                    // Data is available - show recycler view and hide no item view
                    binding.noItemPresentStaff.setVisibility(View.GONE);
                    binding.rcAbsentTeachers.setVisibility(View.VISIBLE);

                    adapter2.updateList(presentEmployeeData.getData());
                    binding.addEmployeeAttendanceRel.setVisibility(View.GONE);
                    binding.addEmployeeAttendanceRel2.setVisibility(View.VISIBLE);

                } else {
                    // No data available - show no item view and hide recycler view
                    binding.noItemPresentStaff.setVisibility(View.VISIBLE);
                    binding.rcAbsentTeachers.setVisibility(View.GONE);
                    binding.addEmployeeAttendanceRel.setVisibility(View.VISIBLE);
                    binding.addEmployeeAttendanceRel2.setVisibility(View.GONE);
                }
            } else {
                // Handle error - show no item view and hide recycler view
                binding.addEmployeeAttendanceRel2.setVisibility(View.GONE);
                binding.noItemPresentStaff.setVisibility(View.VISIBLE);
                binding.rcAbsentTeachers.setVisibility(View.GONE);
                binding.addEmployeeAttendanceRel.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Error: " + response.message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void observePresentPercent(String auth) {
        viewModel.getPresentPercent(auth).observe(this, response -> {
            if (response.isSuccess) {
                AdminDashboardPresentPercent presentPercentData = response.data;
                if (presentPercentData != null && presentPercentData.getData() != null) {
                    // Data is available - update progress bars
                    AdminDashboardPresentPercent.Data data = presentPercentData.getData();

                    // Update Student Present Percentage
                    String studentPercent = data.getStudentPresentPercent();
                    if (studentPercent != null && !studentPercent.isEmpty()) {
                        try {
                            // Remove % sign if present and convert to integer
                            String cleanPercent = studentPercent.replace("%", "").trim();
                            int studentProgress = Integer.parseInt(cleanPercent);
                            binding.presentStudentSeekBar.setProgress(studentProgress);
                            binding.presentStudentSeekBar.setEnabled(false); // Make non-editable
                            binding.percentTvPresentStudents.setText(studentPercent);
                        } catch (NumberFormatException e) {
                            binding.presentStudentSeekBar.setProgress(0);
                            binding.percentTvPresentStudents.setText("0%");
                        }
                    }

                    // Update Staff Present Percentage
                    String staffPercent = data.getStaffPresentPercent();
                    if (staffPercent != null && !staffPercent.isEmpty()) {
                        try {
                            // Remove % sign if present and convert to integer
                            String cleanPercent = staffPercent.replace("%", "").trim();
                            int staffProgress = Integer.parseInt(cleanPercent);
                            binding.presentEmployeeSeekBar.setProgress(staffProgress);
                            binding.presentEmployeeSeekBar.setEnabled(false); // Make non-editable
                            binding.percentTvPresentEmployee.setText(staffPercent);
                        } catch (NumberFormatException e) {
                            binding.presentEmployeeSeekBar.setProgress(0);
                            binding.percentTvPresentEmployee.setText("0%");
                        }
                    }

                    // Update Fees Collection Percentage
                    String feesPercent = data.getFeesCollectionPercent();
                    if (feesPercent != null && !feesPercent.isEmpty()) {
                        try {
                            // Remove % sign if present and convert to integer
                            String cleanPercent = feesPercent.replace("%", "").trim();
                            int feesProgress = Integer.parseInt(cleanPercent);
                            binding.feesCollectionSeekBar.setProgress(feesProgress);
                            binding.feesCollectionSeekBar.setEnabled(false); // Make non-editable
                            binding.percentTvFeeCollection.setText(feesPercent);
                        } catch (NumberFormatException e) {
                            binding.feesCollectionSeekBar.setProgress(0);
                            binding.percentTvFeeCollection.setText("0%");
                        }
                    }

                    // Show the seek bar card
                    binding.seekBarCard.setVisibility(View.VISIBLE);

                } else {
                    // No data available - hide seek bar card or show default values
                    binding.seekBarCard.setVisibility(View.GONE);
                    // Or set default values:
                    // setDefaultProgressValues();
                }
            } else {
                // Handle error - hide seek bar card or show default values
                binding.seekBarCard.setVisibility(View.GONE);
                Toast.makeText(this, "Error loading dashboard data: " + response.message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void observeEstimatedFees(String auth) {
        viewModel.getEstimatedFees(auth).observe(this, response -> {
            if (response.isSuccess) {
                DashboardApiEstimatedFees estimatedFeesData = response.data;
                if (estimatedFeesData != null && estimatedFeesData.getData() != null) {
                    // Data is available - update the estimated fees card
                    DashboardApiEstimatedFees.Data data = estimatedFeesData.getData();

                    // Update Estimated Amount
                    int estimatedAmount = data.getEstimatedAmount();
                    binding.txtAmount.setText("₹" + estimatedAmount);

                    // Update Collected Amount
                    int collectedAmount = data.getCollectedAmount();
                    binding.collectedAmount.setText("₹" + collectedAmount);

                    // Update Remaining Amount
                    int remainingAmount = data.getRemainingAmount();
                    binding.remainingAmount.setText("₹" + remainingAmount);

                    // Show the estimated fees card and hide the no-data views
                    binding.estimatedFeesCard.setVisibility(View.VISIBLE);
                    binding.noItemEstimatedFees.setVisibility(View.GONE);

                    // Hide the seek bar card if it exists (since we're now showing fees data instead of percentages)
                    if (binding.seekBarCard != null) {
                        binding.seekBarCard.setVisibility(View.GONE);
                    }

                } else {
                    // No data available - show no-data state
                    binding.estimatedFeesCard.setVisibility(View.VISIBLE);
                    binding.noItemEstimatedFees.setVisibility(View.VISIBLE);

                    // Set default/empty values
                    binding.txtAmount.setText("₹0");
                    binding.collectedAmount.setText("₹0");
                    binding.remainingAmount.setText("₹0");
                }
            } else {
                // Handle error - show error state or hide card
                binding.estimatedFeesCard.setVisibility(View.VISIBLE);
                binding.noItemEstimatedFees.setVisibility(View.VISIBLE);

                // Set default values
                binding.txtAmount.setText("₹0");
                binding.collectedAmount.setText("₹0");
                binding.remainingAmount.setText("₹0");

                Toast.makeText(this, "Error loading estimated fees: " + response.message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void observeNewAdmission(String auth) {
        viewModel.getNewAdmission(auth).observe(this, response -> {
            if (response.isSuccess) {
                AdminDashboardNewAdmission newAdmissionData = response.data;
                if (newAdmissionData != null && newAdmissionData.getData() != null && !newAdmissionData.getData().isEmpty()) {
                    // Data is available - show recycler view and hide no item view
                    binding.noItemNewAdmission.setVisibility(View.GONE);
                    binding.rcNewAdmissions.setVisibility(View.VISIBLE);

                    adapter3.updateList(newAdmissionData.getData());
                    binding.addNewAdmissionBtn.setVisibility(View.GONE);
                    binding.addNewAdmissionBtn2.setVisibility(View.VISIBLE);

                } else {
                    // No data available - show no item view and hide recycler view
                    binding.noItemNewAdmission.setVisibility(View.VISIBLE);
                    binding.rcNewAdmissions.setVisibility(View.GONE);
                    binding.addNewAdmissionBtn.setVisibility(View.VISIBLE);
                    binding.addNewAdmissionBtn2.setVisibility(View.GONE);
                }
            } else {
                // Handle error - show no item view and hide recycler view
                binding.addNewAdmissionBtn2.setVisibility(View.GONE);
                binding.noItemNewAdmission.setVisibility(View.VISIBLE);
                binding.rcNewAdmissions.setVisibility(View.GONE);
                binding.addNewAdmissionBtn.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Error: " + response.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeClassGraphData(String auth) {

        viewModel.getClassGraph(auth).observe(this, apiResponse -> {
            if (apiResponse != null) {
                if (apiResponse.isSuccess) {
                    List<AdminDashBoardClassGraph> classGraphData = apiResponse.data;
                    if (classGraphData != null && !classGraphData.isEmpty()) {
                        // Handle successful response
                        handleClassGraphSuccess(classGraphData);
                    } else {
                        // Handle empty data
                        handleEmptyClassGraphData();
                    }
                } else {
                    // Handle error
                    String errorMessage = apiResponse.message;
                    handleClassGraphError(errorMessage);
                }
            }
        });
    }

    private void handleClassGraphSuccess(List<AdminDashBoardClassGraph> classGraphData) {
        // Process your class graph data
        for (AdminDashBoardClassGraph classData : classGraphData) {
            String className = classData.getClassName();
            int strength = classData.getStrength();

            // Use the data to populate your chart/graph
            Log.d(TAG, "Class: " + className + ", Strength: " + strength);
        }

        // Update UI or populate chart
        setupBarChart(classGraphData);
    }

    private void handleEmptyClassGraphData() {
        // Handle case when no class data is available
        Log.d(TAG, "No class graph data available");
        // Show empty state or default message
        setupBarChart(null); // This will clear the chart and show "No data available"
    }

    private void handleClassGraphError(String errorMessage) {
        // Handle error case
        Log.e(TAG, "Class graph error: " + errorMessage);
        // Show error message to user or retry option
    }

    private void setupBarChart(List<AdminDashBoardClassGraph> classGraphData) {
        if (classGraphData == null || classGraphData.isEmpty()) {
            // Handle empty data case
            binding.chartClassStrength.clear();
            binding.chartClassStrength.setNoDataText("No class data available");
            binding.chartClassStrength.invalidate();
            return;
        }

        // Create entries dynamically from API data
        List<BarEntry> entries = new ArrayList<>();
        List<String> classNames = new ArrayList<>();

        for (int i = 0; i < classGraphData.size(); i++) {
            AdminDashBoardClassGraph classData = classGraphData.get(i);
            entries.add(new BarEntry(i, classData.getStrength()));
            classNames.add(classData.getClassName());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Students per Class");
        dataSet.setColor(getResources().getColor(R.color.light_blue));
        dataSet.setValueTextColor(getResources().getColor(R.color.light_blue));
        dataSet.setValueTextSize(14f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        binding.chartClassStrength.setData(barData);
        binding.chartClassStrength.setFitBars(true);
        binding.chartClassStrength.getDescription().setEnabled(false);
        binding.chartClassStrength.getLegend().setTextColor(getResources().getColor(R.color.light_blue));

        // Customizing X-Axis (Class Labels) - Dynamic
        XAxis xAxis = binding.chartClassStrength.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0 && value < classNames.size()) {
                    return classNames.get((int) value);
                }
                return "";
            }
        });
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(classNames.size());
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

        // Set Animation
        binding.chartClassStrength.animateY(2000);

        binding.chartClassStrength.invalidate();
    }
    private void loadAccountsOverview(String auth) {

        viewModel.getAccountsOverview(auth).observe(this, response -> {
            if (response.isSuccess && response.data != null) {
                AdminDashboardAccountsOverview accountsData = response.data;

                // Update total revenue display

                // Setup line chart with actual data
                setupLineChart(accountsData);

            } else {
                // Handle error
                Toast.makeText(this, "Failed to load accounts overview: " + response.message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupLineChart(AdminDashboardAccountsOverview accountsData) {
        List<Entry> lineEntries = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();

        // Convert monthly revenue data to chart entries
        ArrayList<AdminDashboardAccountsOverview.MonthlyRevenue> monthlyRevenue = accountsData.getMonthlyRevenue();

        for (int i = 0; i < monthlyRevenue.size(); i++) {
            AdminDashboardAccountsOverview.MonthlyRevenue revenue = monthlyRevenue.get(i);

            // Add entry with index as X value and revenue as Y value
            lineEntries.add(new Entry(i, (float) revenue.getRevenue()));

            // Create label for X-axis (Month/Year format)
            String monthLabel = getMonthName(revenue.getMonth()) + " " + revenue.getYear();
            xAxisLabels.add(monthLabel);
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Monthly Revenue");
        lineDataSet.setColor(R.color.light_blue);
        lineDataSet.setValueTextColor(getResources().getColor(R.color.light_blue));
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleColor(R.color.light_blue);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Smooth curves
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(R.color.light_blue);
        lineDataSet.setFillAlpha(50);

        LineData lineData = new LineData(lineDataSet);
        binding.chartAccountsOverview.setData(lineData);

        // Customize X-axis
        XAxis xAxis = binding.chartAccountsOverview.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(getResources().getColor(R.color.light_blue));
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(xAxisLabels.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxis.setLabelRotationAngle(-45f); // Rotate labels for better readability

        // Customize Y-axis
        YAxis leftAxis = binding.chartAccountsOverview.getAxisLeft();
        leftAxis.setTextColor(getResources().getColor(R.color.light_blue));
        leftAxis.setAxisMinimum(0f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "₹" + String.format("%.0f", value);
            }
        });

        YAxis rightAxis = binding.chartAccountsOverview.getAxisRight();
        rightAxis.setEnabled(false); // Disable right axis for cleaner look

        // Customize legend
        Legend legend = binding.chartAccountsOverview.getLegend();
        legend.setTextColor(getResources().getColor(R.color.light_blue));
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);

        // Disable description
        binding.chartAccountsOverview.getDescription().setEnabled(false);

        // Enable touch gestures
        binding.chartAccountsOverview.setTouchEnabled(true);
        binding.chartAccountsOverview.setDragEnabled(true);
        binding.chartAccountsOverview.setScaleEnabled(true);
        binding.chartAccountsOverview.setPinchZoom(true);

        // Set animation
        binding.chartAccountsOverview.animateX(1500); // 1.5 sec horizontal animation
        binding.chartAccountsOverview.invalidate();
    }

    // Helper method to convert month number to month name
    private String getMonthName(int month) {
        String[] months = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month];
    }

    // Alternative helper method using Calendar for full month names
    private String getFullMonthName(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);
        return new SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.getTime());
    }
}



