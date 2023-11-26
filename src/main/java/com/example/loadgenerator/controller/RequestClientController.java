package com.example.loadgenerator.controller;

import com.example.loadgenerator.service.RequestClientService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/***
 * @description: RequestClientController is a REST controller used to
 * handle HTTP requests related to load generation.
 *
 * @author Harry
 * @date 26/11/2023
 * @version 1.0
 */
@RestController
@RequestMapping("/load")
@Slf4j
public class RequestClientController {

    @Resource
    RequestClientService service;

    @GetMapping("/getTimeout")
    public String getTimeout(){
        return service.getTimeout();
    }
    @GetMapping("/get")
    public String getIt(){
        return "hello world";
    }
}
