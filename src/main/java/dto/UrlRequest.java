package dto;

import validations.ValidUrl;

public class UrlRequest {
    @ValidUrl
    private String url;

    public UrlRequest(){}

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }
}
