package persistence;

public interface LocalCacheDao {
	String getShortCode(String longUrl);
    void putShortCode(String longUrl, String shortCode);

    String getLongUrl(String shortCode);
    void putLongUrl(String shortCode, String longUrl);

    void invalidateByShortCode(String shortCode);
    void invalidateByLongUrl(String longUrl);
}
