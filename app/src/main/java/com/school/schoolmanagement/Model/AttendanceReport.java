package com.school.schoolmanagement.Model;

public class AttendanceReport {
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data data;
    public String message;
    public String status;

    public class Data{
        public ReportInfo getReportInfo() {
            return reportInfo;
        }

        public void setReportInfo(ReportInfo reportInfo) {
            this.reportInfo = reportInfo;
        }

        public CalendarInfo getCalendarInfo() {
            return calendarInfo;
        }

        public void setCalendarInfo(CalendarInfo calendarInfo) {
            this.calendarInfo = calendarInfo;
        }

        public StudentsAttendance getStudentsAttendance() {
            return studentsAttendance;
        }

        public void setStudentsAttendance(StudentsAttendance studentsAttendance) {
            this.studentsAttendance = studentsAttendance;
        }

        public ClassStatistics getClassStatistics() {
            return classStatistics;
        }

        public void setClassStatistics(ClassStatistics classStatistics) {
            this.classStatistics = classStatistics;
        }

        public Legend getLegend() {
            return legend;
        }

        public void setLegend(Legend legend) {
            this.legend = legend;
        }

        public ReportInfo reportInfo;
        public CalendarInfo calendarInfo;
        public StudentsAttendance studentsAttendance;
        public ClassStatistics classStatistics;
        public Legend legend;
    }
}
