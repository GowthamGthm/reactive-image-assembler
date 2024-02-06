package com.example.cif.repository;

import com.example.cif.model.CifRequest;
import com.example.cif.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class RedisImpl {


    @Autowired
    ReactiveRedisTemplate<String, CifRequest> reactiveRedisTemplate;

    public Mono<Long> flushAll(String key) {
        if(!StringUtils.hasText(key)) {
            key = "*";
        }
        // Retrieve all keys using the SCAN command
        Flux<String> keys = reactiveRedisTemplate.keys(key);
        // Delete all keys using the DELETE command
        return reactiveRedisTemplate.delete(keys);
    }

    public Mono<Long> insert(CifRequest cifRequest, String key)  {

        ReactiveListOperations<String, CifRequest> valueOperations = reactiveRedisTemplate.opsForList();

        if(cifRequest.getMetadata().isFirst()) {
            flushAll(key).then();
        }
        return valueOperations.rightPush(key , cifRequest).map(e -> e);
    }


    public Flux<CifRequest> getAllForKeys(String key) {

        ReactiveListOperations<String, CifRequest> listOperations = reactiveRedisTemplate.opsForList();
        return listOperations.range(key, 0, -1)
                .map(b -> ObjectMapperUtils.objectMapper(b, CifRequest.class))
                .collectList().flatMapMany(Flux::fromIterable);

    }


    public Flux<CifRequest> getFirstFragmentForKeys(String key) {

        ReactiveListOperations<String, CifRequest> listOperations = reactiveRedisTemplate.opsForList();
        return listOperations.range(key, 0, -1)
                .map(b -> ObjectMapperUtils.objectMapper(b, CifRequest.class))
                .filter(ele -> ele.getMetadata().isFirst())
                .map(ele -> ele);

    }

    public Mono<CifRequest> dummy(String key) {

        ReactiveValueOperations<String, CifRequest> valueOperations = reactiveRedisTemplate.opsForValue();
        return valueOperations.get("abcd")
                .map(ele -> ele);

    }


    public void test() {

                
                
                
                
    }




}