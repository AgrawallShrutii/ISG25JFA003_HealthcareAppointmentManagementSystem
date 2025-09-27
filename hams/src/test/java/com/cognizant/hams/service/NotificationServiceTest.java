package com.cognizant.hams.service;

import com.cognizant.hams.entity.*;
import com.cognizant.hams.repository.DoctorRepository;
import com.cognizant.hams.repository.NotificationRepository;
import com.cognizant.hams.repository.PatientRepository;
import com.cognizant.hams.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private DoctorRepository doctorRepository; // Needed for getNotificationForDoctor
    @Mock
    private PatientRepository patientRepository; // Needed for getNotificationForPatient
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        Patient patient = new Patient();
        patient.setPatientId(1L);
        patient.setName("Jane Doe");

        Doctor doctor = new Doctor();
        doctor.setDoctorId(1L);
        doctor.setDoctorName("Dr. Who");

        appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
    }

    @Test
    void notifyDoctorOnAppointmentRequest_SavesCorrectNotification() {
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        notificationService.notifyDoctorOnAppointmentRequest(appointment);

        verify(notificationRepository, times(1)).save(notificationCaptor.capture());
        Notification captured = notificationCaptor.getValue();

        assertEquals(Notification.RecipientType.DOCTOR, captured.getRecipientType());
        assertEquals(1L, captured.getRecipientId());
        assertTrue(captured.getMessage().contains("Jane Doe"));
        assertEquals("New appointment request", captured.getTitle());
    }

    @Test
    void notifyPatientOnAppointmentDecision_Confirmed_SavesCorrectNotification() {
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        notificationService.notifyPatientOnAppointmentDecision(appointment, true, null);

        verify(notificationRepository, times(1)).save(notificationCaptor.capture());
        Notification captured = notificationCaptor.getValue();

        assertEquals(Notification.RecipientType.PATIENT, captured.getRecipientType());
        assertEquals(1L, captured.getRecipientId());
        assertEquals("Appointment confirmed", captured.getTitle());
    }

    @Test
    void notifyPatientOnAppointmentDecision_Rejected_SavesCorrectNotification() {
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        notificationService.notifyPatientOnAppointmentDecision(appointment, false, "Doctor is unavailable");

        verify(notificationRepository, times(1)).save(notificationCaptor.capture());
        Notification captured = notificationCaptor.getValue();

        assertEquals(Notification.RecipientType.PATIENT, captured.getRecipientType());
        assertEquals("Appointment rejected", captured.getTitle());
        assertTrue(captured.getMessage().contains("Doctor is unavailable"));
    }
}