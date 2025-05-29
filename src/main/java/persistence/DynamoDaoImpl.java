package persistence;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.Url;
import exceptions.DaoException;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import config.UrlConverterConfig;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

@ApplicationScoped
public class DynamoDaoImpl implements DynamoDao {
	private static final Logger logger = LoggerFactory.getLogger(DynamoDaoImpl.class);

	private final DynamoDbEnhancedAsyncClient enhancedAsyncClient;
	private final UrlConverterConfig config;
	private DynamoDbAsyncTable<Url> table;

	@Inject
	public DynamoDaoImpl(DynamoDbEnhancedAsyncClient enhancedAsyncClient, UrlConverterConfig config) {
		this.enhancedAsyncClient = enhancedAsyncClient;
		this.config = config;
	}

	@PostConstruct
	public void init() {
		table = enhancedAsyncClient.table(config.getDynamoTableName(), TableSchema.fromBean(Url.class));
	}

	@Override
	public Uni<Void> save(Url url) {
		return Uni.createFrom().completionStage(table.putItem(url)).onFailure().invoke(e -> {
			logger.error("Error Dynamo al guardar url: {}", url, e);
			throw new DaoException("Error Dynamo al guardar url: " + url, e);
		}).replaceWithVoid();
	}

	@Override
	public Uni<Optional<Url>> findByShortCode(String shortCode) {
		return Uni.createFrom().completionStage(table.getItem(r -> r.key(k -> k.partitionValue(shortCode))))
				.onFailure().invoke(e -> logger.error("Error Dynamo al buscar por shortCode: {}", shortCode, e))
				.map(Optional::ofNullable);
	}

	@Override
	public Uni<Void> update(Url url) {
		return Uni.createFrom().completionStage(table.updateItem(url)).onFailure().invoke(e -> {
			logger.error("Error Dynamo al actualizar url: {}", url, e);
			throw new DaoException("Error Dynamo al actualizar url: " + url, e);
		}).replaceWithVoid();
	}

}
