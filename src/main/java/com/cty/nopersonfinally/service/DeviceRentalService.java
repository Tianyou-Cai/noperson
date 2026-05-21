package com.cty.nopersonfinally.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cty.nopersonfinally.pojo.dto.DeviceRentalDTO;
import com.cty.nopersonfinally.pojo.entity.DeviceRental;
import java.math.BigDecimal;
import java.util.List;

/**
 * 设备租借记录服务接口
 */
public interface DeviceRentalService extends IService<DeviceRental> {
    
    /**
     * 创建租借记录
     */
    DeviceRental createRentalRecord(Long deviceId, Long flyerId, Long ownerId);
    
    /**
     * 完成归还操作，更新租借记录
     */
    boolean completeReturn(Long rentalId);
    
    /**
     * 获取设备当前租借记录
     */
    DeviceRental getCurrentRentalByDeviceId(Long deviceId);
    
    /**
     * 获取飞手租借历史记录
     */
    List<DeviceRental> getRentalHistoryByFlyerId(Long flyerId, Integer limit);
    
    /**
     * 获取设备租借历史记录
     */
    List<DeviceRental> getRentalHistoryByDeviceId(Long deviceId, Integer limit);
    
    /**
     * 根据飞手ID和设备ID获取正在进行的租借记录
     */
    DeviceRental getActiveRental(Long deviceId, Long flyerId);

    /**
     * 创建租借记录（带租金计算）
     */
    DeviceRental createRentalRecord(DeviceRentalDTO dto, Long flyerId, Long ownerId);

    /**
     * 计算租金
     */
    BigDecimal calculateRentalAmount(Long deviceId, Integer rentalHours);
    
    /**
     * 确认支付租赁费用
     */
    boolean confirmPayment(Long rentalId);
}