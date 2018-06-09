package com.stone.tc.transport.netty;

import com.stone.tc.common.net.Address;
import com.stone.tc.transport.api.Connection;
import com.stone.tc.transport.api.ConnectionManager;
import com.stone.tc.transport.api.support.ConnectionIdBuilder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午7:52
 */
@Slf4j
public class NettyChannelHandler extends ChannelHandlerAdapter {

    protected final ConnectionManager connectionManager;

    public NettyChannelHandler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("caught exception:", cause);
        Channel channel = ctx.channel();
        if (channel != null && !channel.isActive()) {
            String connectionId = getConnectionId(channel);
            Connection connection = connectionManager.getConnection(connectionId);
            if (connection != null) {
                log.info("close connection:{} by exception", connectionId);
                connection.close();
            }
        }
        super.exceptionCaught(ctx, cause);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Connection connection = connectionManager.removeConnection(getConnectionId(ctx.channel()));
        if (connection != null) {
            log.info("close connection:{}", connection.getConnectionId());
            connection.close();
        }
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        Connection connection = connectionManager.removeConnection(getConnectionId(ctx.channel()));
        if (connection != null) {
            connection.onMessage(evt);
        }
        super.userEventTriggered(ctx, evt);
    }


    protected static String getConnectionId(Channel channel) {
        InetSocketAddress local = (InetSocketAddress) channel.localAddress();
        Address localAdd = new Address(local);

        InetSocketAddress remote = (InetSocketAddress) channel.remoteAddress();
        Address remoteAdd = new Address(remote);

        return ConnectionIdBuilder.build(localAdd, remoteAdd);
    }
}
