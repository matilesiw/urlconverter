package persistence;

import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import config.UrlConverterConfig;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LocalCacheDaoImpl implements LocalCacheDao {

    private final Cache<String, String> shortCodeCache;
	private UrlConverterConfig config;

    public LocalCacheDaoImpl(UrlConverterConfig config) {
    	this.config = config;
        this.shortCodeCache = Caffeine.newBuilder()
                .maximumSize(config.getLocalCacheMaximumSize())
                .expireAfterWrite(config.getLocalCacheMinutesExpire(), TimeUnit.MINUTES)
                .build();
    }

    @Override
    public String getShortCode(String longUrl) {
        return shortCodeCache.getIfPresent(config.getRedisPrefixLong() + longUrl);
    }

    @Override
    public void putShortCode(String longUrl, String shortCode) {
        shortCodeCache.put(config.getRedisPrefixLong() + longUrl, shortCode);
    }

    @Override
    public String getLongUrl(String shortCode) {
        return shortCodeCache.getIfPresent(config.getRedisPrefixShort() + shortCode);
    }

    @Override
    public void putLongUrl(String shortCode, String longUrl) {
        shortCodeCache.put(config.getRedisPrefixShort() + shortCode, longUrl);
    }

    @Override
    public void invalidateByShortCode(String shortCode) {
        shortCodeCache.invalidate(config.getRedisPrefixShort() + shortCode);
    }

    @Override
    public void invalidateByLongUrl(String longUrl) {
        shortCodeCache.invalidate(config.getRedisPrefixLong() + longUrl);
    }
}
