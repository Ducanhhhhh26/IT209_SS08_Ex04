package org.example.smart_hub.repository;

import org.example.smart_hub.entity.Incident;
import org.example.smart_hub.entity.IncidentStatus;
import org.example.smart_hub.entity.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByTrackingCode(String trackingCode);
    List<Incident> findByHubCode(String hubCode);
    List<Incident> findByStatus(IncidentStatus status);
    List<Incident> findBySeverity(Severity severity);
    long countByHubCode(String hubCode);
}
