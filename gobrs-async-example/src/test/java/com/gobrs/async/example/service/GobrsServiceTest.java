package com.gobrs.async.example.service;

import com.gobrs.async.GobrsAsync;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author zexuan.Li  2022/6/22
 */
@SpringBootTest
public class GobrsServiceTest {
    @Autowired
    private GobrsAsync gobrsAsync;

    @Test
    void name() {
        gobrsAsync.go("test1", Object::new);
    }
}