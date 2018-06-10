package com.stone.tc.transport.netty;

import com.stone.tc.transport.api.ConnectionManager;
import io.netty.channel.ChannelHandler.Sharable;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午2:58
 */
@Sharable
@Slf4j
public class NettyClientChannelHandler extends NettyChannelHandler {

    public NettyClientChannelHandler(ConnectionManager connectionManager) {
        super(connectionManager);
    }
}
