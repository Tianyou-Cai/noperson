package com.cty.nopersonfinally.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cty.nopersonfinally.pojo.entity.Announcement;
import com.cty.nopersonfinally.pojo.dto.Result;
import com.cty.nopersonfinally.service.AnnouncementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告控制器
 */
@RestController
@RequestMapping("/announcement")
@Api(tags = "公告管理接口")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 分页查询公告列表
     */
    @GetMapping("/list")
    @ApiOperation("分页查询公告列表")
    public Result<IPage<Announcement>> getPageList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer isActive) {
        IPage<Announcement> announcements = announcementService.getPageList(pageNum, pageSize, title, isActive);
        return Result.ok(announcements);
    }

    /**
     * 根据ID获取公告详情
     */
    @GetMapping("/{id}")
    @ApiOperation("获取公告详情")
    public Result<Announcement> getById(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement != null) {
            return Result.ok(announcement);
        }
        return (Result<Announcement>) Result.error("公告不存在");
    }

    /**
     * 新增公告
     */
    @PostMapping
    @ApiOperation("新增公告")
    public Result<Boolean> create(@RequestBody Announcement announcement) {
        boolean success = announcementService.create(announcement);
        return success ? Result.ok(true) : (Result<Boolean>) Result.error("创建失败");
    }

    /**
     * 更新公告
     */
    @PutMapping("/{id}")
    @ApiOperation("更新公告")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        boolean success = announcementService.update(announcement);
        return success ? Result.ok(true) : (Result<Boolean>) Result.error("更新失败");
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除公告")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = announcementService.delete(id);
        return success ? Result.ok(true) : (Result<Boolean>) Result.error("删除失败");
    }

    /**
     * 启用/禁用公告
     */
    @PostMapping("/{id}/status")
    @ApiOperation("启用/禁用公告")
    public Result<Boolean> toggleStatus(@PathVariable Long id, @RequestParam Integer isActive) {
        boolean success = announcementService.toggleStatus(id, isActive);
        return success ? Result.ok(true) : (Result<Boolean>) Result.error("操作失败");
    }

    /**
     * 获取用户可见的公告列表
     */
    @GetMapping("/user/list")
    @ApiOperation("获取用户可见的公告列表")
    public Result<List<Announcement>> getUserAnnouncements(@RequestParam(defaultValue = "ALL") String role) {
        List<Announcement> announcements = announcementService.getUserAnnouncements(role);
        return Result.ok(announcements);
    }
}