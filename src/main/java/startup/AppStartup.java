package startup;

import config.UrlConverterConfig;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import persistence.DynamoInit;
import utils.ResponseUtils;

@Startup
@ApplicationScoped
public class AppStartup {
    private final UrlConverterConfig config;
    private final DynamoInit dynamoInit;
    
    @Inject
    AppStartup(UrlConverterConfig config,  DynamoInit dynamoInit){
        this.config = config;
        this.dynamoInit = dynamoInit;
    }

    @PostConstruct
    public void init() {
        ResponseUtils.init(config.getPrintStack());
        dynamoInit.touch();
    }
}
