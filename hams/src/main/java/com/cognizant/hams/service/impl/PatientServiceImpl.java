package com.cognizant.hams.service.impl;

import com.cognizant.hams.dto.request.PatientDTO;
import com.cognizant.hams.dto.response.DoctorResponseDTO;
import com.cognizant.hams.dto.response.PatientResponseDTO;
import com.cognizant.hams.entity.Patient;
import com.cognizant.hams.exception.APIException;
import com.cognizant.hams.exception.ResourceNotFoundException;
import com.cognizant.hams.repository.PatientRepository;
import com.cognizant.hams.service.DoctorService;
import com.cognizant.hams.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final DoctorService doctorService;


    @Override
    public PatientResponseDTO createPatient(PatientDTO patientCreateDTO){
        Patient patient = modelMapper.map(patientCreateDTO, Patient.class);
        patientRepository.save(patient);
        return modelMapper.map(patient, PatientResponseDTO.class);
    }


    @Override
    public PatientResponseDTO getPatientById(Long patientId){
        // Get the authenticated user's details from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Patient loggedInPatient = (Patient) patientRepository.findByUser_Username(currentUsername)
                .orElseThrow(() -> new APIException("Logged-in user is not a patient."));

        if (!isAdmin && !loggedInPatient.getPatientId().equals(patientId)) {
            throw new AccessDeniedException("You are not authorized to view another patient's details.");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "patientId", patientId));

        return modelMapper.map(patient, PatientResponseDTO.class);
    }

    @Override
    public PatientResponseDTO updatePatient(PatientDTO patientUpdateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Patient existingPatient = (Patient) patientRepository.findByUser_Username(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "username", currentUsername));

        if(patientUpdateDTO.getContactNumber() != null){
            existingPatient.setContactNumber(patientUpdateDTO.getContactNumber());
        }
        if(patientUpdateDTO.getEmail() != null){
            existingPatient.setEmail(patientUpdateDTO.getEmail());
        }
        if(patientUpdateDTO.getAddress() != null){
            existingPatient.setAddress(patientUpdateDTO.getAddress());
        }
        if(patientUpdateDTO.getGender() != null){
            existingPatient.setGender(patientUpdateDTO.getGender());
        }
        if(patientUpdateDTO.getName() != null){
            existingPatient.setName(patientUpdateDTO.getName());
        }
        if(patientUpdateDTO.getBloodGroup() != null){
            existingPatient.setBloodGroup(patientUpdateDTO.getBloodGroup());
        }
        if(patientUpdateDTO.getDateOfBirth() != null){
            existingPatient.setDateOfBirth(patientUpdateDTO.getDateOfBirth());
        }

        Patient updatedPatient = patientRepository.save(existingPatient);

        return modelMapper.map(updatedPatient, PatientResponseDTO.class);
    }

    @Override
    public PatientResponseDTO deletePatient(Long patientId){
        Patient deletePatient = patientRepository.findById(patientId)
                .orElseThrow(()-> new ResourceNotFoundException("Patient", "patientID", patientId));
        patientRepository.delete(deletePatient);
        return modelMapper.map(deletePatient, PatientResponseDTO.class);
    }

    @Override
    public List<DoctorResponseDTO> getAllDoctors(){
        return doctorService.getAllDoctor();
    }


    @Override
    public List<DoctorResponseDTO> searchDoctorByName(String name) {
        return doctorService.searchDoctorsByName(name);
    }

    @Override
    public List<DoctorResponseDTO> searchDoctorBySpecialization(String specialization) {
        return doctorService.searchDoctorsBySpecialization(specialization);
    }

}




