package com.cognizant.hams.controller;

import com.cognizant.hams.dto.Request.AdminUserRequestDTO;
import com.cognizant.hams.dto.Request.DoctorAvailabilityDTO;
import com.cognizant.hams.dto.Response.DoctorDetailsResponseDTO;
import com.cognizant.hams.dto.Response.DoctorAvailabilityResponseDTO;
import com.cognizant.hams.dto.Request.DoctorDTO;
import com.cognizant.hams.dto.Response.DoctorResponseDTO;
import com.cognizant.hams.service.DoctorService;
import com.cognizant.hams.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<DoctorResponseDTO> createDoctor(@Valid @RequestBody AdminUserRequestDTO doctorDTO){
        DoctorResponseDTO savedDoctor = doctorService.createDoctor(doctorDTO);
        return new ResponseEntity<>(savedDoctor, HttpStatus.CREATED);
    }

    @GetMapping("/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorResponseDTO> getDoctorById(@PathVariable("doctorId") Long doctorId){
        DoctorResponseDTO doctor = doctorService.getDoctorById(doctorId);
        return new ResponseEntity<>(doctor,HttpStatus.OK);

    }

    @GetMapping("/get-all-doctors")
    public ResponseEntity<List<DoctorResponseDTO>> getAllDoctor(){
        List<DoctorResponseDTO> doctors = doctorService.getAllDoctor();
        return new ResponseEntity<>(doctors,HttpStatus.OK);
    }

    @PutMapping("/{doctorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponseDTO> updateDoctor(@PathVariable("doctorId") Long doctorId, @RequestBody DoctorDTO doctorDTO){
        DoctorResponseDTO updateDoctor = doctorService.updateDoctor(doctorId,doctorDTO);
        return new ResponseEntity<>(updateDoctor,HttpStatus.OK);

    }

    @DeleteMapping("/{doctorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponseDTO> deleteDoctor(@PathVariable("doctorId") Long doctorId){
        DoctorResponseDTO deleteDoctor = doctorService.deleteDoctor(doctorId);
        return new ResponseEntity<>(deleteDoctor, HttpStatus.OK);
    }

    @GetMapping("/doctor-specialization")
    public ResponseEntity<List<DoctorResponseDTO>> searchDoctorsBySpecialization(@RequestParam("specialization") String specialization){
        List<DoctorResponseDTO> doctors = doctorService.searchDoctorsBySpecialization(specialization);
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @GetMapping("/doctor-name")
    public ResponseEntity<List<DoctorResponseDTO>> searchDoctorsByName(@RequestParam("name") String name){
        List<DoctorResponseDTO> doctors = doctorService.searchDoctorsByName(name);
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

}
