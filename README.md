# 无人机喷洒服务系统 - NoPersonFinally

## 项目概述

本项目是一个完整的无人机喷洒服务管理系统，采用前后端分离架构，包含三个客户端：
- **机主端** (Vue.js)：设备管理、收益查看
- **飞手端** (微信小程序)：设备租赁、订单接单
- **农户端** (微信小程序)：需求发布、订单管理
- **管理端** (Vue.js)：系统管理、数据统计

## 技术架构

### 后端技术栈
- **框架**：Spring Boot 3.x
- **ORM**：MyBatis Plus
- **数据库**：MySQL
- **安全**：Spring Security + JWT
- **文档**：Swagger/OpenAPI

### 前端技术栈
- **框架**：Vue 3
- **UI库**：Element Plus
- **构建工具**：Vite
- **状态管理**：Pinia

### 移动端技术栈
- **框架**：微信小程序
- **API**：腾讯地图SDK

---

## 第一部分：后端代码

### 1. 控制器层 (Controller)

#### 1.1 管理员控制器 (AdminController.java)
**功能说明**：管理员对农户、飞手、机主进行完整的管理操作（查看、修改、删除、禁用）

```java
package com.cty.nopersonfinally.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cty.nopersonfinally.pojo.dto.FarmerUpdateDTO;
import com.cty.nopersonfinally.pojo.dto.FlyerUpdateDTO;
import com.cty.nopersonfinally.pojo.dto.UserOwnerDTO;
import com.cty.nopersonfinally.pojo.vo.UserStatisticsVO;
import com.cty.nopersonfinally.service.FarmerService;
import com.cty.nopersonfinally.service.UserFlyerService;
import com.cty.nopersonfinally.service.UserOwnerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Api(tags = "管理员接口")
@PreAuthorize("hasRole('admin')")
public class AdminController {

    @Autowired
    private FarmerService farmerService;

    @Autowired
    private UserFlyerService userFlyerService;

    @Autowired
    private UserOwnerService userOwnerService;

    @GetMapping("/statistics")
    @ApiOperation("获取系统统计信息")
    public Map<String, Object> getStatistics() {
        return Map.of(
            "totalUsers", farmerService.getTotalCount() + userFlyerService.getTotalCount() + userOwnerService.getTotalCount(),
            "farmers", farmerService.getTotalCount(),
            "flyers", userFlyerService.getTotalCount(),
            "owners", userOwnerService.getTotalCount()
        );
    }

    @GetMapping("/farmers")
    @ApiOperation("分页获取农户列表")
    public Page<Map<String, Object>> getFarmersList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return farmerService.getFarmersPage(pageNum, pageSize);
    }

    @GetMapping("/farmers/{farmerId}")
    @ApiOperation("获取农户详情")
    public Map<String, Object> getFarmerDetail(@PathVariable Long farmerId) {
        return farmerService.getFarmerDetailById(farmerId);
    }

    @PutMapping("/farmers")
    @ApiOperation("更新农户信息")
    public boolean updateFarmer(@RequestBody FarmerUpdateDTO updateDTO) {
        return farmerService.updateFarmer(updateDTO);
    }

    @DeleteMapping("/farmers/{farmerId}")
    @ApiOperation("删除农户")
    public boolean deleteFarmer(@PathVariable Long farmerId) {
        return farmerService.deleteFarmer(farmerId);
    }

    @PutMapping("/farmers/{farmerId}/status")
    @ApiOperation("更新农户状态")
    public boolean updateFarmerStatus(
            @PathVariable Long farmerId,
            @RequestParam Integer status) {
        return farmerService.updateFarmerStatus(farmerId, status);
    }

    @GetMapping("/flyers")
    @ApiOperation("分页获取飞手列表")
    public Page<Map<String, Object>> getFlyersList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return userFlyerService.getFlyersPage(pageNum, pageSize);
    }

    @GetMapping("/flyers/{flyerId}")
    @ApiOperation("获取飞手详情")
    public Map<String, Object> getFlyerDetail(@PathVariable Long flyerId) {
        return userFlyerService.getFlyerDetailById(flyerId);
    }

    @PutMapping("/flyers")
    @ApiOperation("更新飞手信息")
    public boolean updateFlyer(@RequestBody FlyerUpdateDTO updateDTO) {
        return userFlyerService.updateFlyer(updateDTO);
    }

    @DeleteMapping("/flyers/{flyerId}")
    @ApiOperation("删除飞手")
    public boolean deleteFlyer(@PathVariable Long flyerId) {
        return userFlyerService.deleteFlyer(flyerId);
    }

    @PutMapping("/flyers/{flyerId}/status")
    @ApiOperation("更新飞手状态")
    public boolean updateFlyerStatus(
            @PathVariable Long flyerId,
            @RequestParam Integer status) {
        return userFlyerService.updateFlyerStatus(flyerId, status);
    }

    @GetMapping("/owners")
    @ApiOperation("分页获取机主列表")
    public Page<Map<String, Object>> getOwnersList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return userOwnerService.getOwnersPage(pageNum, pageSize);
    }

    @GetMapping("/owners/{ownerId}")
    @ApiOperation("获取机主详情")
    public Map<String, Object> getOwnerDetail(@PathVariable Long ownerId) {
        return userOwnerService.getOwnerDetailById(ownerId);
    }

    @PutMapping("/owners")
    @ApiOperation("更新机主信息")
    public boolean updateOwner(@RequestBody UserOwnerDTO updateDTO) {
        return userOwnerService.updateOwner(updateDTO);
    }

    @DeleteMapping("/owners/{ownerId}")
    @ApiOperation("删除机主")
    public boolean deleteOwner(@PathVariable Long ownerId) {
        return userOwnerService.deleteOwner(ownerId);
    }

    @PutMapping("/owners/{ownerId}/status")
    @ApiOperation("更新机主状态")
    public boolean updateOwnerStatus(
            @PathVariable Long ownerId,
            @RequestParam Integer status) {
        return userOwnerService.updateOwnerStatus(ownerId, status);
    }
}
```

#### 1.2 设备管理控制器 (DeviceController.java)
**功能说明**：无人机设备管理，包括设备租赁、归还、状态管理

