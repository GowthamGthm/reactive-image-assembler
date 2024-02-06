package com.example.cif.controller;


import com.example.cif.Utils;
import com.example.cif.model.CifRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTest {
    String FILE_NAME = "nice_peacock.jpg";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

//    @Test
//    public void test() {
//
//        String base64 = Utils.getBase64(FILE_NAME);
//        String[] chunkArray = Utils.splitBase64(base64);
//        System.out.println(chunkArray.length);
//
//        String base64Chunked = Utils.joinChunks(chunkArray);
//        Utils.writeToFile(base64Chunked);
//        String strHash = Utils.calculateSHA256Hash(base64Chunked);
//
//        List<CifRequest> cifRequest = Utils.prepareData(FILE_NAME);
//        System.out.println(cifRequest.size());
//    }


    @Test
    void greetingShouldReturnDefaultMessage() throws Exception {

        List<CifRequest> cifRequest = Utils.prepareData(FILE_NAME);
        CifRequest request = cifRequest.get(0);

        cifRequest.forEach(ele -> {
            this.restTemplate.postForEntity("http://localhost:" + port + "/insert" , ele , CifRequest.class);
        });

        this.restTemplate.postForEntity("http://localhost:" + port +"/assemble", request , String.class);
    }

}