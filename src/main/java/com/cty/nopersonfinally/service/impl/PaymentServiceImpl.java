package com.cty.nopersonfinally.service.impl;


import com.cty.nopersonfinally.mapper.DemandMapper;
import com.cty.nopersonfinally.mapper.PaymentLogMapper;
import com.cty.nopersonfinally.mapper.PaymentOrderMapper;
import com.cty.nopersonfinally.mapper.SysUserMapper;
import com.cty.nopersonfinally.pojo.dto.PaymentDTO;
import com.cty.nopersonfinally.pojo.entity.OrderDemand;
import com.cty.nopersonfinally.pojo.entity.PaymentLog;
import com.cty.nopersonfinally.pojo.entity.PaymentOrder;
import com.cty.nopersonfinally.pojo.entity.SysUser;
import com.cty.nopersonfinally.pojo.enums.SprayDemandStatusEnum;
import com.cty.nopersonfinally.pojo.vo.PaymentVO;
import com.cty.nopersonfinally.service.AllDemandService;
import com.cty.nopersonfinally.service.PaymentService;
import com.cty.nopersonfinally.utils.BusinessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 支付服务实现类
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    // 手动声明日志对象
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private PaymentOrderMapper paymentOrderMapper;
    
    @Autowired
    private PaymentLogMapper paymentLogMapper;
    
    @Autowired
    private AllDemandService allDemandService;
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Autowired
    private DemandMapper demandMapper;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentVO payDemand(Long demandId, Long userId) {
        try {
            log.info("处理支付请求 - 需求ID: {}, 用户ID: {}", demandId, userId);
            
            // 1. 查询需求信息（使用带权限校验的方法）
            OrderDemand demand = allDemandService.getDemandById(demandId, userId, "farmer");
            log.info("成功获取需求信息 - 需求ID: {}, 农户ID: {}", demandId, demand.getFarmerId());
            // 如果需求不存在或无权限，getDemandById方法会抛出异常，这里不需要重复检查
            
            // 3. 检查支付状态
            if (demand.getPaymentStatus() == 1) {
                throw new BusinessException("需求已支付");
            }
            
            // 4. 创建支付DTO
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setPaymentMethod("WECHAT"); // 默认微信支付
            
            // 5. 创建支付订单
            PaymentVO paymentVO = createPayment(
                paymentDTO,
                userId,
                demandId.toString(),
                demand.getOrderType() == 1 ? "SPRAY" : "INSPECTION",
                demand.getPaymentAmount() != null ? demand.getPaymentAmount() : demand.getBudget()
            );
            
            // 6. 记录支付日志
            logPayment(userId, demandId.toString(), demand.getOrderType() == 1 ? "SPRAY" : "INSPECTION", 
                demand.getPaymentAmount() != null ? demand.getPaymentAmount() : demand.getBudget(),
                "WAITING", "WECHAT", paymentVO.getPaymentId().toString(), "创建支付订单");
            
            return paymentVO;
        } catch (BusinessException e) {
            log.error("支付需求失败: {}. 需求ID: {}, 用户ID: {}", e.getMessage(), demandId, userId, e);
            // 抛出异常时增加更详细的信息，帮助调试
            if ("无权限查看此需求".equals(e.getMessage())) {
                throw new BusinessException("无权限查看此需求 - 请确认您是否为该需求的创建者");
            }
            throw e;
        } catch (Exception e) {
            log.error("支付需求系统错误: {}", e.getMessage(), e);
            throw new BusinessException("支付失败，请稍后重试");
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentVO createPayment(PaymentDTO dto, Long userId, String orderId, String orderType, Double amount) {
        try {
            // 1. 检查是否已存在未完成的支付订单
            PaymentOrder existingOrder = paymentOrderMapper.selectByOrderIdAndType(orderId, orderType);
            if (existingOrder != null) {
                // 如果存在未完成的支付订单，直接返回，不再创建新订单
                if (!"SUCCESS".equals(existingOrder.getStatus())) {
                    log.info("订单 {} 已存在支付订单，直接返回", orderId);
                    PaymentVO paymentVO = new PaymentVO();
                    paymentVO.setPaymentId(existingOrder.getId());
                    paymentVO.setPaymentMethod(existingOrder.getPaymentMethod());
                    paymentVO.setStatus(existingOrder.getStatus());
                    paymentVO.setOutTradeNo(existingOrder.getOutTradeNo());
                    // 处理amount可能为null的情况
                    paymentVO.setTotalAmount(existingOrder.getAmount() != null ? existingOrder.getAmount().doubleValue() : amount);
                    return paymentVO;
                }
                throw new BusinessException("订单已支付");
            }
            
            // 2. 生成外部交易号
            String outTradeNo = generateOutTradeNo(userId, orderType);
            
            // 3. 创建支付订单
            PaymentOrder paymentOrder = new PaymentOrder();
            paymentOrder.setOrderId(orderId);
            paymentOrder.setOrderType(orderType);
            paymentOrder.setUserId(userId);
            paymentOrder.setAmount(amount != null ? BigDecimal.valueOf(amount) : BigDecimal.ZERO);
            paymentOrder.setPaymentMethod(dto != null ? dto.getPaymentMethod() : "UNKNOWN");
            paymentOrder.setOutTradeNo(outTradeNo);
            paymentOrder.setCreateTime(LocalDateTime.now());
            
            // 4. 设置支付状态
            String paymentMethod = dto != null ? dto.getPaymentMethod() : null;
            if ("WALLET".equalsIgnoreCase(paymentMethod)) {
                paymentOrder.setStatus("SUCCESS");
                paymentOrder.setPayTime(LocalDateTime.now());
            } else {
                // 其他支付方式，状态为等待
                paymentOrder.setStatus("WAITING");
            }
            
            // 5. 保存支付订单（必须先保存才能获取ID）
            if (paymentOrderMapper.insert(paymentOrder) <= 0) {
                throw new BusinessException("创建支付订单失败");
            }
            
            // 6. 如果是钱包支付，执行扣款和后续操作
            if ("WALLET".equalsIgnoreCase(paymentMethod)) {
                // 查询用户余额
                SysUser user = sysUserMapper.selectById(userId);
                if (user == null) {
                    throw new BusinessException("用户不存在");
                }
                
                Double balanceDouble = user.getBalance();
                BigDecimal balance = balanceDouble != null ? BigDecimal.valueOf(balanceDouble) : BigDecimal.ZERO;
                if (balance.compareTo(BigDecimal.valueOf(amount)) < 0) {
                    throw new BusinessException("钱包余额不足");
                }
                
                // 扣款
                user.setBalance(balance.subtract(BigDecimal.valueOf(amount)).doubleValue());
                sysUserMapper.updateById(user);
                
                // 记录支付日志（现在paymentOrder已经有ID了）
                logPayment(userId, orderId, orderType, amount, "SUCCESS", "WALLET", paymentOrder.getId().toString(), "Wallet payment success");
                
                // 更新需求状态为待接取
                updateDemandStatusAfterPayment(Long.parseLong(orderId));
            }
            
            // 6. 构建返回结果
            PaymentVO paymentVO = new PaymentVO();
            paymentVO.setPaymentId(paymentOrder.getId());
            paymentVO.setPaymentMethod(paymentOrder.getPaymentMethod());
            paymentVO.setStatus(paymentOrder.getStatus());
            
            if ("WALLET".equalsIgnoreCase(paymentMethod)) {
                paymentVO.setPayUrl("wallet://success");
            } else {
                paymentVO.setPayUrl("https://api.payment.example.com/pay?outTradeNo=" + outTradeNo);
            }
            
            return paymentVO;
        } catch (BusinessException e) {
            log.error("创建支付订单失败: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("创建支付订单系统错误: {}", e.getMessage(), e);
            throw new BusinessException("创建支付订单失败，请稍后重试");
        }
    }
    
    @Override
    public PaymentVO queryPaymentStatus(String orderType, String orderId, Long userId) {
        try {
            // 1. 查询支付订单
            PaymentOrder paymentOrder = paymentOrderMapper.selectByOrderIdAndType(orderId, orderType);
            if (paymentOrder == null) {
                throw new BusinessException("支付订单不存在");
            }
            
            // 2. 验证用户权限
            if (!paymentOrder.getUserId().equals(userId)) {
                throw new BusinessException("无权查询此支付状态");
            }
            
            // 3. 构建返回结果
            PaymentVO paymentVO = new PaymentVO();
            paymentVO.setPaymentId(paymentOrder.getId());
            paymentVO.setPaymentMethod(paymentOrder.getPaymentMethod());
            paymentVO.setStatus(paymentOrder.getStatus());
            paymentVO.setPayTime(paymentOrder.getPayTime());
            
            return paymentVO;
        } catch (BusinessException e) {
            log.error("查询支付状态失败: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("查询支付状态系统错误: {}", e.getMessage(), e);
            throw new BusinessException("查询支付状态失败，请稍后重试");
        }
    }
    
    /**
     * 生成外部交易号
     */
    public String generateOutTradeNo(Long userId, String orderType) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return orderType + "_" + userId + "_" + timestamp + "_" + uuid;
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
     * 记录支付日志
     */
    private void logPayment(Long userId, String orderId, String orderType, Double amount, 
                           String status, String  paymentMethod, String paymentId, String remark) {
        PaymentLog paymentLog = new PaymentLog();
        paymentLog.setUserId(userId);
        paymentLog.setOrderId(orderId);
        paymentLog.setOrderType(orderType);
        paymentLog.setAmount(BigDecimal.valueOf(amount));
        paymentLog.setStatus(status);
        paymentLog.setPaymentMethod(paymentMethod);
        paymentLog.setTransactionId(paymentId);
        paymentLog.setRemark(remark);
        paymentLog.setCreateTime(LocalDateTime.now());
        paymentLogMapper.insert(paymentLog);
    }
}