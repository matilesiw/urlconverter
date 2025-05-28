package config;

import java.util.Set;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UrlConverterConfig {
    @ConfigProperty(name = "env")
    String env;

    @ConfigProperty(name = "url.converter.shortened.url")
    String domainUrl;

    @ConfigProperty(name = "url.converter.default_redirect")
    String defaultRedirect;

    @ConfigProperty(name = "url.converter.redis.prefix.short")
    String redisPrefixShort;

    @ConfigProperty(name = "url.converter.redis.prefix.long")
    String redisPrefixLong;

    @ConfigProperty(name = "url.converter.redis.ttl")
    int redisTtlInSeconds;

    @ConfigProperty(name = "url.converter.allowed_domains")
    Set<String> allowedDomains;

    @ConfigProperty(name = "url.converter.print_stack")
    boolean printStack;

    @ConfigProperty(name = "url.converter.short_code_size")
    int shortCodeSize;

    @ConfigProperty(name = "url.converter.dynamodb.endpoint")
    String dynamoEndpoint;

    @ConfigProperty(name = "url.converter.dynamodb.region")
    String region;

    @ConfigProperty(name = "url.converter.dynamodb.table")
    String tableName;

    @ConfigProperty(name = "aws.credentials.access-key-id")
    String accessKeyId;

    @ConfigProperty(name = "aws.credentials.secret-access-key")
    String secretAccessKey;

    @ConfigProperty(name = "url.cache.local_cache_maximum_size")
	private long localCacheMaximumSize;

    @ConfigProperty(name = "url.cache.local_cache_minutes_expire")
	private long localCacheMinutesExpire;
    
    @ConfigProperty(name = "url.converter.api-key")
	private String apiKey;

    public String getEnv() {
        return env;
    }

    public String getDomainUrl() {
        return domainUrl;
    }

    public String getDefaultRedirect() {
        return defaultRedirect;
    }

    public String getRedisPrefixShort() {
        return redisPrefixShort;
    }

    public String getRedisPrefixLong() {
        return redisPrefixLong;
    }

    public int getRedisTtlInSeconds() {
        return redisTtlInSeconds;
    }

    public Set<String> getAllowedDomains() {
        return allowedDomains;
    }

    public boolean getPrintStack() {
        return printStack;
    }

    public int getShortCodeSize() {
        return shortCodeSize;
    }

    public String getDynamoEndpoint() {
        return dynamoEndpoint;
    }

    public String getRegion() {
        return region;
    }

    public String getDynamoTableName() {
        return tableName;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

	public long getLocalCacheMaximumSize() {
		return localCacheMaximumSize;
	}

	public long getLocalCacheMinutesExpire() {
		return localCacheMinutesExpire;
	}

	public String getApiKey() {
		return apiKey;
	}

}
