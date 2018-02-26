package ru.dmzadorin.interview.tasks.moneytransfer.model.request;

/**
 * Created by Dmitry Zadorin on 17.02.2018
 */
public class MoneyTransferRequest {
    private long sourceAccount;
    private long targetAccount;
    private double amount;
    private String currency;

    public MoneyTransferRequest() {
    }

    public long getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(long sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public long getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(long targetAccount) {
        this.targetAccount = targetAccount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
