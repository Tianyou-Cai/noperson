package com.cty.nopersonfinally.pojo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter

public enum SprayDemandStatusEnum {
    PENDING_PAY(0, "待支付"),
    PENDING(1, "待接取"),
    PROCESSING(2, "处理中"),
    IN_PROGRESS(3, "作业中"),
    WAITING_CONFIRM(4, "待确认"),
    COMPLETED(5, "已完成"),
    CANCELLED(6, "已取消");

    private final int code;
    private final String desc;

    // 根据code获取描述
    public static String getDescByCode(Integer code) {
        if (code == null) {
            return "未知状态";
        }
        for (SprayDemandStatusEnum status : values()) {
            if (status.code == code) {
                return status.desc;
            }
        }
        return "未知状态";
    }

    // 根据code获取枚举
    public static SprayDemandStatusEnum getByCode(int code) {
        for (SprayDemandStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }

    SprayDemandStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}