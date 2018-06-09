package com.stone.tc.transport.api;

import java.util.concurrent.CompletableFuture;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午1:35
 */
public interface RequestCallback<T> {

    CompletableFuture<?> handle(T request, RequestContext context);

    void onException(Throwable e);
}
