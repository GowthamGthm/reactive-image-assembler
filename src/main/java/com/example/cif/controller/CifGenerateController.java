package com.example.cif.controller;

import com.example.cif.model.CifRequest;
import com.example.cif.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;


import java.util.List;

@RestController
@Slf4j
public class CifGenerateController {

    @Autowired
    CifController cifController;

    @Autowired
    ResourceLoader resourceLoader;

    @GetMapping("/generate-data")
    public String generateData() throws IOException {

        String fileName = getRandomFileName();
        log.info("Generating data for fileName: " + fileName);
        List<CifRequest> cifList = AppUtils.prepareData("images/" + fileName);

        cifList.forEach(request -> {
            cifController.insert(request).subscribe();
        });
        return "Done";
    }

    private String getRandomFileName() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:images/");

        // Get a list of all files in the folder
        Path folderPath = resource.getFile().toPath();
        List<Path> files = Files.walk(folderPath)
                .filter(Files::isRegularFile)
                .toList();

        // Choose a random file from the list
        Random random = new Random();
        Path randomFilePath = files.get(random.nextInt(files.size()));
        return randomFilePath.getFileName().toString();
    }


}