package org.example.smart_hub.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smart_hub.entity.Delivery;
import org.example.smart_hub.entity.DeliveryStatus;
import org.example.smart_hub.entity.Incident;
import org.example.smart_hub.entity.IncidentStatus;
import org.example.smart_hub.entity.IncidentType;
import org.example.smart_hub.entity.Severity;
import org.example.smart_hub.repository.DeliveryRepository;
import org.example.smart_hub.repository.IncidentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DeliveryRepository deliveryRepository;
    private final IncidentRepository incidentRepository;

    @Override
    public void run(String... args) {
        if (deliveryRepository.count() == 0) {
            log.info("Populating initial delivery seed data...");
            List<Delivery> sampleDeliveries = List.of(
                    Delivery.builder()
                            .trackingCode("RK-2026-001")
                            .customerName("Nguyễn Văn An")
                            .hubCode("HN-01")
                            .status(DeliveryStatus.IN_TRANSIT)
                            .codAmount(new BigDecimal("1500000.00"))
                            .build(),
                    Delivery.builder()
                            .trackingCode("RK-2026-002")
                            .customerName("Trần Thị Bình")
                            .hubCode("HN-01")
                            .status(DeliveryStatus.DELIVERED)
                            .codAmount(new BigDecimal("320000.00"))
                            .build(),
                    Delivery.builder()
                            .trackingCode("RK-2026-003")
                            .customerName("Lê Hoàng Cường")
                            .hubCode("SG-02")
                            .status(DeliveryStatus.DELAYED)
                            .codAmount(new BigDecimal("850000.00"))
                            .build(),
                    Delivery.builder()
                            .trackingCode("RK-2026-004")
                            .customerName("Phạm Minh Đức")
                            .hubCode("SG-02")
                            .status(DeliveryStatus.IN_TRANSIT)
                            .codAmount(new BigDecimal("2100000.00"))
                            .build(),
                    Delivery.builder()
                            .trackingCode("RK-2026-005")
                            .customerName("Võ Thu Dung")
                            .hubCode("DN-03")
                            .status(DeliveryStatus.IN_TRANSIT)
                            .codAmount(new BigDecimal("450000.00"))
                            .build(),
                    Delivery.builder()
                            .trackingCode("RK-2026-006")
                            .customerName("Hoàng Thanh Giang")
                            .hubCode("HN-01")
                            .status(DeliveryStatus.DAMAGED)
                            .codAmount(new BigDecimal("1200000.00"))
                            .build()
            );
            deliveryRepository.saveAll(sampleDeliveries);
            log.info("Saved {} sample deliveries.", sampleDeliveries.size());
        }

        if (incidentRepository.count() == 0) {
            log.info("Populating initial incident seed data...");
            List<Incident> sampleIncidents = List.of(
                    Incident.builder()
                            .trackingCode("RK-2026-006")
                            .incidentType(IncidentType.HONG_HOC)
                            .hubCode("HN-01")
                            .severity(Severity.MEDIUM)
                            .description("Bưu kiện bị móp méo hộp ngoài trong quá trình trung chuyển tại kho Hà Nội.")
                            .status(IncidentStatus.OPEN)
                            .build()
            );
            incidentRepository.saveAll(sampleIncidents);
            log.info("Saved initial incident.");
        }
    }
}
