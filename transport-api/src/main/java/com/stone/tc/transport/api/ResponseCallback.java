package com.stone.tc.transport.api;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午1:27
 */
public interface ResponseCallback<T> {

    /**
     * 处理响应结果
     *
     * @param response   结果
     * @param connection 连接
     */
    void handle(T response, Connection connection);

    /**
     * 当响应出现异常时，进行处理
     *
     * @param e       异常
     * @param context 响应上下文
     */
    void onException(Throwable e, ResponseContext context);
}
