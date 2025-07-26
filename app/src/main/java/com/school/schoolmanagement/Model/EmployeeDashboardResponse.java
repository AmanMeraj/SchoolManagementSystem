package com.school.schoolmanagement.Model;

public class EmployeeDashboardResponse {
    private int status;
    private String message;
    private Data data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private School school;
        private Employee employee;
        private Attendance attendance;
        private Salary salary;

        public School getSchool() {
            return school;
        }

        public void setSchool(School school) {
            this.school = school;
        }

        public Employee getEmployee() {
            return employee;
        }

        public void setEmployee(Employee employee) {
            this.employee = employee;
        }

        public Attendance getAttendance() {
            return attendance;
        }

        public void setAttendance(Attendance attendance) {
            this.attendance = attendance;
        }

        public Salary getSalary() {
            return salary;
        }

        public void setSalary(Salary salary) {
            this.salary = salary;
        }
    }

    public static class Employee {
        private int id;
        private String name;
        private String profileImage;
        private String role;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class Salary {
        private double monthlySalary;
        private boolean isCurrentMonthSalaryReceived;
        private String lastSalary;

        public double getMonthlySalary() {
            return monthlySalary;
        }

        public void setMonthlySalary(double monthlySalary) {
            this.monthlySalary = monthlySalary;
        }

        public boolean isCurrentMonthSalaryReceived() {
            return isCurrentMonthSalaryReceived;
        }

        public void setCurrentMonthSalaryReceived(boolean currentMonthSalaryReceived) {
            isCurrentMonthSalaryReceived = currentMonthSalaryReceived;
        }

        public String getLastSalary() {
            return lastSalary;
        }

        public void setLastSalary(String lastSalary) {
            this.lastSalary = lastSalary;
        }
    }

    public static class School {
        private String name;
        private String logo;
        private String slogan;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getSlogan() {
            return slogan;
        }

        public void setSlogan(String slogan) {
            this.slogan = slogan;
        }
    }

    public static class Attendance {
        private double currentMonthPercentage;
        private double currentYearPercentage;
        private int currentMonthPresentDays;
        private int currentMonthAbsentDays;
        private int currentMonthLeaveDays;
        private int currentMonthTotalWorkingDays;
        private int currentYearPresentDays;
        private int currentYearAbsentDays;
        private int currentYearLeaveDays;
        private int currentYearTotalWorkingDays;

        public double getCurrentMonthPercentage() {
            return currentMonthPercentage;
        }

        public void setCurrentMonthPercentage(double currentMonthPercentage) {
            this.currentMonthPercentage = currentMonthPercentage;
        }

        public double getCurrentYearPercentage() {
            return currentYearPercentage;
        }

        public void setCurrentYearPercentage(double currentYearPercentage) {
            this.currentYearPercentage = currentYearPercentage;
        }

        public int getCurrentMonthPresentDays() {
            return currentMonthPresentDays;
        }

        public void setCurrentMonthPresentDays(int currentMonthPresentDays) {
            this.currentMonthPresentDays = currentMonthPresentDays;
        }

        public int getCurrentMonthAbsentDays() {
            return currentMonthAbsentDays;
        }

        public void setCurrentMonthAbsentDays(int currentMonthAbsentDays) {
            this.currentMonthAbsentDays = currentMonthAbsentDays;
        }

        public int getCurrentMonthLeaveDays() {
            return currentMonthLeaveDays;
        }

        public void setCurrentMonthLeaveDays(int currentMonthLeaveDays) {
            this.currentMonthLeaveDays = currentMonthLeaveDays;
        }

        public int getCurrentMonthTotalWorkingDays() {
            return currentMonthTotalWorkingDays;
        }

        public void setCurrentMonthTotalWorkingDays(int currentMonthTotalWorkingDays) {
            this.currentMonthTotalWorkingDays = currentMonthTotalWorkingDays;
        }

        public int getCurrentYearPresentDays() {
            return currentYearPresentDays;
        }

        public void setCurrentYearPresentDays(int currentYearPresentDays) {
            this.currentYearPresentDays = currentYearPresentDays;
        }

        public int getCurrentYearAbsentDays() {
            return currentYearAbsentDays;
        }

        public void setCurrentYearAbsentDays(int currentYearAbsentDays) {
            this.currentYearAbsentDays = currentYearAbsentDays;
        }

        public int getCurrentYearLeaveDays() {
            return currentYearLeaveDays;
        }

        public void setCurrentYearLeaveDays(int currentYearLeaveDays) {
            this.currentYearLeaveDays = currentYearLeaveDays;
        }

        public int getCurrentYearTotalWorkingDays() {
            return currentYearTotalWorkingDays;
        }

        public void setCurrentYearTotalWorkingDays(int currentYearTotalWorkingDays) {
            this.currentYearTotalWorkingDays = currentYearTotalWorkingDays;
        }
    }
}
