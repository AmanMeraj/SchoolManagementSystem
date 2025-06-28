package com.school.schoolmanagement.Admin.Model;

public class FeesParticular {
    private String feesName;
    private String amount;
    private boolean isDefault; // To identify default fees


    public FeesParticular(String feesName, String amount, boolean isDefault) {
        this.feesName = feesName;
        this.amount = amount;
        this.isDefault = isDefault;
    }

    public String getFeesName() {
        return feesName;
    }

    public void setFeesName(String feesName) {
        this.feesName = feesName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return "FeesParticular{" +
                "feesName='" + feesName + '\'' +
                ", amount='" + amount + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}