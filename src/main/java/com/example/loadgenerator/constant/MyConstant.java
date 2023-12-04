package com.example.loadgenerator.constant;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MyConstant {

    public static final long TIMEOUT = 10000;

    public static final String TARGET = System.getenv().getOrDefault("TARGET","https://www.baidu.com");
    public static final int FREQUENCY = Integer.parseInt(System.getenv().getOrDefault("FREQUENCY","2"));
    //    private static Integer REQ_TIMES = Integer.parseInt(System.getenv("REQ_TIMES"));
    public static Integer TOTAL_REQ_TIMES = 10000;
    public static AtomicInteger REQ_TIMES = new AtomicInteger(0);

    public static AtomicInteger FAILURE_COUNT = new AtomicInteger(0);

    public static AtomicLong TOTAL_RESP_TIME = new AtomicLong(0);
}

