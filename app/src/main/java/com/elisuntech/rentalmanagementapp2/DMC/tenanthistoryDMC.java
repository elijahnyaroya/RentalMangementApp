package com.elisuntech.rentalmanagementapp2.DMC;

public class tenanthistoryDMC {
    String amount;
    String datedue;
    String dayspassed;
    String id;
    String paidamount;

    public tenanthistoryDMC(String amount, String datedue, String dayspassed, String id, String paidamount) {
        this.amount = amount;
        this.datedue = datedue;
        this.dayspassed = dayspassed;
        this.id = id;
        this.paidamount = paidamount;
    }

    public String getAmount() {
        return amount;
    }

    public String getDatedue() {
        return datedue;
    }

    public String getDayspassed() {
        return dayspassed;
    }

    public String getId() {
        return id;
    }

    public String getPaidamount() {
        return paidamount;
    }
}
