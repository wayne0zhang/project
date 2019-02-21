package com.paxing.test.kaoqin.config.resolver;

/**
 * 返回给客户端的message，包含http状态码，业务码，以及业务码说明
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/2/20
 */
public final class RestResponseMessage {

    private int code;

    private String message;

    private int httpStatus;

    private boolean requestSucceeded = true;

    public RestResponseMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public RestResponseMessage(int code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }


    /**
     * @return 请求是否发送成功
     */
    public boolean isRequestSucceeded() {
        return this.requestSucceeded;
    }


    /**
     * 设置 请求是否发送成功
     *
     * @param requestSucceeded to set
     */
    public void setRequestSucceeded(boolean requestSucceeded) {
        this.requestSucceeded = requestSucceeded;
    }


    /**
     * 业务码
     *
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * 业务信息
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * http状态码
     *
     * @return the httpStatus
     */
    public int getHttpStatus() {
        return httpStatus;
    }


    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RestResponseMessage [code=" + code + ", message=" + message + ", httpStatus=" + httpStatus + ", requestSucceeded="
                + requestSucceeded + "]";
    }

}
