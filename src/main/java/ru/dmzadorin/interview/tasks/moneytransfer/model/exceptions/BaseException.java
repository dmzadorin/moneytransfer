package ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions;

import javax.ws.rs.core.Response;

/**
 * Created by Dmitry Zadorin on 26.04.2016.
 */
public abstract class BaseException extends RuntimeException {
    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public Response.Status responseStatus() {
        return Response.Status.BAD_REQUEST;
    }
}
