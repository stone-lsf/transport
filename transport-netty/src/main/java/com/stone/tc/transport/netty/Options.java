package com.stone.tc.transport.netty;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午2:40
 */
public class Options {
    private static final int DEFAULT_EVENT_LOOP_THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() * 2);

    public static final String DEFAULT_SERIALIZER = "protostuff";

    public static final int DEFAULT_TIMEOUT = 3000;

    public static final int DEFAULT_CLIENT_WORKER_COUNT = 4;

    public static final int DEFAULT_SERVER_WORKER_COUNT = DEFAULT_EVENT_LOOP_THREADS;

    public static final int DEFAULT_ACCEPTOR_COUNT = 1;
}
