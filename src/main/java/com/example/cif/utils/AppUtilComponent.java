package com.example.cif.utils;

import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

@Component
public class AppUtilComponent {

    @Autowired
    ResourceLoader resourceLoader;

    public String getRandomFileName() throws IOException {
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