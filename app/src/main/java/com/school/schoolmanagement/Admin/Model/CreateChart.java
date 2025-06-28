package com.school.schoolmanagement.Admin.Model;

import java.io.Serializable;

public class CreateChart implements Serializable {
    public String getChartName() {
        return chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public String chartName;
    public String chartType;
}
