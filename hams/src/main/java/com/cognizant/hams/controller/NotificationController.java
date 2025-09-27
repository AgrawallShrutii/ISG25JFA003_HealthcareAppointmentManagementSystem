package com.cognizant.hams.controller;

import com.cognizant.hams.dto.response.NotificationResponseDTO;
import com.cognizant.hams.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/patients/notification")
    public ResponseEntity<List<NotificationResponseDTO>> getPatientNotifications(){
        return ResponseEntity.ok(notificationService.getNotificationForPatient());
    }

    @GetMapping("/doctors/notification")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsForDoctor() {
        List<NotificationResponseDTO> notifications = notificationService.getNotificationForDoctor();
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable("notificationId") Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
