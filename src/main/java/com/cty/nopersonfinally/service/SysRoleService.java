package com.cty.nopersonfinally.service;

import com.cty.nopersonfinally.pojo.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口
 */
public interface SysRoleService {

    /**
     * 根据用户ID查询角色列表
     */
    List<SysRole> getRolesByUserId(Long userId);

    /**
     * 根据角色编码查询角色
     */
    SysRole getRoleByCode(String roleCode);

    /**
     * 为用户分配角色
     */
    boolean assignRole(Long userId, String roleCode);

    /**
     * 为用户分配多个角色
     */
    boolean assignRoles(Long userId, List<String> roleCodes);

    /**
     * 检查用户是否有指定角色
     */
    boolean hasRole(Long userId, String roleCode);

    /**
     * 获取用户的角色编码列表
     */
    List<String> getRoleCodesByUserId(Long userId);
}