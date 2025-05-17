package com.school.schoolmanagement.SuperAdmin.SuperDashboard;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.SuperAdmin.Branches.ActivityAddNewBranch;
import com.school.schoolmanagement.SuperAdmin.Branches.ActivityAdminLogin;
import com.school.schoolmanagement.SuperAdmin.Branches.ActivityAllBranches;
import com.school.schoolmanagement.databinding.ActivitySuperAdminDashboardBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.formatter.PercentFormatter;
import java.util.ArrayList;
import java.util.List;

public class ActivitySuperAdminDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
ActivitySuperAdminDashboardBinding binding;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySuperAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbarAndDrawer();
        setupPieChart();
        setupDatePicker();
        setupYearPicker();
        setupLineChart();
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
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        // Set the default selected item
        navigationView.setCheckedItem(R.id.nav_super_admin_dashboard);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here
        int id = item.getItemId();

        if (id == R.id.nav_super_admin_dashboard) {
            // Already on dashboard, no need to do anything
            Toast.makeText(this, "Dashboard Selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_super_admin_all_branches) {

             Intent intent = new Intent(this, ActivityAllBranches.class);
             startActivity(intent);
        } else if (id == R.id.nav_super_admin_add_school) {
             Intent intent = new Intent(this, ActivityAddNewBranch.class);
             startActivity(intent);
        }else if (id == R.id.nav_super_admin_manage_login) {
             Intent intent = new Intent(this, ActivityAdminLogin.class);
             startActivity(intent);
        }

        // Close the drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        // Close drawer if open, otherwise proceed with normal back action
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    // In your activity or fragment
    // In your activity or fragment
    private void setupDatePicker() {
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.date_picker_auto_complete);

        // Generate list of dates (months and years)
        List<String> dateOptions = generateDateOptions();

        // Create adapter for the AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, dateOptions);
        autoCompleteTextView.setAdapter(adapter);

        // Set default value (current month and year)
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String currentMonthYear = dateFormat.format(calendar.getTime());
        autoCompleteTextView.setText(currentMonthYear, false);

        // Handle selection
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDate = dateOptions.get(position);
            // Do something with the selected date
            // For example, update your chart
            updateChartForDate(selectedDate);
        });
    }

    // Generate a list of month-year options (e.g., "January 2025", "February 2025", etc.)
    private List<String> generateDateOptions() {
        List<String> options = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

        // Add current month and previous 11 months (for a total of 12 months)
        for (int i = 0; i < 12; i++) {
            options.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.MONTH, -1); // Go back one month
        }

        return options;
    }

    private void updateChartForDate(String dateString) {
        // Update your chart or data based on the selected date
        // This is where you would refresh your pie chart with new data
    }

    private void setupPieChart() {
        PieChart pieChart = findViewById(R.id.income_expense_pie_chart);

        // Configure the pie chart
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("Income vs Expense");
        pieChart.setCenterTextSize(11f);
        pieChart.setRotationAngle(75);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.animateY(1400);
        pieChart.setEntryLabelColor(getResources().getColor(R.color.white));
        pieChart.setEntryLabelTextSize(12f);

        // Add data to chart
        updatePieChartData(pieChart, 1200f, 200f);
    }

    private void updatePieChartData(PieChart pieChart, float income, float expense) {
        List<PieEntry> entries = new ArrayList<>();

        // Only add entries with values > 0
        if (income > 0) {
            entries.add(new PieEntry(income, "Income"));
        }

        if (expense > 0) {
            entries.add(new PieEntry(expense, "Expense"));
        }

        // If both values are 0, add placeholder entries
        if (entries.isEmpty()) {
            entries.add(new PieEntry(1, "No Data"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");

        // Set colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.light_blue)); // Green for income
        colors.add(getResources().getColor(R.color.orange));  // Red for expense
        colors.add(Color.LTGRAY);            // Gray for placeholder
        dataSet.setColors(colors);

        // Configure data set
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // Create and set data
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.invalidate(); // Refresh chart

        // Update indicator values
        updateIndicator(income, expense);
    }

    private void updateIndicator(float income, float expense) {
        TextView incomeText = findViewById(R.id.tv_indicator_income);
        TextView expenseText = findViewById(R.id.tv_indicator_expense);

        // Get current date or month number
        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Update indicator texts
        TextView dateText = findViewById(R.id.tv_indicator_date);
        dateText.setText(String.format("%02d", dayOfMonth));

        incomeText.setText("Income: " + String.format("%.0f", income));
        expenseText.setText("Expense: " + String.format("%.0f", expense));
    }


    private void setupLineChart() {
        LineChart lineChart = findViewById(R.id.income_expense_line_chart);
        TextView indicatorDate = findViewById(R.id.tv_indicator_date_line);
        TextView indicatorIncome = findViewById(R.id.tv_indicator_income_line);
        TextView indicatorExpense = findViewById(R.id.tv_indicator_expense_line);

        // Basic chart configuration
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setExtraOffsets(10f, 10f, 10f, 10f);

        // Set background color similar to cricket score charts
        lineChart.setBackgroundColor(Color.rgb(242, 242, 242));

        // Configure X axis (months)
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12f);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setAxisLineWidth(1.5f);

        // Configure X axis labels (months)
        final String[] months = new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));

        // Configure left Y axis (values)
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setAxisLineColor(Color.BLACK);
        leftAxis.setAxisLineWidth(1.5f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(12f);
        //leftAxis.setValueFormatter((value, axis) -> "$" + String.format(Locale.US, "%,.0f", value));        // Disable right Y axis
        lineChart.getAxisRight().setEnabled(false);

        // Setup Legend
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);
        legend.setTextColor(Color.BLACK);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        // Create and add data
        setLineChartData(lineChart);

        // Set up chart value selection listener
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int month = (int) e.getX();
                float value = e.getY();
                LineDataSet dataSet = (LineDataSet) lineChart.getData().getDataSetForEntry(e);
                String label = dataSet.getLabel();

                // Update indicator
                indicatorDate.setText(months[month]);

                if (label.equals("Income")) {
                    indicatorIncome.setText("Income: $" + String.format(Locale.US, "%,.0f", value));
                } else {
                    indicatorExpense.setText("Expense: $" + String.format(Locale.US, "%,.0f", value));
                }
            }

            @Override
            public void onNothingSelected() {
                // Reset indicator to default values
                indicatorDate.setText("Select point");
                indicatorIncome.setText("Income: -");
                indicatorExpense.setText("Expense: -");
            }
        });

        // Animate the chart
        lineChart.animateX(1500);
    }

    /**
     * Set up data for the line chart similar to IPL batting scores for two teams
     */
    private void setLineChartData(LineChart lineChart) {
        // Sample data - replace with your actual data from database
        float[] incomeData = new float[]{800, 1200, 950, 1400, 1100, 1350, 1550, 1300, 1450, 1650, 1750, 1900};
        float[] expenseData = new float[]{500, 550, 600, 750, 680, 820, 900, 850, 950, 1000, 1050, 1100};

        // Income entries (like first team)
        ArrayList<Entry> incomeEntries = new ArrayList<>();
        for (int i = 0; i < incomeData.length; i++) {
            incomeEntries.add(new Entry(i, incomeData[i]));
        }

        // Expense entries (like second team)
        ArrayList<Entry> expenseEntries = new ArrayList<>();
        for (int i = 0; i < expenseData.length; i++) {
            expenseEntries.add(new Entry(i, expenseData[i]));
        }

        // Create Income dataset - with blue color like Mumbai Indians
        LineDataSet incomeDataSet = new LineDataSet(incomeEntries, "Income");
        incomeDataSet.setColor(Color.rgb(0, 102, 204)); // Blue color
        incomeDataSet.setLineWidth(2.5f);
        incomeDataSet.setCircleColor(Color.rgb(0, 102, 204));
        incomeDataSet.setCircleRadius(5f);
        incomeDataSet.setFillColor(Color.rgb(0, 102, 204));
        incomeDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        incomeDataSet.setDrawValues(false);
        incomeDataSet.setValueTextSize(10f);
        incomeDataSet.setValueTextColor(Color.rgb(0, 102, 204));
        incomeDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        // Add gradient fill for Income
        incomeDataSet.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            // Fill drawable only supported on API level 18 and above
            Drawable incomeGradient = ContextCompat.getDrawable(this, R.drawable.fade_blue);
            incomeDataSet.setFillDrawable(incomeGradient);
        } else {
            incomeDataSet.setFillAlpha(50);
            incomeDataSet.setFillColor(Color.rgb(0, 102, 204));
        }

        // Create Expense dataset - with yellow color like Chennai Super Kings
        LineDataSet expenseDataSet = new LineDataSet(expenseEntries, "Expense");
        expenseDataSet.setColor(Color.rgb(255, 10, 0)); // Yellow color
        expenseDataSet.setLineWidth(2.5f);
        expenseDataSet.setCircleColor(Color.rgb(255, 10, 0));
        expenseDataSet.setCircleRadius(5f);
        expenseDataSet.setFillColor(Color.rgb(255, 204, 0));
        expenseDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        expenseDataSet.setDrawValues(false);
        expenseDataSet.setValueTextSize(10f);
        expenseDataSet.setValueTextColor(Color.rgb(255, 10, 0));
        expenseDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        // Add gradient fill for Expense
        expenseDataSet.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            // Fill drawable only supported on API level 18 and above
            Drawable expenseGradient = ContextCompat.getDrawable(this, R.drawable.fade_orange);
            expenseDataSet.setFillDrawable(expenseGradient);
        } else {
            expenseDataSet.setFillAlpha(50);
            expenseDataSet.setFillColor(Color.rgb(255, 10, 0));
        }

        // Add highlight lines and markers (like key moments in cricket)
        incomeDataSet.setHighlightLineWidth(1.5f);
        incomeDataSet.setHighLightColor(Color.rgb(0, 102, 204));
        incomeDataSet.setDrawHorizontalHighlightIndicator(true);
        incomeDataSet.setDrawVerticalHighlightIndicator(true);

        expenseDataSet.setHighlightLineWidth(1.5f);
        expenseDataSet.setHighLightColor(Color.rgb(255, 10, 0));
        expenseDataSet.setDrawHorizontalHighlightIndicator(true);
        expenseDataSet.setDrawVerticalHighlightIndicator(true);

        // Combine all datasets
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(incomeDataSet);
        dataSets.add(expenseDataSet);

        // Create and set data
        LineData data = new LineData(dataSets);
        lineChart.setData(data);

        // Refresh
        lineChart.invalidate();
    }

    /**
     * Method to update chart data with new values
     */
    public void updateChartData(float[] incomeValues, float[] expenseValues) {
        LineChart lineChart = findViewById(R.id.income_expense_line_chart);

        LineData data = lineChart.getData();
        if (data != null && data.getDataSetCount() >= 2) {
            LineDataSet incomeDataSet = (LineDataSet) data.getDataSetByIndex(0);
            LineDataSet expenseDataSet = (LineDataSet) data.getDataSetByIndex(1);

            if (incomeDataSet != null && expenseDataSet != null) {
                // Update income data
                for (int i = 0; i < incomeValues.length; i++) {
                    if (i < incomeDataSet.getEntryCount()) {
                        incomeDataSet.getEntryForIndex(i).setY(incomeValues[i]);
                    } else {
                        incomeDataSet.addEntry(new Entry(i, incomeValues[i]));
                    }
                }

                // Update expense data
                for (int i = 0; i < expenseValues.length; i++) {
                    if (i < expenseDataSet.getEntryCount()) {
                        expenseDataSet.getEntryForIndex(i).setY(expenseValues[i]);
                    } else {
                        expenseDataSet.addEntry(new Entry(i, expenseValues[i]));
                    }
                }

                data.notifyDataChanged();
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }
        }
    }

    /**
     * Setup year picker dropdown
     */
    private void setupYearPicker() {
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.date_picker_auto_complete_line);

        // Generate list of years (current year and previous 5 years)
        ArrayList<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 6; i++) {
            years.add(String.valueOf(currentYear - i));
        }

        // Create adapter for the AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, years);
        autoCompleteTextView.setAdapter(adapter);

        // Set default value (current year)
        autoCompleteTextView.setText(String.valueOf(currentYear), false);

        // Handle selection
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedYear = years.get(position);
            // Fetch and update chart data for the selected year
            loadDataForYear(selectedYear);
        });
    }

    /**
     * Load data for a selected year
     */
    private void loadDataForYear(String year) {
        // In a real app, you would fetch data for the selected year from your database
        // For demo, we'll simulate different data for different years
        int yearValue = Integer.parseInt(year);
        float multiplier = (yearValue % 5) * 0.2f + 0.8f;

        float[] incomeData = new float[]{
                800 * multiplier, 1200 * multiplier, 950 * multiplier,
                1400 * multiplier, 1100 * multiplier, 1350 * multiplier,
                1550 * multiplier, 1300 * multiplier, 1450 * multiplier,
                1650 * multiplier, 1750 * multiplier, 1900 * multiplier
        };

        float[] expenseData = new float[]{
                500 * multiplier, 550 * multiplier, 600 * multiplier,
                750 * multiplier, 680 * multiplier, 820 * multiplier,
                900 * multiplier, 850 * multiplier, 950 * multiplier,
                1000 * multiplier, 1050 * multiplier, 1100 * multiplier
        };

        updateChartData(incomeData, expenseData);

        // Update summary values at top of card
        updateSummaryValues(incomeData, expenseData);
    }

    /**
     * Update summary values in the grid layout
     */
    private void updateSummaryValues(float[] incomeData, float[] expenseData) {
        // Calculate totals
        float totalIncome = 0;
        float totalExpense = 0;

        for (float income : incomeData) {
            totalIncome += income;
        }

        for (float expense : expenseData) {
            totalExpense += expense;
        }

        float totalProfit = totalIncome - totalExpense;

        // Update UI elements
        TextView tvIncomeAmount = findViewById(R.id.tv_total_income_amount_line);
        TextView tvExpenseAmount = findViewById(R.id.tv_total_expense_amount_line);
        TextView tvProfitAmount = findViewById(R.id.tv_total_profit_amount_line);
        TextView tvRevenueAmount = findViewById(R.id.tv_total_profit_revenue_line);
        TextView tvWalletAmount = findViewById(R.id.tv_total_wallet_balance_amount_line);

        tvIncomeAmount.setText(String.format(Locale.US, "($) %,.0f", totalIncome));
        tvExpenseAmount.setText(String.format(Locale.US, "($) %,.0f", totalExpense));
        tvProfitAmount.setText(String.format(Locale.US, "($) %,.0f", totalProfit));
        tvRevenueAmount.setText(String.format(Locale.US, "($) %,.0f", totalIncome)); // Assuming revenue = income
        tvWalletAmount.setText(String.format(Locale.US, "($) %,.0f", totalProfit)); // Assuming wallet = profit
    }
}