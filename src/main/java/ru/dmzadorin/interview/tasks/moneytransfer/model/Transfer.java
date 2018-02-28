package ru.dmzadorin.interview.tasks.moneytransfer.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.dmzadorin.interview.tasks.moneytransfer.model.json.LocalDateTimeDeserializer;
import ru.dmzadorin.interview.tasks.moneytransfer.model.json.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public class Transfer {
    private long transferId;
    private long sourceAccountId;
    private long recipientAccountId;
    private BigDecimal amount;
    private Currency currency;

    //We have to setup custom serializer/deserializer since jackson 2.8.4 does not support LocalDateTime class
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    public Transfer() {
    }

    public Transfer(long transferId, long sourceAccountId, long recipientAccountId, BigDecimal amount, Currency currency, LocalDateTime createTime) {
        this.transferId = transferId;
        this.sourceAccountId = sourceAccountId;
        this.recipientAccountId = recipientAccountId;
        this.amount = amount;
        this.currency = currency;
        this.createTime = createTime;
    }

    public long getTransferId() {
        return transferId;
    }

    public void setTransferId(long transferId) {
        this.transferId = transferId;
    }

    public long getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public long getRecipientAccountId() {
        return recipientAccountId;
    }

    public void setRecipientAccountId(long recipientAccountId) {
        this.recipientAccountId = recipientAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return transferId == transfer.transferId &&
                sourceAccountId == transfer.sourceAccountId &&
                recipientAccountId == transfer.recipientAccountId &&
                Objects.equals(amount, transfer.amount) &&
                currency == transfer.currency &&
                Objects.equals(createTime, transfer.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transferId, sourceAccountId, recipientAccountId, amount, currency, createTime);
    }
}
