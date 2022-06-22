package com.gobrs.async.example.task;

import com.gobrs.async.TaskSupport;
import com.gobrs.async.anno.Task;
import com.gobrs.async.task.AsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Task(failSubExec = true, name = "任务A")
@Component
@Slf4j
public class AService extends AsyncTask<Object, Object> {


    @Override
    public void prepare(Object o) {


    }

    @Override
    public Object task(Object o, TaskSupport support) {
        try {
            log.info("AService Begin");
            Thread.sleep(1000);
            log.info("AService Finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "xxxfuck";
    }

    @Override
    public boolean nessary(Object o, TaskSupport support) {
        return true;
    }


    @Override
    public void onSuccess(TaskSupport support) {
        try {
            log.info("AService onSuccess Begin");
            Thread.sleep(3000);
            log.info("AService onSuccess Finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFail(TaskSupport support) {

    }
}