```java
package com.cty.nopersonfinally.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cty.nopersonfinally.pojo.dto.Result;
import com.cty.nopersonfinally.pojo.entity.DroneDevice;
import com.cty.nopersonfinally.pojo.entity.SysUser;
import com.cty.nopersonfinally.pojo.enums.DeviceStatusEnum;
import com.cty.nopersonfinally.pojo.vo.DeviceDetailVO;
import com.cty.nopersonfinally.service.DeviceService;
import com.cty.nopersonfinally.mapper.SysUserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.metadata.IPage;

@RestController
@RequestMapping("/device")
@Api(tags = "设备管理")
public class DeviceController {
    private static final Logger log = LoggerFactory.getLogger(DeviceController.class);

    @Resource
    private DeviceService deviceService;

    @Resource
    private SysUserMapper sysUserMapper;

    @ApiOperation("获取可用设备列表")
    @GetMapping("/available")
    @PreAuthorize("hasRole('flyer')")
    public Result<List<DroneDevice>> getAvailableDevices() {
        List<DroneDevice> devices = deviceService.getAvailableDevices();
        return Result.ok(devices);
    }

    @ApiOperation("检查设备是否可租借")
    @GetMapping("/check-available/{deviceId}")
    @PreAuthorize("hasRole('flyer')")
    public Result<Boolean> checkDeviceAvailable(@PathVariable Long deviceId) {
        boolean available = deviceService.checkDeviceAvailable(deviceId);
        return Result.ok(available);
    }

    @ApiOperation("租借无人机")
    @PostMapping("/rent/{deviceId}")
    @PreAuthorize("hasRole('flyer')")
    public Result rentDevice(@PathVariable Long deviceId, Principal principal) {
        try {
            log.info("====== 收到设备租借请求 ======");
            log.info("设备ID: {}", deviceId);
            Long flyerId = Long.parseLong(principal.getName());
            log.info("飞手ID: {}", flyerId);
            boolean isAvailable = deviceService.checkDeviceAvailable(deviceId);
            log.info("设备可租借检查结果: {}", isAvailable);
            boolean success = deviceService.rentDevice(deviceId, flyerId);
            log.info("deviceService.rentDevice返回结果: {}", success);
            if (success) {
                DroneDevice updatedDevice = deviceService.getById(deviceId);
                if (updatedDevice != null) {
                    log.info("租借后设备状态验证: flyerId={}, rentalStatus={}",
                            updatedDevice.getFlyerId(), updatedDevice.getRentalStatus());
                }
                log.info("设备租借成功，返回成功响应");
                return Result.ok("设备租借成功");
            } else {
                log.warn("设备租借失败，返回失败响应");
                return Result.error("设备租借失败，请检查设备是否可租借");
            }
        } catch (Exception e) {
            log.error("设备租借过程中发生异常: {}", e.getMessage(), e);
            return Result.error("设备租借过程中发生异常: " + e.getMessage());
        }
    }

    @ApiOperation("取消设备租借")
    @PostMapping("/cancel-rental/{deviceId}")
    @PreAuthorize("hasRole('flyer')")
    public Result<?> cancelRental(@PathVariable Long deviceId, Principal principal) {
        try {
            log.info("====== 收到设备取消租借请求 ======");
            log.info("设备ID: {}", deviceId);
            Long flyerId = Long.parseLong(principal.getName());
            log.info("飞手ID: {}", flyerId);
            boolean isDeviceAvailable = deviceService.checkDeviceAvailable(deviceId, flyerId);
            log.info("设备归属检查结果: {}", isDeviceAvailable);
            if (!isDeviceAvailable) {
                log.warn("设备不属于当前飞手或未处于租借状态");
                return Result.error("设备不属于当前飞手或未处于租借状态");
            }
            boolean success = deviceService.unbindDeviceFromFlyer(deviceId, null, true);
            log.info("deviceService.unbindDeviceFromFlyer返回结果: {}", success);
            if (success) {
                DroneDevice updatedDevice = deviceService.getById(deviceId);
                if (updatedDevice != null) {
                    log.info("取消租借后设备状态验证: flyerId={}, rentalStatus={}",
                            updatedDevice.getFlyerId(), updatedDevice.getRentalStatus());
                }
                log.info("设备取消租借成功，返回成功响应");
                return Result.ok("设备取消租借成功");
            } else {
                log.warn("设备取消租借失败");
                return Result.error("设备取消租借失败");
            }
        } catch (Exception e) {
            log.error("设备取消租借过程中发生异常: {}", e.getMessage(), e);
            return Result.error("设备取消租借过程中发生异常: " + e.getMessage());
        }
    }

    @ApiOperation("删除设备")
    @DeleteMapping("/delete/{deviceId}")
    @PreAuthorize("hasRole('owner')")
    public Result<?> deleteDevice(@PathVariable Long deviceId) {
        try {
            Principal principal = SecurityContextHolder.getContext().getAuthentication();
            Long ownerId = Long.parseLong(principal.getName());
            DroneDevice existingDevice = deviceService.getById(deviceId);
            if (existingDevice == null) {
                return Result.error("设备不存在");
            }
            if (!existingDevice.getOwnerId().equals(ownerId)) {
                return Result.error("您没有权限删除此设备");
            }
            if (existingDevice.getStatus() != DeviceStatusEnum.NORMAL.getCode() || existingDevice.getFlyerId() != null) {
                return Result.error("设备正在使用中，无法删除");
            }
            boolean success = deviceService.deleteDevice(deviceId);
            if (success) {
                log.info("设备删除成功，设备ID: {}, 所有者ID: {}", deviceId, ownerId);
                return Result.ok("设备删除成功");
            } else {
                log.error("设备删除失败，设备ID: {}", deviceId);
                return Result.error("设备删除失败");
            }
        } catch (NumberFormatException e) {
            log.error("用户认证信息异常: {}", e.getMessage());
            return Result.error("用户认证信息异常");
        } catch (Exception e) {
            log.error("删除设备时发生异常: {}", e.getMessage(), e);
            return Result.error("删除设备时发生异常: " + e.getMessage());
        }
    }

    @ApiOperation("归还设备")
    @PostMapping("/return/{deviceId}")
    @PreAuthorize("hasRole('flyer')")
    public Result<?> returnDeviceByFlyer(@PathVariable Long deviceId, Principal principal) {
        try {
            log.info("====== 收到设备归还请求 ======");
            log.info("设备ID: {}", deviceId);
            Long flyerId = Long.parseLong(principal.getName());
            log.info("飞手ID: {}", flyerId);
            boolean isDeviceAvailable = deviceService.checkDeviceAvailable(deviceId, flyerId);
            log.info("设备归属检查结果: {}", isDeviceAvailable);
            if (!isDeviceAvailable) {
                log.warn("设备不属于当前飞手或未处于租借状态");
                return Result.error("设备不属于当前飞手或未处于租借状态");
            }
            boolean success = deviceService.unbindDeviceFromFlyer(deviceId, false);
            log.info("deviceService.unbindDeviceFromFlyer返回结果: {}", success);
            if (success) {
                DroneDevice updatedDevice = deviceService.getById(deviceId);
                if (updatedDevice != null) {
                    log.info("归还后设备状态验证: flyerId={}, rentalStatus={}",
                            updatedDevice.getFlyerId(), updatedDevice.getRentalStatus());
                }
                log.info("设备归还成功，返回成功响应");
                return Result.ok("设备归还成功");
            } else {
                log.warn("设备归还失败");
                return Result.error("设备归还失败");
            }
        } catch (Exception e) {
            log.error("设备归还过程中发生异常: {}", e.getMessage(), e);
            return Result.error("设备归还过程中发生异常: " + e.getMessage());
        }
    }

    @ApiOperation("获取飞手租借历史记录")
    @GetMapping("/rental/history")
    @PreAuthorize("hasRole('flyer')")
    public Result<?> getFlyerRentalHistory(Principal principal) {
        try {
            Long flyerId = Long.parseLong(principal.getName());
            log.info("获取飞手租借历史记录，飞手ID: {}", flyerId);
            List<Map<String, Object>> rentalHistory = deviceService.getFlyerRentalHistory(flyerId);
            return Result.ok(rentalHistory);
        } catch (Exception e) {
            log.error("获取租借历史记录失败: {}", e.getMessage(), e);
            return Result.error("获取租借历史记录失败: " + e.getMessage());
        }
    }

    @ApiOperation("获取飞手当前租借的设备")
    @GetMapping("/rental/current")
    @PreAuthorize("hasRole('flyer')")
    public Result<?> getFlyerCurrentRentals(Principal principal) {
        try {
            Long flyerId = Long.parseLong(principal.getName());
            log.info("获取飞手当前租借设备，飞手ID: {}", flyerId);
            List<DroneDevice> currentRentals = deviceService.getDevicesByFlyerId(flyerId);
            return Result.ok(currentRentals);
        } catch (Exception e) {
            log.error("获取当前租借设备失败: {}", e.getMessage(), e);
            return Result.error("获取当前租借设备失败: " + e.getMessage());
        }
    }

    @ApiOperation("根据机主ID获取设备列表")
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('flyer')")
    public Result<?> getDevicesByOwnerId(@PathVariable Long ownerId) {
        try {
            log.info("根据机主ID获取设备列表，机主ID: {}", ownerId);
            List<DroneDevice> devices = deviceService.getDevicesByOwnerId(ownerId);
            return Result.ok(devices);
        } catch (Exception e) {
            log.error("根据机主ID获取设备列表失败: {}", e.getMessage(), e);
            return Result.error("获取设备列表失败: " + e.getMessage());
        }
    }
}
```

#### 1.3 聊天控制器 (ChatController.java)
**功能说明**：飞手与机主之间的在线聊天功能，支持实时消息传递和历史记录查看

```java
package com.cty.nopersonfinally.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cty.nopersonfinally.pojo.dto.MessageDTO;
import com.cty.nopersonfinally.pojo.vo.ConversationVO;
import com.cty.nopersonfinally.pojo.vo.MessageVO;
import com.cty.nopersonfinally.service.ConversationService;
import com.cty.nopersonfinally.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@Api(tags = "聊天功能")
public class ChatController {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private MessageService messageService;

    @GetMapping("/conversations")
    @PreAuthorize("hasAnyRole('flyer', 'owner')")
    public List<ConversationVO> getConversations(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        return conversationService.getUserConversations(userId);
    }

    @GetMapping("/messages/{conversationId}")
    @PreAuthorize("hasAnyRole('flyer', 'owner')")
    public Page<MessageVO> getMessages(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        return messageService.getMessagesByConversationId(conversationId, pageNum, pageSize);
    }

    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('flyer', 'owner')")
    public Map<String, Object> sendMessage(
            @RequestBody MessageDTO messageDTO,
            Principal principal) {
        Long senderId = Long.parseLong(principal.getName());
        messageDTO.setSenderId(senderId);
        boolean success = messageService.sendMessage(messageDTO);
        return Map.of("success", success);
    }

    @PutMapping("/read/{conversationId}")
    @PreAuthorize("hasAnyRole('flyer', 'owner')")
    public Map<String, Object> markAsRead(
            @PathVariable String conversationId,
            Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        boolean success = messageService.markMessagesAsRead(conversationId, userId);
        return Map.of("success", success);
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('flyer', 'owner')")
    public Map<String, Integer> getUnreadCount(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        int count = messageService.getUnreadMessageCount(userId);
        return Map.of("unreadCount", count);
    }
}
```

---

### 2. 服务层 (Service)

#### 2.1 设备服务接口 (DeviceService.java)

```java
package com.cty.nopersonfinally.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cty.nopersonfinally.pojo.entity.DroneDevice;
import com.cty.nopersonfinally.pojo.enums.DeviceStatusEnum;
import java.util.List;
import java.util.Map;

public interface DeviceService extends IService<DroneDevice> {

    boolean checkDeviceAvailable(Long deviceId, Long flyerId);

    boolean updateDeviceStatus(Long deviceId, DeviceStatusEnum status, Long operatorId);

    List<DroneDevice> getDevicesByFlyerId(Long flyerId);

    List<DroneDevice> getDevicesByOwnerId(Long ownerId);

    List<DroneDevice> getDevicesByStatus(DeviceStatusEnum status);

    List<DroneDevice> getAvailableDevices();

    boolean checkDeviceAvailable(Long deviceId);

    int bindDeviceToFlyer(Long deviceId, Long flyerId);

    boolean unbindDeviceFromFlyer(Long deviceId);

    boolean unbindDeviceFromFlyer(Long deviceId, boolean isCancel);

    boolean unbindDeviceFromFlyer(Long deviceId, Long rentalId, boolean isCancel);

    boolean rentDevice(Long deviceId, Long flyerId);

    boolean returnDevice(Long deviceId, Long flyerId);

    boolean deleteDevice(Long deviceId);

    List<Map<String, Object>> getFlyerRentalHistory(Long flyerId);
}
```

#### 2.2 设备服务实现 (DeviceServiceImpl.java)

