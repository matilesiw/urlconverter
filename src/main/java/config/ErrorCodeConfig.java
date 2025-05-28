package config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ErrorCodeConfig {

    @ConfigProperty(name = "validation.url.empty")
    String empty;

    @ConfigProperty(name = "validation.url.too_long")
    String tooLong;

    @ConfigProperty(name = "validation.url.invalid_format")
    String invalidFormat;

    @ConfigProperty(name = "validation.url.domain_not_allowed")
    String domainNotAllowed;

    @ConfigProperty(name = "validation.url.url_not_found_or_inactive")
    String urlNotFoundOrInactive;
    
    @ConfigProperty(name = "validation.api-key.invalid")
    String invalidApiKey;

    @ConfigProperty(name = "validation.url.url_not_desactivate")
	private String urlNotDesactivate;

    @ConfigProperty(name = "validation.method_not_allowed")
	private String methodNotAllowed;

    @ConfigProperty(name = "validation.generic_validation_error")
	private String genericValidationError;

    public String getCodeUrlEmpty(){
        return empty;
    }

    public String getCodeUrlTooLong(){
        return tooLong;
    }

    public String getCodeUrlInvalidFormat(){
        return invalidFormat;
    }

    public String getCodeUrlDomainNotAllowed(){
        return domainNotAllowed;
    }

    public String getUrlNotFoundOrInactive(){
        return urlNotFoundOrInactive;
    }

	public String getInvalidApiKey() {
		return invalidApiKey;
	}

	public String getUrlNotDesactivate() {
		return urlNotDesactivate;
	}

	public String getMethodNotAllowed() {
		return methodNotAllowed;
	}

	public String getGenericValidationError() {
		return genericValidationError;
	}

}
