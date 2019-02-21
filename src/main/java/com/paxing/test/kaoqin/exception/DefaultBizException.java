package com.paxing.test.kaoqin.exception;

/**
 * 默认业务异常
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/2/21
 */
public class DefaultBizException extends RuntimeException {

    private static final long serialVersionUID = 6183090775045325026L;

    /**
     * 异常码
     */
    private Integer code;

    /**
     * 异常信息
     */
    private String msg;

    public DefaultBizException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public DefaultBizException() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
