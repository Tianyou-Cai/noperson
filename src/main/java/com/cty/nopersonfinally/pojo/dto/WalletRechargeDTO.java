package com.cty.nopersonfinally.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 钱包充值DTO
 */
@Data
@Schema(description = "钱包充值请求参数")
public class WalletRechargeDTO {

    @Schema(description = "充值金额", required = true)
    private Double amount;

    @Schema(description = "支付方式（alipay/wechat）", required = true)
    private String paymentMethod;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
