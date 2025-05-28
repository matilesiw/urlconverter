package exceptions;

import config.ErrorCodeConfig;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import utils.ResponseUtils;

@Provider
public class ValidationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {
	private final ErrorCodeConfig messageError;
	
	ValidationExceptionHandler(ErrorCodeConfig messageError){
		this.messageError = messageError;
	}

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        String code = exception.getConstraintViolations()
                               .stream()
                               .findFirst()
                               .map(v -> v.getMessage())
                               .orElse(messageError.getGenericValidationError());

        return ResponseUtils.error(Response.Status.BAD_REQUEST, code, null, null);
    }
}
