package ru.dmzadorin.interview.tasks.moneytransfer.persistence;

import ru.dmzadorin.interview.tasks.moneytransfer.model.Account;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency;
import ru.dmzadorin.interview.tasks.moneytransfer.model.Transfer;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public interface TransferDao {

    /**
     * Creates new money transfer, adding input some to recipient account and subtracting that sum from source account.
     * This method is executed as a single transaction. If there is any issue with creating transfer, updating account's
     * amount then all changes are rolled back.
     *
     * @param sourceId    source account id
     * @param recipientId recipient account id
     * @param amount      amount to transfer
     * @param currency    currency of transfer
     * @return newly created Transfer
     */
    Transfer transferAmount(long sourceId, long recipientId, BigDecimal amount, Currency currency);

    /**
     * Searches transfer by id. If transfer with specified id not found throws EntityNotFoundException
     *
     * @param transferId id to search
     * @return transfer
     * @throws EntityNotFoundException If transfer with specified id not found
     */
    Transfer getTransferById(long transferId) throws EntityNotFoundException;

    /**
     * Gets all created transfers
     *
     * @return
     */
    Collection<Transfer> getTransfers();
}
