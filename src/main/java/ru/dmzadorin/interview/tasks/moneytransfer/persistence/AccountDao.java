package ru.dmzadorin.interview.tasks.moneytransfer.persistence;

import ru.dmzadorin.interview.tasks.moneytransfer.model.Account;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.EntityNotFoundException;

import java.math.BigDecimal;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public interface AccountDao {
    /**
     * Creates new account with specified params
     *
     * @param fullName       account name
     * @param initialBalance initial ammount for account
     * @param currency       account currency
     * @return newly created account
     */
    Account createAccount(String fullName, BigDecimal initialBalance, Currency currency);

    /**
     * Searches account by id. If account with specified id not found throws EntityNotFoundException
     *
     * @param accountId id to search
     * @return account
     * @throws EntityNotFoundException If account with specified id not found
     */
    Account getAccountById(long accountId) throws EntityNotFoundException;
}
