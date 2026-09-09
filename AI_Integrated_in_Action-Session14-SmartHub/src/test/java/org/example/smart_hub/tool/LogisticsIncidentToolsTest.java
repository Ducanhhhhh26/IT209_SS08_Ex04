package org.example.smart_hub.tool;

import org.example.smart_hub.entity.Delivery;
import org.example.smart_hub.entity.DeliveryStatus;
import org.example.smart_hub.entity.Incident;
import org.example.smart_hub.repository.DeliveryRepository;
import org.example.smart_hub.repository.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogisticsIncidentToolsTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private LogisticsIncidentTools incidentTools;

    private Delivery sampleDelivery;

    @BeforeEach
    void setUp() {
        sampleDelivery = Delivery.builder()
                .id(1L)
                .trackingCode("RK-2026-001")
                .customerName("Nguyễn Văn An")
                .hubCode("HN-01")
                .status(DeliveryStatus.IN_TRANSIT)
                .codAmount(new BigDecimal("1500000.00"))
                .build();
    }

    @Test
    @DisplayName("Lập trình phòng vệ (Zero Crash): Xử lý an toàn khi mã vận đơn không tồn tại")
    void testGetDeliveryInfoNotFoundDefensive() {
        when(deliveryRepository.findByTrackingCode("RK-9999-999")).thenReturn(Optional.empty());

        LogisticsIncidentTools.GetDeliveryRequest request = new LogisticsIncidentTools.GetDeliveryRequest("RK-9999-999");
        LogisticsIncidentTools.DeliveryInfoResponse response = incidentTools.getDeliveryInfo(request);

        assertFalse(response.success());
        assertTrue(response.message().contains("Không tìm thấy mã đơn hàng"));
    }

    @Test
    @DisplayName("Tra cứu thành công thông tin đơn hàng tồn tại")
    void testGetDeliveryInfoSuccess() {
        when(deliveryRepository.findByTrackingCode("RK-2026-001")).thenReturn(Optional.of(sampleDelivery));

        LogisticsIncidentTools.GetDeliveryRequest request = new LogisticsIncidentTools.GetDeliveryRequest("RK-2026-001");
        LogisticsIncidentTools.DeliveryInfoResponse response = incidentTools.getDeliveryInfo(request);

        assertTrue(response.success());
        assertEquals("RK-2026-001", response.trackingCode());
        assertEquals("HN-01", response.hubCode());
    }

    @Test
    @DisplayName("Tạo phiếu sự cố thành công khi mã vận đơn hợp lệ")
    void testCreateIncidentSuccess() {
        when(deliveryRepository.findByTrackingCode("RK-2026-001")).thenReturn(Optional.of(sampleDelivery));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> {
            Incident inc = invocation.getArgument(0);
            inc.setId(101L);
            return inc;
        });

        LogisticsIncidentTools.CreateIncidentRequest request = new LogisticsIncidentTools.CreateIncidentRequest(
                "RK-2026-001",
                "HONG_HOC",
                "HN-01",
                "CRITICAL",
                "Hàng bị ướt sũng hỏng đồ"
        );

        LogisticsIncidentTools.IncidentCreatedResponse response = incidentTools.createIncident(request);

        assertTrue(response.success());
        assertEquals(101L, response.incidentId());
        assertEquals("HONG_HOC", response.incidentType());
    }

    @Test
    @DisplayName("Cập nhật trạng thái đơn hàng sang DAMAGED thành công")
    void testUpdateDeliveryStatusSuccess() {
        when(deliveryRepository.findByTrackingCode("RK-2026-001")).thenReturn(Optional.of(sampleDelivery));
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LogisticsIncidentTools.UpdateDeliveryStatusRequest request = new LogisticsIncidentTools.UpdateDeliveryStatusRequest(
                "RK-2026-001",
                "DAMAGED"
        );

        LogisticsIncidentTools.UpdateDeliveryStatusResponse response = incidentTools.updateDeliveryStatus(request);

        assertTrue(response.success());
        assertEquals("IN_TRANSIT", response.oldStatus());
        assertEquals("DAMAGED", response.newStatus());
    }
}
