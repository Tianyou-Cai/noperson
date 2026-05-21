package com.cty.nopersonfinally.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cty.nopersonfinally.pojo.entity.BankCard;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 银行卡Mapper接口
 */
@Mapper
public interface BankCardMapper extends BaseMapper<BankCard> {

    /**
     * 根据用户ID查询银行卡列表
     */
    List<BankCard> selectByUserId(Long userId);

    /**
     * 根据用户ID查询默认银行卡
     */
    BankCard selectDefaultByUserId(Long userId);
}
