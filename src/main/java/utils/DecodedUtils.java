package utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import config.UrlConverterConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DecodedUtils {
	private static final Logger logger = LoggerFactory.getLogger(DecodedUtils.class);

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final UrlConverterConfig config;

    @Inject
    DecodedUtils(UrlConverterConfig config){
        this.config = config;
    }
    
    // Genero siempre el mismo codigo para una misma URL
    public String generateShortCode(String url){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(url.getBytes(StandardCharsets.UTF_8));
            String base62 = encodeBase62(hash);
            int size = config.getShortCodeSize();
            return base62.length() >= size ? base62.substring(0, size) : base62;
        } catch(Exception e){
            // En caso de que falle quiero seguir con el flujo
            logger.error("Error generando shortCode con la URL {}", url);
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
