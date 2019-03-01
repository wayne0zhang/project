package com.paxing.test.kaoqin.exception;

import java.text.MessageFormat;

/**
 * 用户可见的异常
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/2/21
 */
public class UserVisiableException extends RuntimeException {

    private static final long serialVersionUID = 8180463459790197559L;

    private Integer errorCode = 500;

    public UserVisiableException() {
    }

    public UserVisiableException(Integer errorCode, String message, Object... args) {
        super(MessageFormat.format(message, args));
        this.errorCode = errorCode;
    }

    public UserVisiableException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
