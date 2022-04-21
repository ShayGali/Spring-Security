package com.example.demo.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.google.common.net.HttpHeaders;

@Component
@ConfigurationProperties(prefix = "application.jwt")
public class JwtConfig {
    private String secretKey;
    private String tokenPrefix;
    private Integer tokenExpirationAfter;

    public JwtConfig() {
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public Integer getTokenExpirationAfter() {
        return tokenExpirationAfter;
    }

    public void setTokenExpirationAfter(Integer tokenExpirationAfter) {
        this.tokenExpirationAfter = tokenExpirationAfter;
    }

    public String getAuthorizationHeader(){
        return HttpHeaders.AUTHORIZATION;
    }
}
