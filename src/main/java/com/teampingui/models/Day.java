package com.teampingui.models;

import java.time.DayOfWeek;

public enum Day {

    MONDAY(DayOfWeek.MONDAY, "Monday"),
    TUESDAY(DayOfWeek.TUESDAY, "Tuesday"),
    WEDNESDAY(DayOfWeek.WEDNESDAY, "Wednesday"),
    THURSDAY(DayOfWeek.THURSDAY, "Thursday"),
    FRIDAY(DayOfWeek.FRIDAY, "Friday"),
    SATURDAY(DayOfWeek.SATURDAY, "Saturday"),
    SUNDAY(DayOfWeek.SUNDAY, "Sunday");

    final DayOfWeek mDayOfWeek;
    final String mDay;

    Day(DayOfWeek dayOfWeek, String day) {
        this.mDayOfWeek = dayOfWeek;
        this.mDay = day;
    }

    public DayOfWeek getDayOfWeek() {
        return mDayOfWeek;
    }

    public String getShortDay() {
        return mDay.substring(0, 3);
    }

    public String getDay() {
        return mDay;
    }
}
