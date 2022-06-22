package com.gobrs.async.example.task;

import com.gobrs.async.TaskSupport;
import com.gobrs.async.anno.Task;
import com.gobrs.async.task.AsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @program: gobrs-async-starter
 * @ClassName EService
 * @description:
 * @author: sizegang
 * @create: 2022-03-20
 **/
@Component
@Task(callback = true, name = "任务G")
@Slf4j
public class GService extends AsyncTask<Object, Object> {

    @Override
    public void prepare(Object o) {

    }

    @Override
    public Object task(Object o, TaskSupport support) {
        try {
            log.info("GService Begin");
            Thread.sleep(100);
            log.info("GService Finish");
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
