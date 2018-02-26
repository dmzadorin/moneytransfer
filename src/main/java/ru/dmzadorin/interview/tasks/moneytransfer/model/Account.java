package ru.dmzadorin.interview.tasks.moneytransfer.model;


import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public class Account {
    private final long id;
    private final String fullName;
    private final Currency currency;
    private final LocalDateTime createTime;
    private BigDecimal amount;

    public Account(long id, String fullName, BigDecimal amount, Currency currency, LocalDateTime createTime) {
        this.id = id;
        this.fullName = fullName;
        this.amount = amount;
        this.currency = currency;
        this.createTime = createTime;
    }

    public long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void executeWithdraw(Account another, BigDecimal withdrawAmount) {
        amount = amount.subtract(withdrawAmount);
        another.amount = another.amount.add(withdrawAmount);
    }
}
