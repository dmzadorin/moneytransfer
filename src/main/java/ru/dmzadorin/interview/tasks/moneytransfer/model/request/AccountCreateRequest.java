package ru.dmzadorin.interview.tasks.moneytransfer.model.request;

import com.google.common.base.MoreObjects;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.CurrencyNotSupportedException;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.NegativeAmountException;

/**
 * Created by Dmitry Zadorin on 17.02.2018
 */
public class AccountCreateRequest {
    private String fullName;
    private double initialBalance;
    private String currency;

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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fullName", fullName)
                .add("initialBalance", initialBalance)
                .add("currency", currency)
                .toString();
    }

    public void validate() {
        if (initialBalance < 0.0) {
            throw new NegativeAmountException("Account initial balance cannot be less then zero, value: " + initialBalance);
        }
        if (Currency.from(currency) == Currency.NOT_PRESENT) {
            throw new CurrencyNotSupportedException(currency);
        }
    }
}
