package com.ddg.meituan.common.exception;

public enum MeituanCodeEnum {

    VALID_EXCEPTION(10001, "参数校验格式出错"),
    UN_NONE_EXCEPTION(10000, "系统未知异常"),
    GULI_MAIL_EXCEPTION(40001, ""),
    ELASTICSEARCH_EXCEPTION(11000, "商品上架错误");


    private Integer code;

    private String message;

    MeituanCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }

}
