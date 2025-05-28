package validations;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import utils.ResponseUtils;
import jakarta.ws.rs.core.Response;
import java.io.IOException;

import config.ErrorCodeConfig;
import config.UrlConverterConfig;

@Provider
@ApiKeySecured
@Priority(Priorities.AUTHENTICATION)
public class ApiKeyFilter implements ContainerRequestFilter {
	private final UrlConverterConfig config;
	private final ErrorCodeConfig messageError;

	@Inject
	ApiKeyFilter(UrlConverterConfig config, ErrorCodeConfig messageError) {
		this.config = config;
		this.messageError = messageError;
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String apiKey = requestContext.getHeaderString("X-API-KEY");

		if (apiKey == null || !apiKey.equals(config.getApiKey())) {
			requestContext.abortWith(ResponseUtils.error(Response.Status.UNAUTHORIZED, messageError.getInvalidApiKey(),
					"API Key inválida o faltante", null));
		}
	}
}
