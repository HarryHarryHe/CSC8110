package com.example.loadgenerator.service;

import com.example.loadgenerator.constant.MyConstant;
import org.springframework.stereotype.Service;


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
        return "Request Times: " + MyConstant.REQ_TIMES.incrementAndGet() +
                ", Request URL: " + MyConstant.TARGET +
                ", Total AVG Timeout: " + Math.round((float) MyConstant.TOTAL_RESP_TIME.get() / MyConstant.REQ_TIMES.get()) + " ms" +
                ", Failure Times:" + MyConstant.FAILURE_COUNT.get();
    }
}
