package com.stone.tc.transport.api.support;

import com.stone.tc.common.NamedThreadFactory;
import com.stone.tc.transport.api.Connection;
import com.stone.tc.transport.api.Response;
import com.stone.tc.transport.api.exceptions.TimeoutException;

import java.util.concurrent.*;


/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午8:15
 */
public class TimeoutScheduler {
    public static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR;
    private final Connection connection;
    private ConcurrentMap<Integer, ScheduledFuture<?>> scheduledMap = new ConcurrentHashMap<>();

    static {
        SCHEDULED_EXECUTOR = Executors.newScheduledThreadPool(PROCESSORS + 1, new NamedThreadFactory("TimeoutHandler"));
    }

    public TimeoutScheduler(Connection connection) {
        this.connection = connection;
    }

    public void schedule(int requestId, int timeout) {
        ScheduledFuture<?> future = SCHEDULED_EXECUTOR.schedule(new TimeoutTask(requestId, timeout), timeout, TimeUnit.MILLISECONDS);
        scheduledMap.put(requestId, future);
    }


    public void cancel(int requestId) {
        ScheduledFuture<?> future = scheduledMap.remove(requestId);
        if (future != null) {
            future.cancel(false);
        }
    }


    private class TimeoutTask implements Runnable {
        private final int requestId;
        private final int timeout;

        private TimeoutTask(int requestId, int timeout) {
            this.requestId = requestId;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            TimeoutException exception = new TimeoutException(timeout);
            Response response = new Response(requestId);
            response.setException(exception);
            connection.onMessage(response);
        }
    }
}
