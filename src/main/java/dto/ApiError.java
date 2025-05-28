package dto;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ApiError {
    private final String code;
    private final String message;
    private final String stackTrace;

    public ApiError(String code, String message, Throwable ex) {
        this.code = code;
        this.message = message;

        if (ex != null) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            this.stackTrace = sw.toString();
        } else {
            this.stackTrace = null;
        }
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public String getStackTrace() { return stackTrace; }
}
