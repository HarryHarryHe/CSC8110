package com.example.loadgenerator.constant;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MyConstant {


    /**
     * Target request address
     */
    public static final String TARGET = System.getenv().getOrDefault("TARGET", "https://www.baidu.com");
    /**
     * Requests per second
     */
    public static final int FREQUENCY = Integer.parseInt(System.getenv().getOrDefault("FREQUENCY", "2"));
    //    private static Integer REQ_TIMES = Integer.parseInt(System.getenv("REQ_TIMES"));
    /**
     * The total number of requests required to limit the program from running forever
     */
    public static Integer TOTAL_REQ_TIMES = 100;
    /**
     * Requested times
     */
    public static AtomicInteger REQ_TIMES = new AtomicInteger(0);
    /**
     * Number of failed request timeouts
     */
    public static AtomicInteger FAILURE_COUNT = new AtomicInteger(0);
    /**
     * Total request response time
     */
    public static AtomicLong TOTAL_RESP_TIME = new AtomicLong(0);
}

