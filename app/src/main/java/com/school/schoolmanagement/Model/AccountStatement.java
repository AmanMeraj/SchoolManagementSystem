package com.school.schoolmanagement.Model;

import java.util.ArrayList;

public class AccountStatement {
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
        public ArrayList<Income> getIncomes() {
            return incomes;
        }

        public void setIncomes(ArrayList<Income> incomes) {
            this.incomes = incomes;
        }

        public ArrayList<Expense> getExpenses() {
            return expenses;
        }

        public void setExpenses(ArrayList<Expense> expenses) {
            this.expenses = expenses;
        }

        public double getTotalIncome() {
            return totalIncome;
        }

        public void setTotalIncome(double totalIncome) {
            this.totalIncome = totalIncome;
        }

        public double getTotalExpense() {
            return totalExpense;
        }

        public void setTotalExpense(double totalExpense) {
            this.totalExpense = totalExpense;
        }

        public double getTotalBalance() {
            return totalBalance;
        }

        public void setTotalBalance(double totalBalance) {
            this.totalBalance = totalBalance;
        }

        public ArrayList<Income> incomes;
        public ArrayList<Expense> expenses;
        public double totalIncome;
        public double totalExpense;
        public double totalBalance;
    }
}
