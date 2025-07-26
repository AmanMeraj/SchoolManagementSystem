package com.school.schoolmanagement.Admin.Model;

import java.util.ArrayList;

public class AccountReport {
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
        public ArrayList<Record> getRecords() {
            return records;
        }

        public void setRecords(ArrayList<Record> records) {
            this.records = records;
        }

        public Pagination getPagination() {
            return pagination;
        }

        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }

        public ArrayList<Record> records;
        public Pagination pagination;
    }
}
