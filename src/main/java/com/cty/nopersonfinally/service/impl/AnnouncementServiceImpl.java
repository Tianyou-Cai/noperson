package com.cty.nopersonfinally.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cty.nopersonfinally.mapper.AnnouncementMapper;
import com.cty.nopersonfinally.pojo.entity.Announcement;
import com.cty.nopersonfinally.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告服务实现类
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public IPage<Announcement> getPageList(int pageNum, int pageSize, String title, Integer isActive) {
        Page<Announcement> page = new Page<>(pageNum, pageSize);
        return announcementMapper.selectPageList(page, title, isActive);
    }

    @Override
    public Announcement getById(Long id) {
        return announcementMapper.selectById(id);
    }

    @Override
    public boolean create(Announcement announcement) {
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        return announcementMapper.insert(announcement) > 0;
    }

    @Override
    public boolean update(Announcement announcement) {
        announcement.setUpdateTime(LocalDateTime.now());
        return announcementMapper.updateById(announcement) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return announcementMapper.deleteById(id) > 0;
    }

    @Override
    public boolean toggleStatus(Long id, Integer isActive) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement != null) {
            announcement.setIsActive(isActive);
            announcement.setUpdateTime(LocalDateTime.now());
            return announcementMapper.updateById(announcement) > 0;
        }
        return false;
    }

    @Override
    public List<Announcement> getUserAnnouncements(String role) {
        return announcementMapper.selectActiveList(role);
    }
}