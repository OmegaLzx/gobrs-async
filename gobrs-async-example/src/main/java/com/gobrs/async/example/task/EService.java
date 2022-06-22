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
@Task(name = "任务E")
@Slf4j
public class EService extends AsyncTask<Object, Object> {
    int i = 10000;

    @Override
    public void prepare(Object o) {


    }

    @Override
    public Object task(Object o, TaskSupport support) {
        try {
            Thread.sleep(600);
            for (int i1 = 0; i1 < i; i1++) {
                i1 += i1;
            }
            log.info("EService Finish");

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
