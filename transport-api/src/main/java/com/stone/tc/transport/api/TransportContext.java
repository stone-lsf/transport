package com.stone.tc.transport.api;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午2:24
 */
public interface TransportContext {
    String DEFAULT_CONFIG_FILE = "transport.properties";

    String getType();

    String getSerializer();
}
