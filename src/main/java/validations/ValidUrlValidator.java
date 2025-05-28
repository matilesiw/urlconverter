package validations;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.ErrorCodeConfig;
import config.UrlConverterConfig;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidUrlValidator implements ConstraintValidator<ValidUrl, String>{
    private final UrlConverterConfig urlConfig;
    private final ErrorCodeConfig codeConfig;

    @Inject
    ValidUrlValidator(UrlConverterConfig urlConfig, ErrorCodeConfig codeConfig){
        this.urlConfig = urlConfig;
        this.codeConfig = codeConfig;
    }

    private Set<String> allowedDomains;
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^(?:https?://)?([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");
    private static final int MAX_URL_SIZE = 2048;

    @Override
    public void initialize(ValidUrl constraintAnnotation) {
        allowedDomains = urlConfig.getAllowedDomains() != null 
                         ? Set.copyOf(urlConfig.getAllowedDomains()) 
                         : Set.of();
    } 

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        if (url == null || url.isBlank()) {
            this.setErrorMessage(context, codeConfig.getCodeUrlEmpty());
            return false;
        }

        if(url.length() > MAX_URL_SIZE) {
            this.setErrorMessage(context, codeConfig.getCodeUrlTooLong());
            return false;

        }

        Matcher matcher = DOMAIN_PATTERN.matcher(url);
        if (!matcher.find()) {
            this.setErrorMessage(context, codeConfig.getCodeUrlInvalidFormat());
            return false;
        }

        //Se podrian agregar mas medidas de seguridad pero yo dejaria estas por ahora para no afectar el rendimiento

        String host = matcher.group(1).toLowerCase(Locale.ROOT);

        boolean allowed = allowedDomains.stream()
            .anyMatch(domain -> host.equals(domain) || host.endsWith("." + domain));

        if (!allowed) {
            setErrorMessage(context, codeConfig.getCodeUrlDomainNotAllowed());
        }

        return allowed;
    }

    private void setErrorMessage(ConstraintValidatorContext context, String errorMessage){
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(errorMessage)
               .addConstraintViolation();
    }
}
