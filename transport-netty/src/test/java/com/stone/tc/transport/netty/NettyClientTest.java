package com.stone.tc.transport.netty;

import com.stone.tc.common.net.Address;
import com.stone.tc.transport.api.Connection;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author shifeng.luo
 * @version created on 2018/6/10 下午2:46
 */
@Slf4j
public class NettyClientTest {

    private NettyClient client;
    private NettyTransportContext context;

    @Before
    public void init() {
        context = new NettyTransportContext();
        client = new NettyClient(context);
    }

    @Test
    public void connect() throws InterruptedException, ExecutionException, IOException {
        Address remote = new Address(new InetSocketAddress("192.168.10.106", 65123));
        CompletableFuture<Connection> future = client.connect(remote);

        Connection connection = future.get();
        String test = "test";
        connection.send(test.getBytes());

        Thread.sleep(100000);
        log.info("client start success!");
    }

    @Test
    public void close() {
    }

    @Test
    public void close1() {
    }
}