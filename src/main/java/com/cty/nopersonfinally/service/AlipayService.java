package com.cty.nopersonfinally.service;

import com.cty.nopersonfinally.pojo.vo.PaymentVO;

/**
 * 支付宝支付服务接口
 */
public interface AlipayService {
    
    /**
     * 创建支付宝沙箱支付订单
     * @param orderType 订单类型
     * @param orderId 订单ID
     * @param amount 支付金额
     * @param orderTitle 订单标题
     * @param userId 用户ID
     * @return 支付订单信息
     */
    PaymentVO createAlipayOrder(String orderType, String orderId, Double amount, String orderTitle, Long userId);
    
    /**
     * 处理支付宝支付通知
     * @param outTradeNo 外部交易号
     * @param tradeStatus 交易状态
     * @return 处理结果
     */
    boolean handleAlipayNotify(String outTradeNo, String tradeStatus);
    
    /**
     * 查询支付宝订单状态
     * @param outTradeNo 外部交易号
     * @return 支付状态
     */
    String queryAlipayOrder(String outTradeNo);
}