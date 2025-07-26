package com.school.schoolmanagement.Model;

import java.util.ArrayList;

public class CalendarInfo {
    public ArrayList<Integer> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(ArrayList<Integer> workingDays) {
        this.workingDays = workingDays;
    }

    public ArrayList<Integer> getHolidays() {
        return holidays;
    }

    public void setHolidays(ArrayList<Integer> holidays) {
        this.holidays = holidays;
    }

    public ArrayList<Integer> getWeekendDays() {
        return weekendDays;
    }

    public void setWeekendDays(ArrayList<Integer> weekendDays) {
        this.weekendDays = weekendDays;
    }

    public DayNamesMap getDayNamesMap() {
        return dayNamesMap;
    }

    public void setDayNamesMap(DayNamesMap dayNamesMap) {
        this.dayNamesMap = dayNamesMap;
    }

    public ArrayList<Integer> workingDays;
    public ArrayList<Integer> holidays;
    public ArrayList<Integer> weekendDays;
    public DayNamesMap dayNamesMap;
}
