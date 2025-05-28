package utils;

import java.net.URI;

import dto.ApiError;
import dto.ApiResponse;
import jakarta.ws.rs.core.Response;

public class ResponseUtils {

    private static boolean printStack = false;

    public static void init(boolean shouldPrintStack) {
        printStack = shouldPrintStack;
    }

    public static <T> Response ok(T data) {
        return Response.ok(ApiResponse.success(data)).build();
    }

    public static Response error(Response.Status status, String code, String message, Throwable ex) {
        Response.Status responseStatus = (status != null) ? status : Response.Status.BAD_REQUEST;
        ApiError error = printStack
                ? new ApiError(code, message, ex)
                : new ApiError(code, message, null);

        return Response.status(responseStatus)
                .entity(ApiResponse.error(error))
                .build();
    }

    public static Response redirect(String url) {
        return Response.status(Response.Status.FOUND)
                .location(URI.create(url))
                .build();
    }

    public static Response noContent() {
        return Response.status(Response.Status.NO_CONTENT).build();
    }
    
    public static Response validateShortCode(String shortCode, int expectedSize) {
        if (shortCode == null || shortCode.length() != expectedSize) {
            return error(
                Response.Status.BAD_REQUEST,
                "invalid_short_code_size",
                "El shortCode no tiene el tamaño esperado (" + expectedSize + ").",
                null
            );
        }
        return null;
    }
}