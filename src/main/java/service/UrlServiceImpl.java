package service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import config.UrlConverterConfig;
import domain.Url;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import persistence.DynamoDao;
import persistence.LocalCacheDaoImpl;
import persistence.RedisDao;
import utils.DecodedUtils;

@ApplicationScoped
public class UrlServiceImpl implements UrlService {
	private static final Logger logger = LoggerFactory.getLogger(UrlServiceImpl.class);

	private final UrlConverterConfig urlConfig;
	private final DecodedUtils decodedUtils;
	private final RedisDao redisDao;
	private final DynamoDao dynamoDao;
	private LocalCacheDaoImpl localCacheDao;

	@Inject
	UrlServiceImpl(UrlConverterConfig urlConfig, DecodedUtils decodedUtils, RedisDao redisDao, DynamoDao dynamoDao,
			LocalCacheDaoImpl localCacheDao) {
		this.urlConfig = urlConfig;
		this.decodedUtils = decodedUtils;
		this.redisDao = redisDao;
		this.dynamoDao = dynamoDao;
		this.localCacheDao = localCacheDao;
	}

	@Override
	public Uni<String> shortenUrl(String url) {
		String shortCodeLocal = localCacheDao.getShortCode(url);
		if (shortCodeLocal != null) {
			logger.info("Se encontró en Cache Local el shortCode: {} para la url: {}", shortCodeLocal, url);
			return Uni.createFrom().item(this.buildShortUrl(shortCodeLocal));
		}

		return redisDao.getShortCodeByLongUrl(url).onItem().transformToUni(shortCodeRedis -> {
		    if (shortCodeRedis.isPresent()) {
		        String shortCode = shortCodeRedis.get();
		        logger.info("Se encontró en Redis el shortCode: {} para la url: {}", shortCode, url);
		        this.saveInLocalCache(url, shortCode);
		        return Uni.createFrom().item(this.buildShortUrl(shortCode));
		    }

		    String generatedShortCode = decodedUtils.generateShortCode(url);
		    if (generatedShortCode == null || generatedShortCode.isBlank()) {
		        logger.warn("No se generó el shortCode para la URL: {}", url);
		        return Uni.createFrom().item(url);
		    }

		    return dynamoDao.findByShortCode(generatedShortCode).onItem().transformToUni(urlObject -> {
		        if (urlObject.isPresent()) {
		            logger.info("Se encontró en Dynamo el shortCode existente: {}", generatedShortCode);
		            return this.buildShortUrlWhenExistsInDynamo(urlObject.get(), generatedShortCode, url);
		        } else {
		            logger.info("ShortCode nuevo generado: {} para url: {}", generatedShortCode, url);
		            return this.buildShortUrlWhenIsNew(generatedShortCode, url);
		        }
		    });
		}).onFailure().recoverWithItem(err -> {
		    logger.error("Error al acortar la URL: {}", url, err);
		    return url;
		});
	}

	@Override
	public Uni<Optional<String>> getUrlByShortCode(String shortCode) {
		String longUrlLocal = localCacheDao.getLongUrl(shortCode);
		if (longUrlLocal != null) {
			logger.info("Se encontró en Cache Local la url: {} para el shortCode: {}", longUrlLocal, shortCode);
			return Uni.createFrom().item(Optional.of(longUrlLocal));
		}

		return redisDao.getLongUrlByShortCode(shortCode).onItem().transformToUni(longUrl -> {
			if (longUrl.isPresent()) {
				logger.info("Se encontró en Redis la url: {} para el shortCode: {}", longUrl.get(), shortCode);
				this.saveInLocalCache(longUrl.get(), shortCode);
				return Uni.createFrom().item(longUrl);
			}

			return dynamoDao.findByShortCode(shortCode).onItem().invoke(urlOptionalDynamo -> {
				if (urlOptionalDynamo.isPresent() && urlOptionalDynamo.get().isActive()) {
					Url urlObject = urlOptionalDynamo.get();
					String url = urlObject.getLongUrl();
					this.saveInAllCaches(url, shortCode);
					logger.info("Se encontró en Dynamo la url: {} para el shortCode: {}", url, shortCode);
				}
			}).onItem().transform(urlOptional -> {
				if (urlOptional.isPresent() && urlOptional.get().isActive()) {
					return Optional.of(urlOptional.get().getLongUrl());
				} else {
					return Optional.<String>empty();
				}
			});
		}).onFailure().recoverWithItem(err -> {
			logger.error("Error al obtener longUrl desde shortCode: {}", shortCode, err);
			return Optional.empty();
		});
	}