```java
package com.cty.nopersonfinally.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cty.nopersonfinally.mapper.DeviceMapper;
import com.cty.nopersonfinally.mapper.DeviceOperateLogMapper;
import com.cty.nopersonfinally.mapper.SysUserMapper;
import com.cty.nopersonfinally.mapper.UserOwnerMapper;
import com.cty.nopersonfinally.pojo.entity.DeviceOperateLog;
import com.cty.nopersonfinally.pojo.entity.DeviceRental;
import com.cty.nopersonfinally.pojo.entity.DroneDevice;
import com.cty.nopersonfinally.pojo.entity.SysUser;
import com.cty.nopersonfinally.pojo.enums.DeviceStatusEnum;
import com.cty.nopersonfinally.pojo.vo.DeviceDetailVO;
import com.cty.nopersonfinally.service.DeviceMaintainService;
import com.cty.nopersonfinally.service.DeviceRentalService;
import com.cty.nopersonfinally.service.DeviceService;
import com.cty.nopersonfinally.service.FlyerEvaluationService;
import com.cty.nopersonfinally.service.UserInfoService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, DroneDevice> implements DeviceService {

    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private DeviceOperateLogMapper deviceOperateLogMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private DeviceMaintainService deviceMaintainService;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private UserOwnerMapper userOwnerMapper;

    @Resource
    @Lazy
    private DeviceRentalService deviceRentalService;

    @Resource
    @Lazy
    private UserInfoService userInfoService;

    @Resource
    @Lazy
    private FlyerEvaluationService flyerEvaluationService;

    private static final String DEVICE_STATUS_CACHE_KEY = "device:status:";
    private static final String FLYER_DEVICES_CACHE_KEY = "flyer:devices:";

    @Override
    @Transactional
    public boolean updateDeviceStatus(Long deviceId, DeviceStatusEnum status, Long operatorId) {
        DroneDevice device = getById(deviceId);
        if (device == null) {
            return false;
        }
        if (device.getStatus() == status.getCode()) {
            return true;
        }
        DroneDevice updateEntity = new DroneDevice();
        updateEntity.setDeviceId(deviceId);
        updateEntity.setStatus(status.getCode());
        updateEntity.setUpdateTime(LocalDateTime.now());
        boolean result = updateById(updateEntity);
        if (result) {
            DeviceOperateLog operateLog = new DeviceOperateLog();
            operateLog.setDeviceId(deviceId);
            operateLog.setOperatorId(operatorId);
            operateLog.setBeforeStatus(device.getStatus());
            operateLog.setAfterStatus(status.getCode());
            operateLog.setOperateTime(new Date());
            deviceOperateLogMapper.insert(operateLog);
            redisTemplate.opsForValue().set(DEVICE_STATUS_CACHE_KEY + deviceId, status.getCode(), 24, TimeUnit.HOURS);
            redisTemplate.delete(FLYER_DEVICES_CACHE_KEY + device.getFlyerId());
        }
        return result;
    }

    @Override
    public List<DroneDevice> getDevicesByFlyerId(Long flyerId) {
        String cacheKey = FLYER_DEVICES_CACHE_KEY + flyerId;
        List<DroneDevice> devices = (List<DroneDevice>) redisTemplate.opsForValue().get(cacheKey);
        if (devices == null) {
            QueryWrapper<DroneDevice> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("flyer_id", flyerId).eq("is_deleted", 0).orderByDesc("create_time");
            devices = deviceMapper.selectList(queryWrapper);
            redisTemplate.opsForValue().set(cacheKey, devices, 1, TimeUnit.HOURS);
        }
        return devices;
    }

    @Override
    public List<DroneDevice> getDevicesByOwnerId(Long ownerId) {
        String cacheKey = "owner_devices:" + ownerId;
        List<DroneDevice> devices = (List<DroneDevice>) redisTemplate.opsForValue().get(cacheKey);
        if (devices == null) {
            QueryWrapper<DroneDevice> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("owner_id", ownerId).eq("is_deleted", 0).orderByDesc("create_time");
            devices = deviceMapper.selectList(queryWrapper);
            redisTemplate.opsForValue().set(cacheKey, devices, 1, TimeUnit.HOURS);
        }
        return devices;
    }

    @Override
    public List<DroneDevice> getAvailableDevices() {
        QueryWrapper<DroneDevice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", DeviceStatusEnum.NORMAL.getCode())
                .eq("is_deleted", 0)
                .isNull("flyer_id")
                .eq("rental_status", 0);
        queryWrapper.orderByAsc("last_maintain_time");
        return deviceMapper.selectList(queryWrapper);
    }

    @Override
    public boolean checkDeviceAvailable(Long deviceId) {
        DroneDevice device = getById(deviceId);
        if (device == null) {
            System.out.println("checkDeviceAvailable失败：设备ID " + deviceId + " 不存在");
            return false;
        }
        boolean statusCheck = device.getStatus() == DeviceStatusEnum.NORMAL.getCode();
        boolean deletedCheck = device.getIsDeleted() == 0;
        boolean flyerCheck = device.getFlyerId() == null;
        boolean rentalCheck = device.getRentalStatus() == 0;
        boolean available = statusCheck && deletedCheck && flyerCheck && rentalCheck;
        if (!available) {
            System.out.println("checkDeviceAvailable：设备ID " + deviceId + " 不可用");
        }
        return available;
    }

    @Override
    @Transactional
    public int bindDeviceToFlyer(Long deviceId, Long flyerId) {
        DroneDevice device = getById(deviceId);
        if (device == null) {
            return 0;
        }
        if (device.getFlyerId() != null) {
            return 0;
        }
        UpdateWrapper<DroneDevice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("device_id", deviceId)
                    .set("flyer_id", flyerId)
                    .set("rental_status", 1)
                    .set("update_time", LocalDateTime.now());
        boolean result = update(updateWrapper);
        if (result) {
            redisTemplate.delete(DEVICE_STATUS_CACHE_KEY + deviceId);
            if (device.getFlyerId() != null) {
                redisTemplate.delete(FLYER_DEVICES_CACHE_KEY + device.getFlyerId());
            }
            redisTemplate.delete(FLYER_DEVICES_CACHE_KEY + flyerId);
            return 1;
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindDeviceFromFlyer(Long deviceId, boolean isCancel) {
        return unbindDeviceFromFlyer(deviceId, null, isCancel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindDeviceFromFlyer(Long deviceId, Long rentalId, boolean isCancel) {
        try {
            System.out.println("====== 开始执行设备解绑操作 ======");
            System.out.println("设备ID=" + deviceId);
            if (rentalId != null) {
                System.out.println("指定租借记录ID=" + rentalId);
            }
            System.out.println("操作类型：" + (isCancel ? "取消租借" : "归还设备"));

            if (rentalId != null) {
                System.out.println("1. 根据指定的rentalId进行操作...");
                DeviceRental rentalRecord = deviceRentalService.getById(rentalId);
                if (rentalRecord == null) {
                    System.out.println("未找到指定ID的租借记录：" + rentalId);
                    return false;
                }
                System.out.println("找到租借记录：rentalId=" + rentalRecord.getRentalId() + ", deviceId=" + rentalRecord.getDeviceId() + ", flyerId=" + rentalRecord.getFlyerId());
                if (!rentalRecord.getDeviceId().equals(deviceId)) {
                    System.out.println("错误：租借记录的设备ID与传入的设备ID不匹配");
                    return false;
                }
                Long oldFlyerId = rentalRecord.getFlyerId();
                int rentalStatus = isCancel ? 3 : 2;
                System.out.println("更新租借记录状态为：" + rentalStatus + "(" + (isCancel ? "已取消" : "已归还") + ")");

                System.out.println("检查飞手好评率，判断是否需要退还押金...");
                Double positiveRateObj = flyerEvaluationService.getPositiveRate(oldFlyerId);
                double positiveRate = positiveRateObj != null ? positiveRateObj : 0.0;
                System.out.println("飞手好评率：" + positiveRate);

                if (positiveRate <= 50) {
                    System.out.println("飞手好评率<=50，需要退还500押金");
                    try {
                        userInfoService.updateUserBalance(oldFlyerId, 500.0);
                        System.out.println("押金退还成功");
                    } catch (RuntimeException e) {
                        System.out.println("押金退还失败：" + e.getMessage());
                        throw e;
                    }
                } else {
                    System.out.println("飞手好评率>50，无需退还押金");
                }

                DeviceRental updateRental = new DeviceRental();
                updateRental.setRentalId(rentalRecord.getRentalId());
                updateRental.setRentalStatus(rentalStatus);
                updateRental.setRentalEndTime(new Date());
                updateRental.setUpdateTime(new Date());
                boolean rentalUpdateResult = deviceRentalService.updateById(updateRental);
                System.out.println("租借记录更新结果：" + rentalUpdateResult);

                if (!rentalUpdateResult) {
                    System.out.println("租借记录更新失败");
                    throw new RuntimeException("租借记录更新失败");
                }

                System.out.println("2. 解除设备与飞手的绑定...");
                UpdateWrapper<DroneDevice> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("device_id", deviceId)
                             .eq("flyer_id", oldFlyerId)
                             .eq("is_deleted", 0);
                updateWrapper.set("flyer_id", null);
                updateWrapper.set("rental_status", 0);
                updateWrapper.set("update_time", LocalDateTime.now());
                boolean deviceUpdateResult = update(updateWrapper);
                System.out.println("设备解绑结果：" + deviceUpdateResult);

                if (!deviceUpdateResult) {
                    System.out.println("设备解绑失败");
                    throw new RuntimeException("设备解绑失败");
                }

                redisTemplate.delete(DEVICE_STATUS_CACHE_KEY + deviceId);
                redisTemplate.delete(FLYER_DEVICES_CACHE_KEY + oldFlyerId);
                System.out.println("设备解绑操作完成");
                return true;
            } catch (Exception e) {
                System.out.println("设备解绑过程中发生异常：" + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    @Transactional
    public boolean rentDevice(Long deviceId, Long flyerId) {
        try {
            System.out.println("====== 开始执行设备租借操作 ======");
            System.out.println("设备ID: " + deviceId);
            System.out.println("飞手ID: " + flyerId);

            boolean isAvailable = checkDeviceAvailable(deviceId);
            System.out.println("设备可租借检查结果: " + isAvailable);

            if (!isAvailable) {
                System.out.println("设备不可租借，结束操作");
                return false;
            }

            SysUser flyer = sysUserMapper.selectById(flyerId);
            if (flyer == null) {
                System.out.println("飞手不存在，结束操作");
                return false;
            }

            DroneDevice device = getById(deviceId);
            if (device == null) {
                System.out.println("设备不存在，结束操作");
                return false;
            }

            System.out.println("检查飞手好评率，判断是否需要押金...");
            Double positiveRateObj = flyerEvaluationService.getPositiveRate(flyerId);
            double positiveRate = positiveRateObj != null ? positiveRateObj : 0.0;
            System.out.println("飞手好评率：" + positiveRate);

            if (positiveRate <= 50) {
                System.out.println("飞手好评率<=50，需要扣除500押金");
                try {
                    userInfoService.updateUserBalance(flyerId, -500.0);
                    System.out.println("押金扣除成功");
                } catch (RuntimeException e) {
                    System.out.println("押金扣除失败：" + e.getMessage());
                    return false;
                }
            } else {
                System.out.println("飞手好评率>50，无需押金");
            }

            System.out.println("开始绑定设备到飞手...");
            int bindResult = bindDeviceToFlyer(deviceId, flyerId);
            System.out.println("设备绑定结果: " + bindResult);

            if (bindResult != 1) {
                System.out.println("设备绑定失败，结束操作");
                return false;
            }

            System.out.println("创建租借记录...");
            DeviceRental rental = new DeviceRental();
            rental.setDeviceId(deviceId);
            rental.setFlyerId(flyerId);
            rental.setOwnerId(device.getOwnerId());
            rental.setRentalStartTime(new Date());
            rental.setRentalStatus(1);
            rental.setCreateTime(new Date());
            rental.setUpdateTime(new Date());

            boolean rentalCreated = deviceRentalService.save(rental);
            System.out.println("租借记录创建结果: " + rentalCreated);

            if (!rentalCreated) {
                System.out.println("租借记录创建失败，结束操作");
                unbindDeviceFromFlyer(deviceId, true);
                return false;
            }

            System.out.println("设备租借操作完成，租借记录ID: " + rental.getRentalId());
            return true;
        } catch (Exception e) {
            System.out.println("设备租借过程中发生异常：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean returnDevice(Long deviceId, Long flyerId) {
        return unbindDeviceFromFlyer(deviceId, false);
    }

    @Override
    public boolean deleteDevice(Long deviceId) {
        try {
            UpdateWrapper<DroneDevice> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("device_id", deviceId)
                        .set("is_deleted", 1)
                        .set("update_time", LocalDateTime.now());
            return update(updateWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean checkDeviceAvailable(Long deviceId, Long flyerId) {
        return false;
    }

    @Override
    public List<DroneDevice> getDevicesByStatus(DeviceStatusEnum status) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getFlyerRentalHistory(Long flyerId) {
        return null;
    }
}
```

