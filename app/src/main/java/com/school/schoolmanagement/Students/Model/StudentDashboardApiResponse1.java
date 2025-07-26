package com.school.schoolmanagement.Students.Model;

import java.util.ArrayList;

public class StudentDashboardApiResponse1 {
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

    public int status;
    public String message;
    public Data data;

    public class Data{
        public School getSchool() {
            return school;
        }

        public void setSchool(School school) {
            this.school = school;
        }

        public Student getStudent() {
            return student;
        }

        public void setStudent(Student student) {
            this.student = student;
        }

        public Attendance getAttendance() {
            return attendance;
        }

        public void setAttendance(Attendance attendance) {
            this.attendance = attendance;
        }

        public ArrayList<Examination> getExaminations() {
            return examinations;
        }

        public void setExaminations(ArrayList<Examination> examinations) {
            this.examinations = examinations;
        }

        public School school;
        public Student student;
        public Attendance attendance;
        public ArrayList<Examination> examinations;
    }

    public class School{
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

        public String name;
        public String logo;
        public String slogan;
    }

    public class Student{
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

        public String getFatherName() {
            return fatherName;
        }

        public void setFatherName(String fatherName) {
            this.fatherName = fatherName;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public int id;
        public String name;
        public String fatherName;
        public String className;
        public String profileImage;
    }

    public class Yearly{
        public int getPresentDays() {
            return presentDays;
        }

        public void setPresentDays(int presentDays) {
            this.presentDays = presentDays;
        }

        public int getAbsentDays() {
            return absentDays;
        }

        public void setAbsentDays(int absentDays) {
            this.absentDays = absentDays;
        }

        public int getLeaveDays() {
            return leaveDays;
        }

        public void setLeaveDays(int leaveDays) {
            this.leaveDays = leaveDays;
        }

        public int getWorkingDays() {
            return workingDays;
        }

        public void setWorkingDays(int workingDays) {
            this.workingDays = workingDays;
        }

        public int presentDays;
        public int absentDays;
        public int leaveDays;
        public int workingDays;
    }
    public class Examination{
        public String getExamName() {
            return examName;
        }

        public void setExamName(String examName) {
            this.examName = examName;
        }

        public int getTotalMarks() {
            return totalMarks;
        }

        public void setTotalMarks(int totalMarks) {
            this.totalMarks = totalMarks;
        }

        public int getObtainedMarks() {
            return obtainedMarks;
        }

        public void setObtainedMarks(int obtainedMarks) {
            this.obtainedMarks = obtainedMarks;
        }

        public String examName;
        public int totalMarks;
        public int obtainedMarks;
    }

    public class Monthly{
        public int getPresentDays() {
            return presentDays;
        }

        public void setPresentDays(int presentDays) {
            this.presentDays = presentDays;
        }

        public int getAbsentDays() {
            return absentDays;
        }

        public void setAbsentDays(int absentDays) {
            this.absentDays = absentDays;
        }

        public int getLeaveDays() {
            return leaveDays;
        }

        public void setLeaveDays(int leaveDays) {
            this.leaveDays = leaveDays;
        }

        public int getWorkingDays() {
            return workingDays;
        }

        public void setWorkingDays(int workingDays) {
            this.workingDays = workingDays;
        }

        public int presentDays;
        public int absentDays;
        public int leaveDays;
        public int workingDays;
    }
    public class Attendance{
        public double getCurrentMonthPercent() {
            return currentMonthPercent;
        }

        public void setCurrentMonthPercent(double currentMonthPercent) {
            this.currentMonthPercent = currentMonthPercent;
        }

        public double getCurrentYearPercent() {
            return currentYearPercent;
        }

        public void setCurrentYearPercent(double currentYearPercent) {
            this.currentYearPercent = currentYearPercent;
        }

        public Monthly getMonthly() {
            return monthly;
        }

        public void setMonthly(Monthly monthly) {
            this.monthly = monthly;
        }

        public Yearly getYearly() {
            return yearly;
        }

        public void setYearly(Yearly yearly) {
            this.yearly = yearly;
        }

        public double currentMonthPercent;
        public double currentYearPercent;
        public Monthly monthly;
        public Yearly yearly;
    }
}
