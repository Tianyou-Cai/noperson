package com.cty.nopersonfinally.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁工具类
 */
@Component
public class DistributedLock {

    private final StringRedisTemplate redisTemplate;

    public DistributedLock(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 尝试获取分布式锁
     *
     * @param lockKey    锁的key
     * @param lockValue  锁的值（用于标识锁的持有者）
     * @param expireTime 锁的过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String lockValue, long expireTime) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, expireTime, TimeUnit.SECONDS);
        return result != null && result;
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey   锁的key
     * @param lockValue 锁的值（必须与获取锁时的值一致才能释放）
     */
    public void unlock(String lockKey, String lockValue) {
        String currentValue = redisTemplate.opsForValue().get(lockKey);
        if (lockValue.equals(currentValue)) {
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 尝试获取分布式锁（带重试机制）
     *
     * @param lockKey      锁的key
     * @param lockValue    锁的值
     * @param expireTime   锁的过期时间（秒）
     * @param maxRetries   最大重试次数
     * @param retryDelayMs 重试间隔（毫秒）
     * @return 是否获取成功
     */
    public boolean tryLockWithRetry(String lockKey, String lockValue, long expireTime, int maxRetries, long retryDelayMs) {
        int retryCount = 0;
        boolean locked = tryLock(lockKey, lockValue, expireTime);
        
        while (!locked && retryCount < maxRetries) {
            try {
                Thread.sleep(retryDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            locked = tryLock(lockKey, lockValue, expireTime);
            retryCount++;
        }
        
        return locked;
    }

    /**
     * 生成唯一的锁值
     *
     * @return UUID字符串
     */
    public String generateLockValue() {
        return UUID.randomUUID().toString();
    }
}