---

## 第二部分：前端代码

### 1. 机主端 (frontend)

#### 1.1 设备管理页面 (Devices.vue)
**功能说明**：显示设备列表，支持分页查询、设备状态统计、搜索筛选、设备详情查看、设备编辑和删除

```vue
<template>
  <div class="devices">
    <div class="page-header">
      <h1 class="page-title">设备管理</h1>
      <p class="page-description">管理您的无人机设备列表，查看详细信息并进行设备维护</p>
    </div>

    <el-card class="device-card">
      <template #header>
        <div class="card-header">
          <div class="header-title-section">
            <h2 class="header-title">设备列表</h2>
            <div class="header-stats">
              <span class="total-devices">
                <span class="stats-number">{{ deviceList.length }}</span>
                <span class="stats-label">设备</span>
              </span>
              <div class="status-summary">
                <div class="status-item" v-for="status in statusSummary" :key="status.value">
                  <span class="status-dot" :class="`status-${status.type}`"></span>
                  <span class="status-text">{{ status.text }} ({{ status.count }})</span>
                </div>
              </div>
            </div>
          </div>
          <el-button type="primary" @click="handleAddDevice" class="add-device-btn" :icon="Plus">
            添加设备
          </el-button>
        </div>
      </template>

      <div class="search-filter-container">
        <div class="search-section">
          <el-input v-model="searchForm.deviceName" placeholder="搜索设备名称、ID或型号" class="search-input" clearable prefix-icon="Search"></el-input>
          <el-select v-model="searchForm.status" placeholder="设备状态" class="status-select" clearable>
            <el-option label="正常" value="1" />
            <el-option label="维护中" value="2" />
            <el-option label="故障" value="3" />
            <el-option label="停用" value="4" />
          </el-select>
        </div>
        <div class="action-buttons">
          <el-button type="primary" @click="handleSearch" class="search-btn" :icon="Search">查询</el-button>
          <el-button @click="handleReset" class="reset-btn">重置</el-button>
        </div>
      </div>

      <div class="table-section">
        <div class="data-info" style="margin-bottom: 16px; font-size: 14px; color: #606266;">
          共 <span style="color: #409eff; font-weight: bold;">{{pagination.total}}</span> 台设备，
          当前显示 <span style="color: #409eff; font-weight: bold;">{{deviceList.length}}</span> 台
        </div>

        <el-table :data="deviceList" style="width: 100%; background: white; border: 1px solid #ccc;">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column prop="deviceId" label="设备ID" width="100" class-name="device-id-column" />
          <el-table-column prop="deviceName" label="设备名称" width="180" />
          <el-table-column prop="deviceModel" label="设备型号" width="150" />
          <el-table-column prop="brand" label="设备品牌" width="120" />
          <el-table-column prop="serialNumber" label="设备编号" width="180" />
          <el-table-column label="设备图片" width="120" align="center">
            <template #default="scope">
              <el-image :src="getDeviceImage(scope.row.picture)" fit="cover" :preview-src-list="[getDeviceImage(scope.row.picture)]" class="device-table-image" />
            </template>
          </el-table-column>
          <el-table-column prop="status" label="设备状态" width="120">
            <template #default="scope">
              <el-tag :type="getStatusTagType(scope.row.status)" class="status-tag" effect="light">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="maxLoad" label="最大载重(kg)" width="120">
            <template #default="scope">{{ scope.row.maxLoad || 0 }}</template>
          </el-table-column>
          <el-table-column prop="endurance" label="续航时间(分钟)" width="140">
            <template #default="scope">{{ scope.row.endurance || 0 }}</template>
          </el-table-column>
          <el-table-column prop="hourlyRent" label="每小时租金(元)" width="140">
            <template #default="scope">{{ scope.row.hourlyRent != null ? scope.row.hourlyRent : '-' }}</template>
          </el-table-column>
          <el-table-column prop="manufacturer" label="设备制造商" width="150" />
          <el-table-column prop="purchaseDate" label="购买日期" width="140" />
          <el-table-column label="操作" width="220" fixed="right" class-name="operation-column">
            <template #default="scope">
              <div class="operation-buttons">
                <el-tooltip content="编辑设备信息">
                  <el-button type="primary" size="small" @click="handleEditDevice(scope.row.deviceId)" class="edit-btn">编辑</el-button>
                </el-tooltip>
                <el-tooltip content="查看设备详情">
                  <el-button size="small" @click="handleViewDetails(scope.row)" class="detail-btn">详情</el-button>
                </el-tooltip>
                <el-popconfirm title="确定要删除该设备吗？" confirm-button-text="确定" cancel-button-text="取消" @confirm="handleDeleteDevice(scope.row.deviceId)">
                  <template #reference>
                    <el-button type="danger" size="small" class="delete-btn">删除</el-button>
                  </template>
                </el-popconfirm>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-container">
        <div class="pagination-info">
          <span>显示第 {{ (pagination.currentPage - 1) * pagination.pageSize + 1 }}-{{ Math.min(pagination.currentPage * pagination.pageSize, pagination.total) }} 条，共 {{ pagination.total }} 条</span>
        </div>
        <el-pagination v-model:current-page="pagination.currentPage" v-model:page-size="pagination.pageSize" :page-sizes="[10, 20, 50, 100]" layout="sizes, prev, pager, next, jumper" :total="pagination.total" @size-change="handleSizeChange" @current-change="handleCurrentChange" class="pagination-control" />
      </div>
    </el-card>

    <el-dialog v-model="deviceDetailDialogVisible" title="设备详情" width="70%" class="device-detail-dialog" custom-class="modern-dialog">
      <div class="dialog-header-info">
        <div class="device-main-info">
          <h3 class="dialog-device-name">{{ currentDevice.deviceName }}</h3>
          <div class="dialog-device-model">{{ currentDevice.deviceModel }}</div>
        </div>
        <div class="device-status-badge">
          <el-tag :type="getStatusTagType(currentDevice.status)" class="status-badge">
            {{ getStatusText(currentDevice.status) }}
          </el-tag>
        </div>
      </div>
      <div class="device-image-section" style="margin-bottom: 20px; text-align: center;">
        <el-image :src="getDeviceImage(currentDevice.picture)" fit="contain" :preview-src-list="[getDeviceImage(currentDevice.picture)]" class="device-detail-image" lazy />
      </div>
      <el-descriptions :column="2" border class="device-descriptions">
        <el-descriptions-item label="设备ID">{{ currentDevice.deviceId }}</el-descriptions-item>
        <el-descriptions-item label="序列号">{{ currentDevice.serialNumber }}</el-descriptions-item>
        <el-descriptions-item label="设备类型">{{ currentDevice.deviceType }}</el-descriptions-item>
        <el-descriptions-item label="品牌">{{ currentDevice.brand }}</el-descriptions-item>
        <el-descriptions-item label="最大载重(kg)">{{ currentDevice.maxLoad }}</el-descriptions-item>
        <el-descriptions-item label="续航时间(分钟)">{{ currentDevice.endurance }}</el-descriptions-item>
        <el-descriptions-item label="购买日期">{{ currentDevice.purchaseDate }}</el-descriptions-item>
        <el-descriptions-item label="每小时租金(元)">{{ currentDevice.hourlyRent != null ? currentDevice.hourlyRent : '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Search, Picture } from '@element-plus/icons-vue'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'

export default {
  name: 'Devices',
  components: { Plus, Search, Picture },
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const deviceDetailDialogVisible = ref(false)
    const currentDevice = ref({})
    const hoveredRow = ref(null)
    const maintainRecords = ref([])
    const maintainRecordsLoading = ref(false)

    const searchForm = reactive({
      deviceName: '',
      status: ''
    })

    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })

    const deviceList = ref([])

    const statusSummary = computed(() => {
      const statusMap = {
        1: { text: '正常', type: 'success', count: 0 },
        2: { text: '维护中', type: 'warning', count: 0 },
        3: { text: '故障', type: 'danger', count: 0 },
        4: { text: '停用', type: 'info', count: 0 }
      }
      deviceList.value.forEach(device => {
        if (statusMap[device.status]) {
          statusMap[device.status].count++
        }
      })
      return Object.entries(statusMap).map(([value, info]) => ({ value, ...info })).filter(item => item.count > 0)
    })

    const getStatusText = (status) => {
      const statusMap = { 1: '正常', 2: '维护中', 3: '故障', 4: '停用' }
      return statusMap[status] || '未知'
    }

    const getStatusTagType = (status) => {
      const typeMap = { 1: 'success', 2: 'warning', 3: 'danger', 4: 'info' }
      return typeMap[status] || 'info'
    }

    const getDeviceImage = (picture) => {
      if (picture && picture.trim()) {
        if (picture.startsWith('http://') || picture.startsWith('https://')) {
          return picture
        }
        return picture
      }
      return 'https://via.placeholder.com/300x300?text=设备+图片+未设置'
    }

    const handleSearch = () => {
      pagination.currentPage = 1
      fetchDevices()
    }

    const handleReset = () => {
      searchForm.deviceName = ''
      searchForm.status = ''
      pagination.currentPage = 1
      fetchDevices()
    }

    const handleAddDevice = () => {
      router.push('/devices/add')
    }

    const handleEditDevice = (deviceId) => {
      router.push(`/device/${deviceId}/edit`)
    }

    const handleViewDetails = (device) => {
      router.push(`/detail/${device.deviceId}`)
    }

    const handleDeleteDevice = async (deviceId) => {
      try {
        console.log('准备删除设备ID:', deviceId)
        const response = await axios.delete(`/device/delete/${deviceId}`)
        let success = false
        let message = ''
        if (response && typeof response === 'object') {
          if (response.code === 200) {
            success = true
            message = response.message || '设备删除成功'
          } else if (response.data && response.data.code === 200) {
            success = true
            message = response.data.message || '设备删除成功'
          }
        }
        if (success) {
          const index = deviceList.value.findIndex(item => item.deviceId === deviceId)
          if (index !== -1) {
            deviceList.value.splice(index, 1)
          }
          pagination.total -= 1
          ElMessage.success({ message, duration: 2000, showClose: true })
        } else {
          const errorMsg = response?.message || response?.data?.message || '未知错误'
          ElMessage.error({ message: '删除设备失败：' + errorMsg, duration: 2000, showClose: true })
        }
      } catch (error) {
        console.error('删除设备异常:', error)
        const errorMsg = error.response?.data?.message || error.message || '删除设备失败，请重试'
        ElMessage.error({ message: errorMsg, duration: 2000, showClose: true })
      }
    }

    const handleSizeChange = (size) => {
      pagination.pageSize = size
      fetchDevices()
    }

    const handleCurrentChange = (current) => {
      pagination.currentPage = current
      fetchDevices()
    }

    const fetchDevices = async () => {
      console.log('获取设备列表')
      loading.value = true
      try {
        const response = await axios.get('/device/list', {
          params: { pageNum: pagination.currentPage, pageSize: pagination.pageSize }
        })
        let devices = []
        if (response.data && Array.isArray(response.data.records)) {
          devices = response.data.records
          pagination.total = response.data.total || 0
        } else if (response.data && response.data.code === 200 && response.data.data) {
          if (Array.isArray(response.data.data.records)) {
            devices = response.data.data.records
            pagination.total = response.data.data.total || 0
          } else if (Array.isArray(response.data.data)) {
            devices = response.data.data
            pagination.total = devices.length
          }
        } else if (Array.isArray(response.data)) {
          devices = response.data
          pagination.total = devices.length
        }

        const formattedDevices = devices.map(device => {
          const deviceInfo = device.deviceInfo || {}
          let enduranceValue = 0
          if (deviceInfo && deviceInfo.endurance !== undefined && deviceInfo.endurance !== null) {
            enduranceValue = deviceInfo.endurance
          } else if (device && device.endurance !== undefined && device.endurance !== null) {
            enduranceValue = device.endurance
          }
          let pictureValue = ''
          if (deviceInfo && deviceInfo.picture !== undefined && deviceInfo.picture !== null) {
            pictureValue = deviceInfo.picture
          } else if (device && device.picture !== undefined && device.picture !== null) {
            pictureValue = device.picture
          }
          return {
            deviceId: (deviceInfo.deviceId || device.deviceId || deviceInfo.device_id || device.device_id)?.toString() || '未知ID',
            deviceName: deviceInfo.deviceName || device.deviceName || deviceInfo.device_name || device.device_name || '未知设备',
            deviceModel: deviceInfo.model || device.model || '未知型号',
            deviceType: deviceInfo.deviceType || device.deviceType || deviceInfo.device_type || device.device_type || '未知类型',
            purchaseDate: (deviceInfo.purchaseTime || device.purchaseTime || deviceInfo.purchase_time || device.purchase_time) ? (deviceInfo.purchaseTime || device.purchaseTime || deviceInfo.purchase_time || device.purchase_time).split(' ')[0] : '',
            status: deviceInfo.status || device.status || 0,
            location: '',
            lastOnlineTime: deviceInfo.lastMaintainTime || device.lastMaintainTime || deviceInfo.last_maintain_time || device.last_maintain_time || '',
            serialNumber: deviceInfo.serialNumber || device.serialNumber || deviceInfo.serialnumber || device.serialnumber || '未知序列号',
            longitude: '',
            latitude: '',
            flightHours: '',
            flightCount: '',
            remark: '',
            brand: deviceInfo.deviceType || device.deviceType || deviceInfo.device_type || device.device_type || '',
            maxLoad: deviceInfo.maxLoad || device.maxLoad || deviceInfo.max_load || device.max_load || 0,
            endurance: enduranceValue,
            manufacturer: deviceInfo.manufacturer || device.manufacturer || '未知厂商',
            picture: pictureValue,
            hourlyRent: deviceInfo.hourlyRent ?? device.hourlyRent ?? deviceInfo.hourly_rent ?? device.hourly_rent ?? null
          }
        })
        deviceList.value = formattedDevices
        ElMessage.success(`成功加载 ${formattedDevices.length} 台设备`)
        loading.value = false
      } catch (error) {
        console.error('获取设备列表失败:', error)
        ElMessage.error('获取设备列表失败，请稍后重试')
        deviceList.value = []
        pagination.total = 0
        loading.value = false
      }
    }

    onMounted(() => {
      console.log('设备管理页面已挂载')
      fetchDevices()
    })

    return {
      searchForm,
      pagination,
      deviceList,
      loading,
      deviceDetailDialogVisible,
      currentDevice,
      hoveredRow,
      statusSummary,
      getStatusText,
      getStatusTagType,
      getDeviceImage,
      handleSearch,
      handleReset,
      handleAddDevice,
      handleEditDevice,
      handleViewDetails,
      handleDeleteDevice,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
* { box-sizing: border-box; }
.devices { padding: 20px; min-height: 100vh; background: linear-gradient(135deg, #f5f7fa 0%, #e6eaf0 100%); }
.page-header { margin-bottom: 24px; position: relative; }
.page-title { font-size: 28px; font-weight: 700; color: #303133; margin: 0 0 8px 0; background: linear-gradient(135deg, #303133, #606266); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
.page-description { font-size: 14px; color: #606266; margin: 0; }
.device-card { border-radius: 16px; overflow: hidden; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08); transition: all 0.3s ease; background: #ffffff; }
.device-card:hover { box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12); }
.card-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 24px; background: linear-gradient(135deg, #f8f9ff 0%, #f0f2f5 100%); border-bottom: 1px solid #f0f0f0; }
.header-title-section { display: flex; align-items: center; gap: 24px; flex-wrap: wrap; }
.header-title { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
.header-stats { display: flex; align-items: center; gap: 24px; flex-wrap: wrap; }
.total-devices { display: flex; align-items: baseline; gap: 4px; }
.stats-number { font-size: 24px; font-weight: 700; color: #409EFF; }
.stats-label { font-size: 14px; color: #606266; }
.status-summary { display: flex; gap: 16px; flex-wrap: wrap; }
.status-item { display: flex; align-items: center; gap: 6px; font-size: 13px; color: #606266; }
.status-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
.status-dot.status-success { background-color: #67c23a; }
.status-dot.status-warning { background-color: #e6a23c; }
.status-dot.status-danger { background-color: #f56c6c; }
.status-dot.status-info { background-color: #909399; }
.add-device-btn { border-radius: 10px; padding: 10px 20px; font-weight: 500; background: linear-gradient(135deg, #409EFF, #66B1FF); border: none; box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3); transition: all 0.3s ease; }
.add-device-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4); background: linear-gradient(135deg, #66B1FF, #409EFF); }
.search-filter-container { display: flex; justify-content: space-between; align-items: center; padding: 24px; border-bottom: 1px solid #f0f0f0; gap: 16px; flex-wrap: wrap; }
.search-section { display: flex; gap: 16px; flex-wrap: wrap; }
.search-input { width: 320px; }
.status-select { width: 150px; }
.action-buttons { display: flex; gap: 12px; }
.search-btn { background: linear-gradient(135deg, #409EFF, #66B1FF); border: none; color: white; }
.search-btn:hover { background: linear-gradient(135deg, #66B1FF, #409EFF); }
.reset-btn { background: #f5f7fa; border: 1px solid #dcdfe6; color: #606266; }
.reset-btn:hover { background: #e6eaf0; border-color: #409EFF; color: #409EFF; }
.table-section { padding: 24px; }
.device-table-image { width: 60px; height: 60px; border-radius: 8px; object-fit: cover; }
.device-detail-image { width: 300px; height: 200px; border-radius: 12px; object-fit: contain; background: #f5f7fa; padding: 10px; }
.status-tag { border-radius: 6px; font-weight: 500; }
.operation-buttons { display: flex; gap: 8px; align-items: center; }
.edit-btn { background: linear-gradient(135deg, #409EFF, #66B1FF); border: none; color: white; }
.edit-btn:hover { background: linear-gradient(135deg, #66B1FF, #409EFF); }
.detail-btn { background: #f5f7fa; border: 1px solid #dcdfe6; color: #606266; }
.detail-btn:hover { background: #e6eaf0; border-color: #409EFF; color: #409EFF; }
.delete-btn { background: #f56c6c; border: none; color: white; }
.delete-btn:hover { background: #e64242; }
.pagination-container { display: flex; justify-content: space-between; align-items: center; padding: 20px 24px; border-top: 1px solid #f0f0f0; }
.pagination-info { font-size: 14px; color: #606266; }
.device-detail-dialog { border-radius: 16px; }
.dialog-header-info { display: flex; justify-content: space-between; align-items: center; padding: 20px; background: linear-gradient(135deg, #f8f9ff 0%, #f0f2f5 100%); border-radius: 12px; margin-bottom: 20px; }
.device-main-info { flex: 1; }
.dialog-device-name { font-size: 24px; font-weight: 600; color: #303133; margin: 0 0 8px 0; }
.dialog-device-model { font-size: 14px; color: #606266; }
.device-status-badge { display: flex; align-items: center; }
.status-badge { border-radius: 6px; font-weight: 500; padding: 6px 12px; }
.device-descriptions { margin-top: 20px; }
</style>
```

