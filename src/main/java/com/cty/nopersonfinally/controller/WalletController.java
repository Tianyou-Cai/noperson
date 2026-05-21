package com.cty.nopersonfinally.controller;

import com.cty.nopersonfinally.pojo.dto.WalletRechargeDTO;
import com.cty.nopersonfinally.pojo.dto.WalletWithdrawDTO;
import com.cty.nopersonfinally.pojo.entity.BankCard;
import com.cty.nopersonfinally.pojo.dto.Result;
import com.cty.nopersonfinally.service.WalletService;
import com.cty.nopersonfinally.utils.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

/**
 * 钱包控制器
 */
@RestController
@RequestMapping("/owner/wallet")
@Tag(name = "钱包管理", description = "机主钱包相关接口")
public class WalletController {

    @Autowired
    private WalletService walletService;

    /**
     * 获取钱包信息
     */
    @GetMapping("/info")
    
    @Operation(summary = "获取钱包信息", description = "获取账户余额、交易记录和银行卡列表")
    public Result<?> getWalletInfo(@RequestHeader("Authorization") String token) {
        // 从token中获取用户ID
        Long userId = JWTUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("无效的token");
        }
        
        return Result.ok(walletService.getWalletInfo(userId));
    }

    /**
     * 账户充值
     */
    @PostMapping("/recharge")
    
    @Operation(summary = "账户充值", description = "向账户充值")
    public Result<?> recharge(
            @RequestBody WalletRechargeDTO dto,
            @RequestHeader("Authorization") String token) {
        // 从token中获取用户ID
        Long userId = JWTUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("无效的token");
        }
        
        walletService.recharge(dto, userId);
        return Result.ok("充值成功");
    }

    /**
     * 提现申请
     */
    @PostMapping("/withdraw")
    
    @Operation(summary = "提现申请", description = "申请提现到银行卡")
    public Result<?> withdraw(
            @RequestBody WalletWithdrawDTO dto,
            @RequestHeader("Authorization") String token) {
        // 从token中获取用户ID
        Long userId = JWTUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("无效的token");
        }
        
        walletService.withdraw(dto, userId);
        return Result.ok("提现申请已提交");
    }

    /**
     * 添加银行卡
     */
    @PostMapping("/bank-card/add")
    
    @Operation(summary = "添加银行卡", description = "添加提现银行卡")
    public Result<?> addBankCard(
            @RequestBody BankCard bankCard,
            @RequestHeader("Authorization") String token) {
        // 从token中获取用户ID
        Long userId = JWTUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("无效的token");
        }
        
        walletService.addBankCard(bankCard, userId);
        return Result.ok("银行卡添加成功");
    }

    /**
     * 删除银行卡
     */
    @DeleteMapping("/bank-card/{bankCardId}")
    
    @Operation(summary = "删除银行卡", description = "删除提现银行卡")
    public Result<?> deleteBankCard(
            @PathVariable Long bankCardId,
            @RequestHeader("Authorization") String token) {
        // 从token中获取用户ID
        Long userId = JWTUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("无效的token");
        }
        
        walletService.deleteBankCard(bankCardId, userId);
        return Result.ok("银行卡删除成功");
    }

    /**
     * 设置默认银行卡
     */
    @PutMapping("/bank-card/{bankCardId}/default")
    
    @Operation(summary = "设置默认银行卡", description = "设置默认提现银行卡")
    public Result<?> setDefaultBankCard(
            @PathVariable Long bankCardId,
            @RequestHeader("Authorization") String token) {
        // 从token中获取用户ID
        Long userId = JWTUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error("无效的token");
        }
        
        walletService.setDefaultBankCard(bankCardId, userId);
        return Result.ok("默认银行卡设置成功");
    }
}
