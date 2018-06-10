package com.stone.tc.transport.api.support;

import com.stone.tc.transport.api.RequestCallback;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shifeng.luo
 * @version created on 2018/6/10 下午3:18
 */
@Slf4j
public abstract class AbstractRequestCallback<T> implements RequestCallback<T> {

    @Override
    public void onException(Throwable e) {
        log.info("callback:{} handle request caught exception:{}", this, e);
    }
}
