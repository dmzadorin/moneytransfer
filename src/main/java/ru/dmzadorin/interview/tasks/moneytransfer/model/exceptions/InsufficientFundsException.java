package ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions;

import java.math.BigDecimal;

/**
 * Created by Dmitry Zadorin on 26.04.2016.
 */
public class InsufficientFundsException extends ApplicationException {
    private static final String MESSAGE = "Not enough funds on account id %d, current amount = %s, transfer amount = %s";

    public InsufficientFundsException(long accountId, BigDecimal currentAmount, BigDecimal transferAmount) {
        super(String.format(MESSAGE, accountId, currentAmount.toString(), transferAmount.toString()));
    }
}
