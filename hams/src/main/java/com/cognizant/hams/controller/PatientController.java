package com.cognizant.hams.controller;

import com.cognizant.hams.dto.request.PatientDTO;
import com.cognizant.hams.dto.response.PatientResponseDTO;
import com.cognizant.hams.dto.response.DoctorResponseDTO;
import com.cognizant.hams.repository.PatientRepository;
import com.cognizant.hams.service.NotificationService;
import com.cognizant.hams.service.impl.PatientServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patients")
public class PatientController {


    private final PatientServiceImpl patientService;
    private final NotificationService notificationService;
    PatientRepository patientRepository;

//    @PostMapping
//    public ResponseEntity<PatientResponseDTO> createPatient(@Valid @RequestBody PatientDTO createDTO){
//        PatientResponseDTO newPatient = patientService.createPatient(createDTO);
//        return new ResponseEntity<>(newPatient, HttpStatus.CREATED);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> getPatientById(@PathVariable("id") Long patientId) {
        return ResponseEntity.ok(patientService.getPatientById(patientId));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientResponseDTO> updatePatient(@RequestBody PatientDTO patientUpdateDTO){
        PatientResponseDTO existingPatientDTO = patientService.updatePatient(patientUpdateDTO);
        return new ResponseEntity<>(existingPatientDTO,HttpStatus.OK);
    }

    @DeleteMapping("/{patientId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientResponseDTO> deletePatient(@PathVariable("patientId") Long patientId){
        PatientResponseDTO deletePatientDTO = patientService.deletePatient(patientId);
        return new ResponseEntity<>(deletePatientDTO, HttpStatus.OK);
    }

    @GetMapping("/doctor-name")
    public ResponseEntity<List<DoctorResponseDTO>> searchDoctorByName(@RequestParam("name") String name){
        List<DoctorResponseDTO> doctors = patientService.searchDoctorByName(name);
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @GetMapping("/doctor-specialization")
    public ResponseEntity<List<DoctorResponseDTO>> searchDoctorBySpecialization(@RequestParam("specialization") String specialization){
        List<DoctorResponseDTO> doctors = patientService.searchDoctorBySpecialization(specialization);
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @GetMapping("/all-doctors")
    public ResponseEntity<List<DoctorResponseDTO>> getAllDoctors(){
        List<DoctorResponseDTO> doctors = patientService.getAllDoctors();
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }
}


