package com.school.schoolmanagement.Model;

public class ClassStatistics {
    public int getTotalPresentDays() {
        return totalPresentDays;
    }

    public void setTotalPresentDays(int totalPresentDays) {
        this.totalPresentDays = totalPresentDays;
    }

    public int getTotalAbsentDays() {
        return totalAbsentDays;
    }

    public void setTotalAbsentDays(int totalAbsentDays) {
        this.totalAbsentDays = totalAbsentDays;
    }

    public int getTotalLateDays() {
        return totalLateDays;
    }

    public void setTotalLateDays(int totalLateDays) {
        this.totalLateDays = totalLateDays;
    }

    public int getTotalHalfDays() {
        return totalHalfDays;
    }

    public void setTotalHalfDays(int totalHalfDays) {
        this.totalHalfDays = totalHalfDays;
    }

    public double getAverageAttendancePercentage() {
        return averageAttendancePercentage;
    }

    public void setAverageAttendancePercentage(double averageAttendancePercentage) {
        this.averageAttendancePercentage = averageAttendancePercentage;
    }

    public int getStudentsWithPerfectAttendance() {
        return studentsWithPerfectAttendance;
    }

    public void setStudentsWithPerfectAttendance(int studentsWithPerfectAttendance) {
        this.studentsWithPerfectAttendance = studentsWithPerfectAttendance;
    }

    public int getStudentsBelow75Percent() {
        return studentsBelow75Percent;
    }

    public void setStudentsBelow75Percent(int studentsBelow75Percent) {
        this.studentsBelow75Percent = studentsBelow75Percent;
    }

    public double getHighestAttendancePercentage() {
        return highestAttendancePercentage;
    }

    public void setHighestAttendancePercentage(double highestAttendancePercentage) {
        this.highestAttendancePercentage = highestAttendancePercentage;
    }

    public double getLowestAttendancePercentage() {
        return lowestAttendancePercentage;
    }

    public void setLowestAttendancePercentage(double lowestAttendancePercentage) {
        this.lowestAttendancePercentage = lowestAttendancePercentage;
    }

    public int totalPresentDays;
    public int totalAbsentDays;
    public int totalLateDays;
    public int totalHalfDays;
    public double averageAttendancePercentage;
    public int studentsWithPerfectAttendance;
    public int studentsBelow75Percent;
    public double highestAttendancePercentage;
    public double lowestAttendancePercentage;
}
