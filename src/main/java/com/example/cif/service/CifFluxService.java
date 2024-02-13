package com.example.cif.service;

import com.example.cif.model.CifRequest;
import com.example.cif.repository.RedisImpl;
import com.example.cif.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


@Service
@Slf4j
public class CifFluxService {


    @Autowired
    RedisImpl redisImpl;

    @Autowired
    CifService cifService;


    public Flux<CifRequest> generateDataForImages(Flux<CifRequest> cifRequestFlux) {

        String key = cifRequestFlux.take(1).map(AppUtils::getKeyForRequest)
                .switchIfEmpty( Flux.just(""))
                .blockFirst();

      return cifRequestFlux
              .map(ele -> ele)
              .flatMap(ele -> cifService.assemble(ele));
    }

    public Flux<String> doAll(Flux<CifRequest> cifRequestFlux) {
        String key = cifRequestFlux.take(1).map(AppUtils::getKeyForRequest)
                .switchIfEmpty( Flux.just(""))
                .blockFirst();

        return cifRequestFlux
                .map(ele -> ele)
                .flatMap(ele -> cifService.assemble(ele))
                .filter( ele -> ele.getMetadata().isFinall())
                .flatMap(ele -> cifService.processFinalSteps(ele));



    }
}