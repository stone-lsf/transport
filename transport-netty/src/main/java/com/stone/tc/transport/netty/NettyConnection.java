package com.stone.tc.transport.netty;

import com.stone.tc.common.net.Address;
import com.stone.tc.transport.api.Request;
import com.stone.tc.transport.api.Response;
import com.stone.tc.transport.api.exceptions.TimeoutException;
import com.stone.tc.transport.api.support.AbstractConnection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午6:56
 */
@Slf4j
public class NettyConnection extends AbstractConnection {
    private final Channel channel;

    public NettyConnection(Address remote, Address local, int defaultTimeout, Channel channel) {
        super(remote, local, defaultTimeout);
        this.channel = channel;
    }

    @Override
    public String getConnectionId() {
        return connectionId;
    }

    @Override
    protected void sendRequest(Request request) {
        doSend(request);
    }

    @Override
    protected void sendResponse(Response response) {
        doSend(response);
    }

    private void doSend(Object obj) {
        boolean success;
        ChannelFuture future = channel.writeAndFlush(obj);
        try {
            success = future.await(defaultTimeout);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        if (!success) {
            if (!channel.isOpen()) {
                log.info("connectionId:{} not open", getConnectionId());
            }
            throw new TimeoutException(getConnectionId() + " connection send message connectionId", defaultTimeout);
        }
    }
}
