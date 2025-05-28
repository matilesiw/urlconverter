package exceptions;

import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import utils.ResponseUtils;

@Provider
public class MethodNotAllowedExceptionHandler implements ExceptionMapper<NotAllowedException> {
    private final String CODE = "method_not_allowed";
    private final String MESSAGE = "El metodo HTTP no es el requerido por el endpoint";

    @Override
    public Response toResponse(NotAllowedException exception) {
        return ResponseUtils.error(Response.Status.METHOD_NOT_ALLOWED, CODE, MESSAGE, exception);
    }
}
