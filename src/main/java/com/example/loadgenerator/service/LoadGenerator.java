package com.example.loadgenerator.service;

import com.example.loadgenerator.constant.MyConstant;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
public class LoadGenerator {


    public void startCheckTimeout() {
        checkTimeout(null);
    }

    @Async("asyncExecutor")
    @EventListener
    public void checkTimeout(ContextRefreshedEvent event) {
        long responseTime = 0;
        int req_times = MyConstant.TOTAL_REQ_TIMES;
        while ((req_times--) > 0) {
            try {
                //Get the beginning timestamp
                long start = System.currentTimeMillis();
                //Building URL Request
                URL url = new URL(MyConstant.TARGET);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                //Build the link and get the response code
                int responseCode = connection.getResponseCode();

                //Simulate the request time, adding random numbers to demonstrate the timeout
                Thread.sleep(new Random().nextInt(8000, 10000));

                MyConstant.REQ_TIMES++;

                long end = System.currentTimeMillis();
                //responseTime
                responseTime = end - start;
                MyConstant.TOTAL_RESP_TIME += responseTime;

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    if (responseTime - MyConstant.TIMEOUT > 0) {
                        MyConstant.FAILURE_COUNT.incrementAndGet();
                    }
                    log.info("Request Times: " + MyConstant.REQ_TIMES + ", Current Resp_Time: " + responseTime +
                            ", Request URL: " + MyConstant.TARGET +
                            ", AVG Timeout: " + Math.round(((float) MyConstant.TOTAL_RESP_TIME / (MyConstant.REQ_TIMES == 0 ? 1 : MyConstant.REQ_TIMES))) + " ms" +
                            ", Failure Times:" + MyConstant.FAILURE_COUNT.get());
                } else {
                    log.info("Request failed with response code: " + responseCode);
                }
                Thread.sleep(1000 / MyConstant.FREQUENCY);
            } catch (Exception e) {
                MyConstant.FAILURE_COUNT.incrementAndGet();
                log.info("Request failed with exception: " + e.getMessage());
            }
        }
    }
}
