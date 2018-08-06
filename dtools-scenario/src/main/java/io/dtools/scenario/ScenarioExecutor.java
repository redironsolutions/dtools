package io.dtools.scenario;

import io.dtools.generated.scenario.Scenario;
import io.dtools.scenario.process.Processor;
import io.dtools.scenario.util.ProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class ScenarioExecutor {

    @Autowired
    private Scenario scenario;

    @Autowired
    private Processor unboundedProcessor;
    
    @Autowired
    private Processor boundedProcessor;
    
    @Autowired
    private TaskExecutor executor;
    
    @PostConstruct
    public void init(){
        try {
            if (ProcessorUtils.infinite(scenario.getMessages().getRepeat())) {
                executor.execute(() -> unboundedProcessor.process());
            } else {
                executor.execute(() -> boundedProcessor.process());
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception during processing", e);
        }
        log.info("Completed Processing");
    }

}
