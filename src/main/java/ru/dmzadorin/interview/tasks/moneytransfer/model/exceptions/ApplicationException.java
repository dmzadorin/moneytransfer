package ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions;

import javax.ws.rs.core.Response;

/**
 * Created by Dmitry Zadorin on 26.04.2016.
 */
public abstract class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public Response.Status responseStatus() {
        return Response.Status.BAD_REQUEST;
    }
}
