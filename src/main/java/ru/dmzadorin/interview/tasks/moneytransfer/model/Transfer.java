package ru.dmzadorin.interview.tasks.moneytransfer.model;

import java.math.BigDecimal;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public class Transfer {
    private final long transferId;
    private final long sourceAccountId;
    private final long recipientAccountId;
    private final BigDecimal amount;
    private final Currency currency;
    private final long timeStamp;

    public Transfer(long transferId, long sourceAccountId, long recipientAccountId, BigDecimal amount, Currency currency) {
        this.transferId = transferId;
        this.sourceAccountId = sourceAccountId;
        this.recipientAccountId = recipientAccountId;
        this.amount = amount;
        this.currency = currency;
        this.timeStamp = System.currentTimeMillis();
    }

    public long getTransferId() {
        return transferId;
    }

    public long getSourceAccountId() {
        return sourceAccountId;
    }

    public long getRecipientAccountId() {
        return recipientAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
