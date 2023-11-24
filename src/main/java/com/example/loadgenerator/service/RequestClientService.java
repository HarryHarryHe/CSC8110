package com.example.loadgenerator.service;

import com.example.loadgenerator.constant.MyConstant;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RequestClientService {

    public String getTimeout() {
        return "Request Times: " + MyConstant.REQ_TIMES +
                ", Request URL: " + MyConstant.TARGET + ", AVG Timeout: " +
                Math.round((MyConstant.TOTAL_RESP_TIME / (MyConstant.REQ_TIMES == 0 ? 1 : MyConstant.REQ_TIMES))) + " ms" +
                ", Failure Times:" + MyConstant.FAILURE_COUNT.get();
    }
}
