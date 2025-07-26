package com.school.schoolmanagement.Admin.Model;

public class AdminDashboardPresentPercent {
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
        public String getStudentPresentPercent() {
            return studentPresentPercent;
        }

        public void setStudentPresentPercent(String studentPresentPercent) {
            this.studentPresentPercent = studentPresentPercent;
        }

        public String getStaffPresentPercent() {
            return staffPresentPercent;
        }

        public void setStaffPresentPercent(String staffPresentPercent) {
            this.staffPresentPercent = staffPresentPercent;
        }

        public String getFeesCollectionPercent() {
            return feesCollectionPercent;
        }

        public void setFeesCollectionPercent(String feesCollectionPercent) {
            this.feesCollectionPercent = feesCollectionPercent;
        }

        public String studentPresentPercent;
        public String staffPresentPercent;
        public String feesCollectionPercent;
    }
}
