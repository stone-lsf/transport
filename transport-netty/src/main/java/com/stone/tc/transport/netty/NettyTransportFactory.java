package com.stone.tc.transport.netty;

import com.stone.tc.transport.api.Transport;
import com.stone.tc.transport.api.TransportContext;
import com.stone.tc.transport.api.TransportFactory;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午2:29
 */
public class NettyTransportFactory extends TransportFactory {

    @Override
    protected Transport doCreate(TransportContext context) {
        NettyTransportContext nettyContext = (NettyTransportContext) context;
        return new NettyTransport(nettyContext);
    }
}
