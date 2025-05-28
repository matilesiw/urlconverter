package service;

import io.vertx.ext.web.RoutingContext;

public interface MetricService {
    public void buildAndSendMetric(String shortCode, String urlToRedirect, boolean fallback, RoutingContext ctx);
    
}
