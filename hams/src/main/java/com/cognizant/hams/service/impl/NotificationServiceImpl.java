package com.cognizant.hams.service.impl;

import com.cognizant.hams.dto.response.NotificationResponseDTO;
import com.cognizant.hams.entity.Appointment;
import com.cognizant.hams.entity.Doctor;
import com.cognizant.hams.entity.Notification;
import com.cognizant.hams.entity.Patient;
import com.cognizant.hams.exception.ResourceNotFoundException;
import com.cognizant.hams.repository.DoctorRepository;
import com.cognizant.hams.repository.NotificationRepository;
import com.cognizant.hams.repository.PatientRepository;
import com.cognizant.hams.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Override
    public void notifyDoctorOnAppointmentRequest(Appointment appointment) {
        Notification notification = new Notification();
        notification.setAppointment(appointment);
        notification.setRecipientType(Notification.RecipientType.DOCTOR);
        notification.setRecipientId(appointment.getDoctor().getDoctorId());
        notification.setTitle("New appointment request");
        notification.setMessage("Patient " + appointment.getPatient().getName() +
                " request an appointment on " + appointment.getAppointmentDate() +
                " from " + appointment.getStartTime() + " to " + appointment.getEndTime());
        notificationRepository.save(notification);
    }

    @Override
    public void notifyPatientOnAppointmentDecision(Appointment appointment,
                                                   boolean confirmed, String reason) {
        Notification notification = new Notification();
        notification.setAppointment(appointment);
        notification.setRecipientType(Notification.RecipientType.PATIENT);
        notification.setRecipientId(appointment.getPatient().getPatientId());
        notification.setTitle(confirmed ? "Appointment confirmed" : "Appointment rejected");
        String message = confirmed ? "Your appointment with Dr. " + appointment.getDoctor()
                .getDoctorName() + " on " + appointment.getAppointmentDate() + " is confirmed." :
                "Your appointment with Dr. " + appointment.getDoctor().getDoctorName() + " on " +
                        appointment.getAppointmentDate() + " was rejected. Reason: " +
                        (reason == null ? "N/A" : reason);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponseDTO> getNotificationForDoctor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Doctor doctor = (Doctor) doctorRepository.findByUser_Username(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "username", currentUsername));

        List<Notification> notifications = notificationRepository
                .findByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(Notification.RecipientType.DOCTOR, doctor.getDoctorId());

        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponseDTO> getNotificationForPatient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Patient patient = (Patient) patientRepository.findByUser_Username(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "username", currentUsername));

        List<Notification> notifications = notificationRepository
                .findByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(Notification.RecipientType.PATIENT, patient.getPatientId());

        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    private NotificationResponseDTO mapToDTO(Notification notification) {
        NotificationResponseDTO dto = modelMapper.map(notification, NotificationResponseDTO.class);
        if (notification.getAppointment() != null) {
            dto.setAppointmentId(notification.getAppointment().getAppointmentId());
        }
        return dto;
    }
}
