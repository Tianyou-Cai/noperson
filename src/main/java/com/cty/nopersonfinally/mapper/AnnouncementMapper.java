package com.cty.nopersonfinally.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cty.nopersonfinally.pojo.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告Mapper
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {

    /**
     * 分页查询公告列表
     */
    IPage<Announcement> selectPageList(Page<Announcement> page, @Param("title") String title, @Param("isActive") Integer isActive);

    /**
     * 查询启用的公告
     */
    List<Announcement> selectActiveList(@Param("targetRole") String targetRole);
}