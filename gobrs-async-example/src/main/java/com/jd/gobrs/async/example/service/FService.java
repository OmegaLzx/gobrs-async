package com.jd.gobrs.async.example.service;

import com.jd.gobrs.async.example.DataContext;
import com.jd.gobrs.async.example.executor.SerExector;
import com.jd.gobrs.async.gobrs.GobrsAsyncSupport;
import com.jd.gobrs.async.task.AsyncTask;
import com.jd.gobrs.async.task.TaskResult;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @program: gobrs-async
 * @ClassName FService
 * @description:
 * @author: sizegang
 * @create: 2022-02-26 16:01
 * @Version 1.0
 **/
@Service
public class FService implements AsyncTask<DataContext, Map>, SerExector {
    @Override
    public void result(boolean success, DataContext param, TaskResult<Map> workResult) {

    }

    @Override
    public Map task(DataContext params, GobrsAsyncSupport support) {
        return null;
    }

    @Override
    public boolean nessary(DataContext params, GobrsAsyncSupport support) {
        return false;
    }
}
