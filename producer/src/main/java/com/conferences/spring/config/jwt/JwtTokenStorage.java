package com.conferences.spring.config.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtTokenStorage {
    private final ConcurrentHashMap<String, String> activeTokens = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> revokedTokens = new ConcurrentHashMap<>();
    private static final long TOKEN_LIFETIME_MS = 24*60*60*100;

    public void saveToken(String email, String token) {
        String oldToken = activeTokens.put(email, token);
        if(oldToken != null && !oldToken.equals(token)){
            revokedTokens.put(oldToken, Instant.now().toEpochMilli());
        }
    }

    public boolean isTokenValid(String token) {
        return token != null && !revokedTokens.containsKey(token);
    }

    public String getToken(String email) {
        if(activeTokens.containsKey(email)){
            return activeTokens.get(email);
        }
        return null;
    }

    public void removeToken(String email) {
        String token = activeTokens.remove(email);
        if(token != null){
            log.debug("Removing token: {}", token);
            revokedTokens.put(token, Instant.now().toEpochMilli());
        }
    }

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.MINUTES)
    public void cleanRevokedTokens() {
        long now = Instant.now().toEpochMilli();

        Iterator<Map.Entry<String, Long>> iterator = revokedTokens.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, Long> entry = iterator.next();
            if(now - entry.getValue() > TOKEN_LIFETIME_MS){
                iterator.remove();
            }
        }
    }
}
