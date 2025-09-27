package com.cognizant.hams.dto.response;

import com.cognizant.hams.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDTO {
    private Long id;
    private Long appointmentId;
    private Notification.RecipientType recipientType;
    private Long recipientId;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private boolean read;
}
