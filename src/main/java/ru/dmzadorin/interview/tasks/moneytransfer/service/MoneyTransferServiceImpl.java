package ru.dmzadorin.interview.tasks.moneytransfer.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Account;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Transfer;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.AccountNotFoundException;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.DifferentCurrenciesException;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.InsufficientFundsException;
import ru.dmzadorin.interview.tasks.moneytransfer.persistence.AccountDao;
import ru.dmzadorin.interview.tasks.moneytransfer.persistence.TransferDao;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public class MoneyTransferServiceImpl implements MoneyTransferService {
    private static final Logger logger = LogManager.getLogger();

    private final Cache<Long, ReadWriteLock> accountLocks;
    private final AccountDao accountDao;
    private final TransferDao transferDao;

    @Inject
    public MoneyTransferServiceImpl(AccountDao accountDao, TransferDao transferDao) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        accountLocks = CacheBuilder.newBuilder().weakValues().build();
    }

    @Override
    public Account saveAccount(String fullName, BigDecimal amount, Currency currency) {
        Long accountId = requireNonNull(accountDao.saveAccount(fullName, amount, currency), "Account create failed");
        return requireNonNull(getAccountById(accountId), "Account with id " + accountId + " not found");
    }

    @Override
    public Account getAccountById(long accountId) {
        ReadWriteLock lock = getLockForAccount(accountId);
        try {
            lock.readLock().lock();
            return accountDao.getAccountById(accountId);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Transfer withdrawAmount(long sourceId, long recipientId, BigDecimal amount, Currency currency) {
        ReadWriteLock sourceLock = getLockForAccount(sourceId);
        ReadWriteLock recipientLock = getLockForAccount(recipientId);

        ReadWriteLock first;
        ReadWriteLock second;
        if (sourceId > recipientId) {
            first = sourceLock;
            second = recipientLock;
        } else {
            first = recipientLock;
            second = sourceLock;
        }

        try {
            first.writeLock().lock();
            second.writeLock().lock();
            return doCreateTransfer(sourceId, recipientId, amount, currency);
        } finally {
            second.writeLock().unlock();
            first.writeLock().unlock();
        }
    }

    private Transfer doCreateTransfer(long sourceId, long recipientId, BigDecimal amount, Currency currency) {
        Account sourceAccount = requireNonNull(accountDao.getAccountById(sourceId),
                "Source account " + sourceId + " not found");
        Account recipientAccount = requireNonNull(accountDao.getAccountById(recipientId),
                "Recipient account " + recipientId + " not found");
        validate(sourceAccount, recipientAccount, currency);
        Transfer transfer = transferDao.transferAmount(sourceAccount, recipientAccount, amount, currency);
        if (transfer == null) {
            //TODO
        }
        return transfer;
    }

    private void validate(Account source, Account recipient, Currency transferCurrency) {
        Currency sourceCurrency = source.getCurrency();
        Currency recipientCurrency = recipient.getCurrency();
        if (sourceCurrency != recipientCurrency || sourceCurrency != transferCurrency) {
            logger.warn("Currencies differ: {}, {}", sourceCurrency, recipientCurrency);
            throw new DifferentCurrenciesException(sourceCurrency, recipientCurrency);
        }
        BigDecimal sourceAmount = source.getAmount();
        BigDecimal subtract = sourceAmount.subtract(recipient.getAmount());
        if (subtract.doubleValue() < 0.0) {
            throw new InsufficientFundsException("Not enough funds on account id: " + source.getId());
        }
    }

    private <T> T requireNonNull(T valueToValidate, String message) {
        if (valueToValidate == null) {
            throw AccountNotFoundException.accountNotFound(message);
        }
        return valueToValidate;
    }

    private ReadWriteLock getLockForAccount(long accountId) {
        try {
            return accountLocks.get(accountId, ReentrantReadWriteLock::new);
        } catch (ExecutionException e) {
            //Should not happen since we're simple creating new instance of ReentrantReadWriteLock,
            // but in case it will - we need to signal about it
            throw new IllegalStateException(e);
        }
    }
}
