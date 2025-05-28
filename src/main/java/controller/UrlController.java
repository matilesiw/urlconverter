package controller;

import config.ErrorCodeConfig;
import config.UrlConverterConfig;
import dto.ShortUrlResponse;
import dto.UrlRequest;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.MetricService;
import service.UrlService;
import utils.ResponseUtils;
import validations.ApiKeySecured;

@Path("/")
@RequestScoped
public class UrlController {
    private final UrlService urlService;
    private final MetricService metricService;
    private final UrlConverterConfig config;
    private final ErrorCodeConfig errorMessage;

    private final static String URL_DESACTIVATE = "Url desactivada correctamente.";
    private final static String URL_NOT_DESACTIVATE = "Url no desactivada.";

    @Inject
    UrlController(UrlService urlService, MetricService metricService, UrlConverterConfig config, ErrorCodeConfig errorMessage) {
        this.urlService = urlService;
        this.metricService = metricService;
        this.config = config;
        this.errorMessage = errorMessage;
    }

    @POST
    @Path("/shorten")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiKeySecured
    public Uni<Response> shorten(@Valid UrlRequest request) {
    	String longUrl = request.getUrl();

    	return urlService.shortenUrl(longUrl)
    	        .onFailure().recoverWithItem(err -> longUrl)
    	        .onItem().transform(shortUrl -> {
    	            boolean fallback = longUrl.equals(shortUrl);
    	            ShortUrlResponse response = new ShortUrlResponse(shortUrl, fallback);
    	            return ResponseUtils.ok(response);
    	        });
    }

    @GET
    @Path("/{shortCode}")
    public Uni<Response> redirectToLongUrl(@PathParam("shortCode") String shortCode, @Context RoutingContext ctx) {
    	Response validationError = ResponseUtils.validateShortCode(shortCode, config.getShortCodeSize());
    	if (validationError != null) {
    	    return Uni.createFrom().item(validationError);
    	}
    	
        return urlService.getUrlByShortCode(shortCode)
            .onItem().transform(urlOptional -> {
                String urlToRedirect = urlOptional.orElse(config.getDefaultRedirect());
                boolean fallback = !urlOptional.isPresent();
                metricService.buildAndSendMetric(shortCode, urlToRedirect, fallback, ctx);
                return ResponseUtils.redirect(urlToRedirect);
            })
            .onFailure().recoverWithItem(err -> {
                String fallbackUrl = config.getDefaultRedirect();
                metricService.buildAndSendMetric(shortCode, fallbackUrl, true, ctx);
                return ResponseUtils.redirect(fallbackUrl);
            });
    }

    @PATCH
    @Path("/{shortCode}/desactivate")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiKeySecured
    public Uni<Response> desactivateShortUrl(@PathParam("shortCode") String shortCode) {
    	Response validationError = ResponseUtils.validateShortCode(shortCode, config.getShortCodeSize());
    	if (validationError != null) {
    	    return Uni.createFrom().item(validationError);
    	}
    	
        return urlService.deactivateShortUrl(shortCode)
            .onItem().transform(success -> {
                if (success) {
                    return ResponseUtils.ok(URL_DESACTIVATE);
                } else {
                    return ResponseUtils.error(
                        Response.Status.NOT_FOUND,
                        errorMessage.getUrlNotFoundOrInactive(),
                        null, null);
                }
            })
            .onFailure().recoverWithItem(err -> 
                ResponseUtils.error(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    URL_NOT_DESACTIVATE,
                    null, null
                )
            );
    }
}
