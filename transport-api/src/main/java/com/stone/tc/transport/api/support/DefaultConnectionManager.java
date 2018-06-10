package com.stone.tc.transport.api.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.stone.tc.transport.api.CloseListener;
import com.stone.tc.transport.api.Connection;
import com.stone.tc.transport.api.ConnectionManager;
import com.stone.tc.transport.api.RequestCallback;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午7:22
 */
@Slf4j
public class DefaultConnectionManager implements ConnectionManager {

    private final Map<String, Connection> connections = Maps.newHashMap();

    private final List<RequestCallback> callbacks = Lists.newArrayList();

    private final CloseListener closeListener = new ManagerCloseListener();

    @Override
    public synchronized void register(RequestCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public synchronized void addConnection(Connection connection) {
        log.info("add connection:{}", connection.getConnectionId());
        connection.addCloseListener(closeListener);
        connection.registerRequestCallbacks(callbacks);
        connections.put(connection.getConnectionId(), connection);
    }

    @Override
    public synchronized Connection getConnection(String connectionId) {
        return connections.get(connectionId);
    }

    @Override
    public synchronized Connection removeConnection(String connectionId) {
        log.info("remove connection:{}", connectionId);
        return connections.remove(connectionId);
    }

    @Override
    public synchronized List<Connection> getAll() {
        return Lists.newArrayList(connections.values());
    }

    @Override
    public synchronized void closeAll() {
        List<Connection> list = new ArrayList<>(connections.values());
        for (Connection connection : list) {
            connection.close();
        }
    }

    private class ManagerCloseListener implements CloseListener {

        @Override
        public void onClose(Connection connection) {
            DefaultConnectionManager.this.removeConnection(connection.getConnectionId());
        }
    }
}
