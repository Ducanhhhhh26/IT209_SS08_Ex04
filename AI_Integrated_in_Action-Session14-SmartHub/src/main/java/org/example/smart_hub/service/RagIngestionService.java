package org.example.smart_hub.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagIngestionService {

    private final VectorStore vectorStore;
    private final ResourceLoader resourceLoader;

    @PostConstruct
    public void init() {
        try {
            ingestLogisticsPolicy();
        } catch (Exception e) {
            log.warn("VectorStore initial ingestion skipped or failed (will be available on-demand): {}", e.getMessage());
        }
    }

    public synchronized int ingestLogisticsPolicy() {
        log.info("Starting logistics policy document ingestion into VectorStore...");
        try {
            Resource resource = resourceLoader.getResource("classpath:docs/logistics_policy.md");
            if (!resource.exists()) {
                log.error("Logistics policy markdown file not found in classpath!");
                return 0;
            }

            StringBuilder contentBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    contentBuilder.append(line).append("\n");
                }
            }

            String fullText = contentBuilder.toString();
            List<Document> documents = parseAndChunkMarkdown(fullText);

            if (!documents.isEmpty()) {
                vectorStore.add(documents);
                log.info("Successfully ingested {} policy chunks into VectorStore (Pgvector).", documents.size());
            }

            return documents.size();
        } catch (Exception e) {
            log.error("Error during policy ingestion: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to ingest policy documents: " + e.getMessage(), e);
        }
    }

    private List<Document> parseAndChunkMarkdown(String markdown) {
        List<Document> docList = new ArrayList<>();
        // Split by sections / articles: e.g. "### Điều " or "## CHƯƠNG "
        String[] sections = markdown.split("(?=### Điều |## CHƯƠNG )");

        Pattern articlePattern = Pattern.compile("###\\s*(Điều\\s*\\d+[^\\n]*)");

        for (String section : sections) {
            String trimmed = section.trim();
            if (trimmed.isEmpty()) continue;

            String sectionTitle = "Quy định chung RikkeiExpress";
            Matcher matcher = articlePattern.matcher(trimmed);
            if (matcher.find()) {
                sectionTitle = matcher.group(1).trim();
            } else if (trimmed.startsWith("## CHƯƠNG")) {
                int firstLineEnd = trimmed.indexOf('\n');
                if (firstLineEnd > 0) {
                    sectionTitle = trimmed.substring(0, firstLineEnd).replace("#", "").trim();
                }
            }

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("documentName", "QC-LOGISTICS-2026-V1");
            metadata.put("title", "Quy chế Dịch vụ Vận chuyển & Giao nhận RikkeiExpress");
            metadata.put("section", sectionTitle);
            metadata.put("source", "logistics_policy.md");

            // Chunk if section is large (e.g. > 600 chars)
            if (trimmed.length() > 600) {
                List<String> chunks = chunkText(trimmed, 450, 80);
                for (int i = 0; i < chunks.size(); i++) {
                    Map<String, Object> chunkMeta = new HashMap<>(metadata);
                    chunkMeta.put("chunkIndex", i);
                    docList.add(new Document(chunks.get(i), chunkMeta));
                }
            } else {
                docList.add(new Document(trimmed, metadata));
            }
        }

        return docList;
    }

    private List<String> chunkText(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        int length = text.length();
        int start = 0;

        while (start < length) {
            int end = Math.min(start + chunkSize, length);
            if (end < length) {
                // Find nearest newline or punctuation
                int breakPoint = text.lastIndexOf('\n', end);
                if (breakPoint > start + 100) {
                    end = breakPoint;
                } else {
                    int dotBreak = text.lastIndexOf('.', end);
                    if (dotBreak > start + 100) {
                        end = dotBreak + 1;
                    }
                }
            }
            chunks.add(text.substring(start, end).trim());
            start = Math.max(end - overlap, start + chunkSize);
            if (start >= length) break;
        }

        return chunks;
    }
}
