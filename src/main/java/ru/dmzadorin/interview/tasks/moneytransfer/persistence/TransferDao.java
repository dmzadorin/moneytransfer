package ru.dmzadorin.interview.tasks.moneytransfer.persistence;

import ru.dmzadorin.interview.tasks.moneytransfer.model.Account;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Transfer;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public interface TransferDao {

    Transfer transferAmount(long sourceId, long recipientId, BigDecimal amount, Currency currency);

    Collection<Transfer> getTransfers();
}
