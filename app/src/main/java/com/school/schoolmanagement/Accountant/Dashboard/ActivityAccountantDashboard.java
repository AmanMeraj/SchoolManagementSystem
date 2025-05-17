package com.school.schoolmanagement.Accountant.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

import com.google.android.material.navigation.NavigationView;
import com.school.schoolmanagement.Accountant.FeeInvoice.ActivityAccountantCollectFees;
import com.school.schoolmanagement.Accountant.FeeInvoice.ActivityAccountantFeesRecord;
import com.school.schoolmanagement.Accountant.FeeInvoice.ActivityGenerateFeesInvoice;
import com.school.schoolmanagement.Accountant.IncomeOrExpense.ActivityAccountantAccountStatement;
import com.school.schoolmanagement.Accountant.IncomeOrExpense.ActivityAddIncomeOrExpense;
import com.school.schoolmanagement.Accountant.Profile.ActivityAccountantProfile;
import com.school.schoolmanagement.Admin.Accounts.Income.ActivityAddIncome;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.UpdateLoginDetails.ActivityUpdateLoginCredentials;
import com.school.schoolmanagement.databinding.ActivityAccountantDashboardBinding;

public class ActivityAccountantDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    ActivityAccountantDashboardBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityAccountantDashboardBinding.inflate(getLayoutInflater());
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = null;

        int id = item.getItemId();

        if (id == R.id.nav_accountant_dashboard) {
            // Already on dashboard, just close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_accountant_add_income) {
            intent = new Intent(this, ActivityAddIncomeOrExpense.class);
            // Add flag to indicate income mode
            intent.putExtra("MODE", "INCOME");
        } else if (id == R.id.nav_accountant_add_expense) {
            intent = new Intent(this, ActivityAddIncomeOrExpense.class);
            // Add flag to indicate expense mode
            intent.putExtra("MODE", "EXPENSE");
        } else if (id == R.id.nav_accountant_profile) {
            intent = new Intent(this, ActivityAccountantProfile.class);
        } else if (id == R.id.nav_accountant_collect_fees) {
            intent = new Intent(this, ActivityAccountantCollectFees.class);
        } else if (id == R.id.nav_accountant_fees_record) {
            intent = new Intent(this, ActivityAccountantFeesRecord.class);
        } else if (id == R.id.nav_accountant_account_statement) {
            intent = new Intent(this, ActivityAccountantAccountStatement.class);
        } else if (id == R.id.nav_accountant_generate_invoice) {
            intent = new Intent(this, ActivityGenerateFeesInvoice.class);
        } else if (id == R.id.nav_accountant_account_settings) {
            intent = new Intent(this, ActivityUpdateLoginCredentials.class);
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}