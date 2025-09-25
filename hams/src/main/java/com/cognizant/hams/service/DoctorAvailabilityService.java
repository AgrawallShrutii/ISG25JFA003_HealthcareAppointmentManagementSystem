package com.cognizant.hams.service;

import com.cognizant.hams.dto.Request.DoctorAvailabilityDTO;
import com.cognizant.hams.dto.Response.DoctorAndAvailabilityResponseDTO;
import com.cognizant.hams.dto.Response.DoctorAvailabilityResponseDTO;
import com.cognizant.hams.dto.Response.DoctorDetailsResponseDTO;

import java.util.List;

public interface DoctorAvailabilityService {

    //    // Availability Management
    DoctorAvailabilityResponseDTO addAvailability(Long doctorId, DoctorAvailabilityDTO slotDto);
    List<DoctorAvailabilityResponseDTO> getAvailability(Long doctorId);
    DoctorAvailabilityResponseDTO updateAvailabilitySlot(Long doctorId,Long availabilityId, DoctorAvailabilityDTO doctorAvailabilityDTO);

    List<DoctorAndAvailabilityResponseDTO> getAvailableDoctor(String doctorName);

    List<DoctorAndAvailabilityResponseDTO> searchDoctorByName(String doctorName);

}
