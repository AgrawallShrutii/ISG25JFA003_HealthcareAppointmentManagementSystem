package com.cognizant.hams.service;

import com.cognizant.hams.dto.Request.AdminUserRequestDTO;
import com.cognizant.hams.dto.Request.DoctorDTO;
import com.cognizant.hams.dto.Response.DoctorResponseDTO;
import com.cognizant.hams.entity.Doctor;
import com.cognizant.hams.exception.APIException;
import com.cognizant.hams.exception.ResourceNotFoundException;
import com.cognizant.hams.repository.DoctorRepository;
import com.cognizant.hams.service.Impl.DoctorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private Doctor doctor;
    private AdminUserRequestDTO doctorDTO;
    private DoctorResponseDTO doctorResponseDTO;

    @BeforeEach
    void setUp() {
        doctorDTO = new AdminUserRequestDTO("Dr. Strange", "MD", "Neurosurgery", "177A Bleecker Street", 10, "5551234567", "strange@sanctum.com");
        doctor = new Doctor();
        doctor.setDoctorId(1L);
        doctor.setDoctorName("Dr. Strange");
        doctor.setQualification("MD");
        doctor.setSpecialization("Neurosurgery");
        doctor.setYearOfExperience(10);
        doctor.setClinicAddress("177A Bleecker Street");
        doctor.setContactNumber("5551234567");
        doctor.setEmail("strange@sanctum.com");

        doctorResponseDTO = new DoctorResponseDTO(1L, "Dr. Strange", "Neurosurgery", "MD", "177A Bleecker Street", 10, "strange@sanctum.com", "5551234567");
    }

    @Test
    @DisplayName("Test Create Doctor - Success")
    void givenDoctorDTO_whenCreateDoctor_thenReturnSavedDoctor() {

        given(doctorRepository.existsByDoctorNameAndSpecialization(anyString(), anyString())).willReturn(false);
        given(modelMapper.map(doctorDTO, Doctor.class)).willReturn(doctor);
        given(doctorRepository.save(any(Doctor.class))).willReturn(doctor);
        given(modelMapper.map(doctor, DoctorResponseDTO.class)).willReturn(doctorResponseDTO);


        DoctorResponseDTO result = doctorService.createDoctor(doctorDTO);


        assertThat(result).isNotNull();
        assertThat(result.getDoctorName()).isEqualTo("Dr. Strange");
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Test Create Doctor - Throws APIException when Doctor Exists")
    void givenExistingDoctor_whenCreateDoctor_thenThrowsAPIException() {

        given(doctorRepository.existsByDoctorNameAndSpecialization(anyString(), anyString())).willReturn(true);
        given(modelMapper.map(doctorDTO, Doctor.class)).willReturn(doctor);


        assertThrows(APIException.class, () -> doctorService.createDoctor(doctorDTO));
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Test Get Doctor By Id - Success")
    void givenDoctorId_whenGetDoctorById_thenReturnDoctor() {

        given(doctorRepository.findByDoctorId(1L)).willReturn(Optional.of(doctor));
        given(modelMapper.map(Optional.of(doctor), DoctorResponseDTO.class)).willReturn(doctorResponseDTO);


        DoctorResponseDTO result = doctorService.getDoctorById(1L);


        assertThat(result).isNotNull();
        assertThat(result.getDoctorId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Test Get Doctor By Id - Throws APIException")
    void givenInvalidId_whenGetDoctorById_thenThrowsAPIException() {

        given(doctorRepository.findByDoctorId(99L)).willReturn(Optional.empty());


        assertThrows(APIException.class, () -> doctorService.getDoctorById(99L));
    }

    @Test
    @DisplayName("Test Update Doctor - Success")
    void givenDoctorIdAndDTO_whenUpdateDoctor_thenReturnUpdatedDoctor() {
        // Arrange
        DoctorDTO updateDetails = new DoctorDTO();
        updateDetails.setQualification("Sorcerer Supreme");

        given(doctorRepository.findById(1L)).willReturn(Optional.of(doctor));
        given(doctorRepository.save(any(Doctor.class))).willReturn(doctor);
        given(modelMapper.map(doctor, DoctorResponseDTO.class)).willReturn(doctorResponseDTO);


        doctorService.updateDoctor(1L, updateDetails);


        verify(doctorRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }
}