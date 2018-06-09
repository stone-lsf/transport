package com.stone.tc.transport.api.exceptions;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午8:15
 */
public class TimeoutException extends RuntimeException{

    private final int timeout;

    public TimeoutException(int timeout) {
        this.timeout = timeout;
    }

    public TimeoutException(String message, int timeout) {
        super(message);
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }

    @Override
    public String toString() {
        return "TimeoutException{" +
                "timeout=" + timeout +
                '}';
    }
}
