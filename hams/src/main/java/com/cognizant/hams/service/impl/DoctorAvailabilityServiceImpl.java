package com.cognizant.hams.service.impl;

import com.cognizant.hams.dto.request.DoctorAvailabilityDTO;
import com.cognizant.hams.dto.response.DoctorAndAvailabilityResponseDTO;
import com.cognizant.hams.dto.response.DoctorAvailabilityResponseDTO;
import com.cognizant.hams.entity.Doctor;
import com.cognizant.hams.entity.DoctorAvailability;
import com.cognizant.hams.exception.APIException;
import com.cognizant.hams.exception.ResourceNotFoundException;
import com.cognizant.hams.repository.DoctorAvailabilityRepository;
import com.cognizant.hams.repository.DoctorRepository;
import com.cognizant.hams.service.DoctorAvailabilityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityServiceImpl implements DoctorAvailabilityService {

    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;


    // Add Availability
    @Transactional
    @Override
    public DoctorAvailabilityResponseDTO addAvailability(DoctorAvailabilityDTO slotDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Doctor loggedInDoctor = (Doctor) doctorRepository.findByUser_Username(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "username", currentUsername));

        // Check for a duplicate availability slot before saving
        if (doctorAvailabilityRepository.existsByDoctorDoctorIdAndAvailableDateAndStartTime(
                loggedInDoctor.getDoctorId(), slotDto.getAvailableDate(), slotDto.getStartTime())) {
            throw new APIException("The specified time slot is already registered for this doctor.");
        }

        try {
            DoctorAvailability doctorAvailability = modelMapper.map(slotDto, DoctorAvailability.class);
            doctorAvailability.setDoctor(loggedInDoctor);

            DoctorAvailability savedAvailability = doctorAvailabilityRepository.save(doctorAvailability);
            return modelMapper.map(savedAvailability, DoctorAvailabilityResponseDTO.class);

        } catch (DataIntegrityViolationException e) {
            throw new APIException("A database integrity violation occurred, possibly a duplicate entry.");
        }
    }

    // Get Availability

    @Override
    public List<DoctorAvailabilityResponseDTO> getDoctorAvailability() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Doctor loggedInDoctor = (Doctor) doctorRepository.findByUser_Username(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "username", currentUsername));

        List<DoctorAvailability> availabilities = doctorAvailabilityRepository.findByDoctorDoctorId(loggedInDoctor.getDoctorId());

        return availabilities.stream()
                .map(availability -> modelMapper.map(availability, DoctorAvailabilityResponseDTO.class))
                .collect(Collectors.toList());
    }

    // Delete Availability Slot

    @Override
    @Transactional
    public DoctorAvailabilityResponseDTO updateAvailabilitySlot(Long doctorId, Long availabilityId, DoctorAvailabilityDTO doctorAvailabilityDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("Only an ADMIN can update doctor details.");
        }

        DoctorAvailability existingAvailability = doctorAvailabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("DoctorAvailability", "availabilityId", availabilityId));

        if (!existingAvailability.getDoctor().getDoctorId().equals(doctorId)) {
            throw new APIException("Availability slot with ID " + availabilityId + " does not belong to doctor with ID " + doctorId);
        }

        modelMapper.map(doctorAvailabilityDTO, existingAvailability);

        DoctorAvailability updatedAvailability = doctorAvailabilityRepository.save(existingAvailability);

        return modelMapper.map(updatedAvailability, DoctorAvailabilityResponseDTO.class);
    }

    @Override
    public List<DoctorAndAvailabilityResponseDTO> getAvailableDoctor(String doctorName){
        return doctorRepository.findByAvailableDoctorNameAndAvailability(doctorName);
    }


    @Override
    public List<DoctorAndAvailabilityResponseDTO> searchDoctorByName(String doctorName){
        return doctorRepository.findByDoctorNameAndAvailability(doctorName);
    }
}
