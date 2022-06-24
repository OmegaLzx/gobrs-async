package com.gobrs.async;

import com.gobrs.async.domain.TaskResult;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;


@Data
public class TaskSupport {

    public TaskLoader taskLoader;

    public ExecutorService executorService;

    /**
     * The task parameters
     */
    private Object param;


    /**
     * Task result encapsulation
     */
    private Map<Class, TaskResult> resultMap = new HashMap();

}
