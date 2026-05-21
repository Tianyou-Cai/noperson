package com.cty.nopersonfinally.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 农户信息更新DTO
 */
@Data
@ApiModel("农户信息更新DTO")
public class FarmerUpdateDTO {
    
    @ApiModelProperty("用户ID")
    private Long userId;
    
    @ApiModelProperty("用户名")
    private String username;
    
    @ApiModelProperty("真实姓名")
    private String realName;
    
    @ApiModelProperty("电话")
    private String phone;
    
    @ApiModelProperty("状态（1-正常，0-禁用）")
    private Integer status;
}
