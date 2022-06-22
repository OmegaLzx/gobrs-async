package com.gobrs.async.engine;

import com.alibaba.fastjson.JSONArray;
import com.gobrs.async.GobrsAsync;
import com.gobrs.async.rule.Rule;
import com.gobrs.async.spring.GobrsSpring;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
public abstract class AbstractEngine implements RuleEngine {


    /**
     * Process rule analysis
     *
     * @param rule
     */
    @Override
    public void parse(String rule) {
        GobrsAsync gobrsAsync = GobrsSpring.getBean(GobrsAsync.class);
        List<Rule> rules = JSONArray.parseArray(rule, Rule.class);
        for (Rule r : rules) {
            /**
             * true rule enforcer
             */
            doParse(r, false);
            /**
             * Trigger task ready to execute
             */
            gobrsAsync.readyTo(r.getName());
        }
    }

}
