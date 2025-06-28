package com.school.schoolmanagement.Admin.Model;


import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

// Main request model
public class FeesStructureRequest {
    @SerializedName("feesStructure")
    private FeesStructure feesStructure;

    public FeesStructureRequest() {
        this.feesStructure = new FeesStructure();
    }

    public FeesStructure getFeesStructure() {
        return feesStructure;
    }

    public void setFeesStructure(FeesStructure feesStructure) {
        this.feesStructure = feesStructure;
    }
}

// Fees structure model