	@Override
	public Uni<Boolean> deactivateShortUrl(String shortCode) {
		return dynamoDao.findByShortCode(shortCode).flatMap(urlOptional -> {
			if (urlOptional.isEmpty()) {
				logger.info("{} no existe.", shortCode);
				return Uni.createFrom().item(false);
			}

			Url url = urlOptional.get();

			if (!url.isActive()) {
				logger.info("{} ya se encuentra desactivada.", url.getLongUrl());
				return Uni.createFrom().item(false);
			}

			url.setActive(false);
			return dynamoDao.update(url).flatMap(unused -> redisDao.deleteShortCode(shortCode)).onItem()
					.invoke(() -> logger.info("Se eliminó shortCode de Redis: {}", shortCode))
					.flatMap(unused -> redisDao.deleteLongUrl(url.getLongUrl())).onItem()
					.invoke(() -> logger.info("Se eliminó longUrl de Redis: {}", url.getLongUrl())).replaceWith(true);
		}).onFailure().recoverWithItem(err -> {
			logger.error("Error al desactivar shortCode: {}", shortCode, err);
			return false;
		});
	}

	private String buildShortUrl(String shortCode) {
		return urlConfig.getDomainUrl() + shortCode;
	}

	private Uni<String> buildShortUrlWhenExistsInDynamo(Url urlObject, String shortCode, String url) {
	    if (url.equals(urlObject.getLongUrl())) {
	        if (!urlObject.isActive()) {
	            urlObject.setActive(true);
	            return dynamoDao.update(urlObject)
	                .onItem().invoke(() -> this.saveInAllCaches(url, shortCode))
	                .replaceWith(buildShortUrl(shortCode))
	                .onFailure().recoverWithItem(e -> {
	                    logger.error("Error actualizando URL en Dynamo", e);
	                    return url;
	                });
	        } else {
	            this.saveInAllCaches(url, shortCode);
	            return Uni.createFrom().item(buildShortUrl(shortCode));
	        }
	    } else {
	        logger.warn("Colisión detectada con el shortCode: {} para URL: {}", shortCode, url);
	        return Uni.createFrom().item(url);
	    }
	}

	private Uni<String> buildShortUrlWhenIsNew(String shortCode, String url) {
	    Url newUrl = new Url();
	    newUrl.setShortCode(shortCode);
	    newUrl.setLongUrl(url);
	    newUrl.setCreatedAt(System.currentTimeMillis());
	    newUrl.setActive(true);

	    return dynamoDao.save(newUrl)
	        .onItem().invoke(() -> this.saveInAllCaches(url, shortCode))
	        .replaceWith(buildShortUrl(shortCode))
	        .onFailure().recoverWithItem(e -> {
	            logger.error("Error guardando URL nueva en Dynamo", e);
	            return url;
	        });
	}

	private void saveInLocalCache(String url, String shortCode) {
		localCacheDao.putShortCode(url, shortCode);
		localCacheDao.putLongUrl(shortCode, url);
	}

	private void saveInAllCaches(String url, String shortCode) {
		this.saveInLocalCache(url, shortCode);
		redisDao.saveShortCodeToLongUrl(shortCode, url).subscribe().with(unused -> {
		}, err -> logger.error("Error guardando en Redis", err));
		redisDao.saveLongUrToShortCode(url, shortCode).subscribe().with(unused -> {
		}, err -> logger.error("Error guardando en Redis", err));
	}

}
