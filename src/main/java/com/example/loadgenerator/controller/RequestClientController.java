package com.example.loadgenerator.controller;

import com.example.loadgenerator.service.RequestClientService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/load")
@Slf4j
public class RequestClientController {

    @Resource
    RequestClientService service;

    @GetMapping("/getTimeout")
    public String getTimeout(){
        log.info("getTimeout() is executed...");
        return service.getTimeout();
    }
}
