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
public class Account {
    private long id;
    private String fullName;
    private BigDecimal amount;
    private Currency currency;

    //We have to setup custom serializer/deserializer since jackson 2.8.4 does not support LocalDateTime class
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    public Account() {
    }

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

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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
        Account account = (Account) o;
        return id == account.id &&
                Objects.equals(fullName, account.fullName) &&
                Objects.equals(amount, account.amount) &&
                currency == account.currency &&
                Objects.equals(createTime, account.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, amount, currency, createTime);
    }
}
