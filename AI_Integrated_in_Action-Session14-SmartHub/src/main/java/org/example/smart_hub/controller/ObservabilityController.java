package org.example.smart_hub.controller;

import lombok.RequiredArgsConstructor;
import org.example.smart_hub.observability.ObservabilityService;
import org.example.smart_hub.observability.TraceRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/observability")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ObservabilityController {

    private final ObservabilityService observabilityService;

    @GetMapping("/traces")
    public ResponseEntity<List<TraceRecord>> getRecentTraces() {
        return ResponseEntity.ok(observabilityService.getRecentTraces());
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        return ResponseEntity.ok(observabilityService.getObservabilitySummary());
    }
}
