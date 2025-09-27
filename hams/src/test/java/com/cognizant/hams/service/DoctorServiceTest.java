package com.cognizant.hams.service;

import com.cognizant.hams.dto.request.AdminUserRequestDTO;
import com.cognizant.hams.dto.request.DoctorDTO;
import com.cognizant.hams.dto.response.DoctorResponseDTO;
import com.cognizant.hams.entity.Doctor;
import com.cognizant.hams.entity.User;
import com.cognizant.hams.exception.APIException;
import com.cognizant.hams.exception.ResourceNotFoundException;
import com.cognizant.hams.repository.DoctorRepository;
import com.cognizant.hams.repository.UserRepository;
import com.cognizant.hams.service.impl.DoctorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private Doctor doctor;
    private DoctorDTO doctorDTO;
    private DoctorResponseDTO doctorResponseDTO;
    private AdminUserRequestDTO adminUserRequestDTO;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");

        doctor = new Doctor();
        doctor.setDoctorId(1L);
        doctor.setDoctorName("Dr. Smith");
        doctor.setEmail("smith@test.com");
        doctor.setContactNumber("1234567890");
        doctor.setSpecialization("Cardiology"); // Added for completeness
        doctor.setUser(user);

        doctorDTO = new DoctorDTO("Dr. John Doe", "MD", "Cardiology", "123 Clinic St", 10, "0987654321", "john.doe@test.com");
        doctorResponseDTO = new DoctorResponseDTO(1L, "Dr. Smith", "Cardiology", "MD", "Main St", 10, "smith@test.com", "1234567890");

        // FIX 1: Initialize the request DTO with data to avoid passing nulls.
        adminUserRequestDTO = new AdminUserRequestDTO();
        adminUserRequestDTO.setDoctorName("Dr. Smith");
        adminUserRequestDTO.setSpecialization("Cardiology");
        adminUserRequestDTO.setEmail("smith@test.com");
        adminUserRequestDTO.setContactNumber("1234567890");

    }

    @Test
    void createDoctor_WhenSuccessful_ReturnsDoctorResponseDTO() {
        // FIX 2: Use anyString() argument matchers for more flexible stubbing.
        when(doctorRepository.existsByEmailOrContactNumber(anyString(), anyString())).thenReturn(false);
        when(doctorRepository.existsByDoctorNameAndSpecialization(anyString(), anyString())).thenReturn(false);

        when(userRepository.findById(anyString())).thenReturn(Optional.of(new User()));
        when(modelMapper.map(any(AdminUserRequestDTO.class), eq(Doctor.class))).thenReturn(doctor);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);
        when(modelMapper.map(any(Doctor.class), eq(DoctorResponseDTO.class))).thenReturn(doctorResponseDTO);

        DoctorResponseDTO result = doctorService.createDoctor(adminUserRequestDTO);

        assertNotNull(result);
        assertEquals(doctorResponseDTO.getDoctorId(), result.getDoctorId());
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    void createDoctor_WhenEmailExists_ThrowsAPIException() {
        when(modelMapper.map(any(AdminUserRequestDTO.class), eq(Doctor.class))).thenReturn(doctor);
        when(doctorRepository.existsByEmailOrContactNumber(doctor.getEmail(), doctor.getContactNumber())).thenReturn(true);

        assertThrows(APIException.class, () -> doctorService.createDoctor(adminUserRequestDTO));
    }

    @Test
    void getDoctor_WhenDoctorExists_ReturnsDoctorResponseDTO() {
        // Mock security context
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testuser", "password"));
        when(doctorRepository.findByUser_Username("testuser")).thenReturn(Optional.of(doctor));
        when(modelMapper.map(doctor, DoctorResponseDTO.class)).thenReturn(doctorResponseDTO);

        DoctorResponseDTO result = doctorService.getDoctor();

        assertNotNull(result);
        assertEquals("Dr. Smith", result.getDoctorName());
    }

    @Test
    void getDoctor_WhenDoctorNotFound_ThrowsResourceNotFoundException() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testuser", "password"));
        when(doctorRepository.findByUser_Username("testuser")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.getDoctor());
    }

    @Test
    void getAllDoctor_WhenDoctorsExist_ReturnsListOfDTOs() {
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));
        when(modelMapper.map(doctor, DoctorResponseDTO.class)).thenReturn(doctorResponseDTO);

        List<DoctorResponseDTO> result = doctorService.getAllDoctor();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllDoctor_WhenNoDoctors_ThrowsAPIException() {
        when(doctorRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(APIException.class, () -> doctorService.getAllDoctor());
    }

    @Test
    void updateDoctor_WhenSuccessful_ReturnsUpdatedDTO() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);
        when(modelMapper.map(doctor, DoctorResponseDTO.class)).thenReturn(doctorResponseDTO);

        DoctorResponseDTO result = doctorService.updateDoctor(1L, doctorDTO);

        assertNotNull(result);
        verify(doctorRepository, times(1)).save(doctor);
    }

    @Test
    void deleteDoctor_WhenSuccessful_ReturnsDeletedDTO() {
        when(doctorRepository.findByDoctorId(1L)).thenReturn(Optional.of(doctor));
        doNothing().when(doctorRepository).deleteById(1L);
        when(modelMapper.map(doctor, DoctorResponseDTO.class)).thenReturn(doctorResponseDTO);

        DoctorResponseDTO result = doctorService.deleteDoctor(1L);

        assertNotNull(result);
        verify(doctorRepository, times(1)).deleteById(1L);
    }
}