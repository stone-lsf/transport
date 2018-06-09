package com.stone.tc.transport.netty;

import com.stone.tc.common.net.Address;
import com.stone.tc.transport.api.ConnectionListener;
import com.stone.tc.transport.api.ConnectionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午7:34
 */
@Sharable
@Slf4j
public class NettyServerChannelHandler extends NettyChannelHandler {
    private final ConnectionListener listener;
    private final int timeout;


    public NettyServerChannelHandler(ConnectionManager connectionManager, ConnectionListener listener, int timeout) {
        super(connectionManager);
        this.listener = listener;
        this.timeout = timeout;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        InetSocketAddress remote = (InetSocketAddress) channel.remoteAddress();
        Address remoteAddress = new Address(remote);

        InetSocketAddress local = (InetSocketAddress) channel.localAddress();
        Address localAddress = new Address(local);

        NettyConnection connection = new NettyConnection(remoteAddress, localAddress, timeout, channel);
        log.info("receive connection id:{}", connection.getConnectionId());
        connectionManager.addConnection(connection);
        listener.onAdd(connection);
        super.channelActive(ctx);
    }
}
