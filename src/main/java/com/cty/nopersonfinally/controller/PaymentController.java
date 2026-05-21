package com.cty.nopersonfinally.controller;


import com.cty.nopersonfinally.pojo.dto.PaymentDTO;
import com.cty.nopersonfinally.pojo.dto.Result;
import com.cty.nopersonfinally.pojo.vo.PaymentVO;
import com.cty.nopersonfinally.service.AlipayService;
import com.cty.nopersonfinally.service.PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/payment")
@Api(tags = "支付管理")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private AlipayService alipayService;
    
    /**
     * 需求支付接口
     * @param demandId 需求ID
     * @param request 请求对象（用于获取用户信息）
     * @return 支付结果
     */
    @PostMapping("/demand/{demandId}")
    @ApiOperation("支付需求费用")
    public Result<?> payDemand(
            @ApiParam(value = "需求ID", required = true) @PathVariable Long demandId,
            HttpServletRequest request) {
        try {
            // 从请求中获取用户ID（实际项目中应从JWT token中解析）
            Long userId = (Long) request.getAttribute("userId");
            
            PaymentVO paymentVO = paymentService.payDemand(demandId, userId);
            return Result.ok(paymentVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 创建支付订单接口（支持钱包支付）
     * @param params 支付参数（包含orderType, orderId, amount, paymentMethod）
     * @return 支付订单信息
     */
    @PostMapping("/create")
    @ApiOperation("创建支付订单")
    public Result<?> createPayment(
            @ApiParam(value = "支付信息", required = true) @RequestBody Map<String, Object> params) {
        try {
            // 从SecurityContext获取当前登录用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.error("请先登录");
            }
            
            String orderType = (String) params.get("orderType");
            String orderId = (String) params.get("orderId");
            Double amount = params.get("amount") != null ? ((Number) params.get("amount")).doubleValue() : 0.0;
            String paymentMethod = (String) params.get("paymentMethod");
            
            if (paymentMethod == null) {
                paymentMethod = "WALLET"; // 默认钱包支付
            }
            
            PaymentDTO dto = new PaymentDTO();
            dto.setPaymentMethod(paymentMethod);
            
            PaymentVO paymentVO = paymentService.createPayment(dto, userId, orderId, orderType, amount);
            return Result.ok(paymentVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询支付状态接口
     * @param orderType 订单类型
     * @param orderId 订单ID
     * @param request 请求对象（用于获取用户信息）
     * @return 支付状态信息
     */
    @GetMapping("/status")
    @ApiOperation("查询支付状态")
    public Result<?> queryPaymentStatus(
            @ApiParam(value = "订单类型", required = true) @RequestParam String orderType,
            @ApiParam(value = "订单ID", required = true) @RequestParam String orderId,
            HttpServletRequest request) {
        try {
            // 从请求中获取用户ID（实际项目中应从JWT token中解析）
            Long userId = (Long) request.getAttribute("userId");
            
            PaymentVO paymentVO = paymentService.queryPaymentStatus(orderType, orderId, userId);
            return Result.ok(paymentVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 创建支付宝沙箱支付订单
     */
    @PostMapping("/alipay/create")
    @ApiOperation("创建支付宝沙箱支付订单")
    public Result<?> createAlipayOrder(
            @ApiParam(value = "支付信息", required = true) @RequestBody Map<String, Object> params) {
        try {
            // 从SecurityContext获取当前登录用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.error("请先登录");
            }
            
            String orderType = (String) params.get("orderType");
            String orderId = (String) params.get("orderId");
            Double amount = params.get("amount") != null ? ((Number) params.get("amount")).doubleValue() : 0.0;
            String orderTitle = (String) params.get("orderTitle");
            
            PaymentVO paymentVO = alipayService.createAlipayOrder(orderType, orderId, amount, orderTitle, userId);
            return Result.ok(paymentVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 支付宝沙箱支付通知处理
     */
    @PostMapping("/alipay/notify")
    @ApiOperation("支付宝沙箱支付通知")
    public Result<?> alipayNotify(
            @ApiParam(value = "支付通知信息", required = true) @RequestBody Map<String, Object> params) {
        try {
            String outTradeNo = (String) params.get("outTradeNo");
            String tradeStatus = (String) params.get("tradeStatus");
            
            boolean result = alipayService.handleAlipayNotify(outTradeNo, tradeStatus);
            if (result) {
                return Result.ok("支付成功");
            } else {
                return Result.error("支付失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询支付宝订单状态
     */
    @GetMapping("/alipay/status")
    @ApiOperation("查询支付宝订单状态")
    public Result<?> queryAlipayStatus(
            @ApiParam(value = "外部交易号", required = true) @RequestParam String outTradeNo) {
        try {
            String status = alipayService.queryAlipayOrder(outTradeNo);
            return Result.ok(status);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 从SecurityContext获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal != null) {
                try {
                    return Long.parseLong(principal.toString());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }
}