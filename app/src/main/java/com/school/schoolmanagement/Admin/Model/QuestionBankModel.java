package com.school.schoolmanagement.Admin.Model;

import java.util.ArrayList;

public class QuestionBankModel {
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
        public ArrayList<Entry> getEntries() {
            return entries;
        }

        public void setEntries(ArrayList<Entry> entries) {
            this.entries = entries;
        }

        public Pagination getPagination() {
            return pagination;
        }

        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }

        public ArrayList<Entry> entries;
        public Pagination pagination;
    }
}
