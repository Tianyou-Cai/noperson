package com.cty.nopersonfinally.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cty.nopersonfinally.pojo.dto.FarmerUpdateDTO;
import com.cty.nopersonfinally.pojo.vo.FarmerInfoVO;

/**
 * 农户服务接口
 * 提供飞手查询农户信息的服务方法
 */
public interface FarmerService {
    
    /**
     * 飞手分页查询农户列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 农户信息分页结果
     */
    Page<FarmerInfoVO> getFarmersForFlyer(int pageNum, int pageSize);
    
    /**
     * 管理员分页查询农户列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param keyword 关键词搜索（用户名、手机号、真实姓名）
     * @return 农户信息分页结果
     */
    Page<FarmerInfoVO> getFarmersForAdmin(int pageNum, int pageSize, String keyword);
    
    /**
     * 根据ID查询农户详细信息
     * @param farmerId 农户ID
     * @return 农户详细信息
     */
    FarmerInfoVO getFarmerInfoById(Long farmerId);
    
    /**
     * 管理员更新农户信息
     * @param dto 更新数据
     */
    void updateFarmer(FarmerUpdateDTO dto);
    
    /**
     * 管理员删除农户
     * @param farmerId 农户ID
     */
    void deleteFarmer(Long farmerId);
    
    /**
     * 管理员更新农户状态
     * @param farmerId 农户ID
     * @param status 状态（1-正常，0-禁用）
     */
    void updateFarmerStatus(Long farmerId, Integer status);
}