package com.cty.nopersonfinally.service;

import com.cty.nopersonfinally.pojo.dto.WalletRechargeDTO;
import com.cty.nopersonfinally.pojo.dto.WalletWithdrawDTO;
import com.cty.nopersonfinally.pojo.entity.BankCard;
import com.cty.nopersonfinally.pojo.vo.WalletInfoVO;

/**
 * 钱包服务接口
 */
public interface WalletService {

    /**
     * 获取钱包信息
     */
    WalletInfoVO getWalletInfo(Long userId);

    /**
     * 账户充值
     */
    void recharge(WalletRechargeDTO dto, Long userId);

    /**
     * 提现申请
     */
    void withdraw(WalletWithdrawDTO dto, Long userId);

    /**
     * 添加银行卡
     */
    void addBankCard(BankCard bankCard, Long userId);

    /**
     * 删除银行卡
     */
    void deleteBankCard(Long bankCardId, Long userId);

    /**
     * 设置默认银行卡
     */
    void setDefaultBankCard(Long bankCardId, Long userId);

    /**
     * 计算账户余额
     */
    Double calculateBalance(Long userId);
}
