package com.example.cif.utils;

import com.example.cif.model.CifRequest;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@UtilityClass
public class AppUtils {

    public String getKeyForRequest(CifRequest cifRequest) {
        return cifRequest.getIdentifier() + "IMAGE_DATA";
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


    public void writeToFile(String base64URL) {
        byte[] output = Base64.getUrlDecoder().decode(base64URL);
        String filePath = "E:\\MyOutputs\\images-output\\output-peacock-app.jpg";
        File file = new File(filePath);
        try {
            FileUtils.writeByteArrayToFile(file, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}