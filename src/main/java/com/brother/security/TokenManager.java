package com.brother.security;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Coldmoon on 2015/10/30.
 */
@Component
public class TokenManager {
    private static final int REFRESH_TOKEN_EXPIRED_DAYS = 30;
    private static final int ACCESS_TOKEN_EXPIRED_HOURS = 2;
    private static final int ACCESS_TOKEN_EXPIRED_MINUTES = 2;

    private static final String REFRESH_TOKEN_PREFIX = "refresh-token-{0}";
    private static final String ACCESS_TOKEN_PREFIX = "601933_";
    private static final String REFRESH_TOKEN_KEY_PREFIX = "refresh-token-{0}";
    private static final String ACCESS_TOKEN_KEY_PREFIX = "access-token-{0}";

    @Autowired
    private RedisTemplate<String, Long> refreshTokenCache;

    @Autowired
    private RedisTemplate<String, Long> accessTokenCache;

    // mapping of member - refresh token
    @Autowired
    private RedisTemplate<String, Map<String, Date>> refreshTokenMapCache;

    // mapping of member - access token
    @Autowired
    private RedisTemplate<String, Map<String, Date>> accessTokenMapCache;

    // mapping of refresh token - access token
    @Autowired
    private RedisTemplate<String, Map<String, Date>> refreshTokenAccessTokenMapping;


    private void saveRefreshToken(String token, Long memberId) {
        String key = MessageFormat.format(REFRESH_TOKEN_KEY_PREFIX, Long.toString(memberId));

        // 缓存refresh_token
        refreshTokenCache.opsForValue().set(token, memberId);
        refreshTokenCache.expire(token, REFRESH_TOKEN_EXPIRED_DAYS, TimeUnit.DAYS);

        // 创建新的map
        Map<String, Date> newMap = new HashMap<>();
        Map<String, Date> oldMap = refreshTokenMapCache.opsForValue().get(key);
        if (oldMap != null) {
            // 删除过期的token
            for (Map.Entry<String, Date> entry : oldMap.entrySet()) {
                if (new DateTime(entry.getValue()).plusDays(REFRESH_TOKEN_EXPIRED_DAYS).isAfterNow()) {
                    newMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        // 保存新的token
        newMap.put(token, new Date());
        // 缓存新的map
        refreshTokenMapCache.opsForValue().set(key, newMap);
        refreshTokenMapCache.expire(key, REFRESH_TOKEN_EXPIRED_DAYS, TimeUnit.DAYS);
    }

    private void saveAccessToken(String token, Long memberId) {
        String key = MessageFormat.format(ACCESS_TOKEN_KEY_PREFIX, Long.toString(memberId));

        // 缓存access_token
        accessTokenCache.opsForValue().set(token, memberId);
        accessTokenCache.expire(token, ACCESS_TOKEN_EXPIRED_HOURS, TimeUnit.HOURS);

        // 创建新的map
        Map<String, Date> newMap = new HashMap<>();
        Map<String, Date> oldMap = accessTokenMapCache.opsForValue().get(key);
        if (oldMap != null) {
            // 删除过期的token
            for (Map.Entry<String, Date> entry : oldMap.entrySet()) {
                if (new DateTime(entry.getValue()).plusHours(ACCESS_TOKEN_EXPIRED_HOURS).isAfterNow()) {
                    newMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        // 保存新的token
        newMap.put(token, new Date());
        // 缓存新的map
        accessTokenMapCache.opsForValue().set(key, newMap);
        accessTokenMapCache.expire(key, ACCESS_TOKEN_EXPIRED_HOURS, TimeUnit.HOURS);
    }

    private void refreshAccessToken(String refreshToken, String accessToken) {
        String key = MessageFormat.format(REFRESH_TOKEN_PREFIX, refreshToken);

        // 创建新的map
        Map<String, Date> newMap = new HashMap<>();
        Map<String, Date> oldMap = refreshTokenAccessTokenMapping.opsForValue().get(key);
        if (oldMap != null) {
            for (Map.Entry<String, Date> entry : oldMap.entrySet()) {
                String oldAccessToken = entry.getKey();
                Long memberId = accessTokenCache.opsForValue().get(oldAccessToken);
                if (memberId != null) {
                    accessTokenCache.expire(oldAccessToken, ACCESS_TOKEN_EXPIRED_MINUTES, TimeUnit.MINUTES);
                    newMap.put(oldAccessToken, new Date());
                }
            }
        }

        // 保存新的access token
        newMap.put(accessToken, new Date());

        refreshTokenAccessTokenMapping.opsForValue().set(key, newMap);
        refreshTokenAccessTokenMapping.expire(key, REFRESH_TOKEN_EXPIRED_DAYS, TimeUnit.DAYS);
    }


    // 会员登出后，后台删除对应的access token
    public void remove(String token, Long memberId) {
        if (verifyAccessToken(token) != null) {
            accessTokenCache.delete(token);
            String accessTokenKey = MessageFormat.format(ACCESS_TOKEN_KEY_PREFIX, Long.toString(memberId));
            Map<String, Date> accessTokenMap = accessTokenMapCache.opsForValue().get(accessTokenKey);
            if (accessTokenMap != null) {
                accessTokenMap.remove(token);
                accessTokenMapCache.opsForValue().set(accessTokenKey, accessTokenMap);
                accessTokenMapCache.expire(accessTokenKey, ACCESS_TOKEN_EXPIRED_HOURS, TimeUnit.HOURS);
            }
        }
    }

    // 更改密码后，需要remove用户的所有token
    public void removeAll(Long memberId) {
        String refreshTokenKey = MessageFormat.format(REFRESH_TOKEN_KEY_PREFIX, Long.toString(memberId));
        Map<String, Date> refreshTokenMap = refreshTokenMapCache.opsForValue().get(refreshTokenKey);
        if (refreshTokenMap != null) {
            for (String key : refreshTokenMap.keySet()) {
                refreshTokenCache.delete(key);
                // delete mapping of refresh token - access token
                String refreshToken = MessageFormat.format(REFRESH_TOKEN_PREFIX, key);
                refreshTokenAccessTokenMapping.delete(refreshToken);
            }
            refreshTokenMapCache.delete(refreshTokenKey);
        }

        String accessTokenKey = MessageFormat.format(ACCESS_TOKEN_KEY_PREFIX, Long.toString(memberId));
        Map<String, Date> accessTokenMap = accessTokenMapCache.opsForValue().get(accessTokenKey);
        if (accessTokenMap != null) {
            for (String key : accessTokenMap.keySet()) {
                accessTokenCache.delete(key);
            }
            accessTokenMapCache.delete(accessTokenKey);
        }
    }

    public Long verifyAccessToken(String accessToken) {
        if (!accessToken.startsWith(TokenManager.ACCESS_TOKEN_PREFIX)) {
            accessToken = TokenManager.ACCESS_TOKEN_PREFIX + accessToken;
        }

        return accessTokenCache.opsForValue().get(accessToken);
    }

    public Long verifyRefreshToken(String refreshToken) {
        return refreshTokenCache.opsForValue().get(refreshToken);
    }


}