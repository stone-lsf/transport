package com.stone.tc.transport.api;

import com.stone.tc.common.net.Address;

import java.util.concurrent.CompletableFuture;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午1:42
 */
public interface TransportClient extends EndPoint {

    /**
     * build connection with remote address
     *
     * @param address remote address
     * @return CompletableFuture
     */
    CompletableFuture<Connection> connect(Address address);

    /**
     * connection to remote address with max retry times
     *
     * @param address    remote address
     * @param retryTimes max retry times
     * @return CompletableFuture
     */
    CompletableFuture<Connection> connect(Address address, int retryTimes);
}
