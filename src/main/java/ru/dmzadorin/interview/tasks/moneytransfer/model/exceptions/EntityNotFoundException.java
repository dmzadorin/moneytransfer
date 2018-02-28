package ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions;

/**
 * Created by Dmitry Zadorin on 17.02.2018
 */
public class EntityNotFoundException extends ApplicationException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
