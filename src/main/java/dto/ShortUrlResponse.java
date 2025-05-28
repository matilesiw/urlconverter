package dto;

public class ShortUrlResponse {
    private String url;
    private boolean fallback;

    public ShortUrlResponse(String url, boolean fallback){
        this.url = url;
        this.fallback = fallback;
    }

    public String getUrl(){
        return this.url;
    }

    public boolean isFallback(){
        return this.fallback;
    }
    
}
