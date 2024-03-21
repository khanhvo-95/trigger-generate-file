package org.example.functions.controller;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.functions.service.GenerateFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HttpTriggerJava {
//    private final AppInsightsService appInsightsService;
    private final GenerateFileService generateFileService;

    @FunctionName("downloadFile")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, route = "v1/files", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<Map<String, Object>>> request,
            final ExecutionContext context) throws ExecutionException, InterruptedException {
        context.getLogger().info("Java HTTP trigger processed a request.");

        String correlationId = request.getHeaders().get("X-Correlation-Id");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        context.getLogger().info("Download File Service with correlationId: " + correlationId);

        CompletableFuture<ResponseEntity<byte[]>> response = generateFileService.downloadCoi(request.getBody().get());

        return request.createResponseBuilder(HttpStatus.OK)
                .header("X-Correlation-Id", correlationId)
                .body(response.get().getBody())
                .build();
    }
}
