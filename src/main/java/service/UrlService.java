package service;

import java.util.Optional;

import io.smallrye.mutiny.Uni;

public interface UrlService {
	Uni<String> shortenUrl(String url);
    Uni<Optional<String>> getUrlByShortCode(String shortCode);
    Uni<Boolean> deactivateShortUrl(String shortCode);
}
