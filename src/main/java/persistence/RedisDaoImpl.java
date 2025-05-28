package persistence;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import io.quarkus.redis.datasource.value.SetArgs;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import config.UrlConverterConfig;
import exceptions.DaoException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class RedisDaoImpl implements RedisDao {

    private static final Logger logger = LoggerFactory.getLogger(RedisDaoImpl.class);

    private final ReactiveValueCommands<String, String> reactiveValues;
    private final ReactiveKeyCommands<String> reactiveKeys;
    private final UrlConverterConfig config;
    private final SetArgs args;

    @Inject
    public RedisDaoImpl(ReactiveRedisDataSource reactiveDs, UrlConverterConfig config) {
        this.reactiveValues = reactiveDs.value(String.class);
        this.reactiveKeys = reactiveDs.key();
        this.config = config;
        this.args = new SetArgs().ex(config.getRedisTtlInSeconds());
    }

    @Override
    public Uni<Void> saveShortCodeToLongUrl(String shortCode, String longUrl) {
        return reactiveValues.set(config.getRedisPrefixShort() + shortCode, longUrl, args)
            .onFailure().invoke(e -> logger.error("Error Redis al guardar shortCode: {}", shortCode, e))
            .onFailure().transform(f -> new DaoException("Error Redis al guardar shortCode: " + shortCode, f))
            .replaceWithVoid();
    }

    @Override
    public Uni<Void> saveLongUrToShortCode(String longUrl, String shortCode) {
        return reactiveValues.set(config.getRedisPrefixLong() + longUrl, shortCode, args)
            .onFailure().invoke(e -> logger.error("Error Redis al guardar longUrl: {}", longUrl, e))
            .onFailure().transform(f -> new DaoException("Error Redis al guardar longUrl: " + longUrl, f))
            .replaceWithVoid();
    }

    @Override
    public Uni<Optional<String>> getLongUrlByShortCode(String shortCode) {
        return reactiveValues.get(config.getRedisPrefixShort() + shortCode)
            .onFailure().invoke(e -> logger.error("Error Redis al buscar longUrl por shortCode: {}", shortCode, e))
            .onFailure().recoverWithItem(() -> null)
            .map(Optional::ofNullable);
    }

    @Override
    public Uni<Optional<String>> getShortCodeByLongUrl(String longUrl) {
        return reactiveValues.get(config.getRedisPrefixLong() + longUrl)
            .onFailure().invoke(e -> logger.error("Error Redis al buscar shortCode por longUrl: {}", longUrl, e))
            .onFailure().recoverWithItem(() -> null)
            .map(Optional::ofNullable);
    }

    @Override
    public Uni<Void> deleteShortCode(String shortCode) {
        return reactiveKeys.del(config.getRedisPrefixShort() + shortCode)
            .onFailure().invoke(e -> logger.error("Error Redis al eliminar shortCode: {}", shortCode, e))
            .onFailure().transform(f -> new DaoException("Error Redis al eliminar shortCode: " + shortCode, f))
            .replaceWithVoid();
    }

    @Override
    public Uni<Void> deleteLongUrl(String longUrl) {
        return reactiveKeys.del(config.getRedisPrefixLong() + longUrl)
            .onFailure().invoke(e -> logger.error("Error Redis al eliminar longUrl: {}", longUrl, e))
            .onFailure().transform(f -> new DaoException("Error Redis al eliminar longUrl: " + longUrl, f))
            .replaceWithVoid();
    }
}