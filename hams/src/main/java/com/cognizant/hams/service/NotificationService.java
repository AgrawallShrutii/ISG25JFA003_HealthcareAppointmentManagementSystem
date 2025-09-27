package com.cognizant.hams.service;

import com.cognizant.hams.dto.response.NotificationResponseDTO;
import com.cognizant.hams.entity.Appointment;

import java.util.List;

public interface NotificationService {
    void notifyDoctorOnAppointmentRequest(Appointment appointment);
    void notifyPatientOnAppointmentDecision(Appointment appointment, boolean confirmed, String reason);
    List<NotificationResponseDTO> getNotificationForDoctor();
    List<NotificationResponseDTO> getNotificationForPatient();
    void markAsRead(Long notificationId);
}
