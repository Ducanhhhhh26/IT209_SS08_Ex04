package org.example.smart_hub.controller;

import lombok.RequiredArgsConstructor;
import org.example.smart_hub.dto.RagResponseDto;
import org.example.smart_hub.service.RagIngestionService;
import org.example.smart_hub.service.RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/rag")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RagController {

    private final RagService ragService;
    private final RagIngestionService ragIngestionService;

    @GetMapping("/ask")
    public ResponseEntity<RagResponseDto> askQuestion(@RequestParam("question") String question) {
        RagResponseDto response = ragService.askQuestion(question);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ingest")
    public ResponseEntity<Map<String, Object>> triggerIngest() {
        int chunksCount = ragIngestionService.ingestLogisticsPolicy();
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Logistics policy ingested successfully",
                "totalChunks", chunksCount
        ));
    }
}
