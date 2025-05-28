package persistence;

import java.util.Optional;

import io.smallrye.mutiny.Uni;

public interface RedisDao {
    Uni<Void> saveShortCodeToLongUrl(String shortCode, String longUrl);
    Uni<Void> saveLongUrToShortCode(String longUrl, String shortCode);
    Uni<Optional<String>> getLongUrlByShortCode(String shortCode);
    Uni<Optional<String>> getShortCodeByLongUrl(String longUrl);
    Uni<Void> deleteShortCode(String shortCode);
    Uni<Void> deleteLongUrl(String longUrl);
}
