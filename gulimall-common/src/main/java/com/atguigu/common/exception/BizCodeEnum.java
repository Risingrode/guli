package com.atguigu.common.exception;

public enum BizCodeEnum {
    // 枚举之间用逗号隔开
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VAILD_EXCEPTION(10001,"参数格式校验失败"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常");

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String msg;
    BizCodeEnum(int code, String msg) {
        this.code=code;
        this.msg=msg;
    }
}
