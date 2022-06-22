package com.gobrs.async.example.controller;

import com.gobrs.async.GobrsAsync;
import com.gobrs.async.domain.AsyncResult;
import com.gobrs.async.example.service.GobrsService;
import com.gobrs.async.rule.Rule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: gobrs-async-core
 * @ClassName GobrsController
 * @description: Controller
 * @author: sizegang
 * @create: 2022-03-20
 **/

@RestController
@RequestMapping("gobrs")
@Slf4j
public class GobrsController {

    @Autowired
    private GobrsAsync gobrsAsync;

    @Autowired
    private GobrsService gobrsService;

    @RequestMapping("testGobrs")
    public String gobrsTest() {
        AsyncResult test = gobrsAsync.go("test", () -> new Object());
        return "success";
    }


    @RequestMapping("future")
    public void future() {
        long start = System.currentTimeMillis();
        gobrsService.future();

        long coust = System.currentTimeMillis() - start;
        log.info("future " + coust);

    }


    @RequestMapping("gobrsAsync")
    public void setGobrsAsync() {
        //开始时间: 获取当前时间毫秒数
        long start = System.currentTimeMillis();
        gobrsService.gobrsAsync();
        //结束时间: 当前时间 - 开始时间
        long coust = System.currentTimeMillis() - start;
        log.info("gobrs-Async " + coust);

    }

    @RequestMapping("updateRule")
    public void updateRule(@RequestBody Rule rule) {
        gobrsService.updateRule(rule);
    }
}
