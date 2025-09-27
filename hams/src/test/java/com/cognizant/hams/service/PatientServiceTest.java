package com.cognizant.hams.service;

import com.cognizant.hams.dto.request.PatientDTO;
import com.cognizant.hams.dto.response.PatientResponseDTO;
import com.cognizant.hams.entity.Patient;
import com.cognizant.hams.entity.User;
import com.cognizant.hams.exception.ResourceNotFoundException;
import com.cognizant.hams.repository.PatientRepository;
import com.cognizant.hams.service.DoctorService;
import com.cognizant.hams.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient patient;
    private PatientDTO patientDTO;
    private PatientResponseDTO patientResponseDTO;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testpatient");

        patient = new Patient();
        patient.setPatientId(1L);
        patient.setName("John Doe");
        patient.setUser(user);

        patientDTO = new PatientDTO("John Doe", LocalDate.now().minusYears(30), "Male", "1122334455", "john.doe@test.com", "123 Main St", "O+");
        patientResponseDTO = new PatientResponseDTO(1L, "John Doe", "john.doe@test.com", "1122334455", "123 Main St", "Male", LocalDate.now().minusYears(30), "O+");
    }

    @Test
    void createPatient_WhenSuccessful_ReturnsPatientResponseDTO() {
        when(modelMapper.map(patientDTO, Patient.class)).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(modelMapper.map(patient, PatientResponseDTO.class)).thenReturn(patientResponseDTO);

        PatientResponseDTO result = patientService.createPatient(patientDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    void getPatientById_AsAdmin_ReturnsPatientDTO() {
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", "pass", authorities));

        when(patientRepository.findByUser_Username(anyString())).thenReturn(Optional.of(new Patient())); // Mock any patient for admin
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(modelMapper.map(patient, PatientResponseDTO.class)).thenReturn(patientResponseDTO);

        PatientResponseDTO result = patientService.getPatientById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getPatientId());
    }

    @Test
    void getPatientById_AsPatientForSelf_ReturnsPatientDTO() {
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATIENT"));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testpatient", "pass", authorities));

        when(patientRepository.findByUser_Username("testpatient")).thenReturn(Optional.of(patient));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(modelMapper.map(patient, PatientResponseDTO.class)).thenReturn(patientResponseDTO);

        PatientResponseDTO result = patientService.getPatientById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getPatientId());
    }

    @Test
    void getPatientById_AsPatientForOther_ThrowsAccessDeniedException() {
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATIENT"));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testpatient", "pass", authorities));

        // Logged-in user has patientId 1, but is requesting patientId 2
        when(patientRepository.findByUser_Username("testpatient")).thenReturn(Optional.of(patient));

        assertThrows(AccessDeniedException.class, () -> patientService.getPatientById(2L));
    }

    @Test
    void deletePatient_WhenPatientExists_ReturnsDeletedDTO() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        doNothing().when(patientRepository).delete(patient);
        when(modelMapper.map(patient, PatientResponseDTO.class)).thenReturn(patientResponseDTO);

        PatientResponseDTO result = patientService.deletePatient(1L);

        assertNotNull(result);
        verify(patientRepository, times(1)).delete(patient);
    }

    @Test
    void deletePatient_WhenPatientNotFound_ThrowsResourceNotFoundException() {
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> patientService.deletePatient(99L));
    }
}