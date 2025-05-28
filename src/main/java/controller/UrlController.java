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

    @Inject
    UrlController(UrlService urlService, 
    		MetricService metricService, 
    		UrlConverterConfig config, 
    		ErrorCodeConfig errorMessage) {
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
    public Uni<Response> shorten(
    		@Valid UrlRequest request) {
    	String longUrl = request.getUrl();

    	return urlService.shortenUrl(longUrl)
    			// En caso de error devolvemos URL original
    	        .onFailure().recoverWithItem(err -> longUrl)
    	        // En caso de no fallar el service devolvemos la URL que retorna
    	        .onItem().transform(url -> {
    	        	// Si trae la URL original es por que no se pudo acortar la URL
    	        	// pero para no perder disponibilidad devolvemos la URL original
    	            boolean fallback = longUrl.equals(url);
    	            ShortUrlResponse response = new ShortUrlResponse(url, fallback);
    	            return ResponseUtils.ok(response);
    	        });
    }

    @GET
    @Path("/{shortCode}")
    public Uni<Response> redirectToLongUrl(
    		@PathParam("shortCode") String shortCode, 
    		@Context RoutingContext ctx) {
    	// Solo se aceptan codigos con nuestra longitud configurada
    	Response validationError = ResponseUtils.validateShortCode(shortCode, config.getShortCodeSize());
    	if (validationError != null) {
    	    return Uni.createFrom().item(validationError);
    	}
    	
        return urlService.getUrlByShortCode(shortCode)
        	// En caso de no fallar el service redireccionamos segun corresponda
            .onItem().transform(urlOptional -> {
            	// Si existe URL generada previamente redireccionamos alli,
            	// si no se redirecciona a una configurada por default
            	boolean fallback = !urlOptional.isPresent();
                String urlToRedirect = urlOptional.orElse(config.getDefaultRedirect());
                // Se envian las metricas a un servicio externo
                metricService.buildAndSendMetric(shortCode, urlToRedirect, fallback, ctx);
                return ResponseUtils.redirect(urlToRedirect);
            })
            // En caso de error redirecciona a URL configurada por default
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
    public Uni<Response> desactivateShortUrl(
    		@PathParam("shortCode") String shortCode) {
    	//Solo se aceptan codigos con nuestra longitud configurada
    	Response validationError = ResponseUtils.validateShortCode(shortCode, config.getShortCodeSize());
    	if (validationError != null) {
    	    return Uni.createFrom().item(validationError);
    	}
    	
        return urlService.deactivateShortUrl(shortCode)
        	// Recibimos un boolean definiendo si se desactivo la URL
            .onItem().transform(success -> {
                if (success) {
                	// true devolvemos un 200
                    return ResponseUtils.ok("Url desactivada correctamente.");
                } else {
                	// false devolvemos un 404 con un codigo de error
                    return ResponseUtils.error(
                        Response.Status.NOT_FOUND,
                        errorMessage.getUrlNotFoundOrInactive(),
                        null, null);
                }
            })
            // En caso de error devolvemos un 500 con un codigo de error
            .onFailure().recoverWithItem(err -> 
                ResponseUtils.error(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    errorMessage.getUrlNotDesactivate(),
                    null, null
                )
            );
    }
}
