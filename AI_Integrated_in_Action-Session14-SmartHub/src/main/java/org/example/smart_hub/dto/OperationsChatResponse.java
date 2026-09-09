package org.example.smart_hub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationsChatResponse {
    private String userMessage;
    private String agentResponse;
    private ExtractedEntitiesDto extractedEntities;
    private List<String> actionsExecuted;
    private boolean success;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtractedEntitiesDto {
        private String trackingCode;
        private String incidentType;
        private String hubCode;
        private String severity;
    }
}