#### 1.2 设备添加页面 (AddDevice.vue)
**功能说明**：添加新设备表单，支持设备信息填写和图片上传

```vue
<template>
  <div class="add-device">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>添加设备</span>
        </div>
      </template>

      <el-form :model="deviceForm" :rules="rules" ref="deviceFormRef" label-width="120px" class="device-form">
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="deviceForm.deviceName" placeholder="请输入设备名称"></el-input>
        </el-form-item>

        <el-form-item label="设备型号" prop="deviceModel">
          <el-input v-model="deviceForm.deviceModel" placeholder="请输入设备型号"></el-input>
        </el-form-item>

        <el-form-item label="设备制造商" prop="manufacturer">
          <el-input v-model="deviceForm.manufacturer" placeholder="请输入设备制造商"></el-input>
        </el-form-item>

        <el-form-item label="设备类型" prop="deviceType">
          <el-select v-model="deviceForm.deviceType" placeholder="请选择设备类型">
            <el-option label="航拍无人机" value="航拍无人机" />
            <el-option label="测绘无人机" value="测绘无人机" />
            <el-option label="喷洒无人机" value="喷洒无人机" />
            <el-option label="巡检无人机" value="巡检无人机" />
            <el-option label="其他类型" value="其他类型" />
          </el-select>
        </el-form-item>

        <el-form-item label="序列号" prop="serialNumber">
          <el-input v-model="deviceForm.serialNumber" placeholder="请输入设备序列号"></el-input>
        </el-form-item>

        <el-form-item label="购买日期" prop="purchaseDate">
          <el-date-picker v-model="deviceForm.purchaseDate" type="date" placeholder="选择购买日期" style="width: 100%;" />
        </el-form-item>

        <el-form-item label="设备状态" prop="status">
          <el-radio-group v-model="deviceForm.status">
            <el-radio label="1">正常</el-radio>
            <el-radio label="2">维护中</el-radio>
            <el-radio label="4">停用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="品牌" prop="brand">
          <el-select v-model="deviceForm.brand" placeholder="请选择品牌">
            <el-option label="DJI大疆" value="DJI大疆" />
            <el-option label="Parrot派诺特" value="Parrot派诺特" />
            <el-option label="Yuneec昊翔" value="Yuneec昊翔" />
            <el-option label="Autel道通" value="Autel道通" />
            <el-option label="极飞XAG" value="极飞XAG" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="最大载重(kg)" prop="maxLoad">
          <el-input v-model.number="deviceForm.maxLoad" type="number" placeholder="请输入最大载重" :min="0" step="0.1"></el-input>
        </el-form-item>

        <el-form-item label="续航时间(分钟)" prop="endurance">
          <el-input v-model.number="deviceForm.endurance" type="number" placeholder="请输入续航时间" :min="0"></el-input>
        </el-form-item>

        <el-form-item label="设备图片" prop="picture">
          <el-upload class="avatar-uploader" action="/api/device/upload" :show-file-list="false" :on-success="handleUploadSuccess" :before-upload="beforeUpload">
            <img v-if="deviceForm.picture" :src="deviceForm.picture" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">提交</el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'

export default {
  name: 'AddDevice',
  components: { Plus },
  setup() {
    const router = useRouter()
    const deviceFormRef = ref(null)
    const loading = ref(false)

    const deviceForm = reactive({
      deviceName: '',
      deviceModel: '',
      manufacturer: '',
      deviceType: '',
      serialNumber: '',
      purchaseDate: '',
      status: '1',
      brand: '',
      maxLoad: null,
      endurance: null,
      picture: ''
    })

    const rules = {
      deviceName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
      deviceModel: [{ required: true, message: '请输入设备型号', trigger: 'blur' }],
      manufacturer: [{ required: true, message: '请输入设备制造商', trigger: 'blur' }],
      deviceType: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
      serialNumber: [{ required: true, message: '请输入设备序列号', trigger: 'blur' }],
      brand: [{ required: true, message: '请选择品牌', trigger: 'change' }]
    }

    const handleUploadSuccess = (res) => {
      deviceForm.picture = res.url
      ElMessage.success('上传成功')
    }

    const beforeUpload = (file) => {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt5M = file.size / 1024 / 1024 < 5
      if (!isJPG) {
        ElMessage.error('上传图片只能是 JPG/PNG 格式!')
      }
      if (!isLt5M) {
        ElMessage.error('上传图片大小不能超过 5MB!')
      }
      return isJPG && isLt5M
    }

    const submitForm = async () => {
      if (!deviceFormRef.value) return
      await deviceFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            const response = await axios.post('/device/add', deviceForm)
            if (response.data.code === 200) {
              ElMessage.success('添加成功')
              router.push('/devices')
            } else {
              ElMessage.error(response.data.message || '添加失败')
            }
          } catch (error) {
            ElMessage.error('添加失败')
          } finally {
            loading.value = false
          }
        }
      })
    }

    const resetForm = () => {
      if (deviceFormRef.value) {
        deviceFormRef.value.resetFields()
      }
    }

    const goBack = () => {
      router.back()
    }

    return {
      deviceFormRef,
      deviceForm,
      rules,
      loading,
      handleUploadSuccess,
      beforeUpload,
      submitForm,
      resetForm,
      goBack
    }
  }
}
</script>

<style scoped>
.add-device { padding: 20px; }
.card-header { font-size: 18px; font-weight: 600; }
.device-form { max-width: 600px; }
.avatar-uploader { border: 1px dashed #d9d9d9; border-radius: 6px; cursor: pointer; position: relative; overflow: hidden; transition: border-color 0.3s; }
.avatar-uploader:hover { border-color: #409EFF; }
.avatar-uploader-icon { font-size: 28px; color: #8c939d; width: 148px; height: 148px; display: flex; align-items: center; justify-content: center; }
.avatar { width: 148px; height: 148px; display: block; object-fit: cover; }
</style>
```

