package com.stone.tc.transport.netty;

import com.stone.tc.transport.api.TransportContext;
import org.apache.commons.lang3.StringUtils;


/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午2:29
 */
public class NettyTransportContext implements TransportContext {
    private static final String TYPE = "netty";

    private String configFile;

    private String serializer;

    private int timeout;

    private int clientWorkerCount;

    private int serverWorkerCount;

    private int acceptorCount;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getSerializer() {
        return StringUtils.isBlank(serializer) ? Options.DEFAULT_SERIALIZER : serializer;
    }

    public String getConfigFile() {
        return StringUtils.isBlank(configFile) ? DEFAULT_CONFIG_FILE : configFile;
    }

    public int getTimeout() {
        return timeout == 0 ? Options.DEFAULT_TIMEOUT : timeout;
    }

    public int getClientWorkerCount() {
        return clientWorkerCount == 0 ? Options.DEFAULT_CLIENT_WORKER_COUNT : clientWorkerCount;
    }

    public int getServerWorkerCount() {
        return serverWorkerCount == 0 ? Options.DEFAULT_SERVER_WORKER_COUNT : serverWorkerCount;
    }

    public int getAcceptorCount() {
        return acceptorCount == 0 ? Options.DEFAULT_ACCEPTOR_COUNT : acceptorCount;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setClientWorkerCount(int clientWorkerCount) {
        this.clientWorkerCount = clientWorkerCount;
    }

    public void setServerWorkerCount(int serverWorkerCount) {
        this.serverWorkerCount = serverWorkerCount;
    }

    public void setAcceptorCount(int acceptorCount) {
        this.acceptorCount = acceptorCount;
    }
}
