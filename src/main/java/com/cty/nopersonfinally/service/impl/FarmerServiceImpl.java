package com.cty.nopersonfinally.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cty.nopersonfinally.mapper.FarmerMapper;
import com.cty.nopersonfinally.mapper.SysUserMapper;
import com.cty.nopersonfinally.pojo.dto.FarmerUpdateDTO;
import com.cty.nopersonfinally.pojo.entity.SysUser;
import com.cty.nopersonfinally.pojo.vo.FarmerInfoVO;
import com.cty.nopersonfinally.service.FarmerService;
import com.cty.nopersonfinally.utils.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 农户服务实现类
 */
@Service
public class FarmerServiceImpl implements FarmerService {

    @Autowired
    private FarmerMapper farmerMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Override
    public Page<FarmerInfoVO> getFarmersForFlyer(int pageNum, int pageSize) {
        // 创建分页对象
        Page<FarmerInfoVO> page = new Page<>(pageNum, pageSize);
        
        // 调用Mapper层方法查询农户列表
        return farmerMapper.selectFarmersForFlyer(page);
    }
    
    @Override
    public Page<FarmerInfoVO> getFarmersForAdmin(int pageNum, int pageSize, String keyword) {
        // 创建分页对象
        Page<FarmerInfoVO> page = new Page<>(pageNum, pageSize);
        
        // 调用Mapper层方法查询农户列表（带关键词搜索）
        return farmerMapper.selectFarmersForAdmin(page, keyword);
    }
    
    @Override
    public FarmerInfoVO getFarmerInfoById(Long farmerId) {
        // 调用Mapper层方法根据ID查询农户信息
        return farmerMapper.selectFarmerInfoById(farmerId);
    }
    
    @Override
    @Transactional
    public void updateFarmer(FarmerUpdateDTO dto) {
        // 查询农户用户信息
        SysUser user = sysUserMapper.selectById(dto.getUserId());
        if (user == null) {
            throw new BusinessException("农户不存在");
        }
        
        // 验证是否为农户
        if (user.getRoleType() != 1) {
            throw new BusinessException("用户不是农户");
        }
        
        // 更新用户信息
        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getRealName() != null) {
            user.setRealName(dto.getRealName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        
        // 保存更新
        sysUserMapper.updateById(user);
    }
    
    @Override
    @Transactional
    public void deleteFarmer(Long farmerId) {
        // 查询农户用户信息
        SysUser user = sysUserMapper.selectById(farmerId);
        if (user == null) {
            throw new BusinessException("农户不存在");
        }
        
        // 验证是否为农户
        if (user.getRoleType() != 1) {
            throw new BusinessException("用户不是农户");
        }
        
        // 逻辑删除（设置is_deleted=1）
        user.setIsDeleted(1);
        sysUserMapper.updateById(user);
    }
    
    @Override
    @Transactional
    public void updateFarmerStatus(Long farmerId, Integer status) {
        // 查询农户用户信息
        SysUser user = sysUserMapper.selectById(farmerId);
        if (user == null) {
            throw new BusinessException("农户不存在");
        }
        
        // 验证是否为农户
        if (user.getRoleType() != 1) {
            throw new BusinessException("用户不是农户");
        }
        
        // 验证状态合法性
        if (status != 0 && status != 1) {
            throw new BusinessException("状态值无效，只能是0（禁用）或1（正常）");
        }
        
        // 更新状态
        user.setStatus(status);
        sysUserMapper.updateById(user);
    }
}