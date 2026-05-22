package com.cty.nopersonfinally.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisListenerConfig {
    
    private static final Logger log = LoggerFactory.getLogger(RedisListenerConfig.class);
    
    @Autowired
    private RedisKeyExpirationListener redisKeyExpirationListener;
    
    @Autowired(required = false)
    private RedisConnectionFactory redisConnectionFactory;
    
    /**
     * 初始化Redis配置，确保键过期事件通知已开启
     */
    @PostConstruct
    public void init() {
        if (redisConnectionFactory == null) {
            log.warn("RedisConnectionFactory未配置，跳过Redis键过期事件配置");
            return;
        }
        
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            // 检查当前配置
            java.util.Properties configProps = connection.getConfig("notify-keyspace-events");
            String currentConfig = configProps != null ? configProps.getProperty("notify-keyspace-events") : null;
            log.info("当前Redis notify-keyspace-events配置: {}", currentConfig);
            
            // 如果未配置或配置不包含过期事件，设置为包含过期事件的配置
            if (currentConfig == null || currentConfig.isEmpty() || !currentConfig.contains("E")) {
                // Ex: 发送key过期事件通知
                connection.setConfig("notify-keyspace-events", "Ex");
                log.info("Redis notify-keyspace-events已设置为: Ex");
            }
        } catch (Exception e) {
            log.warn("配置Redis notify-keyspace-events失败(可能Redis未运行): {}", e.getMessage());
        }
    }
    
    /**
     * 配置Redis消息监听容器，订阅键过期事件
     */
    @Bean
    @Lazy
    public RedisMessageListenerContainer container() {
        if (redisConnectionFactory == null) {
            log.warn("RedisConnectionFactory未配置，跳过Redis消息监听容器创建");
            return null;
        }
        
        try {
            RedisMessageListenerContainer container = new RedisMessageListenerContainer();
            container.setConnectionFactory(redisConnectionFactory);
            // 订阅__keyevent@0__:expired频道，监听db0的键过期事件
            container.addMessageListener(redisKeyExpirationListener, new PatternTopic("__keyevent@0__:expired"));
            return container;
        } catch (Exception e) {
            log.warn("创建Redis消息监听容器失败(可能Redis未运行): {}", e.getMessage());
            return null;
        }
    }
}