package org.example.smart_hub.tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smart_hub.entity.*;
import org.example.smart_hub.repository.DeliveryRepository;
import org.example.smart_hub.repository.IncidentRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogisticsIncidentTools {

    private final DeliveryRepository deliveryRepository;
    private final IncidentRepository incidentRepository;

    public record GetDeliveryRequest(
            @JsonProperty(required = true)
            @JsonPropertyDescription("Mã vận đơn cần tra cứu, ví dụ: RK-2026-001")
            String trackingCode
    ) {}

    public record DeliveryInfoResponse(
            boolean success,
            String trackingCode,
            String customerName,
            String hubCode,
            String status,
            String codAmount,
            String message
    ) {}

    @Tool(description = "Tra cứu thông tin chi tiết đơn hàng theo mã vận đơn trước khi tạo sự cố hoặc cập nhật trạng thái.")
    public DeliveryInfoResponse getDeliveryInfo(GetDeliveryRequest request) {
        log.info("Tool getDeliveryInfo called with trackingCode: {}", request.trackingCode());
        if (request.trackingCode() == null || request.trackingCode().isBlank()) {
            return new DeliveryInfoResponse(false, null, null, null, null, null, "Mã vận đơn không được để trống.");
        }

        Optional<Delivery> deliveryOpt = deliveryRepository.findByTrackingCode(request.trackingCode().trim());
        if (deliveryOpt.isEmpty()) {
            log.warn("Delivery not found for trackingCode: {}", request.trackingCode());
            return new DeliveryInfoResponse(false, request.trackingCode(), null, null, null, null,
                    "Không tìm thấy mã đơn hàng '" + request.trackingCode() + "' trong cơ sở dữ liệu RikkeiExpress.");
        }

        Delivery delivery = deliveryOpt.get();
        return new DeliveryInfoResponse(
                true,
                delivery.getTrackingCode(),
                delivery.getCustomerName(),
                delivery.getHubCode(),
                delivery.getStatus().name(),
                delivery.getCodAmount().toString() + " VNĐ",
                "Tra cứu đơn hàng thành công."
        );
    }

    public record CreateIncidentRequest(
            @JsonProperty(required = true)
            @JsonPropertyDescription("Mã vận đơn phát sinh sự cố, ví dụ: RK-2026-001")
            String trackingCode,

            @JsonProperty(required = true)
            @JsonPropertyDescription("Loại sự cố: HONG_HOC (hàng vỡ, ướt), GIAO_TRE (giao chậm), THAT_LAC (mất hàng)")
            String incidentType,

            @JsonProperty(required = true)
            @JsonPropertyDescription("Mã bưu cục liên quan, ví dụ: HN-01, SG-02, DN-03")
            String hubCode,

            @JsonProperty(required = true)
            @JsonPropertyDescription("Mức độ nghiêm trọng: LOW, MEDIUM, CRITICAL")
            String severity,

            @JsonProperty(required = true)
            @JsonPropertyDescription("Mô tả chi tiết nội dung sự cố do khách hàng phản ánh")
            String description
    ) {}

    public record IncidentCreatedResponse(
            boolean success,
            Long incidentId,
            String trackingCode,
            String incidentType,
            String status,
            String message
    ) {}

    @Tool(description = "Tạo phiếu ghi nhận sự cố mới vào cơ sở dữ liệu khi khách hàng phản ánh hư hỏng, giao trễ hoặc thất lạc.")
    @Transactional
    public IncidentCreatedResponse createIncident(CreateIncidentRequest request) {
        log.info("Tool createIncident called for trackingCode: {}, type: {}, severity: {}",
                request.trackingCode(), request.incidentType(), request.severity());

        try {
            if (request.trackingCode() == null || request.trackingCode().isBlank()) {
                return new IncidentCreatedResponse(false, null, null, null, null, "Lỗi: Mã vận đơn không được để trống.");
            }

            // Verify delivery existence (Defensive programming)
            Optional<Delivery> deliveryOpt = deliveryRepository.findByTrackingCode(request.trackingCode().trim());
            if (deliveryOpt.isEmpty()) {
                return new IncidentCreatedResponse(false, null, request.trackingCode(), null, null,
                        "Lỗi phòng vệ: Không thể tạo sự cố vì mã vận đơn '" + request.trackingCode() + "' không tồn tại trong hệ thống.");
            }

            IncidentType type;
            try {
                String typeStr = request.incidentType().toUpperCase()
                        .replace("HỎNG_HÓC", "HONG_HOC")
                        .replace("GIAO_TRỄ", "GIAO_TRE")
                        .replace("THẤT_LẠC", "THAT_LAC");
                type = IncidentType.valueOf(typeStr);
            } catch (Exception e) {
                type = IncidentType.HONG_HOC;
            }

            Severity sev;
            try {
                sev = Severity.valueOf(request.severity().toUpperCase());
            } catch (Exception e) {
                sev = Severity.MEDIUM;
            }

            Incident incident = Incident.builder()
                    .trackingCode(request.trackingCode().trim())
                    .incidentType(type)
                    .hubCode(request.hubCode() != null && !request.hubCode().isBlank() ? request.hubCode() : deliveryOpt.get().getHubCode())
                    .severity(sev)
                    .description(request.description() != null ? request.description() : "Sự cố phát sinh qua kênh AI Agent")
                    .status(IncidentStatus.OPEN)
                    .build();

            Incident saved = incidentRepository.save(incident);
            log.info("Successfully created incident with ID: {}", saved.getId());

            return new IncidentCreatedResponse(
                    true,
                    saved.getId(),
                    saved.getTrackingCode(),
                    saved.getIncidentType().name(),
                    saved.getStatus().name(),
                    "Đã tạo phiếu sự cố thành công với mã phiếu #" + saved.getId() + " ở trạng thái OPEN."
            );
        } catch (Exception e) {
            log.error("Failed to create incident: {}", e.getMessage(), e);
            return new IncidentCreatedResponse(false, null, request.trackingCode(), null, null,
                    "Lỗi hệ thống khi tạo phiếu sự cố: " + e.getMessage());
        }
    }

    public record UpdateDeliveryStatusRequest(
            @JsonProperty(required = true)
            @JsonPropertyDescription("Mã vận đơn cần cập nhật trạng thái, ví dụ: RK-2026-001")
            String trackingCode,

            @JsonProperty(required = true)
            @JsonPropertyDescription("Trạng thái mới: IN_TRANSIT, DELIVERED, DELAYED, DAMAGED")
            String newStatus
    ) {}

    public record UpdateDeliveryStatusResponse(
            boolean success,
            String trackingCode,
            String oldStatus,
            String newStatus,
            String message
    ) {}

    @Tool(description = "Cập nhật trạng thái đơn hàng sang DAMAGED (khi bị hỏng hóc) hoặc DELAYED (khi bị chậm trễ) trong cơ sở dữ liệu.")
    @Transactional
    public UpdateDeliveryStatusResponse updateDeliveryStatus(UpdateDeliveryStatusRequest request) {
        log.info("Tool updateDeliveryStatus called for trackingCode: {} -> {}", request.trackingCode(), request.newStatus());

        try {
            if (request.trackingCode() == null || request.trackingCode().isBlank()) {
                return new UpdateDeliveryStatusResponse(false, null, null, null, "Mã vận đơn không được để trống.");
            }

            Optional<Delivery> deliveryOpt = deliveryRepository.findByTrackingCode(request.trackingCode().trim());
            if (deliveryOpt.isEmpty()) {
                return new UpdateDeliveryStatusResponse(false, request.trackingCode(), null, null,
                        "Lỗi phòng vệ: Không tìm thấy mã đơn hàng '" + request.trackingCode() + "' để cập nhật trạng thái.");
            }

            Delivery delivery = deliveryOpt.get();
            String oldStatus = delivery.getStatus().name();

            DeliveryStatus status;
            try {
                status = DeliveryStatus.valueOf(request.newStatus().toUpperCase());
            } catch (Exception e) {
                return new UpdateDeliveryStatusResponse(false, request.trackingCode(), oldStatus, null,
                        "Trạng thái không hợp lệ: '" + request.newStatus() + "'. Các trạng thái hợp lệ: IN_TRANSIT, DELIVERED, DELAYED, DAMAGED.");
            }

            delivery.setStatus(status);
            deliveryRepository.save(delivery);
            log.info("Updated delivery {} status from {} to {}", delivery.getTrackingCode(), oldStatus, status);

            return new UpdateDeliveryStatusResponse(
                    true,
                    delivery.getTrackingCode(),
                    oldStatus,
                    status.name(),
                    "Đã cập nhật trạng thái đơn hàng " + delivery.getTrackingCode() + " từ " + oldStatus + " sang " + status.name() + " thành công."
            );
        } catch (Exception e) {
            log.error("Failed to update delivery status: {}", e.getMessage(), e);
            return new UpdateDeliveryStatusResponse(false, request.trackingCode(), null, null,
                    "Lỗi cập nhật trạng thái đơn hàng: " + e.getMessage());
        }
    }
}
