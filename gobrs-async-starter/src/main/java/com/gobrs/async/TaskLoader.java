package com.gobrs.async;

import com.gobrs.async.callback.AsyncTaskExceptionInterceptor;
import com.gobrs.async.callback.AsyncTaskPostInterceptor;
import com.gobrs.async.callback.AsyncTaskPreInterceptor;
import com.gobrs.async.callback.ErrorCallback;
import com.gobrs.async.domain.AsyncResult;
import com.gobrs.async.enums.ExpState;
import com.gobrs.async.exception.GobrsAsyncException;
import com.gobrs.async.exception.TimeoutException;
import com.gobrs.async.spring.GobrsSpring;
import com.gobrs.async.task.AsyncTask;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @program: gobrs-async-starter
 * @ClassName
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/
@Slf4j
public class TaskLoader {
    private final static ArrayList<Future<?>> EmptyFutures = new ArrayList<>(0);
    private final ExecutorService executorService;
    private final CountDownLatch completeLatch;
    private final Map<AsyncTask, TaskActuator> processMap;
    private final long timeout;
    private final Lock lock = new ReentrantLock();
    public TaskTrigger.AssistantTask assistantTask;
    public ArrayList<Future<?>> futures;
    public Map<AsyncTask, Future> futuresAsync = new ConcurrentHashMap<>();
    /**
     * Interruption code
     */
    private AtomicInteger expCode = new AtomicInteger(ExpState.DEFAULT.getCode());
    /**
     * task Loader is Running
     */
    private AtomicBoolean isRunning = new AtomicBoolean(true);
    private AsyncTaskExceptionInterceptor asyncExceptionInterceptor = GobrsSpring.getBean(AsyncTaskExceptionInterceptor.class);
    private AsyncTaskPreInterceptor asyncTaskPreInterceptor = GobrsSpring.getBean(AsyncTaskPreInterceptor.class);
    private AsyncTaskPostInterceptor asyncTaskPostInterceptor = GobrsSpring.getBean(AsyncTaskPostInterceptor.class);
    private volatile Throwable error;
    private volatile boolean canceled = false;

    TaskLoader(ExecutorService executorService, Map<AsyncTask, TaskActuator> processMap,
               long timeout) {
        this.executorService = executorService;
        this.processMap = processMap;
        completeLatch = new CountDownLatch(1);
        this.timeout = timeout;

        if (this.timeout > 0) {
            futures = new ArrayList<>(1);
        } else {
            futures = EmptyFutures;
        }
    }

    AsyncResult load() {
        ArrayList<TaskActuator> begins = getBeginProcess();
        log.info("启动没有任何依赖关系的任务线程 {}", begins.stream().map(t -> t.getTask().getName()).collect(Collectors.toList()));
        for (TaskActuator process : begins) {
            /**
             * Start the thread to perform tasks without any dependencies
             */
            startProcess(process);
        }
        // wait
        waitIfNecessary();
        // 返回结果
        return back(begins);
    }

    private ArrayList<TaskActuator> getBeginProcess() {
        ArrayList<TaskActuator> beginsWith = new ArrayList<>(1);
        for (TaskActuator process : processMap.values()) {
            if (!process.hasUnsatisfiedDependcies()) {
                beginsWith.add(process);
            }
        }
        return beginsWith;
    }

    void completed() {
        completeLatch.countDown();
    }

    /**
     * Abnormal callback
     *
     * @param errorCallback Exception parameter encapsulation
     */
    public void error(ErrorCallback errorCallback) {
        asyncExceptionInterceptor.exception(errorCallback);
    }

    /**
     * The process is interrupted by a task exception
     *
     * @param errorCallback
     */
    public void errorInterrupted(ErrorCallback errorCallback) {
        this.error = errorCallback.getThrowable();

        cancel();

        completeLatch.countDown();
        /**
         * manual stopAsync  exception  is null
         */
        if (errorCallback.getThrowable() != null) {
            /**
             * Global interception listening
             */
            asyncExceptionInterceptor.exception(errorCallback);
        }
    }

