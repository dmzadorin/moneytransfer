package ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions;

/**
 * Created by Dmitry Zadorin on 17.02.2018
 */
public class AccountNotFoundException extends ApplicationException {
    private AccountNotFoundException(String message) {
        super(message);
    }

    public static AccountNotFoundException accountNotFound(String account){
        return new AccountNotFoundException("Account with id " + account + " not found in storage");
    }

    public static AccountNotFoundException sourceNotFound(String account){
        return new AccountNotFoundException("Source account with id " + account + " not found in storage");
    }

    public static AccountNotFoundException targetNotFound(String account){
        return new AccountNotFoundException("Target account with id " + account + " not found in storage");
    }
}
