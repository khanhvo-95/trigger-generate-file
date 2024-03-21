package org.example.functions.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.functions.dto.CoiRequestDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class GenerateFileService {

    private final ValidatorService validatorService;

    public byte[] downloadCoi(CoiRequestDto coiRequestDto) {
        String url = "https://pdfobject.com/pdf/sample.pdf";

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

//            loggerService.log("Success retrieve COI file");

            byte[] pdfContent = response.getBody();
            if (pdfContent == null || pdfContent.length == 0) {
//                loggerService.log("Failed retrieve COI file, Response data: " + response.getStatusCode());
                throw new RuntimeException("Empty response/corrupted response received from downstream");
            }

            return pdfContent;
        } catch (Exception e) {
//            loggerService.log(e.getMessage());
            throw new RuntimeException("Failed retrieve COI file", e);
        }
    }

    public CompletableFuture<ResponseEntity<byte[]>> downloadCoi(Map<String, Object> request) {
        try {
            CoiRequestDto coiRequestDto = extractRequestData(request);

//            validatorService.validateRequest(coiRequestDto);

            byte[] bufferArray = this.downloadCoi(coiRequestDto);

            return handleDownloadCoiResponse(coiRequestDto.getMemberPolicyNumber(), bufferArray);

        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    private CompletableFuture<ResponseEntity<byte[]>> handleDownloadCoiResponse(String memberPolicyNumber, byte[] bufferArray) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment","COI_" + memberPolicyNumber + ".pdf");

        return CompletableFuture.completedFuture(
                new ResponseEntity<>(bufferArray, headers, HttpStatus.OK)
        );
    }

    private CoiRequestDto extractRequestData(Map<String, Object> requestData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(requestData, CoiRequestDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to CoiRequestDto", e);
        }
    }
}
