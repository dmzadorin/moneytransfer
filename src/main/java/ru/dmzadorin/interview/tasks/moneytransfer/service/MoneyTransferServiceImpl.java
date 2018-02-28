package ru.dmzadorin.interview.tasks.moneytransfer.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Account;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Transfer;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.DifferentCurrenciesException;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.EntityNotFoundException;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.InsufficientFundsException;
import ru.dmzadorin.interview.tasks.moneytransfer.persistence.AccountDao;
import ru.dmzadorin.interview.tasks.moneytransfer.persistence.TransferDao;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public class MoneyTransferServiceImpl implements MoneyTransferService {
    private static final Logger logger = LogManager.getLogger();
    private static final String SOURCE_RECIPIENT_DIFFER = "Source currency %s does not match recipient currency %s";
    private static final String SOURCE_TRANSFER_DIFFER = "Source currency %s does not match transfer currency %s";


    private final Cache<Long, ReadWriteLock> accountLocks;
    private final AccountDao accountDao;
    private final TransferDao transferDao;

    @Inject
    public MoneyTransferServiceImpl(AccountDao accountDao, TransferDao transferDao) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.accountLocks = CacheBuilder.newBuilder().weakValues().build();
    }

    @Override
    public Account createAccount(String fullName, BigDecimal initialBalance, Currency currency) {
        return accountDao.createAccount(fullName, initialBalance, currency);
    }

    @Override
    public Account getAccountById(long accountId) throws EntityNotFoundException {
        return doWithReadLock(accountId, () -> accountDao.getAccountById(accountId));
    }

    @Override
    public Transfer transferMoney(long sourceId, long recipientId, BigDecimal transferAmount, Currency currency)
            throws InsufficientFundsException, DifferentCurrenciesException, EntityNotFoundException {
        return doWithWriteLock(sourceId, recipientId, () -> {
            logger.info("Got new transfer request. Source account id: {}, recipient account id: {}, transfer amount: {}, transfer currency: {}",
                    sourceId, recipientId, transferAmount, currency);
            Account sourceAccount = accountDao.getAccountById(sourceId);
            Account recipientAccount = accountDao.getAccountById(recipientId);
            validate(sourceAccount, recipientAccount, transferAmount, currency);

            return transferDao.transferAmount(sourceAccount.getId(), recipientAccount.getId(), transferAmount, currency);
        });
    }

    @Override
    public Collection<Transfer> getAllTransfers() {
        return transferDao.getTransfers();
    }

    @Override
    public Transfer getTransferById(long transferId) throws EntityNotFoundException {
        return transferDao.getTransferById(transferId);
    }

    private void validate(Account source, Account recipient, BigDecimal transferAmount, Currency transferCurrency) {
        Currency sourceCurrency = source.getCurrency();
        Currency recipientCurrency = recipient.getCurrency();
        compareCurrencies(sourceCurrency, recipientCurrency, SOURCE_RECIPIENT_DIFFER);
        compareCurrencies(sourceCurrency, transferCurrency, SOURCE_TRANSFER_DIFFER);

        BigDecimal sourceAmount = source.getAmount();
        BigDecimal subtract = sourceAmount.subtract(transferAmount);
        if (subtract.signum() == -1) {
            logger.warn("Not enough funds on account: {}, current amount: {}, transfer amount: {}", source.getId(),
                    sourceAmount, transferAmount);
            throw new InsufficientFundsException(source.getId(), sourceAmount, transferAmount);
        }
    }

    private void compareCurrencies(Currency first, Currency second, String messageFormat) {
        if (first != second) {
            String message = String.format(messageFormat, first, second);
            logger.warn(message);
            throw new DifferentCurrenciesException(message);
        }
    }

    private <T> T doWithReadLock(long accountId, Supplier<T> supplier) {
        ReadWriteLock lock = getLockForAccount(accountId);
        try {
            lock.readLock().lock();
            return supplier.get();
        } finally {
            lock.readLock().unlock();
        }
    }

    private <T> T doWithWriteLock(long sourceId, long recipientId, Supplier<T> supplier) {
        ReadWriteLock sourceLock = getLockForAccount(sourceId);
        ReadWriteLock recipientLock = getLockForAccount(recipientId);

        ReadWriteLock first;
        ReadWriteLock second;
        //Determine lock order based on ids
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
            return supplier.get();
        } finally {
            second.writeLock().unlock();
            first.writeLock().unlock();
        }
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
