package org.example.smart_hub.controller;

import lombok.RequiredArgsConstructor;
import org.example.smart_hub.mcp.LogisticsMcpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final LogisticsMcpService logisticsMcpService;

    @GetMapping("/hub-report")
    public ResponseEntity<Map<String, Object>> getHubReport(@RequestParam(value = "hubCode", defaultValue = "HN-01") String hubCode) {
        String markdownReport = logisticsMcpService.generateHubPerformanceReport(hubCode);
        return ResponseEntity.ok(Map.of(
                "hubCode", hubCode,
                "reportMarkdown", markdownReport
        ));
    }

    @PostMapping("/safe-sql")
    public ResponseEntity<Map<String, Object>> executeSafeSql(@RequestBody Map<String, String> request) {
        String sql = request.get("sql");
        Map<String, Object> result = logisticsMcpService.executeSafeSqlQuery(sql);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> naturalLanguageAnalytics(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        Map<String, Object> result = logisticsMcpService.analyzeNaturalLanguageQuery(query);
        return ResponseEntity.ok(result);
    }
}
