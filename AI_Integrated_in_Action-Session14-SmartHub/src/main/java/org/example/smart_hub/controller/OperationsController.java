package org.example.smart_hub.controller;

import lombok.RequiredArgsConstructor;
import org.example.smart_hub.dto.OperationsChatRequest;
import org.example.smart_hub.dto.OperationsChatResponse;
import org.example.smart_hub.entity.Delivery;
import org.example.smart_hub.entity.Incident;
import org.example.smart_hub.repository.DeliveryRepository;
import org.example.smart_hub.repository.IncidentRepository;
import org.example.smart_hub.service.OperationsAgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/operations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OperationsController {

    private final OperationsAgentService operationsAgentService;
    private final DeliveryRepository deliveryRepository;
    private final IncidentRepository incidentRepository;

    @PostMapping("/chat")
    public ResponseEntity<OperationsChatResponse> handleOperationsChat(@RequestBody OperationsChatRequest request) {
        OperationsChatResponse response = operationsAgentService.processCustomerMessage(request.getMessage());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deliveries")
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryRepository.findAll());
    }

    @GetMapping("/incidents")
    public ResponseEntity<List<Incident>> getAllIncidents() {
        return ResponseEntity.ok(incidentRepository.findAll());
    }
}
