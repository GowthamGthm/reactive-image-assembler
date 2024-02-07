package com.example.cif.utils;

import com.example.cif.model.CifRequest;
import com.example.cif.model.MetaData;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@UtilityClass
public class AppUtils {

    private static final int CHUNK_SIZE = 1000;

    public String getKeyForRequest(CifRequest cifRequest) {
        return cifRequest.getIdentifier() + "_IMAGE_DATA";
    }

    public String calculateSHA256Hash(String input) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }


    public void writeToFile(String base64URL, String fileName) {
        byte[] output = Base64.getUrlDecoder().decode(base64URL);

        String extension = "." + FilenameUtils.getExtension(fileName);
        String newFileName = fileName.replace(extension , "");
        newFileName = newFileName + "_" + RandomStringUtils.randomNumeric(5) + extension;

        String filePath = "E:\\MyOutputs\\images-output\\" + newFileName;
        File file = new File(filePath);
        try {
            FileUtils.writeByteArrayToFile(file, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBase64(String fileName) {

        try {
            InputStream loader = ClassLoader.getSystemResourceAsStream(fileName);
            byte[] imageBytes = IOUtils.toByteArray(loader);
            String encoded = Base64.getUrlEncoder().encodeToString(imageBytes);
            return encoded;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] splitBase64(String input) {

        int length = input.length();
        int numOfChunks = (int) Math.ceil((double) length / CHUNK_SIZE);
        String[] chunks = new String[numOfChunks];

        for (int i = 0; i < numOfChunks; i++) {
            int start = i * CHUNK_SIZE;
            int end = Math.min((i + 1) * CHUNK_SIZE, length);
            chunks[i] = input.substring(start, end);
        }
        return chunks;
    }


    public List<CifRequest> prepareData(String fileName) {

        String identifier = RandomStringUtils.randomNumeric(20);

        String base64 = getBase64(fileName);
        String[] chunks = splitBase64(base64);
        String hash = calculateSHA256Hash(base64);

        List<CifRequest> requestList = new ArrayList<>();

        requestList.add(buildFirstRequest(hash,identifier, fileName));
        for (int i = 0; i < chunks.length; i++) {

            MetaData metadata;
            CifRequest cifRequest;
            String chunk = chunks[i];

            metadata = MetaData.builder()
                    .second(true)
                    .hash(hash)
                    .build();

            cifRequest = CifRequest.builder()
                    .metadata(metadata)
                    .identifier(identifier)
                    .chunk(chunk)
                    .chunkNumber((long) i +1)
                    .build();
            requestList.add(cifRequest);
        }
        requestList.add(buildLastRequest(identifier));

        return requestList;

    }


    public CifRequest buildFirstRequest(String hash , String identifier, String fileName) {
        MetaData metadata = MetaData.builder()
                .first(true)
                .hash(hash)
                .fileName(fileName)
                .build();
        return CifRequest.builder()
                .metadata(metadata)
                .identifier(identifier)
                .build();
    }

    public CifRequest buildLastRequest(String identifer) {
        MetaData metadata = MetaData.builder()
                .finall(true)
                .build();
        return CifRequest.builder()
                .metadata(metadata)
                .identifier(identifer)
                .build();
    }



}