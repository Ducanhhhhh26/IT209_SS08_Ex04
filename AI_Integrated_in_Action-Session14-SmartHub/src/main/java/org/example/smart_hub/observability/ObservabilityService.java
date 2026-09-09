package org.example.smart_hub.observability;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class ObservabilityService {

    // Store in-memory traces (last 100 traces)
    private final List<TraceRecord> traceHistory = new CopyOnWriteArrayList<>();
    private final Map<String, Integer> loopGuardCounters = new ConcurrentHashMap<>();

    private static final int MAX_ITERATIONS = 6;
    // Estimated pricing for gpt-4o-mini / embedding: $0.15 / 1M input, $0.60 / 1M output
    private static final double INPUT_COST_PER_TOKEN = 0.15 / 1_000_000.0;
    private static final double OUTPUT_COST_PER_TOKEN = 0.60 / 1_000_000.0;

    public void checkAndIncrementLoopGuard(String sessionId) {
        int count = loopGuardCounters.merge(sessionId, 1, Integer::sum);
        if (count > MAX_ITERATIONS) {
            log.error("Infinite loop detected for session {}! Iterations exceeded limit: {}", sessionId, MAX_ITERATIONS);
            throw new IllegalStateException("An toàn vận hành: Đã vượt quá số lượt gọi công cụ tối đa (max-iterations <= 6). Ngắt chu trình để bảo vệ hệ thống.");
        }
    }

    public void resetLoopGuard(String sessionId) {
        loopGuardCounters.remove(sessionId);
    }

    public void recordTrace(TraceRecord trace) {
        if (trace.getLatencyMs() == 0 && trace.getStartTime() != null && trace.getEndTime() != null) {
            trace.setLatencyMs(java.time.Duration.between(trace.getStartTime(), trace.getEndTime()).toMillis());
        }

        if (trace.getTotalTokens() == 0) {
            trace.setTotalTokens(trace.getPromptTokens() + trace.getCompletionTokens());
        }

        if (trace.getEstimatedCostUsd() == 0.0) {
            double cost = (trace.getPromptTokens() * INPUT_COST_PER_TOKEN) + (trace.getCompletionTokens() * OUTPUT_COST_PER_TOKEN);
            trace.setEstimatedCostUsd(cost);
        }

        traceHistory.add(0, trace); // Add to head
        if (traceHistory.size() > 100) {
            traceHistory.remove(traceHistory.size() - 1);
        }

        log.info("Logged Observability Trace: [{}] - Latency: {}ms, Tokens: {}, Cost: ${}",
                trace.getName(), trace.getLatencyMs(), trace.getTotalTokens(), String.format("%.6f", trace.getEstimatedCostUsd()));
    }

    public List<TraceRecord> getRecentTraces() {
        return Collections.unmodifiableList(traceHistory);
    }

    public Map<String, Object> getObservabilitySummary() {
        int totalTraces = traceHistory.size();
        long totalTokens = traceHistory.stream().mapToLong(TraceRecord::getTotalTokens).sum();
        long totalPromptTokens = traceHistory.stream().mapToLong(TraceRecord::getPromptTokens).sum();
        long totalCompletionTokens = traceHistory.stream().mapToLong(TraceRecord::getCompletionTokens).sum();
        double totalCost = traceHistory.stream().mapToDouble(TraceRecord::getEstimatedCostUsd).sum();
        double avgLatency = traceHistory.stream().mapToLong(TraceRecord::getLatencyMs).average().orElse(0.0);

        return Map.of(
                "totalTraces", totalTraces,
                "totalTokens", totalTokens,
                "totalPromptTokens", totalPromptTokens,
                "totalCompletionTokens", totalCompletionTokens,
                "totalCostUsd", totalCost,
                "formattedCostUsd", String.format("$%.6f", totalCost),
                "avgLatencyMs", Math.round(avgLatency),
                "maxAllowedIterations", MAX_ITERATIONS,
                "observabilityPlatform", "Langfuse / OpenTelemetry"
        );
    }
}
