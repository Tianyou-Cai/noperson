package com.cty.nopersonfinally.config;

import com.cty.nopersonfinally.mapper.DemandMapper;
import com.cty.nopersonfinally.pojo.entity.OrderDemand;
import com.cty.nopersonfinally.pojo.enums.SprayDemandStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RedisKeyExpirationListener implements MessageListener {
    
    private static final Logger log = LoggerFactory.getLogger(RedisKeyExpirationListener.class);
    
    // Redis键前缀
    private static final String ORDER_TIMEOUT_PREFIX = "order:timeout:";
    
    @Autowired
    private DemandMapper demandMapper;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = new String(message.getBody());
        log.debug("Redis键过期: {}", expiredKey);
        
        // 判断是否为订单超时键
        if (expiredKey.startsWith(ORDER_TIMEOUT_PREFIX)) {
            try {
                // 提取订单ID
                String demandIdStr = expiredKey.substring(ORDER_TIMEOUT_PREFIX.length());
                Long demandId = Long.parseLong(demandIdStr);
                
                // 查询订单状态
                OrderDemand demand = demandMapper.selectById(demandId);
                if (demand != null) {
                    // 如果订单仍处于待支付状态，自动取消
                    if (demand.getStatus() == SprayDemandStatusEnum.PENDING_PAY.getCode() 
                        && demand.getPaymentStatus() == 0) {
                        
                        // 更新订单状态为已取消
                        demand.setStatus(SprayDemandStatusEnum.CANCELLED.getCode());
                        demand.setUpdateTime(LocalDateTime.now());
                        demandMapper.updateById(demand);
                        
                        log.info("订单 {} 超时未支付，已自动取消", demandId);
                    } else {
                        // 订单已支付或已取消，不需要处理
                        log.debug("订单 {} 状态已变更，无需自动取消", demandId);
                    }
                } else {
                    log.warn("订单 {} 不存在", demandId);
                }
            } catch (NumberFormatException e) {
                log.error("解析订单ID失败: {}", expiredKey, e);
            } catch (Exception e) {
                log.error("处理订单超时失败: {}", expiredKey, e);
            }
        }
    }
}