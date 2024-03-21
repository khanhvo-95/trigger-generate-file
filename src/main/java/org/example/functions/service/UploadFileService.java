package org.example.functions.service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UploadFileService {
    private final ApiService apiService;

    @Value("${paths.downloadCoiPath}")
    private String downloadCoiPath;

    @Value("${paths.uploadCoiPath}")
    private String uploadCoiPath;
    private String downloadCoi(String memberPolicyNumber) {
        Map<String, Object> data = new HashMap<>();
        data.put("memberPolicyNumber", memberPolicyNumber);
        String response = apiService.post(downloadCoiPath, data, String.class);

        return Base64.getEncoder().encodeToString(response.getBytes());
    }

    public void uploadCoi(String memberPolicyNumber) {
        String fileContent = downloadCoi(memberPolicyNumber);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("MPH_name", "demonest");

        Map<String, Object> data = new HashMap<>();
        data.put("memberPolicyNumber", memberPolicyNumber);
        Map<String, Object> fileDetail = new HashMap<>();
        fileDetail.put("fileName", memberPolicyNumber);
        fileDetail.put("fileExtension", ".pdf");
        fileDetail.put("fileContent", fileContent);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("fileName", memberPolicyNumber);
        metadata.put("fileExtension", "pdf");
        fileDetail.put("metadata", metadata);
        data.put("fileDetail", fileDetail);

        apiService.post(uploadCoiPath, data, String.class);
    }
}