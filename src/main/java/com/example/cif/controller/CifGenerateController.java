package com.example.cif.controller;

import com.example.cif.model.CifRequest;
import com.example.cif.utils.AppUtilComponent;
import com.example.cif.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
public class CifGenerateController {

    @Autowired
    CifController cifController;

    @Autowired
    AppUtilComponent appUtilComponent;

    @GetMapping("/generate-data")
    public String generateData() throws IOException {

        String fileName = appUtilComponent.getRandomFileName();
        log.info("Generating data for fileName: " + fileName);
        List<CifRequest> cifList = AppUtils.prepareData("images/" + fileName);

        cifList.forEach(request -> {
            cifController.insert(request).subscribe();
        });
        return "Done";
    }


    @GetMapping("/generate-data-flux")
    public String generateDataFlux() throws IOException {

        String fileName = appUtilComponent.getRandomFileName();
        log.info("Generating data for fileName: " + fileName);
        List<CifRequest> cifList = AppUtils.prepareData("images/" + fileName);

        cifController.insert1(Flux.fromIterable(cifList)).subscribe();

        return "Done";
    }

    @GetMapping("do-all")
    public Flux<String> getData() throws IOException {

        String fileName = appUtilComponent.getRandomFileName();
        log.info("Generating data for fileName: " + fileName);
        List<CifRequest> cifList = AppUtils.prepareData("images/" + fileName);
        return cifController.doAll(Flux.fromIterable(cifList));
    }

}