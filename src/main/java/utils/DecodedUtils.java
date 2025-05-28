package utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.jboss.logging.Logger;

import config.UrlConverterConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DecodedUtils {
    private static final Logger logger = Logger.getLogger(DecodedUtils.class);

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final UrlConverterConfig config;

    @Inject
    DecodedUtils(UrlConverterConfig config){
        this.config = config;
    }
    
    public String generateShortCode(String url){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(url.getBytes(StandardCharsets.UTF_8));
            String base62 = encodeBase62(hash);
            int size = config.getShortCodeSize();
            return base62.length() >= size ? base62.substring(0, size) : base62;
        } catch(Exception e){
            logger.error("Error generando shortCode con la url " + url);
            return null;
        }
    }

    private String encodeBase62(byte[] data) {
        BigInteger bi = new BigInteger(1, data);
        StringBuilder sb = new StringBuilder();
        while (bi.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divmod = bi.divideAndRemainder(BigInteger.valueOf(62));
            sb.append(BASE62.charAt(divmod[1].intValue()));
            bi = divmod[0];
        }
        return sb.reverse().toString();
    }
}
