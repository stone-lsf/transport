package com.stone.tc.transport.api;

import com.stone.tc.common.Closable;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午1:46
 */
public interface EndPoint extends Closable {

    /**
     * get {@link ConnectionManager}
     *
     * @return ConnectionManager
     */
    ConnectionManager getConnectionManager();

    /**
     * close with timeout
     *
     * @param timeout timeout mills
     */
    void close(int timeout);

    /**
     * if close
     *
     * @return if closed return ture,else false
     */
    boolean isClosed();
}
