package com.school.schoolmanagement.Admin.Model;

import java.io.Serializable;

public class Class2 implements Serializable {
    public Class2(String className,String section) {
        this.className = className;
        this.section=section;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String className;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String section;
}
