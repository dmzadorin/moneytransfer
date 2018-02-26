package ru.dmzadorin.interview.tasks.moneytransfer.model.request;

/**
 * Created by Dmitry Zadorin on 17.02.2018
 */
public class AccountCreateRequest {
    private String fullName;
    private double initialBalance;
    private String currency;

    public AccountCreateRequest() {

    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public double getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(double initialBalance) {
        this.initialBalance = initialBalance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
