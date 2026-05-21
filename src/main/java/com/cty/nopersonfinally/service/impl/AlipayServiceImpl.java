package com.cty.nopersonfinally.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.cty.nopersonfinally.config.AlipayConfig;
import com.cty.nopersonfinally.mapper.DemandMapper;
import com.cty.nopersonfinally.mapper.PaymentOrderMapper;
import com.cty.nopersonfinally.pojo.entity.OrderDemand;
import com.cty.nopersonfinally.pojo.entity.PaymentOrder;
import com.cty.nopersonfinally.pojo.enums.SprayDemandStatusEnum;
import com.cty.nopersonfinally.pojo.vo.PaymentVO;
import com.cty.nopersonfinally.service.AlipayService;
import com.cty.nopersonfinally.utils.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 支付宝沙箱支付服务实现类
 */
@Service
public class AlipayServiceImpl implements AlipayService {
    
    private static final Logger log = LoggerFactory.getLogger(AlipayServiceImpl.class);
    
    @Autowired
    private AlipayConfig alipayConfig;
    
    @Autowired
    private PaymentOrderMapper paymentOrderMapper;
    
    @Autowired
    private DemandMapper demandMapper;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentVO createAlipayOrder(String orderType, String orderId, Double amount, String orderTitle, Long userId) {
        try {
            log.info("创建支付宝沙箱支付订单 - 订单类型: {}, 订单ID: {}, 金额: {}, 用户ID: {}", 
                orderType, orderId, amount, userId);
            
            // 生成外部交易号
            String outTradeNo = generateOutTradeNo(userId, orderType);
            
            // 创建AlipayClient
            AlipayClient alipayClient = new DefaultAlipayClient(
                alipayConfig.getGatewayUrl(),
                alipayConfig.getAppId(),
                alipayConfig.getPrivateKey(),
                "json",
                alipayConfig.getCharset(),
                alipayConfig.getPublicKey(),
                alipayConfig.getSignType()
            );
            
            // 创建预下单请求
            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
            request.setNotifyUrl("http://localhost:8082/api/payment/alipay/notify");
            
            String bizContent = String.format(
                "{\"out_trade_no\":\"%s\",\"total_amount\":\"%.2f\",\"subject\":\"%s\"}",
                outTradeNo,
                amount,
                orderTitle
            );
            request.setBizContent(bizContent);
            
            // 调用支付宝API生成二维码
            AlipayTradePrecreateResponse response = alipayClient.execute(request);
            
            if (!response.isSuccess()) {
                log.error("支付宝预下单失败 - 错误码: {}, 错误信息: {}", 
                    response.getCode(), response.getMsg());
                throw new BusinessException("创建支付宝订单失败：" + response.getMsg());
            }
            
            String qrCodeUrl = response.getQrCode();
            log.info("支付宝预下单成功 - 外部交易号: {}, 二维码链接: {}", outTradeNo, qrCodeUrl);
            
            // 创建支付订单记录
            PaymentOrder paymentOrder = new PaymentOrder();
            paymentOrder.setOrderId(orderId);
            paymentOrder.setOrderType(orderType);
            paymentOrder.setUserId(userId);
            paymentOrder.setAmount(BigDecimal.valueOf(amount));
            paymentOrder.setPaymentMethod("ALIPAY");
            paymentOrder.setOutTradeNo(outTradeNo);
            paymentOrder.setStatus("WAITING");
            paymentOrder.setCreateTime(LocalDateTime.now());
            
            // 保存支付订单
            if (paymentOrderMapper.insert(paymentOrder) <= 0) {
                throw new BusinessException("创建支付宝订单失败");
            }
            
            // 构建返回结果
            PaymentVO paymentVO = new PaymentVO();
            paymentVO.setPaymentId(paymentOrder.getId());
            paymentVO.setPaymentMethod("ALIPAY");
            paymentVO.setOutTradeNo(outTradeNo);
            paymentVO.setTotalAmount(amount);
            paymentVO.setOrderTitle(orderTitle);
            paymentVO.setQrCode(qrCodeUrl);
            
            log.info("支付宝沙箱订单创建成功 - 外部交易号: {}", outTradeNo);
            return paymentVO;
            
        } catch (AlipayApiException e) {
            log.error("支付宝API调用失败: {}", e.getMessage(), e);
            throw new BusinessException("创建支付宝订单失败：" + e.getMessage());
        } catch (BusinessException e) {
            log.error("创建支付宝订单失败: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("创建支付宝订单系统错误: {}", e.getMessage(), e);
            throw new BusinessException("创建支付宝订单失败，请稍后重试");
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAlipayNotify(String outTradeNo, String tradeStatus) {
        try {
            log.info("处理支付宝支付通知 - 外部交易号: {}, 交易状态: {}", outTradeNo, tradeStatus);
            
            // 根据外部交易号查询支付订单
            PaymentOrder paymentOrder = paymentOrderMapper.selectByOutTradeNo(outTradeNo);
            if (paymentOrder == null) {
                throw new BusinessException("支付订单不存在");
            }
            
            // 更新支付订单状态
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                paymentOrder.setStatus("SUCCESS");
                paymentOrder.setPayTime(LocalDateTime.now());
                paymentOrder.setTransactionId("ALIPAY" + System.currentTimeMillis());
                
                int result = paymentOrderMapper.updateById(paymentOrder);
                if (result > 0) {
                    log.info("支付订单状态更新成功 - 外部交易号: {}, 状态: SUCCESS", outTradeNo);
                    
                    // 更新需求状态为待接取
                    updateDemandStatusAfterPayment(Long.parseLong(paymentOrder.getOrderId()));
                    
                    return true;
                }
            } else if ("TRADE_CLOSED".equals(tradeStatus)) {
                paymentOrder.setStatus("FAILED");
                paymentOrderMapper.updateById(paymentOrder);
                log.info("支付订单状态更新为失败 - 外部交易号: {}", outTradeNo);
            }
            
            return false;
            
        } catch (BusinessException e) {
            log.error("处理支付宝通知失败: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("处理支付宝通知系统错误: {}", e.getMessage(), e);
            throw new BusinessException("处理支付通知失败，请稍后重试");
        }
    }
    
    @Override
    public String queryAlipayOrder(String outTradeNo) {
        try {
            PaymentOrder paymentOrder = paymentOrderMapper.selectByOutTradeNo(outTradeNo);
            if (paymentOrder == null) {
                return "NOT_EXIST";
            }
            return paymentOrder.getStatus();
        } catch (Exception e) {
            log.error("查询支付宝订单状态失败: {}", e.getMessage(), e);
            return "UNKNOWN";
        }
    }
    
    /**
     * 支付成功后更新需求状态为待接取
     */
    private void updateDemandStatusAfterPayment(Long demandId) {
        try {
            OrderDemand demand = demandMapper.selectById(demandId);
            if (demand != null) {
                demand.setStatus(SprayDemandStatusEnum.PENDING.getCode());
                demand.setPaymentStatus(1);
                demand.setPaymentTime(LocalDateTime.now());
                demand.setUpdateTime(LocalDateTime.now());
                demandMapper.updateById(demand);
                
                // 删除Redis超时键，防止订单被自动取消
                String timeoutKey = "order:timeout:" + demandId;
                stringRedisTemplate.delete(timeoutKey);
                
                log.info("需求 {} 支付成功，状态已更新为待接取，Redis超时键已删除", demandId);
            }
        } catch (Exception e) {
            log.error("更新需求状态失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 生成外部交易号
     */
    private String generateOutTradeNo(Long userId, String orderType) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "ALIPAY_" + orderType + "_" + userId + "_" + timestamp + "_" + uuid;
    }
}