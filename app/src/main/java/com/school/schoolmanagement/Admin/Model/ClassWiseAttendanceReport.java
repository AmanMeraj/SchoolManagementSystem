package com.school.schoolmanagement.Admin.Model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ClassWiseAttendanceReport {
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

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }

    public int status;
    public String message;
    public ArrayList<Datum> data;

    public static class Datum{
        public int getAbsent() {
            return absent;
        }

        public void setAbsent(int absent) {
            this.absent = absent;
        }

        public String getMyclass() {
            return myclass;
        }

        public void setMyclass(String myclass) {
            this.myclass = myclass;
        }

        public int getLeave() {
            return leave;
        }

        public void setLeave(int leave) {
            this.leave = leave;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getPresent() {
            return present;
        }

        public void setPresent(int present) {
            this.present = present;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int absent;
        @SerializedName("class")
        public String myclass;
        public int leave;
        public String date;
        public int present;
        public int total;
    }
}