#### 1.3 设备编辑页面 (EditDevice.vue)
**功能说明**：编辑已有设备信息，支持设备信息修改和图片更新

```vue
<template>
  <div class="edit-device">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>编辑设备</span>
        </div>
      </template>

      <el-form :model="deviceForm" :rules="rules" ref="deviceFormRef" label-width="120px" class="device-form">
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="deviceForm.deviceName" placeholder="请输入设备名称"></el-input>
        </el-form-item>

        <el-form-item label="设备型号" prop="deviceModel">
          <el-input v-model="deviceForm.deviceModel" placeholder="请输入设备型号"></el-input>
        </el-form-item>

        <el-form-item label="设备制造商" prop="manufacturer">
          <el-input v-model="deviceForm.manufacturer" placeholder="请输入设备制造商"></el-input>
        </el-form-item>

        <el-form-item label="设备类型" prop="deviceType">
          <el-select v-model="deviceForm.deviceType" placeholder="请选择设备类型">
            <el-option label="航拍无人机" value="航拍无人机" />
            <el-option label="测绘无人机" value="测绘无人机" />
            <el-option label="喷洒无人机" value="喷洒无人机" />
            <el-option label="巡检无人机" value="巡检无人机" />
            <el-option label="其他类型" value="其他类型" />
          </el-select>
        </el-form-item>

        <el-form-item label="序列号" prop="serialNumber">
          <el-input v-model="deviceForm.serialNumber" placeholder="请输入设备序列号"></el-input>
        </el-form-item>

        <el-form-item label="设备状态" prop="status">
          <el-radio-group v-model="deviceForm.status">
            <el-radio label="1">正常</el-radio>
            <el-radio label="2">维护中</el-radio>
            <el-radio label="3">故障</el-radio>
            <el-radio label="4">停用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="品牌" prop="brand">
          <el-select v-model="deviceForm.brand" placeholder="请选择品牌">
            <el-option label="DJI大疆" value="DJI大疆" />
            <el-option label="Parrot派诺特" value="Parrot派诺特" />
            <el-option label="Yuneec昊翔" value="Yuneec昊翔" />
            <el-option label="Autel道通" value="Autel道通" />
            <el-option label="极飞XAG" value="极飞XAG" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="设备图片" prop="picture">
          <el-upload class="avatar-uploader" action="/api/device/upload" :show-file-list="false" :on-success="handleUploadSuccess" :before-upload="beforeUpload">
            <img v-if="deviceForm.picture" :src="deviceForm.picture" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">提交</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'

export default {
  name: 'EditDevice',
  components: { Plus },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const deviceFormRef = ref(null)
    const loading = ref(false)
    const deviceId = route.params.deviceId

    const deviceForm = reactive({
      deviceId: '',
      deviceName: '',
      deviceModel: '',
      manufacturer: '',
      deviceType: '',
      serialNumber: '',
      status: '1',
      brand: '',
      picture: ''
    })

    const rules = {
      deviceName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
      deviceModel: [{ required: true, message: '请输入设备型号', trigger: 'blur' }],
      deviceType: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
      brand: [{ required: true, message: '请选择品牌', trigger: 'change' }]
    }

    const fetchDeviceDetail = async () => {
      try {
        const response = await axios.get(`/device/detail/${deviceId}`)
        if (response.data.code === 200) {
          const data = response.data.data
          Object.assign(deviceForm, {
            deviceId: data.deviceId,
            deviceName: data.deviceName,
            deviceModel: data.deviceModel,
            manufacturer: data.manufacturer,
            deviceType: data.deviceType,
            serialNumber: data.serialNumber,
            status: String(data.status),
            brand: data.brand,
            picture: data.picture || ''
          })
        }
      } catch (error) {
        ElMessage.error('获取设备信息失败')
      }
    }

    const handleUploadSuccess = (res) => {
      deviceForm.picture = res.url
      ElMessage.success('上传成功')
    }

    const beforeUpload = (file) => {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt5M = file.size / 1024 / 1024 < 5
      if (!isJPG) {
        ElMessage.error('上传图片只能是 JPG/PNG 格式!')
      }
      if (!isLt5M) {
        ElMessage.error('上传图片大小不能超过 5MB!')
      }
      return isJPG && isLt5M
    }

    const submitForm = async () => {
      if (!deviceFormRef.value) return
      await deviceFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            const response = await axios.put('/device/update', deviceForm)
            if (response.data.code === 200) {
              ElMessage.success('更新成功')
              router.push('/devices')
            } else {
              ElMessage.error(response.data.message || '更新失败')
            }
          } catch (error) {
            ElMessage.error('更新失败')
          } finally {
            loading.value = false
          }
        }
      })
    }

    const goBack = () => {
      router.back()
    }

    onMounted(() => {
      fetchDeviceDetail()
    })

    return {
      deviceFormRef,
      deviceForm,
      rules,
      loading,
      handleUploadSuccess,
      beforeUpload,
      submitForm,
      goBack
    }
  }
}
</script>

<style scoped>
.edit-device { padding: 20px; }
.card-header { font-size: 18px; font-weight: 600; }
.device-form { max-width: 600px; }
.avatar-uploader { border: 1px dashed #d9d9d9; border-radius: 6px; cursor: pointer; position: relative; overflow: hidden; transition: border-color 0.3s; }
.avatar-uploader:hover { border-color: #409EFF; }
.avatar-uploader-icon { font-size: 28px; color: #8c939d; width: 148px; height: 148px; display: flex; align-items: center; justify-content: center; }
.avatar { width: 148px; height: 148px; display: block; object-fit: cover; }
</style>
```

