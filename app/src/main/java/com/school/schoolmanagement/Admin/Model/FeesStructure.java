package com.school.schoolmanagement.Admin.Model;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

public class FeesStructure {
    @SerializedName("feesStructure")
    private Map<String, String> feesStructure = new HashMap<>();

    public FeesStructure() {}

    // Get the fees structure map
    public Map<String, String> getFeesStructure() {
        return feesStructure != null ? feesStructure : new HashMap<>();
    }

    public void setFeesStructure(Map<String, String> feesStructure) {
        this.feesStructure = feesStructure;
    }

    // Generic method to get any fee by name
    public String getFee(String feeType) {
        return getFeesStructure().getOrDefault(feeType, "0");
    }

    // Generic method to set any fee
    public void setFee(String feeType, String amount) {
        if (this.feesStructure == null) {
            this.feesStructure = new HashMap<>();
        }
        this.feesStructure.put(feeType, amount);
    }

    // Convenience methods for common fees (optional)
    public String getAdmissionFees() {
        return getFee("admissionFees");
    }

    public String getTuitionFees() {
        // Handle both "tuitionFees" and "tutionfees"
        String tuitionFees = getFee("tuitionFees");
        if ("0".equals(tuitionFees)) {
            tuitionFees = getFee("tutionfees");
        }
        return tuitionFees;
    }

    public String getExaminationFees() {
        return getFee("examinationFees");
    }

    // Add a new fee dynamically
    public void addFee(String feeType, String amount) {
        setFee(feeType, amount);
    }

    // Remove a fee
    public void removeFee(String feeType) {
        if (this.feesStructure != null) {
            this.feesStructure.remove(feeType);
        }
    }

    // Get all fee types
    public java.util.Set<String> getAllFeeTypes() {
        return getFeesStructure().keySet();
    }

    // Calculate total fees
    public double getTotalFees() {
        return getFeesStructure().values().stream()
                .mapToDouble(amount -> {
                    try {
                        return Double.parseDouble(amount);
                    } catch (NumberFormatException e) {
                        return 0.0;
                    }
                })
                .sum();
    }

    // Check if a specific fee exists
    public boolean hasFee(String feeType) {
        return getFeesStructure().containsKey(feeType);
    }

    @Override
    public String toString() {
        return "FeesStructure{" +
                "feesStructure=" + feesStructure +
                ", totalFees=" + getTotalFees() +
                '}';
    }
}