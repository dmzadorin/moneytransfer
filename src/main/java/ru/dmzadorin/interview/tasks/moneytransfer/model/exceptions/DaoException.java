package ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions;

import javax.ws.rs.core.Response;

/**
 * Created by Dmitry Zadorin on 18.02.2018.
 */
public class DaoException extends ApplicationException {
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public Response.Status responseStatus() {
        return Response.Status.INTERNAL_SERVER_ERROR;
    }
}
