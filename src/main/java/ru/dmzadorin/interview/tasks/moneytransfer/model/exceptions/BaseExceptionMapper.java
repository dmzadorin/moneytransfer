package ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions;

import ru.dmzadorin.interview.tasks.moneytransfer.model.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created by Dmitry Zadorin on 17.02.2018
 */
public class BaseExceptionMapper implements ExceptionMapper<BaseException> {
    @Override
    public Response toResponse(BaseException exception) {
        return Response
                .status(exception.responseStatus())
                .entity(new ErrorMessage(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
