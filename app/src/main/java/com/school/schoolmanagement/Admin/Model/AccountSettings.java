package com.school.schoolmanagement.Admin.Model;

public class AccountSettings {
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String userName;
    public String password;
    public String timezone;
    public String currency;

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String subscription;

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String expiry;
    public String symbolOfCurrency;
}
