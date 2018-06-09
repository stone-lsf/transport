package com.stone.tc.transport.api;

import com.stone.tc.common.Closable;
import com.stone.tc.common.net.Address;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午1:21
 */
public interface Connection extends Closable{

    /**
     * connection identifier
     *
     * @return connection identifier
     */
    String getConnectionId();

    /**
     * 处理收到的消息
     *
     * @param message 消息
     */
    void onMessage(Object message);

    /**
     * 发送消息，该消息不会产生返回结果
     *
     * @param message 消息
     */
    void send(Object message) throws IOException;

    /**
     * 同步发送请求，使用默认超时时间，会阻塞一直等待返回结果或超时
     *
     * @param message 请求消息
     * @return 结果
     */
    <T> T syncRequest(Object message) throws IOException;

    /**
     * 同步发送请求，并指定超时时间，会阻塞一直等待返回结果或超时
     *
     * @param message 请求消息
     * @return 结果
     */
    <T> T syncRequest(Object message, int timeout) throws IOException;

    /**
     * 发送消息
     *
     * @param message 消息
     * @return {@link CompletableFuture}，用来阻塞直到有消息返回或者超时
     */
    <T> CompletableFuture<T> request(Object message);

    /**
     * 发送消息
     *
     * @param message 消息
     * @param timeout 超时时间
     * @return {@link CompletableFuture}，用来阻塞直到有消息返回或者超时
     */
    <T> CompletableFuture<T> request(Object message, int timeout);

    /**
     * 异步发送消息，此时使用默认的超时时间
     *
     * @param message  消息
     * @param callback 返回结果回调
     */
    <T> void requestWithCallback(Object message, ResponseCallback<T> callback);

    /**
     * 异步发送消息
     *
     * @param message  消息
     * @param timeout  超时时间
     * @param callback 返回结果回调
     */
    <T> void requestWithCallback(Object message, int timeout, ResponseCallback<T> callback);

    /**
     * 注册请求回调
     *
     * @param callbacks 请求回调
     */
    void registerRequestCallbacks(List<RequestCallback> callbacks);

    /**
     * 设置关闭监听器，当{@link Connection}关闭时，会触发该监听器
     *
     * @param listener 关闭监听器
     */
    void addCloseListener(CloseListener listener);

    /**
     * 连接是否关闭
     *
     * @return 如果关闭则返回true，否则返回false
     */
    boolean closed();

    /**
     * 获取本地地址
     *
     * @return {@link Address}
     */
    Address localAddress();

    /**
     * 获取远程地址
     *
     * @return {@link Address}
     */
    Address remoteAddress();
}
