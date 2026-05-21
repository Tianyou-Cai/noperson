package com.cty.nopersonfinally.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统公告实体
 */
@Data
@TableName("announcement")
public class Announcement {
    @TableId(type = IdType.AUTO)
    @TableField("announcement_id")
    private Long id;

    private String title; // 公告标题

    private String content; // 公告内容

    @TableField("target_role")
    private String targetRole; // 目标角色（ALL-全部，FARMER-农户，FLYER-飞手）

    @TableField("is_active")
    private Integer isActive; // 是否启用（0-禁用，1-启用）

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}