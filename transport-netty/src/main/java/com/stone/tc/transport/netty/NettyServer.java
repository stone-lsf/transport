package com.stone.tc.transport.netty;

import com.stone.tc.common.net.Address;
import com.stone.tc.common.utils.AddressUtil;
import com.stone.tc.serialize.api.SerializeContext;
import com.stone.tc.serialize.api.Serializer;
import com.stone.tc.serialize.api.SerializerFactory;
import com.stone.tc.transport.api.ConnectionListener;
import com.stone.tc.transport.api.ConnectionManager;
import com.stone.tc.transport.api.TransportServer;
import com.stone.tc.transport.api.TransportTypes;
import com.stone.tc.transport.api.support.DefaultConnectionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午2:27
 */
@Slf4j
public class NettyServer implements TransportServer {

    private final ServerBootstrap bootstrap;
    private final EventLoopGroup workerGroup;
    private final int timeout;
    private final int acceptorCount;
    private final String serializer;
    private final ConnectionManager connectionManager = new DefaultConnectionManager();

    private Address bindAddress;

    public NettyServer(NettyTransportContext context) {
        this.timeout = context.getTimeout();
        this.acceptorCount = context.getAcceptorCount();
        this.serializer = context.getSerializer();
        this.bootstrap = new ServerBootstrap();
        this.workerGroup = new NioEventLoopGroup(context.getServerWorkerCount());
    }

    @Override
    public void listen(int port, ConnectionListener listener) {
        NettyServerChannelHandler channelHandler = new NettyServerChannelHandler(connectionManager, listener, timeout);
        SerializeContext serializeContext = new SerializeContext(serializer, TransportTypes.newInstance());
        Serializer serializer = SerializerFactory.create(serializeContext);

        bootstrap.group(new NioEventLoopGroup(acceptorCount), workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                        ch.pipeline().addLast("encoder", new LengthFieldPrepender(4, false));

                        ch.pipeline().addLast("serializer", new NettySerializerHandler(serializer));
                        ch.pipeline().addLast("handler", channelHandler);
                    }
                });
        try {
            this.bindAddress = AddressUtil.getLocalAddress(port);
            bootstrap.bind(bindAddress.getIp(), bindAddress.getPort()).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("Started hetu server on port: " + bindAddress.getPort());
                } else {
                    log.error("Failed to bind hetu server on port: " + bindAddress.getPort(), future.cause());
                    throw new Exception("Failed to start hetu server on port: " + bindAddress.getPort());
                }
            }).sync();

            log.info("bind port:[{}] success!", port);
        } catch (InterruptedException e) {
            throw new RuntimeException("bind port " + port + " failed", e);
        }
    }

    @Override
    public Address getBindAddress() {
        return null;
    }

    @Override
    public ConnectionManager getConnectionManager() {
        return null;
    }

    @Override
    public void close(int timeout) {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void close() {

    }
}
