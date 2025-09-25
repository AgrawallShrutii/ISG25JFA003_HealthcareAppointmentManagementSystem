package com.cognizant.hams.controller;

import com.cognizant.hams.dto.Request.MedicalRecordDTO;
import com.cognizant.hams.dto.Response.MedicalRecordResponseDTO;
import com.cognizant.hams.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {
    private final MedicalRecordService medicalRecordService;
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalRecordResponseDTO> createRecord(@Valid @RequestBody MedicalRecordDTO dto) {
        MedicalRecordResponseDTO saved = medicalRecordService.createRecord(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<MedicalRecordResponseDTO>> getRecordsForPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalRecordService.getRecordsForPatient(patientId));
    }
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<MedicalRecordResponseDTO>> getRecordsForDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(medicalRecordService.getRecordsForDoctor(doctorId));
    }
}
