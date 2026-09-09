package org.example.smart_hub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smart_hub.dto.OperationsChatResponse;
import org.example.smart_hub.observability.ObservabilityService;
import org.example.smart_hub.observability.TraceRecord;
import org.example.smart_hub.repository.IncidentRepository;
import org.example.smart_hub.tool.LogisticsIncidentTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationsAgentService {

    private final ChatClient.Builder chatClientBuilder;
    private final LogisticsIncidentTools logisticsIncidentTools;
    private final IncidentRepository incidentRepository;
    private final ObservabilityService observabilityService;

    private static final String OPERATIONS_AGENT_SYSTEM = """
            Bạn là Trợ lý Điều phối Vận hành & Sự cố Thông minh của RikkeiExpress (SmartHub Ops Agent).
            Nhiệm vụ của bạn là tiếp nhận phản ánh của khách hàng qua ngôn ngữ tự nhiên, bóc tách thực thể và tự động gọi các công cụ (Tools) để ghi nhận sự cố và cập nhật đơn hàng.

            QUY TRÌNH XỬ LÝ (AUTONOMOUS WORKFLOW):
            1. BÓC TÁCH THỰC THỂ TỪ TIN NHẮN:
               - Mã vận đơn: định dạng RK-YYYY-XXX (ví dụ: RK-2026-001)
               - Loại sự cố: HONG_HOC (hàng ướt, vỡ nát, móp méo), GIAO_TRE (chậm trễ, giao muộn), THAT_LAC (không nhận được, mất hàng)
               - Bưu cục/Kho: HN-01 (Hà Nội), SG-02 (Sài Gòn/TPHCM), DN-03 (Đà Nẵng), v.v. Nếu khách hàng chỉ nói 'Hà Nội' hãy ánh xạ sang HN-01, 'Sài Gòn' sang SG-02, 'Đà Nẵng' sang DN-03.
               - Mức độ nghiêm trọng: CRITICAL (hỏng nặng, mất toàn bộ, ướt sũng hàng giá trị cao), MEDIUM (móp méo, trễ trên 24h), LOW (chậm nhẹ, hộp xước nhẹ)
            
            2. THỰC THI CÔNG CỤ (FUNCTION CALLING):
               - Bước 1: Gọi 'getDeliveryInfo' để kiểm tra xem đơn hàng có tồn tại trong hệ thống hay không.
               - Bước 2: Nếu đơn hàng tồn tại:
                 + Gọi 'createIncident' để tạo phiếu sự cố mới.
                 + Gọi 'updateDeliveryStatus' để cập nhật trạng thái đơn hàng sang DAMAGED (nếu hỏng) hoặc DELAYED (nếu giao trễ).
               - Bước 3: Nếu đơn hàng KHÔNG tồn tại:
                 + Tuyệt đối KHÔNG làm sập hệ thống, hãy phản hồi nhẹ nhàng và thông báo cho khách hàng kiểm tra lại mã vận đơn.

            3. PHẢN HỒI CHO KHÁCH HÀNG:
               - Luôn tóm tắt rõ: Mã đơn hàng, loại sự cố ghi nhận, mã phiếu sự cố (nếu đã tạo), trạng thái mới của đơn hàng và cam kết thời gian bồi thường/xử lý theo quy chế RikkeiExpress (tối đa 2 ngày làm việc).
            """;

    public OperationsChatResponse processCustomerMessage(String userMessage) {
        LocalDateTime startTime = LocalDateTime.now();
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        List<TraceRecord.SpanRecord> spans = new ArrayList<>();
        log.info("Processing operations agent message [{}] : {}", traceId, userMessage);

        // Loop guard check
        String sessionId = "ops-session-" + traceId;
        observabilityService.checkAndIncrementLoopGuard(sessionId);

        long incidentCountBefore = incidentRepository.count();

        // Build ChatClient instance with Tools registered
        ChatClient opsChatClient = chatClientBuilder
                .defaultSystem(OPERATIONS_AGENT_SYSTEM)
                .defaultTools(logisticsIncidentTools)
                .build();

        String responseContent;
        List<String> actionsExecuted = new ArrayList<>();
        boolean success = true;

        long agentStart = System.currentTimeMillis();
        try {
            responseContent = opsChatClient.prompt()
                    .user(userMessage)
                    .call()
                    .content();
            log.info("Agent response generated successfully.");
            spans.add(TraceRecord.SpanRecord.builder()
                    .spanId(UUID.randomUUID().toString().substring(0, 8))
                    .name("Operations Agent Function Execution")
                    .type("TOOL_EXECUTION")
                    .latencyMs(System.currentTimeMillis() - agentStart)
                    .status("SUCCESS")
                    .details("Executed function calling loop")
                    .build());
        } catch (Exception e) {
            log.error("Error executing operations agent: {}", e.getMessage(), e);
            responseContent = "Hệ thống SmartHub ghi nhận yêu cầu nhưng gặp sự cố trong quá trình kết nối công cụ: " + e.getMessage();
            success = false;
            spans.add(TraceRecord.SpanRecord.builder()
                    .spanId(UUID.randomUUID().toString().substring(0, 8))
                    .name("Operations Agent Execution")
                    .type("TOOL_EXECUTION")
                    .latencyMs(System.currentTimeMillis() - agentStart)
                    .status("ERROR")
                    .details(e.getMessage())
                    .build());
        } finally {
            observabilityService.resetLoopGuard(sessionId);
        }

        // Extract tracking code using regex
        Pattern trackingPattern = Pattern.compile("RK-\\d{4}-\\d{3}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = trackingPattern.matcher(userMessage);
        String trackingCode = matcher.find() ? matcher.group().toUpperCase() : null;

        // Check if an incident was created
        long incidentCountAfter = incidentRepository.count();
        if (incidentCountAfter > incidentCountBefore) {
            actionsExecuted.add("Tạo mới phiếu sự cố trong bảng incidents");
            actionsExecuted.add("Cập nhật trạng thái đơn hàng trong bảng deliveries");
        } else if (trackingCode != null) {
            actionsExecuted.add("Tra cứu thông tin vận đơn " + trackingCode);
        }

        // Infer extracted entities for response visualization
        String incidentType = inferIncidentType(userMessage);
        String hubCode = inferHubCode(userMessage);
        String severity = inferSeverity(userMessage);

        int promptTokens = (OPERATIONS_AGENT_SYSTEM.length() + userMessage.length()) / 4;
        int compTokens = (responseContent != null ? responseContent.length() : 0) / 4;

        observabilityService.recordTrace(TraceRecord.builder()
                .traceId(traceId)
                .name("Operations Incident Agent")
                .module("Module 2: Agent Ops")
                .startTime(startTime)
                .endTime(LocalDateTime.now())
                .promptTokens(promptTokens)
                .completionTokens(compTokens)
                .status(success ? "SUCCESS" : "ERROR")
                .inputSummary(userMessage)
                .outputSummary(responseContent != null && responseContent.length() > 100 ? responseContent.substring(0, 100) + "..." : responseContent)
                .spans(spans)
                .build());

        OperationsChatResponse.ExtractedEntitiesDto entitiesDto = OperationsChatResponse.ExtractedEntitiesDto.builder()
                .trackingCode(trackingCode)
                .incidentType(incidentType)
                .hubCode(hubCode)
                .severity(severity)
                .build();

        return OperationsChatResponse.builder()
                .userMessage(userMessage)
                .agentResponse(responseContent)
                .extractedEntities(entitiesDto)
                .actionsExecuted(actionsExecuted)
                .success(success)
                .build();
    }

    private String inferIncidentType(String msg) {
        String lower = msg.toLowerCase();
        if (lower.contains("ướt") || lower.contains("hỏng") || lower.contains("vỡ") || lower.contains("móp")) return "HONG_HOC";
        if (lower.contains("trễ") || lower.contains("chậm") || lower.contains("muộn")) return "GIAO_TRE";
        if (lower.contains("mất") || lower.contains("thất lạc") || lower.contains("không thấy")) return "THAT_LAC";
        return "CHƯA_XÁC_ĐỊNH";
    }

    private String inferHubCode(String msg) {
        String lower = msg.toLowerCase();
        if (lower.contains("hà nội") || lower.contains("hn")) return "HN-01";
        if (lower.contains("sài gòn") || lower.contains("hồ chí minh") || lower.contains("tphcm") || lower.contains("sg")) return "SG-02";
        if (lower.contains("đà nẵng") || lower.contains("dn")) return "DN-03";
        return "UNKNOWN";
    }

    private String inferSeverity(String msg) {
        String lower = msg.toLowerCase();
        if (lower.contains("ướt sũng") || lower.contains("vỡ nát") || lower.contains("nghiêm trọng") || lower.contains("khẩn cấp")) return "CRITICAL";
        if (lower.contains("trễ") || lower.contains("móp méo")) return "MEDIUM";
        return "LOW";
    }
}
