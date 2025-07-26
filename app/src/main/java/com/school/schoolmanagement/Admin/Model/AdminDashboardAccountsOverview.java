package com.school.schoolmanagement.Admin.Model;

import java.util.ArrayList;

public class AdminDashboardAccountsOverview {
    public ArrayList<MonthlyRevenue> getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(ArrayList<MonthlyRevenue> monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public ArrayList<MonthlyRevenue> monthlyRevenue;
    public double totalRevenue;

    public class MonthlyRevenue{
        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public double getRevenue() {
            return revenue;
        }

        public void setRevenue(double revenue) {
            this.revenue = revenue;
        }

        public int month;
        public int year;
        public double revenue;
    }
}
