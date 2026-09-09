package org.example.smart_hub.observability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraceRecord {
    private String traceId;
    private String name;
    private String module;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long latencyMs;
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
    private double estimatedCostUsd;
    private String status;
    private String inputSummary;
    private String outputSummary;
    private List<SpanRecord> spans;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpanRecord {
        private String spanId;
        private String name;
        private String type; // LLM_CALL, TOOL_EXECUTION, DB_QUERY, RAG_RETRIEVAL
        private long latencyMs;
        private String status;
        private String details;
    }
}
