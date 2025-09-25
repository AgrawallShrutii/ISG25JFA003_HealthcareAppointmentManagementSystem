package com.cognizant.hams.controller;

import com.cognizant.hams.dto.Request.DoctorAvailabilityDTO;
import com.cognizant.hams.dto.Response.DoctorAndAvailabilityResponseDTO;
import com.cognizant.hams.dto.Response.DoctorAvailabilityResponseDTO;
import com.cognizant.hams.dto.Response.DoctorDetailsResponseDTO;
import com.cognizant.hams.service.DoctorAvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorAvailabilityController {

    private final DoctorAvailabilityService doctorAvailabilityService;

    @PostMapping("/{doctorId}/availability")
    public ResponseEntity<DoctorAvailabilityResponseDTO> addAvailability(@PathVariable("doctorId") Long doctorId, @Valid @RequestBody DoctorAvailabilityDTO slotDto) {
        DoctorAvailabilityResponseDTO savedSlot = doctorAvailabilityService.addAvailability(doctorId, slotDto);
        return new ResponseEntity<>(savedSlot, HttpStatus.CREATED);
    }

    @GetMapping("/{doctorId}/availability")
    public ResponseEntity<List<DoctorAvailabilityResponseDTO>> getAvailability(@PathVariable("doctorId") Long doctorId) {
        List<DoctorAvailabilityResponseDTO> availability = doctorAvailabilityService.getAvailability(doctorId);
        return new ResponseEntity<>(availability, HttpStatus.OK);
    }

    @PutMapping("/{doctorId}/availability/{availabilityId}")
    public ResponseEntity<DoctorAvailabilityResponseDTO> updateAvailabilitySlot(@PathVariable("doctorId") Long doctorId, @PathVariable("availabilityId") Long availabilityId, @RequestBody  DoctorAvailabilityDTO doctorAvailabilityDTO ) {
        DoctorAvailabilityResponseDTO doctorResponseDTO = doctorAvailabilityService.updateAvailabilitySlot(doctorId, availabilityId,doctorAvailabilityDTO);
        return new ResponseEntity<>(doctorResponseDTO,HttpStatus.OK);
    }

    @GetMapping("/doctor-availability")
    public ResponseEntity<List<DoctorAndAvailabilityResponseDTO>> getAvailableDoctor(@RequestParam("name") String doctorName){
        List<DoctorAndAvailabilityResponseDTO> doctorAndAvailabilityResponseDTOList = doctorAvailabilityService.getAvailableDoctor(doctorName);
        return new ResponseEntity<>(doctorAndAvailabilityResponseDTOList, HttpStatus.OK);
    }

    @GetMapping("/searchDoctor")
    public ResponseEntity<List<DoctorAndAvailabilityResponseDTO>> searchDoctorByName(@RequestParam("name") String doctorName){
        List<DoctorAndAvailabilityResponseDTO> doctorAndAvailabilityResponseDTOList = doctorAvailabilityService.searchDoctorByName(doctorName);
        return new ResponseEntity<>(doctorAndAvailabilityResponseDTOList, HttpStatus.OK);
    }

}
