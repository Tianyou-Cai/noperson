package com.cty.nopersonfinally.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cty.nopersonfinally.mapper.DeviceRentalMapper;
import com.cty.nopersonfinally.pojo.dto.DeviceRentalDTO;
import com.cty.nopersonfinally.pojo.entity.DeviceRental;
import com.cty.nopersonfinally.pojo.entity.DroneDevice;
import com.cty.nopersonfinally.service.DeviceRentalService;
import com.cty.nopersonfinally.service.DeviceService;
import com.cty.nopersonfinally.service.UserInfoService;
import com.cty.nopersonfinally.service.TransactionRecordService;
import com.cty.nopersonfinally.service.FlyerEvaluationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 设备租借记录服务实现类
 */
@Service
public class DeviceRentalServiceImpl extends ServiceImpl<DeviceRentalMapper, DeviceRental> implements DeviceRentalService {
    
    @Resource
    private DeviceRentalMapper deviceRentalMapper;
    
    @Resource
    private DeviceService deviceService;
    
    @Resource
    private UserInfoService userInfoService;
    
    @Resource
    private TransactionRecordService transactionRecordService;
    
    @Resource
    private FlyerEvaluationService flyerEvaluationService;
    
    @Override
    @Transactional
    public DeviceRental createRentalRecord(Long deviceId, Long flyerId, Long ownerId) {
        DeviceRental rental = new DeviceRental();
        rental.setDeviceId(deviceId);
        rental.setFlyerId(flyerId);
        rental.setOwnerId(ownerId);
        rental.setRentalStartTime(new Date());
        rental.setRentalStatus(1); // 1-租借中
        rental.setPaymentStatus(0); // 0-未支付
        
        // 插入租借记录
        save(rental);
        
        return rental;
    }
    
    @Override
    @Transactional
    public boolean completeReturn(Long rentalId) {
        return deviceRentalMapper.updateRentalStatus(rentalId, 2, new Date()) > 0; // 2-已归还
    }
    
    @Override
    public DeviceRental getCurrentRentalByDeviceId(Long deviceId) {
        return deviceRentalMapper.getCurrentRentalByDeviceId(deviceId);
    }
    
