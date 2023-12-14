package com.example.loadgenerator.service;

import com.example.loadgenerator.constant.MyConstant;
import com.example.loadgenerator.executors.MyExecutor;
import com.example.loadgenerator.util.MyTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.channels.ClosedChannelException;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

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
public class LoadGenerator implements ApplicationRunner {

    @Autowired
    private MyExecutor executorPool;

    private final HttpClient httpClient;

    public LoadGenerator() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(60)) // Set connection timeout 60s
                .build();
    }

    /***
     * This method is automatically executed after the
     * Spring Boot application is fully started by implementing the ApplicationRunner interface.
     * Async for start docker build, if there are not this annotation, it could not be build
     * @param args
     * @throws Exception
     */
    @Override
    @Async
    public void run(ApplicationArguments args) throws Exception {
        startRequest();
    }

    /***
     * Makes a request based on the given TARGET URL and FREQUENCY requested per second
     */
    public void startRequest() {
        // Parameters validation check
        assert !MyConstant.TARGET.isBlank() : "TARGET SHOULD NOT BE BLANK";
        assert MyConstant.FREQUENCY > 0 : "FREQUENCY SHOULD BE GREATER THAN 0";

        // Get the customized ThreadPoolTaskExecutor
        ThreadPoolTaskExecutor myExecutor = executorPool.getExecutor();

        // Building HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(MyConstant.TARGET))
                .timeout(Duration.ofSeconds(60)) // Set request timeout 60s
                .GET()
                .build();

        while ((MyConstant.TOTAL_REQ_TIMES--) > 0) {
            // Create a CountDownLatch that limit allowed requests per second(FREQUENCY)
            CountDownLatch countDownLatch = new CountDownLatch(MyConstant.FREQUENCY);
            for (int i = 0; i < MyConstant.FREQUENCY; i++) {
                myExecutor.execute(() -> {
                    // Define the response time
                    long respTime = 0L;
                    // Create a HttpResponse
                    try {
                        // Get current timestamp
                        long start = System.currentTimeMillis();
                        // Create a handler that converts the body of the response to a string
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                        // Get the current time after the request is completed
                        long end = System.currentTimeMillis();
                        // Get response time
                        respTime = end - start;
                        // When response time is longer than 10s, then the timeout_failure_time plus 1
                        if (respTime > MyConstant.TIMEOUT_LIMIT_MS) {
                            MyConstant.TIMEOUT_FAILURE_COUNT.incrementAndGet();
                        }
                        // Calculate the total response time
                        MyConstant.TOTAL_RESP_TIME.addAndGet(respTime);
                        log.info("Request Times: " + MyConstant.REQ_TIMES.incrementAndGet() +
                                ", Request URL: " + MyConstant.TARGET +
                                ", Response Status: " + response.statusCode() +
                                ", Response Timestamp: " + respTime + " ms" +
                                ", Total AVG Timeout: " + Math.round((float) MyConstant.TOTAL_RESP_TIME.get() / MyConstant.REQ_TIMES.get()) + " ms" +
                                ", Failure Times:" + MyConstant.TIMEOUT_FAILURE_COUNT.get() +
                                ", Current DateTime: " + MyTools.date2str(new Date()));
                    } catch (HttpTimeoutException e) {
                        log.info("HttpTimeoutException occurred" + e);
                        // Handling timeout exceptions
                        MyConstant.TIMEOUT_FAILURE_COUNT.incrementAndGet();
                        // Total response time plus 60s
                        MyConstant.TOTAL_RESP_TIME.addAndGet(600000);
                    } catch (ClosedChannelException e) {
                        log.info("ClosedChannelException occurred: " + e);
                    } catch (ConnectException e){
                        log.info("ConnectException: the connection could not be established successfully " + e);
                    } catch (Exception e) {
                        log.info("other Exception occurred: " + e);
                    } finally {
                        // Countdown the completed request
                        countDownLatch.countDown();
                    }
                });
            }
            try {
                // Waiting all requests finished
                countDownLatch.await();
            } catch (InterruptedException e) {
                log.info("countDownLatch InterruptedException Exception occurred: " + e);
            }

            try {
                // Simulate requests per second
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.info("InterruptedException Exception occurred: " + e);
            }

        }

        // Shutdown the tasks in executor pool
        myExecutor.shutdown();
        // Waiting unfinished tasks to finish and shutdown the executor pool in 10s
        try {
            if (!myExecutor.getThreadPoolExecutor().awaitTermination(10, TimeUnit.SECONDS)) {
                log.info("Shutdown Now! Even though there are still some tasks that do not be finished...");
                myExecutor.shutdown();
            }
        } catch (InterruptedException e) {
            log.info("myExecutor InterruptedException Exception occurred: " + e);
            throw new RuntimeException(e);
        }


    }


}
