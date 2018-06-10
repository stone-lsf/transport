package com.stone.tc.transport.netty;

import com.stone.tc.common.net.Address;
import com.stone.tc.serialize.api.SerializeContext;
import com.stone.tc.serialize.api.Serializer;
import com.stone.tc.serialize.api.SerializerFactory;
import com.stone.tc.transport.api.Connection;
import com.stone.tc.transport.api.ConnectionManager;
import com.stone.tc.transport.api.TransportClient;
import com.stone.tc.transport.api.TransportTypes;
import com.stone.tc.transport.api.support.DefaultConnectionManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午2:27
 */
@Slf4j
public class NettyClient implements TransportClient {

    private final int timeout;
    private final Bootstrap bootstrap;
    private final ConnectionManager connectionManager = new DefaultConnectionManager();
    private AtomicBoolean closed = new AtomicBoolean(false);

    public NettyClient(NettyTransportContext context) {
        this.timeout = context.getTimeout();
        this.bootstrap = new Bootstrap();

        NettyClientChannelHandler channelHandler = new NettyClientChannelHandler(connectionManager);
        SerializeContext serializeContext = new SerializeContext(context.getSerializer(), TransportTypes.newInstance());
        Serializer serializer = SerializerFactory.create(serializeContext);

        bootstrap.group(new NioEventLoopGroup(context.getClientWorkerCount()))
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                        ch.pipeline().addLast("encoder", new LengthFieldPrepender(4, false));
                        ch.pipeline().addLast("serializer", new NettySerializerHandler(serializer));
                        ch.pipeline().addLast("handler", channelHandler);
                    }
                });
    }

    @Override
    public CompletableFuture<Connection> connect(Address address) {
        CompletableFuture<Connection> result = new CompletableFuture<>();
        InetSocketAddress socketAddress = new InetSocketAddress(address.getIp(), address.getPort());
        bootstrap.connect(socketAddress).addListener(future -> {
            ChannelFuture channelFuture = (ChannelFuture) future;
            if (channelFuture.isSuccess()) {
                Channel channel = channelFuture.channel();
                InetSocketAddress remote = (InetSocketAddress) channel.remoteAddress();
                Address remoteAddress = new Address(remote);

                InetSocketAddress local = (InetSocketAddress) channel.localAddress();
                Address localAddress = new Address(local);

                NettyConnection connection = new NettyConnection(remoteAddress, localAddress, timeout, channel);
                connectionManager.addConnection(connection);
                log.info("connect to:{} success,connectionId:{}", address, connection.getConnectionId());

                result.complete(connection);
            } else {
                Throwable cause = channelFuture.cause();
                log.error("connect to address:[{}] failed,cased by exception:{}", address, cause);
                result.completeExceptionally(cause);
            }
        });

        return result;
    }

    @Override
    public CompletableFuture<Connection> connect(Address address, int retryTimes) {
        return null;
    }

    @Override
    public ConnectionManager getConnectionManager() {
        return null;
    }

    @Override
    public void close(int timeout) {
        if (closed.compareAndSet(false, true)) {
            connectionManager.closeAll();
            if (bootstrap.group() != null) {
                bootstrap.group().shutdownGracefully(2, timeout, TimeUnit.MILLISECONDS);
            }
            log.info("successful close netty client");
        }
    }

    @Override
    public boolean isClosed() {
        return closed.get();
    }

    @Override
    public void close() {
        close(10 * 1000);
    }
}
