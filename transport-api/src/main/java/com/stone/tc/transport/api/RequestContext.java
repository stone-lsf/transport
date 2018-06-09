package com.stone.tc.transport.api;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午1:30
 */
public class RequestContext {

    private final int requestId;
    private final Connection connection;

    public RequestContext(int requestId, Connection connection) {
        this.requestId = requestId;
        this.connection = connection;
    }
}
