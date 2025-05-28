package exceptions;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import utils.ResponseUtils;

import java.util.UUID;

import jakarta.ws.rs.core.Response;

@Provider
public class GenericExceptionHandler implements ExceptionMapper<Throwable> {
    private final String GENERIC_MESSAGE = "Ocurrió un error inesperado.";

    @Override
    public Response toResponse(Throwable exception) {
        String errorId = UUID.randomUUID().toString().substring(0, 8);

        return ResponseUtils.error(
            Response.Status.INTERNAL_SERVER_ERROR,
            errorId,
            GENERIC_MESSAGE,
            exception
        );
    }
}
