package com.gobrs.async.example.task;

import com.gobrs.async.TaskSupport;
import com.gobrs.async.anno.Task;
import com.gobrs.async.domain.TaskResult;
import com.gobrs.async.task.AsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@Task(name = "任务C")
public class CService extends AsyncTask<Object, Integer> {


    @Override
    public void prepare(Object o) {

    }

    @Override
    public Integer task(Object o, TaskSupport support) {
        try {
            log.info("CService Begin");
            //获取 所依赖的父任务的结果
            AService result = getResult(support, AService.class);
            Thread.sleep(3000);
            log.info("CService Finish");
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
        // 获取自身task 执行完成之后的结果
        Integer result = getResult(support);

        //获取 任务结果封装 包含执行状态
        TaskResult<Integer> taskResult = getTaskResult(support);
    }

    @Override
    public void onFail(TaskSupport support) {
    }
}
