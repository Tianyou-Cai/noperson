package com.cty.nopersonfinally.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cty.nopersonfinally.mapper.SysUserMapper;
import com.cty.nopersonfinally.mapper.SysUserRoleMapper;
import com.cty.nopersonfinally.pojo.entity.SysUser;
import com.cty.nopersonfinally.pojo.entity.SysUserRole;
import com.cty.nopersonfinally.pojo.entity.UserFlyer;
import com.cty.nopersonfinally.mapper.UserFlyerMapper;
import com.cty.nopersonfinally.pojo.dto.Result;
import com.cty.nopersonfinally.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mini")
public class MiniLoginController {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private UserFlyerMapper userFlyerMapper;

    @Value("${wechat.miniapp.appid}")
    private String appId;

    @Value("${wechat.miniapp.secret}")
    private String appSecret;

    private static final String WECHAT_CODE2SESSION_URL =
        "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, Object> request) {
        String code = (String) request.get("code");
        String nickname = (String) request.get("nickname");
        String avatar = (String) request.get("avatar");

        if (code == null || code.isEmpty()) {
            return Result.error("code不能为空");
        }

        String openId;
        try {
            openId = getOpenIdFromWx(code);
        } catch (Exception e) {
            return Result.error("微信登录失败：" + e.getMessage());
        }

