package com.example.cif.controller;

import com.example.cif.model.CifRequest;
import com.example.cif.repository.RedisImpl;
import com.example.cif.service.CifService;
import com.example.cif.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class CifController {


    @Autowired
    ReactiveRedisTemplate<String, CifRequest> reactiveRedisTemplate;

    @Autowired
    CifService cifService;

    @Autowired
    RedisImpl redisImpl;

    AtomicInteger integer = new AtomicInteger(0);

    @PostMapping("/insert")
    public Mono<CifRequest> assemble(@RequestBody CifRequest cifRequest) {
         System.out.println("request number is : " + integer.incrementAndGet());
         return   cifService.assemble(cifRequest);
    }

    @PostMapping("/assemble")
    public Flux<String> testAssemble(@RequestBody CifRequest request) {
        return cifService.processFinalSteps(request);
    }


    @PostMapping("/get-all")
    public Flux<CifRequest> testAssemble1(@RequestBody CifRequest request) {
        return redisImpl.getAllForKeys(AppUtils.getKeyForRequest(request));
    }


}