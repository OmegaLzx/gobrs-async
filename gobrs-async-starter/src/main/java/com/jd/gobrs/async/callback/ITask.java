package com.jd.gobrs.async.callback;

import java.util.Map;

import com.jd.gobrs.async.wrapper.TaskWrapper;

/**
 * 每个最小执行单元需要实现该接口
 *
 * @author sizegang wrote on 2019-11-19.
 */
@FunctionalInterface
public interface ITask<T, V> {
    /**
     * 在这里做耗时操作，如rpc请求、IO等
     *
     * @param object      object
     * @param dataSources 任务包装
     * @param businessId 业务ID
     */
    V task(T object, Map<String, TaskWrapper> dataSources, Long businessId);

    /**
     * 超时、异常时，返回的默认值
     *
     * @return 默认值
     */
    default V defaultValue() {
        return null;
    }

    default boolean nessary(T t) {
        return true;
    }




}