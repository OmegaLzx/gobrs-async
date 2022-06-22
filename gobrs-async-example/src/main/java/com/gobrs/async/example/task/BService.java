package com.gobrs.async.example.task;

import com.gobrs.async.TaskSupport;
import com.gobrs.async.anno.Task;
import com.gobrs.async.domain.TaskResult;
import com.gobrs.async.task.AsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Task(failSubExec = true, name = "任务B")
@Slf4j
public class BService extends AsyncTask<Object, Object> {
    @Override
    public void prepare(Object o) {

    }

    @Override
    public Object task(Object o, TaskSupport support) {
        Map<Class, TaskResult> resultMap = support.getResultMap();
        TaskResult taskResult = resultMap.get(AService.class);
        log.info("{}", taskResult);
        try {
            log.info("BService Begin");
            Thread.sleep(3000);
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

    }
}
