package com.stone.tc.transport.api;

import com.stone.tc.common.ServiceLoader;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午2:06
 */
public abstract class TransportFactory {

    public static Transport create(TransportContext context) {
        TransportFactory factory = ServiceLoader.findService(context.getType(), TransportFactory.class);
        return factory.doCreate(context);
    }

    protected abstract Transport doCreate(TransportContext context);
}
