package org.example.functions.queue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.functions.service.UploadFileService;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.UUID;

/**
 * Azure Functions with Service Bus Trigger.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class ServiceBusQueueTriggerJava {
    private final UploadFileService uploadFileService;
    private final ObjectMapper objectMapper;
    @FunctionName("ServiceBusQueueTriggerJava")
    public void run(
            @ServiceBusQueueTrigger(name = "message", queueName = "demoqueue", connection = "SERVICE_BUS_QUEUE_CONNECTION") String message,
            final ExecutionContext context
    ) {
        context.getLogger().info("Java Service Bus Queue trigger function executed.");
        context.getLogger().info(message);
        try {
            JsonNode messageAsNode = objectMapper.readTree(message);
            String memberPolicyNumber = messageAsNode.get("memberPolicyNumber").asText();
            String correlationId = messageAsNode.has("X-Correlation-Id")
                    ? messageAsNode.get("X-Correlation-Id").asText()
                    : UUID.randomUUID().toString();

            log.info(String.format("Upload started for member policy number: '%s'.", memberPolicyNumber));
            log.info(String.format("Received with correlationId: '%s'", correlationId));

            uploadFileService.uploadCoi(memberPolicyNumber);
            log.info(String.format("Upload finished for member policy number: '%s'.", memberPolicyNumber));

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