        SysUser user = null;
        try {
            LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysUser::getWxOpenid, openId);
            user = sysUserMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            // 用户不存在，user为null
        }

        if (user == null) {
            user = new SysUser();
            user.setUsername(openId);
            user.setWxOpenid(openId);
            user.setPassword("");
            user.setRealName(nickname != null ? nickname : "用户");
            user.setAvatar(avatar);
            user.setRoleType(0);
            user.setAuditStatus(1);
            user.setStatus(1);
            user.setBalance(0.0);
            user.setCreateTime(LocalDateTime.now());
            user.setLastLoginTime(LocalDateTime.now());

            int insertResult = sysUserMapper.insert(user);
            if (insertResult <= 0) {
                return Result.error("用户创建失败");
            }
        } else {
            if (nickname != null && !nickname.isEmpty()) {
                user.setRealName(nickname);
            }
            if (avatar != null && !avatar.isEmpty()) {
                user.setAvatar(avatar);
            }
            user.setLastLoginTime(LocalDateTime.now());
            sysUserMapper.updateById(user);
        }

        // 获取用户的所有角色
        List<String> roles = getUserRolesInternal(user.getUserId());

        String token = JWTUtil.generateToken(user.getUserId(), roles.isEmpty() ? "" : roles.get(0));

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getUserId());
        result.put("username", user.getRealName());
        result.put("avatar", user.getAvatar());
        result.put("roles", roles);

        Result<Map<String, Object>> response = new Result<>();
        response.setCode(200);
        response.setMessage("登录成功");
        response.setData(result);
        return response;
    }

    @PostMapping("/select-role")
    public Result<?> selectRole(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        String roleCode = (String) request.get("roleCode");

        if (userId == null || roleCode == null || roleCode.isEmpty()) {
            return Result.error("参数不能为空");
        }

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 验证角色编码是否有效
        roleCode = roleCode.toUpperCase();
        if (!Arrays.asList("FARMER", "FLYER", "OWNER").contains(roleCode)) {
            return Result.error("无效的角色");
        }

        // 检查用户是否已有该角色
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId)
                   .eq(SysUserRole::getRoleCode, roleCode);
        SysUserRole existingRole = sysUserRoleMapper.selectOne(queryWrapper);

        if (existingRole == null) {
            // 添加新角色
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleCode(roleCode);
            userRole.setCreateTime(LocalDateTime.now());
            sysUserRoleMapper.insert(userRole);

            // 如果选择飞手角色，自动创建飞手信息记录
            if ("FLYER".equals(roleCode)) {
                LambdaQueryWrapper<UserFlyer> flyerQueryWrapper = new LambdaQueryWrapper<>();
                flyerQueryWrapper.eq(UserFlyer::getUserId, userId);
                UserFlyer existingFlyer = userFlyerMapper.selectOne(flyerQueryWrapper);
                
                if (existingFlyer == null) {
                    UserFlyer flyer = new UserFlyer();
                    flyer.setUserId(userId);
                    // 设置用户名，优先级：realName > username > 默认值
                    String userName = user.getRealName();
                    if (userName == null || userName.isEmpty()) {
                        userName = user.getUsername();
                    }
                    if (userName == null || userName.isEmpty()) {
                        userName = "飞手用户";
                    }
                    flyer.setUserName(userName);
                    flyer.setReputation(50.0); // 初始信誉分50
                    flyer.setIsFree(1); // 默认空闲
                    flyer.setAuditStatus(0); // 待审核
                    flyer.setCreditScore(0); // 初始信用分0
                    flyer.setCreateTime(LocalDateTime.now());
                    userFlyerMapper.insert(flyer);
                }
            }
        }

        String token = JWTUtil.generateToken(userId, roleCode);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("role", roleCode);

        Result<Map<String, Object>> response = new Result<>();
        response.setCode(200);
        response.setMessage("角色选择成功");
        response.setData(result);
        return response;
    }

    @GetMapping("/user-roles/{userId}")
    public Result<?> getUserRoles(@PathVariable Long userId) {
        List<String> roles = getUserRolesInternal(userId);

        Result<List<String>> response = new Result<>();
        response.setCode(200);
        response.setMessage("获取成功");
        response.setData(roles);
        return response;
    }

    @GetMapping("/available-roles")
    public Result<?> getAvailableRoles() {
        List<String> roles = Arrays.asList("FARMER", "FLYER", "OWNER");
        Map<String, Object> descriptions = new HashMap<>();
        descriptions.put("FARMER", "农户端");
        descriptions.put("FLYER", "飞手端");
        descriptions.put("OWNER", "机主端");

        Map<String, Object> result = new HashMap<>();
        result.put("roles", roles);
        result.put("descriptions", descriptions);

        Result<Map<String, Object>> response = new Result<>();
        response.setCode(200);
        response.setMessage("获取成功");
        response.setData(result);
        return response;
    }

    @PostMapping("/switch-role")
    public Result<?> switchRole(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        String roleCode = (String) request.get("roleCode");

        if (userId == null || roleCode == null || roleCode.isEmpty()) {
            return Result.error("参数不能为空");
        }

        // 验证用户是否有该角色
        List<String> userRoles = getUserRolesInternal(userId);
        roleCode = roleCode.toUpperCase();

        if (!userRoles.contains(roleCode)) {
            return Result.error("您没有该角色，请先选择");
        }

        String token = JWTUtil.generateToken(userId, roleCode);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("role", roleCode);

        Result<Map<String, Object>> response = new Result<>();
        response.setCode(200);
        response.setMessage("角色切换成功");
        response.setData(result);
        return response;
    }

    private List<String> getUserRolesInternal(Long userId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(queryWrapper);
        return userRoles.stream()
                .map(SysUserRole::getRoleCode)
                .collect(Collectors.toList());
    }

    private String getOpenIdFromWx(String code) {
        String url = String.format(WECHAT_CODE2SESSION_URL, appId, appSecret, code);
        
        RestTemplate restTemplate = new RestTemplate();
        org.springframework.http.ResponseEntity<String> responseEntity;
        
        try {
            responseEntity = restTemplate.getForEntity(url, String.class);
        } catch (Exception e) {
            throw new RuntimeException("微信服务请求失败：" + e.getMessage());
        }
        
        String responseBody = responseEntity.getBody();
        if (responseBody == null || responseBody.isEmpty()) {
            throw new RuntimeException("微信服务响应为空");
        }

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        Map<String, Object> response;
        try {
            response = mapper.readValue(responseBody, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("微信响应解析失败：" + responseBody);
        }

        Object errcodeObj = response.get("errcode");
        if (errcodeObj != null) {
            int errcode;
            if (errcodeObj instanceof Integer) {
                errcode = (Integer) errcodeObj;
            } else {
                errcode = Integer.parseInt(String.valueOf(errcodeObj));
            }
            if (errcode != 0) {
                String errmsg = (String) response.get("errmsg");
                throw new RuntimeException("微信登录失败：" + (errmsg != null ? errmsg : "未知错误"));
            }
        }

        String openid = (String) response.get("openid");
        if (openid == null || openid.isEmpty()) {
            throw new RuntimeException("获取openid失败");
        }

        return openid;
    }
}