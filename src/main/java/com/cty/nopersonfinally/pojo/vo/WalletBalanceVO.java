package com.cty.nopersonfinally.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 钱包余额VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalanceVO {
    
    /**
     * 余额
     */
    private Double balance;
}