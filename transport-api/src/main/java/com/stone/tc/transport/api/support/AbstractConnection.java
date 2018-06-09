package com.stone.tc.transport.api.support;

import com.stone.tc.common.IntegerIdGenerator;
import com.stone.tc.common.net.Address;
import com.stone.tc.common.utils.ReflectUtil;
import com.stone.tc.transport.api.*;
import com.stone.tc.transport.api.exceptions.RemoteException;
import com.stone.tc.transport.api.exceptions.TimeoutException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午7:07
 */
@Slf4j
public abstract class AbstractConnection implements Connection {

    protected final Address remote;
    protected final Address local;
    protected final int defaultTimeout;
    protected final String connectionId;
    protected final TimeoutScheduler timeoutScheduler;

    protected volatile boolean closed = false;

    private final IntegerIdGenerator idGenerator = new IntegerIdGenerator();
    private final CopyOnWriteArrayList<CloseListener> listeners = new CopyOnWriteArrayList<>();

    private final ConcurrentMap<Class, RequestCallback> requestCallbacks = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, CompletableFuture> responseFutures = new ConcurrentHashMap<>();

    public AbstractConnection(Address remote, Address local, int defaultTimeout) {
        this.remote = remote;
        this.local = local;
        this.defaultTimeout = defaultTimeout;
        this.connectionId = buildConnectionId();
        this.timeoutScheduler = new TimeoutScheduler(this);
    }

    private String buildConnectionId() {
        return ConnectionIdBuilder.build(local, remote);
    }

    @Override
    public void send(Object message) throws IOException {
        StringBuilder logBuilder = new StringBuilder();
        try {
            if (message instanceof Response) {
                sendResponse((Response) message);
            } else {
                int id = idGenerator.nextId();
                Request request = new Request(id, message);
                sendRequest(request);
            }
        } catch (Exception e) {
            logException(logBuilder, e);
            throw new IOException(e);
        }
    }

    @Override
    public <T> T syncRequest(Object message) throws IOException {
        return syncRequest(message, defaultTimeout);
    }

    @Override
    public <T> T syncRequest(Object message, int timeout) throws IOException {
        CompletableFuture<T> future = request(message, timeout);
        try {
            return future.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RemoteException) {
                throw (RemoteException) cause;
            }

            if (cause instanceof TimeoutException) {
                throw (TimeoutException) cause;
            }

            throw new IOException(cause);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> void requestWithCallback(Object message, ResponseCallback<T> callback) {
        requestWithCallback(message, defaultTimeout, callback);
    }

    @Override
    public <T> void requestWithCallback(Object message, int timeout, ResponseCallback<T> callback) {
        this.<T>request(message, timeout).whenComplete((response, error) -> {
            if (error != null) {
                ResponseContext context = new ResponseContext(this);
                callback.onException(error, context);
            } else {
                callback.handle(response, this);
            }
        });
    }

    @Override
    public <T> CompletableFuture<T> request(Object message) {
        return request(message, defaultTimeout);
    }

    @Override
    public <T> CompletableFuture<T> request(Object message, int timeout) {
        CompletableFuture<T> result = new CompletableFuture<>();
        int id = idGenerator.nextId();
        Request request = new Request(id, message);
        responseFutures.put(id, result);

        try {
            sendRequest(request);
            timeoutScheduler.schedule(request.getId(), timeout);
        } catch (Exception e) {
            result.completeExceptionally(e);
        }
        return result;
    }


    @Override
    public void onMessage(Object message) {
        if (message instanceof Request) {
            Request request = (Request) message;
            handRequest(request);
        } else if (message instanceof Response) {
            Response response = (Response) message;
            handResponse(response);
        } else {
            log.warn("error message ,neither request nor response:{}", message);
        }
    }

    @SuppressWarnings("unchecked")
    private void handRequest(Request request) {
        Object message = request.getMessage();
        RequestCallback callback = requestCallbacks.get(message.getClass());
        if (callback == null) {
            log.error("[{}] don't have matched com.sm.charge.memory.handler ", message.getClass());
            return;
        }

        int requestId = request.getId();
        try {
            RequestContext context = new RequestContext(requestId, this);

            CompletableFuture<?> future = callback.handle(message, context);
            if (future == null) {
                return;
            }

            future.whenComplete((response, error) -> {
                if (error != null) {
                    handleRequestFailure(requestId, error, callback);
                } else if (response != null) {
                    handleRequestSuccess(requestId, response);
                }
            });
        } catch (Throwable e) {
            handleRequestFailure(requestId, e, callback);
        }
    }

    private void handleRequestFailure(int requestId, Throwable error, RequestCallback callback) {
        RemoteException exception = new RemoteException(error);
        Response response = new Response(requestId, exception);
        try {
            sendResponse(response);
        } catch (Throwable e) {
            log.error("send exception response failed, cased by exception", e);
        } finally {
            callback.onException(error);
        }
    }

    private void handleRequestSuccess(int requestId, Object responseMessage) {
        Response response = responseMessage == null ? new Response(requestId) : new Response(requestId, responseMessage);

        try {
            sendResponse(response);
        } catch (Throwable e) {
            log.error("send success response failed, cased by exception", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void handResponse(Response response) {
        CompletableFuture future = responseFutures.remove(response.getId());
        if (future == null) {
            log.info("receive timeout response:{}", response);
            return;
        }

        timeoutScheduler.cancel(response.getId());
        if (!response.hasException()) {
            future.complete(response.getMessage());
        } else {
            future.completeExceptionally(response.getException());
        }
    }

    protected abstract void sendRequest(Request request);

    protected abstract void sendResponse(Response response);

    @Override
    public void registerRequestCallbacks(List<RequestCallback> callbacks) {
        for (RequestCallback callback : callbacks) {
            Class<Object> type = ReflectUtil.getSuperClassGenericType(callback.getClass());
            requestCallbacks.put(type, callback);
        }
    }

    @Override
    public void addCloseListener(CloseListener listener) {
        listeners.add(listener);
    }

    @Override
    public boolean closed() {
        return closed;
    }


    @Override
    public void close() {
        closed = true;
        responseFutures.clear();
        for (CloseListener listener : listeners) {
            listener.onClose(this);
        }
    }

    @Override
    public Address localAddress() {
        return local;
    }

    @Override
    public Address remoteAddress() {
        return remote;
    }


    private void logException(StringBuilder logBuilder, Throwable throwable) {
        StringBuffer message = getThrowableMessage(throwable);

        logBuilder.append("###").append(message)
                .append("###").append(connectionId);
        log.error(logBuilder.toString());
    }

    private StringBuffer getThrowableMessage(Throwable throwable) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        return writer.getBuffer();
    }
}
