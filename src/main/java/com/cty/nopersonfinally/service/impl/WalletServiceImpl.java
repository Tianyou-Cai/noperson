package com.cty.nopersonfinally.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cty.nopersonfinally.mapper.BankCardMapper;
import com.cty.nopersonfinally.mapper.TransactionRecordMapper;
import com.cty.nopersonfinally.pojo.dto.WalletRechargeDTO;
import com.cty.nopersonfinally.pojo.dto.WalletWithdrawDTO;
import com.cty.nopersonfinally.pojo.entity.BankCard;
import com.cty.nopersonfinally.pojo.entity.TransactionRecord;
import com.cty.nopersonfinally.pojo.vo.WalletInfoVO;
import com.cty.nopersonfinally.service.WalletService;
import com.cty.nopersonfinally.utils.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 钱包服务实现类
 */
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    @Autowired
    private TransactionRecordMapper transactionRecordMapper;

    @Autowired
    private BankCardMapper bankCardMapper;

    @Override
    public WalletInfoVO getWalletInfo(Long userId) {
        WalletInfoVO walletInfoVO = new WalletInfoVO();
        
        // 获取账户余额
        Double balance = calculateBalance(userId);
        walletInfoVO.setBalance(balance);
        
        // 获取交易记录
        List<TransactionRecord> transactions = transactionRecordMapper.selectList(
            new LambdaQueryWrapper<TransactionRecord>()
                .eq(TransactionRecord::getUserId, userId)
                .eq(TransactionRecord::getStatus, 1)
                .orderByDesc(TransactionRecord::getCreateTime)
                .last("LIMIT 20")
        );
        walletInfoVO.setTransactions(transactions);
        
        // 获取银行卡列表
        List<BankCard> bankCards = bankCardMapper.selectByUserId(userId);
        walletInfoVO.setBankCards(bankCards);
        
        return walletInfoVO;
    }

    @Override
    @Transactional
    public void recharge(WalletRechargeDTO dto, Long userId) {
        if (dto.getAmount() == null || dto.getAmount()< 100) {
            throw new BusinessException("充值金额不能少于100元");
        }
        
        // 创建充值交易记录
        TransactionRecord transaction = new TransactionRecord();
        transaction.setUserId(userId);
        transaction.setAmount(dto.getAmount());
        transaction.setTransactionType("INCOME");
        transaction.setStatus(1);
        transaction.setDescription("账户充值（" + dto.getPaymentMethod() + "）");
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setUpdateTime(LocalDateTime.now());
        
        transactionRecordMapper.insert(transaction);
    }

    @Override
    @Transactional
    public void withdraw(WalletWithdrawDTO dto, Long userId) {
        if (dto.getAmount() == null || dto.getAmount()< 100) {
            throw new BusinessException("提现金额不能少于100元");
        }
        
        // 检查余额是否充足
        Double balance = calculateBalance(userId);
        if (balance< dto.getAmount()) {
            throw new BusinessException("账户余额不足");
        }
        
        // 检查银行卡是否存在且属于该用户
        BankCard bankCard = bankCardMapper.selectById(dto.getBankCardId());
        if (bankCard == null) {
            throw new BusinessException("银行卡不存在");
        }
        if (!bankCard.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此银行卡");
        }
        
        // 创建提现交易记录
        TransactionRecord transaction = new TransactionRecord();
        transaction.setUserId(userId);
        transaction.setAmount(-dto.getAmount());
        transaction.setTransactionType("EXPENSE");
        transaction.setStatus(1);
        transaction.setDescription("提现到银行卡：" + bankCard.getBankName() + " " + maskCardNumber(bankCard.getCardNumber()));
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setUpdateTime(LocalDateTime.now());
        
        transactionRecordMapper.insert(transaction);
    }

    @Override
    @Transactional
    public void addBankCard(BankCard bankCard, Long userId) {
        if (bankCard.getBankName() == null || bankCard.getBankName().trim().isEmpty()) {
            throw new BusinessException("银行名称不能为空");
        }
        if (bankCard.getCardNumber() == null || bankCard.getCardNumber().trim().isEmpty()) {
            throw new BusinessException("银行卡号不能为空");
        }
        if (bankCard.getCardHolder() == null || bankCard.getCardHolder().trim().isEmpty()) {
            throw new BusinessException("持卡人姓名不能为空");
        }
        
        // 设置用户ID
        bankCard.setUserId(userId);
        
        // 如果是第一张卡，设为默认卡
        List<BankCard> existingCards = bankCardMapper.selectByUserId(userId);
        if (existingCards.isEmpty()) {
            bankCard.setIsDefault(true);
        } else {
            bankCard.setIsDefault(false);
        }
        
        bankCard.setCreateTime(LocalDateTime.now());
        bankCard.setUpdateTime(LocalDateTime.now());
        
        bankCardMapper.insert(bankCard);
    }

    @Override
    @Transactional
    public void deleteBankCard(Long bankCardId, Long userId) {
        BankCard bankCard = bankCardMapper.selectById(bankCardId);
        if (bankCard == null) {
            throw new BusinessException("银行卡不存在");
        }
        if (!bankCard.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此银行卡");
        }
        
        bankCardMapper.deleteById(bankCardId);
    }

    @Override
    @Transactional
    public void setDefaultBankCard(Long bankCardId, Long userId) {
        BankCard bankCard = bankCardMapper.selectById(bankCardId);
        if (bankCard == null) {
            throw new BusinessException("银行卡不存在");
        }
        if (!bankCard.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此银行卡");
        }
        
        // 先将所有卡设置为非默认
        BankCard updateCard = new BankCard();
        updateCard.setIsDefault(false);
        bankCardMapper.update(
            updateCard,
            new LambdaQueryWrapper<BankCard>()
                .eq(BankCard::getUserId, userId)
        );
        
        // 设置选中的卡为默认
        bankCard.setIsDefault(true);
        bankCard.setUpdateTime(LocalDateTime.now());
        bankCardMapper.updateById(bankCard);
    }

    @Override
    public Double calculateBalance(Long userId) {
        List<TransactionRecord> transactions = transactionRecordMapper.selectList(
            new LambdaQueryWrapper<TransactionRecord>()
                .eq(TransactionRecord::getUserId, userId)
                .eq(TransactionRecord::getStatus, 1)
        );
        
        return transactions.stream()
            .mapToDouble(TransactionRecord::getAmount)
            .sum();
    }

    /**
     * 隐藏银行卡号中间部分
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length()< 8) {
            return cardNumber;
        }
        String prefix = cardNumber.substring(0, 4);
        String suffix = cardNumber.substring(cardNumber.length() - 4);
        return prefix + "****" + suffix;
    }
}
