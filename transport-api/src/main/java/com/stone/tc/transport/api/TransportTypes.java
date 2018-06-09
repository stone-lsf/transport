package com.stone.tc.transport.api;

import com.stone.tc.serialize.api.AbstractSerializeTypes;

/**
 * @author shifeng.luo
 * @version created on 2018/6/9 下午6:43
 */
public class TransportTypes extends AbstractSerializeTypes {
    private static final TransportTypes INSTANCE = new TransportTypes();

    private TransportTypes() {
        MessageTypes[] types = MessageTypes.values();
        for (MessageTypes type : types) {
            register(type.code, type.clazz);
        }
    }

    public static TransportTypes newInstance() {
        return INSTANCE;
    }


    public enum MessageTypes {
        REQUEST((byte) 1, Request.class),
        RESPONSE((byte) 2, Response.class);


        public byte code;
        public Class<?> clazz;

        MessageTypes(byte code, Class<?> clazz) {
            this.code = code;
            this.clazz = clazz;
        }
    }
}
