package com.cognizant.hams.service.impl;

import com.cognizant.hams.dto.request.MedicalRecordDTO;
import com.cognizant.hams.dto.response.MedicalRecordResponseDTO;
import com.cognizant.hams.entity.Appointment;
import com.cognizant.hams.entity.Doctor;
import com.cognizant.hams.entity.MedicalRecord;
import com.cognizant.hams.entity.Patient;
import com.cognizant.hams.entity.User;
import com.cognizant.hams.exception.APIException;
import com.cognizant.hams.exception.ResourceNotFoundException;
import com.cognizant.hams.repository.AppointmentRepository;
import com.cognizant.hams.repository.DoctorRepository;
import com.cognizant.hams.repository.MedicalRecordRepository;
import com.cognizant.hams.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceImplTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MedicalRecordServiceImpl medicalRecordService;

    private MedicalRecordDTO medicalRecordDTO;
    private Appointment appointment;
    private Patient patient;
    private Doctor doctor;
    private MedicalRecord medicalRecord;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setPatientId(1L);
        patient.setName("John Patient");

        doctor = new Doctor();
        doctor.setDoctorId(1L);
        doctor.setDoctorName("Dr. Smith");

        appointment = new Appointment();
        appointment.setAppointmentId(1L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        medicalRecordDTO = new MedicalRecordDTO(1L, 1L, 1L, "Headache", "Migraine", "Rest");

        medicalRecord = new MedicalRecord();
        medicalRecord.setRecordId(1L);
        medicalRecord.setPatient(patient);
        medicalRecord.setDoctor(doctor);
    }

    @Test
    void createRecord_WhenValid_ReturnsResponseDTO() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);

        MedicalRecordResponseDTO result = medicalRecordService.createRecord(medicalRecordDTO);

        assertNotNull(result);
        assertEquals(1L, result.getRecordId());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    @Test
    void createRecord_WhenAppointmentMismatch_ThrowsAPIException() {
        Doctor wrongDoctor = new Doctor();
        wrongDoctor.setDoctorId(99L);
        appointment.setDoctor(wrongDoctor); // Appointment doctor ID is now 99

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor)); // DTO doctor ID is 1

        assertThrows(APIException.class, () -> medicalRecordService.createRecord(medicalRecordDTO));
    }

    @Test
    void getRecordsForPatient_WhenPatientExists_ReturnsRecordList() {
        User user = new User();
        user.setUsername("testpatient");
        patient.setUser(user);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testpatient", "pass"));
        when(patientRepository.findByUser_Username("testpatient")).thenReturn(Optional.of(patient));
        when(medicalRecordRepository.findByPatient_PatientIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(medicalRecord));

        List<MedicalRecordResponseDTO> result = medicalRecordService.getRecordsForPatient();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}