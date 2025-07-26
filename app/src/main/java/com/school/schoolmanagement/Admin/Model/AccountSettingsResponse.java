package com.school.schoolmanagement.Admin.Model;

public class AccountSettingsResponse {
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
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUserName() {
            return username;
        }

        public void setUserName(String userName) {
            this.username = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getSymbolOfCurrency() {
            return symbolOfCurrency;
        }

        public void setSymbolOfCurrency(String symbolOfCurrency) {
            this.symbolOfCurrency = symbolOfCurrency;
        }

        public int id;
        public String username;
        public String password;
        public String timezone;
        public String currency;
        public String symbolOfCurrency;
    }
}
