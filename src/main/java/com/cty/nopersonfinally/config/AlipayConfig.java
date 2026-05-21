package com.cty.nopersonfinally.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝沙箱支付配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "alipay.sandbox")
public class AlipayConfig {
    
    /**
     * 应用ID
     */
    private String appId;
    
    /**
     * 应用私钥
     */
    private String privateKey;
    
    /**
     * 支付宝公钥
     */
    private String publicKey;
    
    /**
     * 支付宝网关
     */
    private String gatewayUrl;
    
    /**
     * 签名类型
     */
    private String signType = "RSA2";
    
    /**
     * 字符编码
     */
    private String charset = "UTF-8";
}