    /**
     * Premission interceptor
     *
     * @param object   task parameter
     * @param taskName taskName
     */
    public void preInterceptor(Object object, String taskName) {
        asyncTaskPreInterceptor.preProcess(object, taskName);
    }

    /**
     * Mission post-intercept
     *
     * @param object   task Result
     * @param taskName taskName
     */
    public void postInterceptor(Object object, String taskName) {
        asyncTaskPostInterceptor.postProcess(object, taskName);
    }

    private void cancel() {
        lock.lock();
        try {
            canceled = true;
            for (Future<?> future : futures) {
                /**
                 * Enforced interruptions
                 */
                future.cancel(true);
            }
        } finally {
            lock.unlock();
        }

    }

    /**
     * The main process interrupts and waits for the task to flow
     */
    private void waitIfNecessary() {
        log.info("主进程中断并等待任务执行 超时时间 {}", timeout);
        try {
            if (timeout > 0) {
                if (!completeLatch.await(timeout, TimeUnit.MILLISECONDS)) {
                    cancel();
                    throw new TimeoutException();
                }
            } else {
                completeLatch.await();
            }
            if (error != null) {
                throw new GobrsAsyncException(error);
            }
        } catch (InterruptedException e) {
            throw new GobrsAsyncException(e);
        }
    }


    TaskActuator getProcess(AsyncTask asyncTask) {
        return processMap.get(asyncTask);
    }

    void startProcess(TaskActuator taskActuator) {

        if (timeout > 0 || taskActuator.getGobrsAsyncProperties().isTaskInterrupt()) {
            /*
             * If you need to interrupt then you need to save all the task threads and you need to manipulate shared variables
             * 如果你需要中断，那么你需要保存所有的任务线程，并操作共享变量
             */
            lock.lock();
            try {
                if (!canceled) {
                    Future<?> submit = executorService.submit(taskActuator);
                    futures.add(submit);
                    futuresAsync.put(taskActuator.task, submit);
                }
            } finally {
                lock.unlock();
            }
        } else {
            /*
             * Run the command without setting the timeout period
             * 无超时时间,执行该任务线程
             */
            log.info("执行非中断分支，taskActuator {}", taskActuator);
            Future<?> submit = executorService.submit(taskActuator);
            futuresAsync.put(taskActuator.task, submit);
        }
    }

    /**
     * End of single mission line
     *
     * @param taskLines
     */
    public void stopSingleTaskLine(Integer taskLines) {
        TaskActuator taskActuator = processMap.get(assistantTask);
        for (Integer i = 0; i < taskLines; i++) {
            processMap.get(assistantTask).releasingDependency();
        }
        if (!taskActuator.hasUnsatisfiedDependcies()) {
            taskActuator.run();
        }
    }

    /**
     * Get the task Bus
     *
     * @param begins Collection of subtask processes
     * @return
     */
    private TaskSupport getSupport(List<TaskActuator> begins) {
        return begins.get(0).getTaskSupport();
    }

    /**
     * Encapsulate return parameter
     *
     * @param begins
     * @return
     */
    private AsyncResult back(List<TaskActuator> begins) {
        log.info("主线程等待，同步获取结果");
        TaskSupport support = getSupport(begins);
        AsyncResult asyncResult = new AsyncResult();
        asyncResult.setResultMap(support.getResultMap());
        asyncResult.setExpCode(expCode.get());
        asyncResult.setSuccess(true);
        return asyncResult;
    }

    public AtomicInteger getExpCode() {
        return expCode;
    }

    public void setExpCode(AtomicInteger expCode) {
        this.expCode = expCode;
    }

    public AtomicBoolean isRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = new AtomicBoolean(isRunning);
    }

    public TaskTrigger.AssistantTask getAssistantTask() {
        return assistantTask;
    }

    public void setAssistantTask(TaskTrigger.AssistantTask assistantTask) {
        this.assistantTask = assistantTask;
    }
}
