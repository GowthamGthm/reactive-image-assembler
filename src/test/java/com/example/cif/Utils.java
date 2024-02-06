package com.example.cif;

import com.example.cif.model.CifRequest;
import com.example.cif.model.MetaData;
import lombok.experimental.UtilityClass;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

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
public class Utils {

    private static final int CHUNK_SIZE = 10000;

    public void writeToFile(String base64URL) {
        byte[] output = Base64.getUrlDecoder().decode(base64URL);
        String filePath = "E:\\MyOutputs\\images-test\\output-peacock.jpg";
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

    public String joinChunks(String[] chunks) {
        StringBuilder joinedStringBuilder = new StringBuilder();

        for (String chunk : chunks) {
            joinedStringBuilder.append(chunk);
        }

        return joinedStringBuilder.toString();
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


    public List<CifRequest> prepareData(String fileName) {

        Double identifier = Math.random();

        String base64 = getBase64(fileName);
        String[] chunks = splitBase64(base64);
        String hash = calculateSHA256Hash(base64);

        List<CifRequest> requestList = new ArrayList<>();

        requestList.add(buildFirstRequest(hash,identifier));
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


    public CifRequest buildFirstRequest(String hash , Double identifier) {
        MetaData metadata = MetaData.builder()
                .first(true)
                .hash(hash)
                .build();
        return CifRequest.builder()
                .metadata(metadata)
                .identifier(identifier)
                .build();
    }

    public CifRequest buildLastRequest(Double identifer) {
        MetaData metadata = MetaData.builder()
                .finall(true)
                .build();
        return CifRequest.builder()
                .metadata(metadata)
                .identifier(identifer)
                .build();
    }

}