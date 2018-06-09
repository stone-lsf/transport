package com.stone.tc.transport.api;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午1:41
 */
public interface Transport {

    TransportClient client();

    TransportServer server();
}
