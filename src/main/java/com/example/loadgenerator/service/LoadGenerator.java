package com.example.loadgenerator.service;

import com.example.loadgenerator.constant.MyConstant;
import com.example.loadgenerator.executors.MyExecutor;
import com.example.loadgenerator.util.MyTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.Semaphore;

/***
 * @description: This method will start asynchronously, set the specified request FREQUENCY and
 * request the specified URL, and record the response time and
 * timeout times to global variables
 *
 * @author Harry
 * @date 26/11/2023
 * @version 1.0
 */
@Component
@Slf4j
public class LoadGenerator implements CommandLineRunner {

    @Autowired
    private MyExecutor executorPool;

    private final HttpClient httpClient;

    public LoadGenerator() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10)) // Set connection timeout 10s
                .build();
    }

    @Override
    public void run(String... args) throws Exception {
        startRequest();
    }

    public void startRequest() {
        // Parameters validation check
        assert !MyConstant.TARGET.isBlank() : "TARGET SHOULD NOT BE BLANK";
        assert MyConstant.FREQUENCY > 0 : "FREQUENCY SHOULD BE GREATER THAN 0";

        // Get the customized ThreadPoolTaskExecutor
        ThreadPoolTaskExecutor myExecutor = executorPool.getExecutor();

        // Building HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(MyConstant.TARGET))
                .timeout(Duration.ofSeconds(10)) // Set request timeout 10s
                .GET()
                .build();

        // Create a Semaphore that limit allowed requests per second(FREQUENCY)
        final Semaphore semaphore = new Semaphore(MyConstant.FREQUENCY);

        while ((MyConstant.TOTAL_REQ_TIMES--) > 0) {
            for (int i = 0; i < MyConstant.FREQUENCY; i++) {
                myExecutor.execute(() -> {
                    try {
                        // Get one semaphore
                        semaphore.acquire();

                        // Get current timestamp
                        long start = System.currentTimeMillis();
                        // Create a handler that converts the body of the response to a string
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                        // Get the current time after the request is completed
                        long end = System.currentTimeMillis();
                        // Get response time
                        long respTime = end - start;
                        // Calculate the total response time
                        MyConstant.TOTAL_RESP_TIME.addAndGet(respTime);
                        log.info("Request Times: " + MyConstant.REQ_TIMES.incrementAndGet() +
                                ", Request URL: " + MyConstant.TARGET +
                                ", Response statusCode: " + response.statusCode() +
                                ", Response Timestamp: " + respTime +
                                ", Total AVG Timeout: " + Math.round((float) MyConstant.TOTAL_RESP_TIME.get() / MyConstant.REQ_TIMES.get()) + " ms" +
                                ", Failure Times:" + MyConstant.FAILURE_COUNT.get() +
                                ", Current DateTime: " + MyTools.date2str(new Date()));
                    } catch (HttpTimeoutException e) {
                        log.info("HttpTimeoutException occurred");
                        // Handling timeout exceptions
                        MyConstant.FAILURE_COUNT.incrementAndGet();
                    } catch (IOException e) {
                        log.info("IOException occurred: " + e);
                    } catch (InterruptedException e) {
                        log.info("InterruptedException occurred: " + e);
                    } catch (Exception e) {
                        log.info("UNKNOWN Exception occurred: " + e);
                    } finally {
                        // Release one semaphore
                        semaphore.release();
                    }
                });
            }

            try {
                // Simulate requests per second
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // Shutdown the tasks in executor pool
        // myExecutor.shutdown();
        // Shutdown the executor pool in 10s waiting unfinished tasks to finish
        myExecutor.setAwaitTerminationSeconds(10);
    }
}
