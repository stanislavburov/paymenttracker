package ru.bsv.paymenttracker;

public class IntervalStats {
    private String startDate;
    private long daysPassed;
    private String avgDailyExpense;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public long getDaysPassed() {
        return daysPassed;
    }

    public void setDaysPassed(long daysPassed) {
        this.daysPassed = daysPassed;
    }

    public String getAvgDailyExpense() {
        return avgDailyExpense;
    }

    public void setAvgDailyExpense(String avgDailyExpense) {
        this.avgDailyExpense = avgDailyExpense;
    }
}
