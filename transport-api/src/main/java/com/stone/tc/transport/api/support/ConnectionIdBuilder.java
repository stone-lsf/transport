package com.stone.tc.transport.api.support;

import com.stone.tc.common.net.Address;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午7:37
 */
public class ConnectionIdBuilder {

    public static String build(Address local,Address remote){
        return local.getIp() + ":" + local.getPort() + "/" + remote.getIp() + ":" + remote.getPort();
    }
}
