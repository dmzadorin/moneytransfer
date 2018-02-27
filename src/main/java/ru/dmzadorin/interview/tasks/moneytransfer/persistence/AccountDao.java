package ru.dmzadorin.interview.tasks.moneytransfer.persistence;

import ru.dmzadorin.interview.tasks.moneytransfer.model.Account;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.AccountNotFoundException;

import java.math.BigDecimal;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public interface AccountDao {
    Long saveAccount(String fullName, BigDecimal amount, Currency currency);

    void updateAmount(long id, BigDecimal amount);

    Account getAccountById(long accountId) throws AccountNotFoundException;
}
