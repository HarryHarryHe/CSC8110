package com.example.loadgenerator.executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/***
 * @description: Custom asynchronous threadPool,
 * used to asynchronously start the checkTimeout method when the program starts.
 *
 * @author Harry
 * @date 26/11/2023
 * @version 1.0
 */
@Configuration
@EnableAsync
public class MyExecutor {
    @Bean(name = "asyncExecutor")
    public Executor myExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("myExecutor-");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