#### 1.4 个人资料页面 (Profile.vue)
**功能说明**：显示和编辑用户个人信息，包括机主信息、资质认证等

```vue
<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人资料</span>
        </div>
      </template>

      <el-form :model="profileForm" :rules="rules" ref="profileFormRef" label-width="120px">
        <el-form-item label="用户名">
          <el-input v-model="profileForm.username" disabled></el-input>
        </el-form-item>

        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="profileForm.realName" placeholder="请输入真实姓名"></el-input>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="profileForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>

        <el-form-item label="地址" prop="address">
          <el-input v-model="profileForm.address" placeholder="请输入地址"></el-input>
        </el-form-item>

        <el-form-item label="头像">
          <el-upload class="avatar-uploader" action="/api/upload/avatar" :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
            <img v-if="profileForm.avatar" :src="profileForm.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-divider content-position="left">机主信息</el-divider>

        <el-form-item label="驾照类型">
          <el-input v-model="profileForm.licenseType" placeholder="请输入驾照类型"></el-input>
        </el-form-item>

        <el-form-item label="驾照号码">
          <el-input v-model="profileForm.licenseNumber" placeholder="请输入驾照号码"></el-input>
        </el-form-item>

        <el-form-item label="常用作业区域">
          <el-input v-model="profileForm.commonArea" placeholder="请输入常用作业区域"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'

export default {
  name: 'Profile',
  components: { Plus },
  setup() {
    const profileFormRef = ref(null)
    const loading = ref(false)

    const profileForm = reactive({
      username: '',
      realName: '',
      phone: '',
      address: '',
      avatar: '',
      licenseType: '',
      licenseNumber: '',
      commonArea: ''
    })

    const rules = {
      realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
      phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
    }

    const fetchProfile = async () => {
      try {
        const response = await axios.get('/user/profile')
        if (response.data.code === 200) {
          Object.assign(profileForm, response.data.data)
        }
      } catch (error) {
        ElMessage.error('获取个人信息失败')
      }
    }

    const handleAvatarSuccess = (res) => {
      profileForm.avatar = res.url
      ElMessage.success('头像上传成功')
    }

    const beforeAvatarUpload = (file) => {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isJPG) {
        ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
      }
      if (!isLt2M) {
        ElMessage.error('上传头像图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    }

    const submitForm = async () => {
      if (!profileFormRef.value) return
      await profileFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            const response = await axios.put('/user/profile', profileForm)
            if (response.data.code === 200) {
              ElMessage.success('保存成功')
            } else {
              ElMessage.error(response.data.message || '保存失败')
            }
          } catch (error) {
            ElMessage.error('保存失败')
          } finally {
            loading.value = false
          }
        }
      })
    }

    onMounted(() => {
      fetchProfile()
    })

    return {
      profileFormRef,
      profileForm,
      rules,
      loading,
      handleAvatarSuccess,
      beforeAvatarUpload,
      submitForm
    }
  }
}
</script>

<style scoped>
.profile-container { padding: 20px; }
.profile-card { max-width: 800px; margin: 0 auto; }
.card-header { font-size: 18px; font-weight: 600; }
.avatar-uploader { border: 1px dashed #d9d9d9; border-radius: 6px; cursor: pointer; }
.avatar-uploader:hover { border-color: #409EFF; }
.avatar-uploader-icon { font-size: 28px; color: #8c939d; width: 100px; height: 100px; display: flex; align-items: center; justify-content: center; }
.avatar { width: 100px; height: 100px; display: block; object-fit: cover; }
</style>
```

#### 1.5 聊天页面 (Chat.vue)
**功能说明**：飞手与机主之间的在线聊天功能，支持实时消息传递

