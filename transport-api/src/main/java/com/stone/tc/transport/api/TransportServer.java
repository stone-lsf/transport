package com.stone.tc.transport.api;

import com.stone.tc.common.net.Address;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午1:42
 */
public interface TransportServer extends EndPoint {

    /**
     * 绑定本地端口，并设置客户端连接监听器
     *
     * @param port     端口号
     * @param listener 连接监听器
     */
    void listen(int port, ConnectionListener listener);

    /**
     * 获取本地地址
     *
     * @return 本地地址
     */
    Address getBindAddress();
}
