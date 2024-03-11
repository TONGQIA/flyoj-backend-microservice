package com.tong.flyojbackendmodel.model.codesandbox;

import org.apache.commons.lang3.ObjectUtils;

/**
 * 用户角色枚举
 *
 * @author tong
 * 
 */
public enum ExecuteStatusEnum {

    SUCCESS(0, "运行成功"),
    PARAMS_ERROR(1, "请求参数错误"),
    SYSTEM_ERROR(2, "系统内部异常"),
    RUN_ERROR(3, "程序运行异常"),
    COMPILE_ERROR(4, "编译错误"),
    NO_AUTH_ERROR(5, "无权限"),
    OPERATION_ERROR(6, "操作失败");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 信息
     */
    private final String message;

    ExecuteStatusEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static ExecuteStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ExecuteStatusEnum anEnum : ExecuteStatusEnum.values()) {
            if (anEnum.code.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
