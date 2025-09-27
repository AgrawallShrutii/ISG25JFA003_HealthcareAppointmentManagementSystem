package com.cognizant.hams.controller;

import com.cognizant.hams.dto.request.DoctorAvailabilityDTO;
import com.cognizant.hams.dto.response.DoctorAndAvailabilityResponseDTO;
import com.cognizant.hams.dto.response.DoctorAvailabilityResponseDTO;
import com.cognizant.hams.service.DoctorAvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DoctorAvailabilityController {

    private final DoctorAvailabilityService doctorAvailabilityService;

    @PostMapping("/doctors/availability")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorAvailabilityResponseDTO> addAvailability(@Valid @RequestBody DoctorAvailabilityDTO slotDto) {
        DoctorAvailabilityResponseDTO savedSlot = doctorAvailabilityService.addAvailability(slotDto);
        return new ResponseEntity<>(savedSlot, HttpStatus.CREATED);
    }

    @GetMapping("/doctors/availability")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<DoctorAvailabilityResponseDTO>> getDoctorAvailability() {
        List<DoctorAvailabilityResponseDTO> availability = doctorAvailabilityService.getDoctorAvailability();
        return new ResponseEntity<>(availability, HttpStatus.OK);
    }

    @PutMapping("/admin/{doctorId}/availability/{availabilityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorAvailabilityResponseDTO> updateAvailabilitySlot(@PathVariable("doctorId") Long doctorId, @PathVariable("availabilityId") Long availabilityId, @RequestBody  DoctorAvailabilityDTO doctorAvailabilityDTO ) {
        DoctorAvailabilityResponseDTO doctorResponseDTO = doctorAvailabilityService.updateAvailabilitySlot(doctorId, availabilityId,doctorAvailabilityDTO);
        return new ResponseEntity<>(doctorResponseDTO,HttpStatus.OK);
    }

    @GetMapping("/patients/doctor-availability")
    public ResponseEntity<List<DoctorAndAvailabilityResponseDTO>> getAvailableDoctor(@RequestParam("name") String doctorName){
        List<DoctorAndAvailabilityResponseDTO> doctorAndAvailabilityResponseDTOList = doctorAvailabilityService.getAvailableDoctor(doctorName);
        return new ResponseEntity<>(doctorAndAvailabilityResponseDTOList, HttpStatus.OK);
    }

    @GetMapping("/patients/searchDoctor")
    public ResponseEntity<List<DoctorAndAvailabilityResponseDTO>> searchDoctorByName(@RequestParam("name") String doctorName){
        List<DoctorAndAvailabilityResponseDTO> doctorAndAvailabilityResponseDTOList = doctorAvailabilityService.searchDoctorByName(doctorName);
        return new ResponseEntity<>(doctorAndAvailabilityResponseDTOList, HttpStatus.OK);
    }

}
