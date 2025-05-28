package persistence;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import config.UrlConverterConfig;

@ApplicationScoped
//Crea la base de datos si es necesario echo para local y no crear la base de datos con una herramienta externa
public class DynamoInit {
	private static final Logger logger = LoggerFactory.getLogger(DynamoInit.class);

    private final DynamoDbAsyncClient dynamoDbAsyncClient;
    private final UrlConverterConfig config;

    @Inject
    public DynamoInit(DynamoDbAsyncClient dynamoDbAsyncClient, UrlConverterConfig config) {
        this.dynamoDbAsyncClient = dynamoDbAsyncClient;
        this.config = config;
    }

    @PostConstruct
    public void init() {
        if (!("local").equalsIgnoreCase(config.getEnv())) {
            return;
        }

        String tableName = config.getDynamoTableName();

        dynamoDbAsyncClient.listTables()
            .thenCompose(response -> {
                if (!response.tableNames().contains(tableName)) {
                    return createTableAsync(tableName);
                } else {
                    return CompletableFuture.completedFuture(null);
                }
            })
            .exceptionally(e -> {
                return null;
            });
    }

    private CompletableFuture<Void> createTableAsync(String tableName) {
        CreateTableRequest request = CreateTableRequest.builder()
            .tableName(tableName)
            .attributeDefinitions(
                AttributeDefinition.builder()
                    .attributeName("shortCode")
                    .attributeType(ScalarAttributeType.S)
                    .build()
            )
            .keySchema(
                KeySchemaElement.builder()
                    .attributeName("shortCode")
                    .keyType(KeyType.HASH)
                    .build()
            )
            .provisionedThroughput(
                ProvisionedThroughput.builder()
                    .readCapacityUnits(5L)
                    .writeCapacityUnits(5L)
                    .build()
            )
            .build();

        return dynamoDbAsyncClient.createTable(request)
        	    .thenAccept(response -> logger.info("Tabla '{}' creada exitosamente.", tableName))
        	    .whenComplete((res, ex) -> {
        	        if (ex != null) {
        	            logger.error("Fallo al crear la tabla '{}'", tableName, ex);
        	        }
        	    });
    }

    public void touch() {
        logger.info("Bean inicializado");
    }
}
