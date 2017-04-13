package com.business.peter.shoeseller.adapter;

import com.business.peter.shoeseller.model.ListData;

/**
 * Created by Victor on 01/03/2017.
 */
public class Expense extends ListData {
    String expense;
    String date_incurred;
    String amount;
    String expId;

    public Expense() {
    }

    public Expense(String expense, String date_incurred, String amount, String expId) {
        this.expense = expense;
        this.date_incurred = date_incurred;
        this.amount = amount;
        this.expId = expId;
    }

    public String getExpense() {
        return expense;
    }

    public void setExpense(String expense) {
        this.expense = expense;
    }

    public String getDate_incurred() {
        return date_incurred;
    }

    public void setDate_incurred(String date_incurred) {
        this.date_incurred = date_incurred;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getExpId() {
        return expId;
    }

    public void setExpId(String expId) {
        this.expId = expId;
    }
}
