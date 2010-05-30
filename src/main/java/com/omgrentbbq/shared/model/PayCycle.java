package com.omgrentbbq.shared.model;

/**
 * bill is due
 */
public enum PayCycle {
    Monthly("Day of the month"),
    Daily,
    Weekly("Day of the week"),
    BiWeekly("First Week of Month", "Day of the Week"),
    BiMonthly("First month of the year", "Day of the month"),
    Quarterly("First month of the year", "Day of the month"),
    DayOfTheYear("Day of the Year"),
    DateOnEachYear("Day of the Month", "Day of the year"),
    WeekdaysOfTheMonth("First week due of Month", "Day of the Week"),;

    public String[] scheduleOptionDescriptions;

    PayCycle(String... scheduleOptionDescriptions) {
        this.scheduleOptionDescriptions = scheduleOptionDescriptions;
    }
}
