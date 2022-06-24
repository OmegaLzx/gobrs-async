package com.gobrs.async.example.task;

import com.gobrs.async.TaskSupport;
import com.gobrs.async.anno.Task;
import com.gobrs.async.task.AsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Task(failSubExec = false, name = "任务B", retryCount = 3)
@Slf4j
public class BService extends AsyncTask<Object, Object> {
    @Override
    public void prepare(Object o) {

    }

    @Override
    public Object task(Object o, TaskSupport support) {
        try {
            log.info("BService Begin");
            Thread.sleep(1000);
            int i = 1 / 0;
            log.info("BService Finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean nessary(Object o, TaskSupport support) {
        return true;
    }

    @Override
    public void onSuccess(TaskSupport support) {

    }

    @Override
    public void onFail(TaskSupport support) {
        log.info("执行B的onFail回调");
    }
}
