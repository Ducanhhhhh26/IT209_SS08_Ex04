package org.example.smart_hub.observability;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ObservabilityServiceTest {

    private ObservabilityService observabilityService;

    @BeforeEach
    void setUp() {
        observabilityService = new ObservabilityService();
    }

    @Test
    @DisplayName("Ghi nhận Trace thành công và tính toán chính xác tổng Tokens và Chi phí USD")
    void testRecordTraceAndCalculateCost() {
        TraceRecord trace = TraceRecord.builder()
                .traceId("tr-001")
                .name("Test Trace")
                .module("Module 1: RAG Q&A")
                .startTime(LocalDateTime.now().minusSeconds(1))
                .endTime(LocalDateTime.now())
                .promptTokens(1000)
                .completionTokens(500)
                .status("SUCCESS")
                .build();

        observabilityService.recordTrace(trace);

        assertEquals(1, observabilityService.getRecentTraces().size());
        TraceRecord recorded = observabilityService.getRecentTraces().get(0);
        assertEquals(1500, recorded.getTotalTokens());
        assertTrue(recorded.getEstimatedCostUsd() > 0);

        Map<String, Object> summary = observabilityService.getObservabilitySummary();
        assertEquals(1, summary.get("totalTraces"));
        assertEquals(1500L, summary.get("totalTokens"));
    }

    @Test
    @DisplayName("Cơ chế Infinite Loop Guard tự động ngắt và ném ngoại lệ khi vượt quá 6 lượt gọi")
    void testInfiniteLoopGuardTriggered() {
        String sessionId = "test-session-loop";

        for (int i = 1; i <= 6; i++) {
            assertDoesNotThrow(() -> observabilityService.checkAndIncrementLoopGuard(sessionId));
        }

        // 7th call must trigger the loop guard exception
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                observabilityService.checkAndIncrementLoopGuard(sessionId)
        );

        assertTrue(ex.getMessage().contains("max-iterations <= 6"));
    }
}
