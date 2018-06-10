package com.stone.tc.transport.netty;

import com.stone.tc.transport.api.*;
import com.stone.tc.transport.api.support.AbstractRequestCallback;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

/**
 * @author shifeng.luo
 * @version created on 2018/6/10 下午2:05
 */
@Slf4j
public class NettyServerTest {

    private NettyServer server;
    private NettyTransportContext context;


    @Before
    public void init() {
        context = new NettyTransportContext();
        server = new NettyServer(context);
    }

    @Test
    public void listen() throws InterruptedException {
        ConnectionManager manager = server.getConnectionManager();
        manager.register(new ByteArrayCallback());

        server.listen(65123, connection -> log.info("receive connection:{}", connection.getConnectionId()));

        Thread.sleep(100000);
        log.info("server start success!");
    }


    public static class ByteArrayCallback extends AbstractRequestCallback<byte[]> {

        @Override
        public CompletableFuture<?> handle(byte[] request, RequestContext context) {
            String str = new String(request);
            log.info("receive request:{}", str);
            return null;
        }

    }
}