    @Override
    public List<DeviceRental> getRentalHistoryByFlyerId(Long flyerId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认查询10条
        }
        return deviceRentalMapper.getRentalHistoryByFlyerId(flyerId, limit);
    }
    
    @Override
    public List<DeviceRental> getRentalHistoryByDeviceId(Long deviceId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认查询10条
        }
        return deviceRentalMapper.getRentalHistoryByDeviceId(deviceId, limit);
    }
    
    @Override
    public DeviceRental getActiveRental(Long deviceId, Long flyerId) {
        // 使用QueryWrapper查询飞手和设备的有效租借记录（状态为1-租借中）
        QueryWrapper<DeviceRental> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_id", deviceId)
                   .eq("flyer_id", flyerId)
                   .eq("rental_status", 1); // 1表示租借中
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("LIMIT 1");
        
        return deviceRentalMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public DeviceRental createRentalRecord(DeviceRentalDTO dto, Long flyerId, Long ownerId) {
        // 计算租金
        BigDecimal rentalAmount = calculateRentalAmount(dto.getDeviceId(), dto.getRentalHours());
        
        // 创建租借记录（未支付状态）
        DeviceRental rental = new DeviceRental();
        rental.setDeviceId(dto.getDeviceId());
        rental.setFlyerId(flyerId);
        rental.setOwnerId(ownerId);
        rental.setRentalStartTime(dto.getPickupTime()); // 使用取设备时间作为开始时间
        // 计算结束时间（开始时间 + 租赁时长）
        Date endTime = new Date(dto.getPickupTime().getTime() + dto.getRentalHours() * 60 * 60 * 1000L);
        rental.setRentalEndTime(endTime);
        rental.setRentalStatus(1); // 1-租借中
        rental.setRentalAmount(rentalAmount);
        rental.setPaymentStatus(0); // 0-未支付
        rental.setRemark(dto.getRemark());
        
        // 插入租借记录
        save(rental);
        
        return rental;
    }

    @Override
    public BigDecimal calculateRentalAmount(Long deviceId, Integer rentalHours) {
        // 获取设备信息
        DroneDevice device = deviceService.getById(deviceId);
        if (device == null) {
            throw new RuntimeException("设备不存在");
        }
        
        // 获取设备每小时租金
        BigDecimal hourlyRent = device.getHourlyRent();
        if (hourlyRent == null || hourlyRent.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("设备未设置租金");
        }
        
        // 计算总租金：每小时租金 * 租赁时长
        return hourlyRent.multiply(BigDecimal.valueOf(rentalHours));
    }
    
    @Override
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public boolean confirmPayment(Long rentalId) {
        System.out.println("====== 开始执行支付确认 ======");
        System.out.println("租赁记录ID: " + rentalId);
        
        // 查询租赁记录
        DeviceRental rental = getById(rentalId);
        if (rental == null) {
            System.out.println("租赁记录不存在");
            throw new RuntimeException("租赁记录不存在");
        }
        
        System.out.println("租赁记录信息: deviceId=" + rental.getDeviceId() + 
                         ", flyerId=" + rental.getFlyerId() + 
                         ", ownerId=" + rental.getOwnerId() + 
                         ", rentalAmount=" + rental.getRentalAmount() + 
                         ", paymentStatus=" + rental.getPaymentStatus());
        
        // 检查支付状态
        if (rental.getPaymentStatus() != 0) {
            System.out.println("该租赁记录已支付或支付失败，当前状态: " + rental.getPaymentStatus());
            throw new RuntimeException("该租赁记录已支付或支付失败");
        }
        
        // 获取飞手ID、机主ID和租金金额
        Long flyerId = rental.getFlyerId();
        Long ownerId = rental.getOwnerId();
        BigDecimal rentalAmount = rental.getRentalAmount();
        
        if (rentalAmount == null || rentalAmount.compareTo(BigDecimal.ZERO)<= 0) {
            System.out.println("租金金额无效: " + rentalAmount);
            throw new RuntimeException("租金金额无效");
        }
        
        try {
            // 检查飞手好评率，判断是否需要押金
            System.out.println("检查飞手好评率...");
            Double positiveRateObj = flyerEvaluationService.getPositiveRate(flyerId);
            double positiveRate = positiveRateObj != null ? positiveRateObj : 0.0;
            System.out.println("飞手好评率: " + positiveRate);
            
            // 计算需要支付的总金额（租金 + 押金）
            double rentPrice = rentalAmount.doubleValue();
            double totalAmount = rentPrice;
            double depositAmount = 0;
            
            // 如果好评率<=50，需要押金
            if (positiveRate <= 50) {
                depositAmount = 500.0;
                totalAmount += depositAmount;
                System.out.println("飞手好评率<=50，需要500押金，总计: " + totalAmount + "元");
            } else {
                System.out.println("飞手好评率>50，无需押金，总计: " + totalAmount + "元");
            }
            
            System.out.println("开始支付，金额: " + totalAmount + "元");
            
            // 获取飞手当前余额
            Double flyerBalance = userInfoService.getUserBalance(flyerId);
            System.out.println("飞手当前余额: " + flyerBalance + "元");
            
            // 检查飞手余额是否足够
            if (flyerBalance< totalAmount) {
                throw new RuntimeException("余额不足，请充值！");
            }
            
            // 扣除飞手账户余额
            System.out.println("开始扣除飞手余额...");
            boolean flyerSuccess = userInfoService.updateUserBalance(flyerId, -totalAmount);
            System.out.println("飞手余额扣除结果: " + flyerSuccess);
            
            if (!flyerSuccess) {
                throw new RuntimeException("飞手余额扣除失败");
            }
            
            // 获取机主当前余额
            Double ownerBalance = userInfoService.getUserBalance(ownerId);
            System.out.println("机主当前余额: " + ownerBalance + "元");
            
            // 将租金添加到机主账户余额（押金不计入机主余额）
            System.out.println("开始添加机主余额...");
            boolean ownerSuccess = userInfoService.updateUserBalance(ownerId, rentPrice);
            System.out.println("机主余额添加结果: " + ownerSuccess);
            
            if (!ownerSuccess) {
                throw new RuntimeException("机主余额添加失败");
            }
            
            // 将设备绑定到飞手（设置设备状态为已租借）
            System.out.println("开始绑定设备到飞手...");
            System.out.println("设备ID: " + rental.getDeviceId() + ", 飞手ID: " + flyerId);
            
            // 先查询设备当前状态
            DroneDevice deviceBefore = deviceService.getById(rental.getDeviceId());
            System.out.println("绑定前设备状态: flyerId=" + deviceBefore.getFlyerId() + ", rentalStatus=" + deviceBefore.getRentalStatus());
            
            int bindResult = deviceService.bindDeviceToFlyer(rental.getDeviceId(), flyerId);
            System.out.println("设备绑定结果: " + bindResult);
            
            // 查询绑定后的设备状态
            DroneDevice deviceAfter = deviceService.getById(rental.getDeviceId());
            System.out.println("绑定后设备状态: flyerId=" + deviceAfter.getFlyerId() + ", rentalStatus=" + deviceAfter.getRentalStatus());
            
            if (bindResult<= 0) {
                throw new RuntimeException("设备绑定失败，绑定结果: " + bindResult);
            }
            
            // 更新支付状态
            System.out.println("开始更新支付状态...");
            rental.setPaymentStatus(1); // 1-已支付
            rental.setPaymentTime(new Date()); // 设置支付时间
            
            // 保存更新
            boolean updateResult = updateById(rental);
            System.out.println("支付状态更新结果: " + updateResult);
            
            System.out.println("====== 支付确认完成 ======");
            return updateResult;
            
        } catch (RuntimeException e) {
            System.out.println("支付失败: " + e.getMessage());
            // 捕获余额不足等异常并重新抛出
            throw new RuntimeException("支付失败: " + e.getMessage());
        }
    }
}