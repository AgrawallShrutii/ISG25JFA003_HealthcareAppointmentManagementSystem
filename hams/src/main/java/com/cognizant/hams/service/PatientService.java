package com.cognizant.hams.service;

import com.cognizant.hams.dto.request.PatientDTO;
import com.cognizant.hams.dto.response.DoctorResponseDTO;
import com.cognizant.hams.dto.response.PatientResponseDTO;

import java.util.List;

public interface PatientService {
    PatientResponseDTO createPatient(PatientDTO patientCreateDTO);

    PatientResponseDTO getPatientById(Long patientId);

    PatientResponseDTO updatePatient(PatientDTO patientUpdateDTO);

    PatientResponseDTO deletePatient(Long patientId);

    List<DoctorResponseDTO> getAllDoctors();


    List<DoctorResponseDTO> searchDoctorByName(String name);

    List<DoctorResponseDTO> searchDoctorBySpecialization(String specialization);

}

