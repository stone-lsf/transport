package com.stone.tc.transport.netty;

import com.stone.tc.transport.api.Transport;
import com.stone.tc.transport.api.TransportClient;
import com.stone.tc.transport.api.TransportServer;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午2:26
 */
public class NettyTransport implements Transport {

    private final NettyTransportContext context;

    public NettyTransport(NettyTransportContext context) {
        this.context = context;
    }

    @Override
    public TransportClient client() {
        return null;
    }

    @Override
    public TransportServer server() {
        return null;
    }
}
