package com.school.schoolmanagement.Admin.Model;

public class FeeItem {
    private int itemNumber;
    private String feeName;
    private String feeAmount;

    public FeeItem(int itemNumber, String feeName, String feeAmount) {
        this.itemNumber = itemNumber;
        this.feeName = feeName;
        this.feeAmount = feeAmount;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public String getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(String feeAmount) {
        this.feeAmount = feeAmount;
    }
}
