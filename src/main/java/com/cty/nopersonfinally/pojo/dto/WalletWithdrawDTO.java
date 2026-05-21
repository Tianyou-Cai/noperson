package com.cty.nopersonfinally.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 钱包提现DTO
 */
@Data
@Schema(description = "钱包提现请求参数")
public class WalletWithdrawDTO {

    @Schema(description = "提现金额", required = true)
    private Double amount;

    @Schema(description = "银行卡ID", required = true)
    private Long bankCardId;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getBankCardId() {
        return bankCardId;
    }

    public void setBankCardId(Long bankCardId) {
        this.bankCardId = bankCardId;
    }
}
