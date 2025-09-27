package com.cognizant.hams.service;

import com.cognizant.hams.dto.request.AdminUserRequestDTO;
import com.cognizant.hams.dto.request.DoctorDTO;
import com.cognizant.hams.dto.response.DoctorResponseDTO;

import java.util.List;

public interface DoctorService {

    // Basic Curd operation
    DoctorResponseDTO createDoctor(AdminUserRequestDTO doctorDto);
    DoctorResponseDTO getDoctor();
    List<DoctorResponseDTO> getAllDoctor();
    DoctorResponseDTO updateDoctor(Long doctorId,DoctorDTO doctorDto);
    DoctorResponseDTO deleteDoctor(Long doctorId);


    // Domain Specific
    List<DoctorResponseDTO> searchDoctorsBySpecialization(String specialization);
    List<DoctorResponseDTO> searchDoctorsByName(String name);



}
