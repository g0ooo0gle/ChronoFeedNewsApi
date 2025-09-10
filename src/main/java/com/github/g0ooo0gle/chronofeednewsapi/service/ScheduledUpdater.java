package com.github.g0ooo0gle.chronofeednewsapi.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;

@Component
public class ScheduledUpdater {

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${data.sources}")
    private String[] dataSources;

    @PostConstruct
    public void scheduleUpdates() {
        Arrays.stream(dataSources).forEach(source -> {
            DataSourceService service = applicationContext.getBean(source.replace("-", "") + "Service", DataSourceService.class);
            String intervalProp = "data.sources." + source + ".update.interval.ms";
            long interval = Long.parseLong(applicationContext.getEnvironment().getProperty(intervalProp));

            taskScheduler.scheduleAtFixedRate(service::fetchData, Duration.ofMillis(interval));
        });
    }
}
