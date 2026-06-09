package com.bank.common;

/**
 * 未登录异常
 */
public class NotLoginException extends RuntimeException {

    public NotLoginException() {
        super("未登录，请先登录");
    }

    public NotLoginException(String message) {
        super(message);
    }
}
