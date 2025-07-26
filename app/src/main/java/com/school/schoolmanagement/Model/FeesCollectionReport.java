package com.school.schoolmanagement.Model;

import com.school.schoolmanagement.Admin.Model.Pagination;

import java.util.ArrayList;

public class FeesCollectionReport {
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
        public ArrayList<FeesCollectionEntries> getEntries() {
            return entries;
        }

        public void setEntries(ArrayList<FeesCollectionEntries> entries) {
            this.entries = entries;
        }

        public Pagination getPagination() {
            return pagination;
        }

        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }

        public ArrayList<FeesCollectionEntries> entries;
        public Pagination pagination;
    }
}
