package com.cty.nopersonfinally.pojo.vo;

import com.cty.nopersonfinally.pojo.entity.BankCard;
import com.cty.nopersonfinally.pojo.entity.TransactionRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 钱包信息VO
 */
@Data
@Schema(description = "钱包信息")
public class WalletInfoVO {

    @Schema(description = "账户余额")
    private Double balance;

    @Schema(description = "交易记录列表")
    private List<TransactionRecord> transactions;

    @Schema(description = "银行卡列表")
    private List<BankCard> bankCards;

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public List<TransactionRecord> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionRecord> transactions) {
        this.transactions = transactions;
    }

    public List<BankCard> getBankCards() {
        return bankCards;
    }

    public void setBankCards(List<BankCard> bankCards) {
        this.bankCards = bankCards;
    }
}
