package com.petshop.petopia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisPendingOrderService {

    private static final String PREFIX = "pending_order:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void savePendingOrder(Long orderCode, Object orderData, long timeoutInMinutes) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String key = PREFIX + orderCode;
        ops.set(key, orderData, timeoutInMinutes, TimeUnit.MINUTES);
    }

    public Object getPendingOrder(Long orderCode) {
        String key = PREFIX + orderCode;
        return redisTemplate.opsForValue().get(key);
    }

    public void removePendingOrder(Long orderCode) {
        String key = PREFIX + orderCode;
        redisTemplate.delete(key);
    }

    public boolean exists(Long orderCode) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + orderCode));
    }
}