```vue
<template>
  <div class="chat-container">
    <el-card class="chat-card">
      <template #header>
        <div class="card-header">
          <span>消息中心</span>
        </div>
      </template>

      <div class="chat-wrapper">
        <div class="conversation-list">
          <div v-for="conv in conversations" :key="conv.conversationId" class="conversation-item" :class="{ active: currentConversationId === conv.conversationId }" @click="selectConversation(conv)">
            <div class="conv-avatar">
              <el-avatar :src="conv.avatar" :size="40">{{ conv.nickname?.charAt(0) }}</el-avatar>
            </div>
            <div class="conv-info">
              <div class="conv-name">{{ conv.nickname }}</div>
              <div class="conv-last-msg">{{ conv.lastMessage }}</div>
            </div>
            <div class="conv-time">{{ formatTime(conv.lastMessageTime) }}</div>
          </div>
        </div>

        <div class="chat-area">
          <div class="chat-header">
            <span class="chat-with-name">{{ currentChatName }}</span>
          </div>

          <div class="message-list" ref="messageListRef">
            <div v-for="msg in messages" :key="msg.id" class="message-item" :class="{ mine: msg.senderId === myId, yours: msg.senderId !== myId }">
              <div class="message-content">{{ msg.content }}</div>
              <div class="message-time">{{ formatTime(msg.createTime) }}</div>
            </div>
          </div>

          <div class="chat-input">
            <el-input v-model="messageContent" placeholder="输入消息..." @keyup.enter="sendMessage"></el-input>
            <el-button type="primary" @click="sendMessage">发送</el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted, nextTick } from 'vue'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'

export default {
  name: 'Chat',
  setup() {
    const conversations = ref([])
    const currentConversationId = ref('')
    const currentChatName = ref('')
    const messages = ref([])
    const messageContent = ref('')
    const messageListRef = ref(null)
    const myId = ref('')

    const fetchConversations = async () => {
      try {
        const response = await axios.get('/chat/conversations')
        if (response.data.code === 200) {
          conversations.value = response.data.data
          if (conversations.value.length > 0) {
            selectConversation(conversations.value[0])
          }
        }
      } catch (error) {
        ElMessage.error('获取会话列表失败')
      }
    }

    const selectConversation = async (conv) => {
      currentConversationId.value = conv.conversationId
      currentChatName.value = conv.nickname
      await fetchMessages()
    }

    const fetchMessages = async () => {
      try {
        const response = await axios.get(`/chat/messages/${currentConversationId.value}`)
        if (response.data.code === 200) {
          messages.value = response.data.data.records || []
          scrollToBottom()
        }
      } catch (error) {
        ElMessage.error('获取消息列表失败')
      }
    }

    const sendMessage = async () => {
      if (!messageContent.value.trim()) return
      try {
        await axios.post('/chat/send', {
          conversationId: currentConversationId.value,
          content: messageContent.value
        })
        messageContent.value = ''
        await fetchMessages()
      } catch (error) {
        ElMessage.error('发送消息失败')
      }
    }

    const scrollToBottom = () => {
      nextTick(() => {
        if (messageListRef.value) {
          messageListRef.value.scrollTop = messageListRef.value.scrollHeight
        }
      })
    }

    const formatTime = (time) => {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    }

    onMounted(() => {
      myId.value = localStorage.getItem('userId')
      fetchConversations()
    })

    return {
      conversations,
      currentConversationId,
      currentChatName,
      messages,
      messageContent,
      messageListRef,
      myId,
      selectConversation,
      sendMessage,
      formatTime
    }
  }
}
</script>

<style scoped>
.chat-container { padding: 20px; }
.chat-card { height: calc(100vh - 40px); }
.card-header { font-size: 18px; font-weight: 600; }
.chat-wrapper { display: flex; height: calc(100% - 60px); }
.conversation-list { width: 280px; border-right: 1px solid #e6eaf0; overflow-y: auto; }
.conversation-item { display: flex; align-items: center; padding: 12px; cursor: pointer; border-bottom: 1px solid #f0f0f0; }
.conversation-item:hover { background: #f5f7fa; }
.conversation-item.active { background: #ecf5ff; }
.conv-avatar { margin-right: 12px; }
.conv-info { flex: 1; overflow: hidden; }
.conv-name { font-weight: 500; margin-bottom: 4px; }
.conv-last-msg { font-size: 12px; color: #909399; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.conv-time { font-size: 12px; color: #c0c4cc; }
.chat-area { flex: 1; display: flex; flex-direction: column; }
.chat-header { padding: 12px 16px; border-bottom: 1px solid #e6eaf0; font-weight: 500; }
.message-list { flex: 1; overflow-y: auto; padding: 16px; }
.message-item { margin-bottom: 16px; }
.message-item.mine { text-align: right; }
.message-item.yours { text-align: left; }
.message-content { display: inline-block; padding: 8px 12px; border-radius: 8px; max-width: 70%; word-break: break-word; }
.message-item.mine .message-content { background: #409EFF; color: white; }
.message-item.yours .message-content { background: #f5f7fa; color: #303133; }
.message-time { font-size: 11px; color: #c0c4cc; margin-top: 4px; }
.chat-input { display: flex; gap: 12px; padding: 12px 16px; border-top: 1px solid #e6eaf0; }
</style>
```

---

## 第三部分：微信小程序代码

### 飞手端设备租赁页面 (noperson/pages/flyer/device-rent/index.js)

```javascript
const app = getApp();

Page({
  data: {
    deviceId: '',
    deviceInfo: null,
    duration: 1,
    deposit: 0,
    totalAmount: 0,
    hourlyRate: 0,
    showDepositInfo: false,
    isAgreed: false
  },

  onLoad: function(options) {
    if (options.deviceId) {
      this.setData({ deviceId: options.deviceId });
      this.fetchDeviceInfo();
    }
  },

  fetchDeviceInfo: function() {
    wx.request({
      url: `${app.globalData.apiBaseUrl}/device/detail/${this.data.deviceId}`,
      method: 'GET',
      header: { 'Authorization': 'Bearer ' + wx.getStorageSync('token') },
      success: (res) => {
        if (res.data.code === 200) {
          const device = res.data.data;
          const hourlyRate = device.hourlyRent || 0;
          this.setData({
            deviceInfo: device,
            hourlyRate: hourlyRate,
            totalAmount: hourlyRate * this.data.duration
          });
          this.checkFlyerQualification();
        } else {
          wx.showToast({ title: '获取设备信息失败', icon: 'none' });
        }
      },
      fail: () => {
        wx.showToast({ title: '网络请求失败', icon: 'none' });
      }
    });
  },

  checkFlyerQualification: function() {
    wx.request({
      url: `${app.globalData.apiBaseUrl}/flyer/qualification`,
      method: 'GET',
      header: { 'Authorization': 'Bearer ' + wx.getStorageSync('token') },
      success: (res) => {
        if (res.data.code === 200) {
          const flyer = res.data.data;
          const praiseRate = flyer.praiseRate || 0;
          if (praiseRate <= 50) {
            this.setData({
              showDepositInfo: true,
              deposit: 500
            });
          }
        }
      }
    });
  },

  onDurationChange: function(e) {
    const duration = parseInt(e.detail.value);
    this.setData({
      duration: duration,
      totalAmount: this.data.hourlyRate * duration + (this.data.showDepositInfo ? this.data.deposit : 0)
    });
  },

  onAgreementChange: function(e) {
    this.setData({ isAgreed: e.detail.value.length > 0 });
  },

  confirmRent: function() {
    if (!this.data.isAgreed) {
      wx.showToast({ title: '请阅读并同意租赁协议', icon: 'none' });
      return;
    }

    wx.showModal({
      title: '确认租赁',
      content: `设备：${this.data.deviceInfo.deviceName}\n时长：${this.data.duration}小时\n押金：${this.data.showDepositInfo ? this.data.deposit : 0}元\n合计：${this.data.totalAmount}元`,
      success: (res) => {
        if (res.confirm) {
          this.executeRent();
        }
      }
    });
  },

  executeRent: function() {
    wx.showLoading({ title: '正在提交...' });
    wx.request({
      url: `${app.globalData.apiBaseUrl}/device/rent/${this.data.deviceId}`,
      method: 'POST',
      header: { 'Authorization': 'Bearer ' + wx.getStorageSync('token') },
      success: (res) => {
        wx.hideLoading();
        if (res.data.code === 200) {
          wx.showToast({ title: '租赁成功', icon: 'success' });
          setTimeout(() => {
            wx.navigateBack();
          }, 1500);
        } else {
          wx.showToast({ title: res.data.message || '租赁失败', icon: 'none' });
        }
      },
      fail: () => {
        wx.hideLoading();
        wx.showToast({ title: '网络请求失败', icon: 'none' });
      }
    });
  }
});
```

### 飞手端聊天详情页面 (noperson/pages/flyer/chat/detail.js)

```javascript
const app = getApp();

Page({
  data: {
    conversationId: '',
    messages: [],
    inputContent: '',
    toView: '',
    scrollTop: 0
  },

  onLoad: function(options) {
    if (options.conversationId) {
      this.setData({ conversationId: options.conversationId });
      this.fetchMessages();
      this.startPolling();
    }
  },

  onUnload: function() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  },

  fetchMessages: function() {
    wx.request({
      url: `${app.globalData.apiBaseUrl}/chat/messages/${this.data.conversationId}`,
      method: 'GET',
      header: { 'Authorization': 'Bearer ' + wx.getStorageSync('token') },
      success: (res) => {
        if (res.data.code === 200) {
          this.setData({
            messages: res.data.data.records || [],
            toView: 'msg-' + (res.data.data.records?.length || 0) - 1
          });
        }
      }
    });
  },

  startPolling: function() {
    this.intervalId = setInterval(() => {
      this.fetchMessages();
    }, 3000);
  },

  onInputChange: function(e) {
    this.setData({ inputContent: e.detail.value });
  },

  sendMessage: function() {
    if (!this.data.inputContent.trim()) return;

    const content = this.data.inputContent;
    this.setData({ inputContent: '', messages: [] });

    wx.request({
      url: `${app.globalData.apiBaseUrl}/chat/send`,
      method: 'POST',
      header: { 'Authorization': 'Bearer ' + wx.getStorageSync('token') },
      data: {
        conversationId: this.data.conversationId,
        content: content
      },
      success: (res) => {
        if (res.data.code === 200) {
          this.fetchMessages();
        } else {
          wx.showToast({ title: '发送失败', icon: 'none' });
        }
      },
      fail: () => {
        wx.showToast({ title: '网络请求失败', icon: 'none' });
      }
    });
  },

  scrollToBottom: function() {
    this.setData({ scrollTop: 999999 });
  }
});
```

---

## 系统功能说明

### 1. 设备管理功能
- **设备添加**：机主可以添加新的无人机设备，填写设备名称、型号、制造商、序列号等信息
- **设备编辑**：机主可以编辑已有设备的信息
- **设备删除**：机主可以删除未在使用的设备
- **设备状态管理**：支持设备状态的切换（正常、维护中、故障、停用）

### 2. 设备租赁功能
- **设备查询**：飞手可以查询可用的无人机设备
- **设备租赁**：飞手可以租赁设备，系统根据飞手好评率决定是否需要押金
- **设备归还**：飞手可以归还租赁的设备
- **押金管理**：好评率低于50%的飞手需要支付500元押金

### 3. 聊天功能
- **会话列表**：显示与机主/飞手的所有会话
- **消息发送**：支持实时消息发送
- **消息提醒**：未读消息数量提醒

### 4. 用户管理功能
- **个人信息**：用户可以查看和编辑个人信息
- **角色管理**：系统支持农户、飞手、机主三种角色
- **状态管理**：管理员可以禁用/启用用户账号
