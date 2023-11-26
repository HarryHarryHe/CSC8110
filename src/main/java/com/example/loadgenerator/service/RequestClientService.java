package com.example.loadgenerator.service;

import com.example.loadgenerator.constant.MyConstant;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * @description: RequestClientService is a Service Layer used to
 * handle HTTP requests related to get requestTimeout.
 *
 * @author Harry
 * @date 26/11/2023
 * @version 1.0
 */
@Service
public class RequestClientService {

    /***
     * Get the number of requests, request url, average response time
     * and number of failed request timeouts recorded in global variables
     * @return Type of String that includes Request Times, Request URL, AVG Response Time and Failure Times
     */
    public String getTimeout() {
        return "Request Times: " + MyConstant.REQ_TIMES +
                ", Request URL: " + MyConstant.TARGET + ", AVG Timeout: " +
                Math.round((MyConstant.TOTAL_RESP_TIME / (MyConstant.REQ_TIMES == 0 ? 1 : MyConstant.REQ_TIMES))) + " ms" +
                ", Failure Times:" + MyConstant.FAILURE_COUNT.get();
    }
}
