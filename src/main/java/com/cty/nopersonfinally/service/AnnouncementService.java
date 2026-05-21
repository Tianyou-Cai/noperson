package com.cty.nopersonfinally.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cty.nopersonfinally.pojo.entity.Announcement;

import java.util.List;

/**
 * 公告服务接口
 */
public interface AnnouncementService {

    /**
     * 分页查询公告列表
     */
    IPage<Announcement> getPageList(int pageNum, int pageSize, String title, Integer isActive);

    /**
     * 根据ID获取公告
     */
    Announcement getById(Long id);

    /**
     * 新增公告
     */
    boolean create(Announcement announcement);

    /**
     * 更新公告
     */
    boolean update(Announcement announcement);

    /**
     * 删除公告
     */
    boolean delete(Long id);

    /**
     * 启用/禁用公告
     */
    boolean toggleStatus(Long id, Integer isActive);

    /**
     * 获取用户可见的公告列表
     */
    List<Announcement> getUserAnnouncements(String role);
}