package com.cty.nopersonfinally.pojo.dto;

import lombok.Data;

import java.util.Date;

/**
 * 设备租赁DTO
 */
@Data
public class DeviceRentalDTO {

    /**
     * 设备ID
     */
    private Long deviceId;

    /**
     * 租赁时长（小时）
     */
    private Integer rentalHours;

    /**
     * 取设备时间
     */
    private Date pickupTime;

    /**
     * 备注
     */
    private String remark;
}