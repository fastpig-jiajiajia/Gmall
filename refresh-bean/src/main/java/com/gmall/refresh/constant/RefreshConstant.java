package com.gmall.refresh.constant;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

import java.util.ArrayList;
import java.util.List;

import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.*;

/**
 * @author rui.xu
 * @date 2020/12/04 15:31
 * @description \
 **/
public interface RefreshConstant {

    String ZK_PROPERTY_NAME = "zkRefreshConfigSource";

    String SCOPE_NAME = "refresh";

    String PREFIX = "/";

    /**
     * 收到这些 ZK的事件类型时进行刷新 Bean的操作
     */
    List<PathChildrenCacheEvent.Type> RERESH_TYPES = new ArrayList<PathChildrenCacheEvent.Type>(){
        {
            add(CHILD_ADDED);
            add(CHILD_UPDATED);
            add(CHILD_REMOVED);
            add(INITIALIZED);
        }
    };

}