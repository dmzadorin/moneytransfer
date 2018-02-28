package ru.dmzadorin.interview.tasks.moneytransfer.service;

import ru.dmzadorin.interview.tasks.moneytransfer.model.Account;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Transfer;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.DifferentCurrenciesException;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.EntityNotFoundException;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public interface MoneyTransferService {
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

    /**
     * Creates new money transfer, adding amount to recipient account and subtracting that amount from source account.
     * Verifies that source and recipient account exist, checks that currency is the same and verifies that there is
     * enough amount on source account. If any of above verifications fails - throws an exception and cancels money transfer
     *
     * @param sourceId    source account id
     * @param recipientId recipient account id
     * @param amount      amount to transfer
     * @param currency    currency of transfer
     * @return newly created Transfer
     * @throws InsufficientFundsException   if there is not enough funds on source account
     * @throws DifferentCurrenciesException if one of currencies differ: source currency vs recipient currency, source vs transfer currency
     * @throws EntityNotFoundException      if source or recipient account is not found
     */
    Transfer transferMoney(long sourceId, long recipientId, BigDecimal amount, Currency currency) throws InsufficientFundsException, DifferentCurrenciesException, EntityNotFoundException;

    /**
     * Gets all created transfers
     *
     * @return
     */
    Collection<Transfer> getAllTransfers();

    /**
     * Searches transfer by id. If transfer with specified id not found throws EntityNotFoundException
     *
     * @param transferId id to search
     * @return transfer
     * @throws EntityNotFoundException If transfer with specified id not found
     */
    Transfer getTransferById(long transferId) throws EntityNotFoundException;
}
