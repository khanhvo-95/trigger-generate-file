package org.example.functions.service;
import org.springframework.stereotype.Service;

@Service
public class ValidatorService {

    public boolean isImage(String mimeType) {
        String[] imageMimeTypes = {"image/jpeg", "image/png"};
        for (String type : imageMimeTypes) {
            if (type.equals(mimeType)) {
                return true;
            }
        }
        return false;
    }
}