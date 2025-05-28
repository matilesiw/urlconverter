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

}
