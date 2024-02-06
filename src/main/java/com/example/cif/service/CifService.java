package com.example.cif.service;


import com.example.cif.model.CifRequest;
import com.example.cif.repository.RedisImpl;
import com.example.cif.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@Service
@Slf4j
public class CifService {

    @Autowired
    RedisImpl redisImpl;


    public Mono<CifRequest> assemble(CifRequest cifRequest) {

        if(cifRequest.getMetadata().isSecond()) {
            return processSecondFragment(cifRequest);
        } else if(cifRequest.getMetadata().isFinall()) {
            return processLastFragment(cifRequest);
        } else if(cifRequest.getMetadata().isFirst())  {
            return processFirstFragment(cifRequest);
        } else {
            return Mono.error(new RuntimeException("Fragment Type Not Found"));
        }
    }

    private Mono<CifRequest> processFirstFragment(CifRequest cifRequest) {

        return redisImpl.insert(cifRequest , AppUtils.getKeyForRequest(cifRequest))
                .map(ele -> cifRequest);

    }

    private Mono<CifRequest> processLastFragment(CifRequest cifRequest) {

        return redisImpl.insert(cifRequest , AppUtils.getKeyForRequest(cifRequest))
                .map(ele -> cifRequest);

    }

    private Mono<CifRequest> processSecondFragment(CifRequest cifRequest) {
        return redisImpl.insert(cifRequest,AppUtils.getKeyForRequest(cifRequest))
                .map(e -> cifRequest);
    }


    public Flux<String> processFinalSteps(CifRequest cifRequest) {
        return redisImpl.getFirstFragmentForKeys(AppUtils.getKeyForRequest(cifRequest))
                .doFirst(() -> log.info("getting first fragment"))
                .flatMap(ele -> redisImpl.getAllForKeys(AppUtils.getKeyForRequest(cifRequest))
                        .doFirst(() -> log.info("getting all fragment"))
                        .filter(obj -> obj.getMetadata().isSecond())
                        .sort(Comparator.comparing(CifRequest::getChunkNumber))
                        .map(CifRequest::getChunk)
                        .collectList()
                        .map(item -> String.join("", item))
                        .flatMap(ele1 -> {
                            if (AppUtils.calculateSHA256Hash(ele1).equalsIgnoreCase(ele.getMetadata().getHash())) {
                                log.info("Hash matched");
                                AppUtils.writeToFile(ele1);
                                return Mono.just("Hash Matched");
                            } else {
                                log.info("Hash Not matched");
                                return Mono.error(new RuntimeException("Hash Didn't Match"));
                            }
                        })
                        .onErrorResume(error -> {
                            System.out.println("Error occurred: " + error.getMessage());
                            log.error("Error occurred: ", error);
                            return Mono.just(error.getMessage());
                        })
                        .doFinally(e -> log.info("Completed operation")));

    }

//    public void working() {
//        return  redisImpl.getFirstFragmentForKeys(AppUtils.getKeyForRequest(cifRequest))
//                .doFirst(() -> log.info("getting first fragment"))
//                .map(ele -> {
//                    redisImpl.getAllForKeys(AppUtils.getKeyForRequest(cifRequest))
//                            .doFirst(() -> log.info("getting all fragment"))
//                            .filter(obj -> obj.getMetadata().isSecond())
//                            .sort(Comparator.comparing(CifRequest::getChunkNumber))
//                            .map(obj -> obj.getChunk())
//                            .collectList()
//                            .map(item -> String.join("", item))
//                            .map(ele1 -> {
//                                if(AppUtils.calculateSHA256Hash(ele1).equalsIgnoreCase(ele.getMetadata().getHash())) {
//                                    log.info("Hash matched");
//                                    AppUtils.writeToFile((String) ele1);
//                                    return ele1;
//                                }
//                                log.info("Hash Not matched");
//                                return Mono.error(new RuntimeException("Hash Dint Match"));
//                            })
//                            .onErrorResume(error -> {
//                                System.out.println("Error occurred: " + error.getMessage());
//                                log.error("Error occured: ",error);
//                                return Mono.just("Fallback Value");
//                            })
//                            .doFinally(e -> log.info("Completed operation"))
//                            .subscribe();
//                    return "Completed";
//                });
//    }

}