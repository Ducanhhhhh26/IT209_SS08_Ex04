package org.example.smart_hub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smart_hub.dto.RagResponseDto;
import org.example.smart_hub.dto.SourceDocumentDto;
import org.example.smart_hub.observability.ObservabilityService;
import org.example.smart_hub.observability.TraceRecord;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final ObservabilityService observabilityService;

    private static final String RAG_SYSTEM_PROMPT = """
            Bạn là Trợ lý Tra cứu Quy chế Thông minh của Trung tâm Vận hành Logistics RikkeiExpress (SmartHub).
            
            NGUYÊN TẮC BẮT BUỘC (ANTI-HALLUCINATION & CITATION POLICY):
            1. Bạn CHỈ ĐƯỢC PHÉP trả lời dựa trên thông tin có trong phần [NGỮ CẢNH TÀI LIỆU QUY CHẾ] dưới đây.
            2. BẮT BUỘC trích dẫn rõ tên tài liệu (QC-LOGISTICS-2026-V1) và số Điều/Khoản tương ứng (ví dụ: "Theo Điều 8, Khoản 2 Quy chế...").
            3. Nếu thông tin không có trong ngữ cảnh hoặc không đủ cơ sở để trả lời, bạn PHẢI từ chối lịch sự với nội dung:
               "RikkeiExpress xin thông báo: Tôi không tìm thấy thông tin liên quan đến vấn đề này trong tài liệu quy chế vận chuyển hiện hành. Quý khách vui lòng liên hệ tổng đài CSKH hoặc bưu cục gần nhất để được hỗ trợ chi tiết."
            4. Tuyệt đối KHÔNG tự suy diễn, bịa đặt thêm điều khoản hoặc số liệu ngoài tài liệu đã cấp.
            """;

    public RagResponseDto askQuestion(String question) {
        LocalDateTime startTime = LocalDateTime.now();
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        List<TraceRecord.SpanRecord> spans = new ArrayList<>();
        log.info("Processing RAG question [{}] : {}", traceId, question);

        long retrievalStart = System.currentTimeMillis();
        List<Document> similarDocuments = new ArrayList<>();
        try {
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(question)
                    .topK(4)
                    .similarityThreshold(0.40)
                    .build();
            similarDocuments = vectorStore.similaritySearch(searchRequest);
            spans.add(TraceRecord.SpanRecord.builder()
                    .spanId(UUID.randomUUID().toString().substring(0, 8))
                    .name("Pgvector Similarity Retrieval")
                    .type("RAG_RETRIEVAL")
                    .latencyMs(System.currentTimeMillis() - retrievalStart)
                    .status("SUCCESS")
                    .details("Retrieved " + (similarDocuments != null ? similarDocuments.size() : 0) + " chunks")
                    .build());
        } catch (Exception e) {
            log.error("VectorStore search failed: {}", e.getMessage(), e);
            spans.add(TraceRecord.SpanRecord.builder()
                    .spanId(UUID.randomUUID().toString().substring(0, 8))
                    .name("Pgvector Similarity Retrieval")
                    .type("RAG_RETRIEVAL")
                    .latencyMs(System.currentTimeMillis() - retrievalStart)
                    .status("ERROR")
                    .details(e.getMessage())
                    .build());
        }

        // Check if relevant context was retrieved
        if (similarDocuments == null || similarDocuments.isEmpty()) {
            log.info("No matching policy documents found for question: {}", question);
            String refuseAnswer = "RikkeiExpress xin thông báo: Tôi không tìm thấy thông tin liên quan đến vấn đề này trong tài liệu quy chế vận chuyển hiện hành. Quý khách vui lòng liên hệ tổng đài CSKH hoặc bưu cục gần nhất để được hỗ trợ chi tiết.";
            
            observabilityService.recordTrace(TraceRecord.builder()
                    .traceId(traceId)
                    .name("RAG Policy Q&A (No Match)")
                    .module("Module 1: RAG Q&A")
                    .startTime(startTime)
                    .endTime(LocalDateTime.now())
                    .promptTokens(question.length() / 4)
                    .completionTokens(refuseAnswer.length() / 4)
                    .status("NO_CONTEXT_REFUSAL")
                    .inputSummary(question)
                    .outputSummary(refuseAnswer)
                    .spans(spans)
                    .build());

            return RagResponseDto.builder()
                    .question(question)
                    .answer(refuseAnswer)
                    .sourceDocuments(List.of())
                    .foundInKnowledgeBase(false)
                    .build();
        }

        // Build context from retrieved chunks
        String contextText = similarDocuments.stream()
                .map(doc -> String.format("--- Đoạn trích từ [%s - %s] ---\n%s",
                        doc.getMetadata().getOrDefault("documentName", "QC-LOGISTICS-2026-V1"),
                        doc.getMetadata().getOrDefault("section", "Quy chế"),
                        doc.getText()))
                .collect(Collectors.joining("\n\n"));

        List<SourceDocumentDto> sourceDtos = similarDocuments.stream()
                .map(doc -> SourceDocumentDto.builder()
                        .documentName((String) doc.getMetadata().getOrDefault("documentName", "QC-LOGISTICS-2026-V1"))
                        .section((String) doc.getMetadata().getOrDefault("section", "Quy định"))
                        .contentSnippet(doc.getText().length() > 200 ? doc.getText().substring(0, 200) + "..." : doc.getText())
                        .score(doc.getScore())
                        .build())
                .collect(Collectors.toList());

        // Generate answer with LLM using ChatClient
        String promptUser = String.format("""
                [NGỮ CẢNH TÀI LIỆU QUY CHẾ]
                %s
                
                [CÂU HỎI CỦA NGƯỜI DÙNG]
                %s
                
                Hãy trả lời câu hỏi trên thật rõ ràng, chính xác và có trích dẫn điều khoản cụ thể.
                """, contextText, question);

        long llmStart = System.currentTimeMillis();
        String answer;
        try {
            answer = chatClient.prompt()
                    .system(RAG_SYSTEM_PROMPT)
                    .user(promptUser)
                    .call()
                    .content();
            spans.add(TraceRecord.SpanRecord.builder()
                    .spanId(UUID.randomUUID().toString().substring(0, 8))
                    .name("LLM Generation with Citation")
                    .type("LLM_CALL")
                    .latencyMs(System.currentTimeMillis() - llmStart)
                    .status("SUCCESS")
                    .details("Generated answer with policy citation")
                    .build());
        } catch (Exception e) {
            log.error("LLM Call error in RAG: {}", e.getMessage(), e);
            answer = "RikkeiExpress xin lỗi, kết nối tới hệ thống AI đang gián đoạn: " + e.getMessage();
        }

        int promptTokens = (contextText.length() + question.length() + RAG_SYSTEM_PROMPT.length()) / 4;
        int compTokens = (answer != null ? answer.length() : 0) / 4;

        observabilityService.recordTrace(TraceRecord.builder()
                .traceId(traceId)
                .name("RAG Policy Q&A")
                .module("Module 1: RAG Q&A")
                .startTime(startTime)
                .endTime(LocalDateTime.now())
                .promptTokens(promptTokens)
                .completionTokens(compTokens)
                .status("SUCCESS")
                .inputSummary(question)
                .outputSummary(answer != null && answer.length() > 100 ? answer.substring(0, 100) + "..." : answer)
                .spans(spans)
                .build());

        return RagResponseDto.builder()
                .question(question)
                .answer(answer)
                .sourceDocuments(sourceDtos)
                .foundInKnowledgeBase(true)
                .build();
    }
}
