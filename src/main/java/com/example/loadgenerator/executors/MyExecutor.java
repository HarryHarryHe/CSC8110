package com.example.loadgenerator.executors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * @description: Custom asynchronous threadPool,
 * used to asynchronously start the checkTimeout method when the program starts.
 *
 * @author Harry
 * @date 26/11/2023
 * @version 1.0
 */
@Configuration
@Slf4j
public class MyExecutor {

    private static final AtomicInteger REJECTED_COUNTS = new AtomicInteger(0);

    @Bean(name = "asyncExecutor")
    public ThreadPoolTaskExecutor getExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("myTask-");
        // CorePoolSize is 8, as the machine own 4 cpus
        executor.setCorePoolSize(8);
        // Maximum PoolSize
        executor.setMaxPoolSize(16);
        // Idle lifetime of non-core threads, 30s
        executor.setKeepAliveSeconds(30);
        // The capacity of task queue
        executor.setQueueCapacity(100);

        // Customize the rejected policy and Override rejected policy
        RejectedExecutionHandler handler = (runnable, exec) -> {
            System.out.println("Info: Task submission rejected! -- " + REJECTED_COUNTS.incrementAndGet());
        };
        // Rejected strategy is likely DiscardOldestPolicy, that discards ths unfinished task in the queue
        executor.setRejectedExecutionHandler(handler);
        executor.initialize();

        return executor;
    }
}
