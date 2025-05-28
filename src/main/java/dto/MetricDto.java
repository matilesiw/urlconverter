package dto;

public class MetricDto {

    private String shortCode;
    private String longUrl;
    private boolean fallback;
    private long accessedAt;
    private String ipAddress;
    private String userAgent;

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public boolean isFallback() {
        return fallback;
    }

    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }

    public long getAccessedAt() {
        return accessedAt;
    }

    public void setAccessedAt(long accessedAt) {
        this.accessedAt = accessedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return "MetricDto{" +
                "shortCode='" + shortCode + '\'' +
                ", longUrl='" + longUrl + '\'' +
                ", fallback=" + fallback +
                ", accessedAt=" + accessedAt +
                ", ipAddress='" + ipAddress + '\'' +
                ", userAgent='" + userAgent + '\'' +
                '}';
    }
}
