package org.example.functions.service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApiService {

    private final RestTemplate restTemplate;

    public <T> T post(String baseUrl, Map<String, Object> body, Class<T> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("MPH_name", "demonest");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            URI uri = new URI(baseUrl);
            return restTemplate.exchange(uri, HttpMethod.POST, entity, responseType).getBody();

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T get(String url, Class<T> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-ebao-tenant-id", "TENANT_ID_VALUE");
            HttpEntity<String> entity = new HttpEntity<>(null, headers);

            URI uri = new URI(url);

            return restTemplate.exchange(uri, HttpMethod.GET, entity, responseType).getBody();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}