package ra.notificationservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    @GetMapping
    public ResponseEntity<List<String>> getAllNotifications(){
        return ResponseEntity.ok().body(List.of("Notification 1", "Notification 2", "Notification 3"));
    }
}
