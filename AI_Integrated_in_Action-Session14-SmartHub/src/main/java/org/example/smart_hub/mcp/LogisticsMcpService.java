package org.example.smart_hub.mcp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smart_hub.entity.DeliveryStatus;
import org.example.smart_hub.entity.Incident;
import org.example.smart_hub.observability.ObservabilityService;
import org.example.smart_hub.observability.TraceRecord;
import org.example.smart_hub.repository.DeliveryRepository;
import org.example.smart_hub.repository.IncidentRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogisticsMcpService {

    private final JdbcTemplate jdbcTemplate;
    private final SafeSqlValidator safeSqlValidator;
    private final DeliveryRepository deliveryRepository;
    private final IncidentRepository incidentRepository;
    private final ChatClient chatClient;
    private final ObservabilityService observabilityService;

    public Map<String, Object> executeSafeSqlQuery(String sql) {
        log.info("Evaluating SQL query through SafeSqlValidator: {}", sql);
        SafeSqlValidator.SqlValidationResult validation = safeSqlValidator.validateAndSanitize(sql);

        if (!validation.isSafe()) {
            return Map.of(
                    "success", false,
                    "error", validation.errorMessage(),
                    "executedQuery", sql
            );
        }

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(validation.sanitizedQuery());
            return Map.of(
                    "success", true,
                    "sanitizedQuery", validation.sanitizedQuery(),
                    "rowCount", rows.size(),
                    "data", rows
            );
        } catch (Exception e) {
            log.error("SQL execution error: {}", e.getMessage(), e);
            return Map.of(
                    "success", false,
                    "error", "Lỗi thực thi CSDL: " + e.getMessage(),
                    "sanitizedQuery", validation.sanitizedQuery()
            );
        }
    }

    public String generateHubPerformanceReport(String hubCode) {
        LocalDateTime startTime = LocalDateTime.now();
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        log.info("Generating MCP Markdown Performance Report [{}] for Hub: {}", traceId, hubCode);
        String targetHub = (hubCode != null && !hubCode.isBlank()) ? hubCode.toUpperCase().trim() : "HN-01";

        long totalDeliveries = deliveryRepository.countByHubCode(targetHub);
        long delivered = deliveryRepository.countByHubCodeAndStatus(targetHub, DeliveryStatus.DELIVERED);
        long inTransit = deliveryRepository.countByHubCodeAndStatus(targetHub, DeliveryStatus.IN_TRANSIT);
        long delayed = deliveryRepository.countByHubCodeAndStatus(targetHub, DeliveryStatus.DELAYED);
        long damaged = deliveryRepository.countByHubCodeAndStatus(targetHub, DeliveryStatus.DAMAGED);

        List<Incident> hubIncidents = incidentRepository.findByHubCode(targetHub);

        double onTimeRate = totalDeliveries > 0 ? ((double) delivered / totalDeliveries) * 100 : 100.0;
        double delayRate = totalDeliveries > 0 ? ((double) delayed / totalDeliveries) * 100 : 0.0;
        double damageRate = totalDeliveries > 0 ? ((double) damaged / totalDeliveries) * 100 : 0.0;

        String healthStatus = (damageRate > 10.0 || delayRate > 20.0) ? "CẢNH BÁO (WARNING)" : "HOẠT ĐỘNG TỐT (HEALTHY)";

        StringBuilder sb = new StringBuilder();
        sb.append("# 📊 BÁO CÁO HIỆU SUẤT VẬN HÀNH BƯU CỤC ").append(targetHub).append("\n\n");
        sb.append("**Thời điểm xuất báo cáo:** ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("**Trạng thái bưu cục:** `").append(healthStatus).append("`\n\n");

        sb.append("## 1. Thống Kê Tổng Quan Đơn Hàng\n\n");
        sb.append("| Chỉ Số Vận Hành | Số Lượng | Tỷ Lệ (%) |\n");
        sb.append("| :--- | :--- | :--- |\n");
        sb.append(String.format("| **Tổng đơn quản lý** | **%d** | **100%%** |\n", totalDeliveries));
        sb.append(String.format("| Đã phát thành công (DELIVERED) | %d | %.1f%% |\n", delivered, onTimeRate));
        sb.append(String.format("| Đang luân chuyển (IN_TRANSIT) | %d | %.1f%% |\n", inTransit, totalDeliveries > 0 ? (double) inTransit / totalDeliveries * 100 : 0));
        sb.append(String.format("| Đơn bị trễ hạn (DELAYED) | %d | %.1f%% |\n", delayed, delayRate));
        sb.append(String.format("| Đơn gặp sự cố hư hỏng (DAMAGED) | %d | %.1f%% |\n\n", damaged, damageRate));

        sb.append("## 2. Chi Tiết Sự Cố Phát Sinh Tại Bưu Cục\n\n");
        if (hubIncidents.isEmpty()) {
            sb.append("✅ Không ghi nhận phiếu sự cố nào đang mở tại bưu cục ").append(targetHub).append(".\n\n");
        } else {
            sb.append("| Mã Phiếu | Mã Đơn | Phân Loại | Mức Độ | Trạng Thái | Mô Tả |\n");
            sb.append("| :--- | :--- | :--- | :--- | :--- | :--- |\n");
            for (Incident inc : hubIncidents) {
                sb.append(String.format("| #%d | `%s` | %s | **%s** | `%s` | %s |\n",
                        inc.getId(),
                        inc.getTrackingCode(),
                        inc.getIncidentType().name(),
                        inc.getSeverity().name(),
                        inc.getStatus().name(),
                        inc.getDescription()
                ));
            }
            sb.append("\n");
        }

        sb.append("## 3. Khuyến Nghị Vận Hành Từ AI SmartHub\n\n");
        if (delayed > 0) {
            sb.append("- ⚠️ Cần điều phối bổ sung shipper tuyến giao cho bưu cục ").append(targetHub).append(" để giải phóng các đơn `DELAYED`.\n");
        }
        if (damaged > 0) {
            sb.append("- ⚠️ Rà soát quy trình bốc dỡ hàng tại kho ").append(targetHub).append(" nhằm giảm thiểu tỷ lệ bưu gửi bị `DAMAGED`.\n");
        }
        if (delayed == 0 && damaged == 0) {
            sb.append("- 🌟 Bưu cục đang duy trì hiệu suất vận hành xuất sắc, đạt chuẩn SLA RikkeiExpress.\n");
        }

        String report = sb.toString();

        observabilityService.recordTrace(TraceRecord.builder()
                .traceId(traceId)
                .name("MCP Hub Performance Report")
                .module("Module 3: MCP Analytics")
                .startTime(startTime)
                .endTime(LocalDateTime.now())
                .promptTokens(50)
                .completionTokens(report.length() / 4)
                .status("SUCCESS")
                .inputSummary("hubCode: " + targetHub)
                .outputSummary("Generated Markdown Report for Hub " + targetHub)
                .spans(List.of(
                        TraceRecord.SpanRecord.builder()
                                .spanId(UUID.randomUUID().toString().substring(0, 8))
                                .name("Hub Aggregations Query")
                                .type("DB_QUERY")
                                .latencyMs(15)
                                .status("SUCCESS")
                                .details("Queried deliveries & incidents for hub " + targetHub)
                                .build()
                ))
                .build());

        return report;
    }

    public Map<String, Object> analyzeNaturalLanguageQuery(String query) {
        LocalDateTime startTime = LocalDateTime.now();
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        List<TraceRecord.SpanRecord> spans = new ArrayList<>();
        log.info("Analyzing natural language analytics query [{}] via MCP Protocol: {}", traceId, query);

        String systemPrompt = """
                Bạn là Chuyên gia Đối soát & Phân tích Dữ liệu Logistics thuộc hệ thống SmartHub MCP.
                CSDL PostgreSQL có các bảng sau:
                1. `deliveries` (id, tracking_code, customer_name, hub_code, status, cod_amount, created_at)
                2. `incidents` (id, tracking_code, incident_type, hub_code, severity, description, status, created_at)
                
                Nhiệm vụ:
                - Hãy sinh câu truy vấn SQL SELECT an toàn, chuẩn xác để trả lời câu hỏi của người dùng.
                - KHÔNG dùng dấu ngoặc nhọn, chỉ trả về duy nhất câu lệnh SQL SELECT thuần túy (không kèm giải thích dài dòng).
                """;

        long sqlGenStart = System.currentTimeMillis();
        String rawSql;
        try {
            rawSql = chatClient.prompt()
                    .system(systemPrompt)
                    .user("Câu hỏi: " + query)
                    .call()
                    .content();
            
            // Clean markdown code fence if present
            rawSql = rawSql.replaceAll("```sql", "").replaceAll("```", "").trim();
            spans.add(TraceRecord.SpanRecord.builder()
                    .spanId(UUID.randomUUID().toString().substring(0, 8))
                    .name("MCP Text-to-SQL Conversion")
                    .type("LLM_CALL")
                    .latencyMs(System.currentTimeMillis() - sqlGenStart)
                    .status("SUCCESS")
                    .details("Generated SQL: " + rawSql)
                    .build());
        } catch (Exception e) {
            rawSql = "SELECT hub_code, status, count(*) as count FROM deliveries GROUP BY hub_code, status";
        }

        long dbQueryStart = System.currentTimeMillis();
        Map<String, Object> queryResult = executeSafeSqlQuery(rawSql);
        spans.add(TraceRecord.SpanRecord.builder()
                .spanId(UUID.randomUUID().toString().substring(0, 8))
                .name("Safe SQL Database Query")
                .type("DB_QUERY")
                .latencyMs(System.currentTimeMillis() - dbQueryStart)
                .status((Boolean) queryResult.get("success") ? "SUCCESS" : "ERROR")
                .details("Sanitized Query: " + queryResult.get("sanitizedQuery"))
                .build());

        // Generate report summary
        String summaryPrompt = String.format("""
                Dưới đây là câu hỏi của người dùng: %s
                Câu lệnh SQL đã chạy: %s
                Kết quả dữ liệu trả về: %s
                
                Hãy tổng hợp và đưa ra báo cáo ngắn gọn bằng Markdown tiếng Việt chuyên nghiệp.
                """, query, rawSql, queryResult.get("data"));

        long reportGenStart = System.currentTimeMillis();
        String finalReport;
        try {
            finalReport = chatClient.prompt()
                    .user(summaryPrompt)
                    .call()
                    .content();
            spans.add(TraceRecord.SpanRecord.builder()
                    .spanId(UUID.randomUUID().toString().substring(0, 8))
                    .name("MCP Report Synthesis")
                    .type("LLM_CALL")
                    .latencyMs(System.currentTimeMillis() - reportGenStart)
                    .status("SUCCESS")
                    .details("Synthesized final Markdown analytics report")
                    .build());
        } catch (Exception e) {
            finalReport = "Đã thực thi truy vấn thành công với " + queryResult.get("rowCount") + " bản ghi.";
        }

        int promptTokens = (systemPrompt.length() + query.length() + summaryPrompt.length()) / 4;
        int compTokens = (rawSql.length() + finalReport.length()) / 4;

        observabilityService.recordTrace(TraceRecord.builder()
                .traceId(traceId)
                .name("MCP Natural Language Analytics")
                .module("Module 3: MCP Analytics")
                .startTime(startTime)
                .endTime(LocalDateTime.now())
                .promptTokens(promptTokens)
                .completionTokens(compTokens)
                .status("SUCCESS")
                .inputSummary(query)
                .outputSummary(finalReport.length() > 100 ? finalReport.substring(0, 100) + "..." : finalReport)
                .spans(spans)
                .build());

        Map<String, Object> response = new HashMap<>();
        response.put("query", query);
        response.put("generatedSql", rawSql);
        response.put("sqlExecution", queryResult);
        response.put("markdownReport", finalReport);

        return response;
    }
}
