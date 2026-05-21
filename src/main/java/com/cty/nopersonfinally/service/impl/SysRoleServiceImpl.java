package com.cty.nopersonfinally.service.impl;

import com.cty.nopersonfinally.mapper.SysRoleMapper;
import com.cty.nopersonfinally.mapper.SysUserRoleMapper;
import com.cty.nopersonfinally.pojo.entity.SysRole;
import com.cty.nopersonfinally.pojo.entity.SysUserRole;
import com.cty.nopersonfinally.service.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(queryWrapper);
        
        List<String> roleCodes = userRoles.stream()
                .map(SysUserRole::getRoleCode)
                .collect(Collectors.toList());
        
        if (roleCodes.isEmpty()) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<SysRole> roleQuery = new LambdaQueryWrapper<>();
        roleQuery.in(SysRole::getRoleCode, roleCodes);
        return sysRoleMapper.selectList(roleQuery);
    }

    @Override
    public SysRole getRoleByCode(String roleCode) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleCode, roleCode);
        return sysRoleMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public boolean assignRole(Long userId, String roleCode) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId)
                   .eq(SysUserRole::getRoleCode, roleCode);
        
        SysUserRole existing = sysUserRoleMapper.selectOne(queryWrapper);
        if (existing != null) {
            return true;
        }

        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleCode(roleCode);
        return sysUserRoleMapper.insert(userRole) > 0;
    }

    @Override
    @Transactional
    public boolean assignRoles(Long userId, List<String> roleCodes) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        sysUserRoleMapper.delete(queryWrapper);

        for (String roleCode : roleCodes) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleCode(roleCode);
            sysUserRoleMapper.insert(userRole);
        }
        return true;
    }

    @Override
    public boolean hasRole(Long userId, String roleCode) {
        List<SysRole> roles = getRolesByUserId(userId);
        return roles.stream()
                .anyMatch(role -> roleCode.equals(role.getRoleCode()));
    }

    @Override
    public List<String> getRoleCodesByUserId(Long userId) {
        List<SysRole> roles = getRolesByUserId(userId);
        return roles.stream()
                .map(SysRole::getRoleCode)
                .collect(Collectors.toList());
    }
}