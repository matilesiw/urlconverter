package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dto.MetricDto;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class MetricServiceImpl implements MetricService{

   
	private static final Logger logger = LoggerFactory.getLogger(MetricServiceImpl.class);

    @Override
    public void buildAndSendMetric(String shortCode, String urlToRedirect, boolean fallback, RoutingContext ctx) {
        try {
            MetricDto dto = new MetricDto();
            dto.setShortCode(shortCode);
            dto.setLongUrl(urlToRedirect);
            dto.setFallback(fallback);
            dto.setAccessedAt(System.currentTimeMillis());
            dto.setIpAddress(ctx.request().remoteAddress().host());
            dto.setUserAgent(ctx.request().getHeader("User-Agent"));
            //Se pueden agregar mas dependiendo que sea lo que necesite

            this.send(dto);

        } catch (Exception e) {
            logger.warn("Error enviando métrica -> ", e);
        }
    }

    private void send(MetricDto dto) {
        //Aca enviaria a otro servicio (puede ser uno propio o de aws) que se encargue de las metricas para sacarle esa responsabilidad a este servicio.
        logger.info("Métrica enviada {} ", dto);
    }
    
}
