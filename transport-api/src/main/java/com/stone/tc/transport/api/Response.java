package com.stone.tc.transport.api;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午1:22
 */
public class Response {

    private int id;

    private Object message;

    private Exception exception;

    public Response(int id) {
        this.id = id;
    }

    public Response(int id, Object message) {
        this.id = id;
        this.message = message;
    }

    public Response(int id, Exception exception) {
        this.id = id;
        this.exception = exception;
    }

    public boolean hasException() {
        return exception != null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
