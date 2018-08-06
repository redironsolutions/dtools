package io.dtools.scenario.configuration;

import io.dtools.generated.scenario.Scenario;
import io.dtools.scenario.util.InputValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.xml.transofmr.stream.StreamSource;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@Configuration
@ComponentScan({
        "io.dtools.scenario",
        "io.dtools.scenario.message",
        "io.dtools.scenario.process",
        "io.dtools.scenario.properties",
        "io.dtools.scenario.reporting",
        "io.dtools.scenario.template",
        "io.dtools.scenario.publish",
})
public class ScenarioConfiguration {

    @Value("${input}")
    private String inputFile;
    
    @Value("${schema:schemas/scenario.xsd}")
    private String schemaFile;

    @Value("${process.delay:1}")
    private String delay;
    
    @Bean
    public Scenario scenario(){
        File input = new File(inputFile);
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream(schemaFile);
            StreamSource schemaReader = new StreamSource(new InputStreamReader(in));
            if(in.available() > 0) {
                return InputValidationUtils.validateAndParse(input, schemaReader);
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            log.warn("Could not find schema file. Input will not be validated.");
            return InputValidationUtils.parseXml(input);
        }
    }
    
    @Bean
    public long delayInMilliseconds() {
        return Long.valueOf(delay);
    }
    
    @PostConstruct
    public void info() {
        log.info("-------- Scenario Configuration --------");
        log.info("Input: " + inputFile);
        log.info("Schema: " + schemaFile);
        log.info("Delay: " delay + "ms");
        log.info("-------- ---------------------- --------");
    }
}
