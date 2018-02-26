package ru.dmzadorin.interview.tasks.moneytransfer.service;

import ru.dmzadorin.interview.tasks.moneytransfer.model.Account;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Transfer;

import java.math.BigDecimal;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public interface MoneyTransferService {
    Account saveAccount(String fullName, BigDecimal amount, Currency currency);

    Account getAccountById(long accountId);

    Transfer withdrawAmount(long sourceId, long recipientId, BigDecimal amount, Currency currency);
}
