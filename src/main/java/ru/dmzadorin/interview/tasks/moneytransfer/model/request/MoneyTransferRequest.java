package ru.dmzadorin.interview.tasks.moneytransfer.model.request;

import com.google.common.base.MoreObjects;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.CurrencyNotSupportedException;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.NegativeAmountException;

/**
 * Created by Dmitry Zadorin on 17.02.2018
 */
public class MoneyTransferRequest {
    private long sourceAccount;
    private long recipientAccount;
    private double amount;
    private String currency;

    public long getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(long sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public long getRecipientAccount() {
        return recipientAccount;
    }

    public void setRecipientAccount(long recipientAccount) {
        this.recipientAccount = recipientAccount;
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("sourceAccount", sourceAccount)
                .add("recipientAccount", recipientAccount)
                .add("amount", amount)
                .add("currency", currency)
                .toString();
    }

    public void validate() {
        if (amount < 0.0) {
            throw new NegativeAmountException("Transfer amount cannot be less then zero, value: " + amount);
        }
        if (Currency.from(currency) == Currency.NOT_PRESENT) {
            throw new CurrencyNotSupportedException(currency);
        }
    }
}
