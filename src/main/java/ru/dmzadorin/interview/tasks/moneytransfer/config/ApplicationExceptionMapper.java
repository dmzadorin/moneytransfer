package ru.dmzadorin.interview.tasks.moneytransfer.config;

import ru.dmzadorin.interview.tasks.moneytransfer.model.json.ErrorMessage;
import ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions.ApplicationException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created by Dmitry Zadorin on 17.02.2018
 */
public class ApplicationExceptionMapper implements ExceptionMapper<ApplicationException> {
    @Override
    public Response toResponse(ApplicationException exception) {
        return Response
                .status(exception.responseStatus())
                .entity(new ErrorMessage(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
