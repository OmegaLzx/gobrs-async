package com.gobrs.async.engine;

import com.gobrs.async.GobrsAsync;
import com.gobrs.async.GobrsPrint;
import com.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.gobrs.async.exception.NotTaskRuleException;
import com.gobrs.async.rule.Rule;
import com.gobrs.async.spring.GobrsSpring;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.Optional;

/**
 * @author sizegang1
 * @program: gobrs-async
 * @ClassName RulePostProcessor
 * @description: Task flow resolver
 * The implementation ApplicationListener gets the Spring context, which in turn gets the Bean instance
 **/
public class RulePostProcessor implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        GobrsAsyncProperties properties = applicationContext.getBean(GobrsAsyncProperties.class);
        GobrsAsync gobrsAsync = GobrsSpring.getBean(GobrsAsync.class);
        List<Rule> rules = properties.getRules();
        Optional.ofNullable(rules).map((data) -> {
            /**
             * The primary purpose of resolving a rule is to check that the rule is correct
             * Extensible task flow resolution up
             *
             *  recommend : Custom rules engine can be extended using SPI
             */

            RuleEngine engine = applicationContext.getBean(RuleEngine.class);
            for (Rule rule : rules) {
                engine.doParse(rule, false);
                gobrsAsync.readyTo(rule.getName());
            }
            GobrsPrint.printBanner();
            GobrsPrint.getVersion();
            return 1;
        }).orElseThrow(() -> new NotTaskRuleException("spring.gobrs.async.rules is empty"));
    }


}
