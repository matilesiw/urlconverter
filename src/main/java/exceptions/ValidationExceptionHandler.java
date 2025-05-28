package exceptions;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import utils.ResponseUtils;

@Provider
public class ValidationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        String code = exception.getConstraintViolations()
                               .stream()
                               .findFirst()
                               .map(v -> v.getMessage())
                               .orElse("validation_error");

        return ResponseUtils.error(Response.Status.BAD_REQUEST, code, null, null);
    }
}
