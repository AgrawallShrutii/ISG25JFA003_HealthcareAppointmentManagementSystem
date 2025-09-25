package com.cognizant.hams.service.Impl;

import com.cognizant.hams.dto.Request.DoctorAvailabilityDTO;
import com.cognizant.hams.dto.Response.DoctorAndAvailabilityResponseDTO;
import com.cognizant.hams.dto.Response.DoctorAvailabilityResponseDTO;
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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityServiceImpl implements DoctorAvailabilityService {

    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;


    // Add Availability

    @Override
    @Transactional
    public DoctorAvailabilityResponseDTO addAvailability(Long doctorId, DoctorAvailabilityDTO slotDto) {
        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "doctorId", doctorId));

        try {
            DoctorAvailability doctorAvailability = modelMapper.map(slotDto, DoctorAvailability.class);
            doctorAvailability.setDoctor(doctor);

            DoctorAvailability savedAvailability = doctorAvailabilityRepository.save(doctorAvailability);
            return modelMapper.map(savedAvailability, DoctorAvailabilityResponseDTO.class);

        } catch (DataIntegrityViolationException e) {
            throw new APIException("The specified time slot is already registered for this doctor.");
        }
    }

    // Get Availability

    @Override
    public List<DoctorAvailabilityResponseDTO> getAvailability(Long doctorId) {
        List<DoctorAvailability> availabilities = doctorAvailabilityRepository.findByDoctorDoctorId(doctorId);
        return availabilities.stream()
                .map(availability -> modelMapper.map(availability, DoctorAvailabilityResponseDTO.class))
                .toList();
    }

    // Delete Availability Slot

    @Override
    @Transactional
    public DoctorAvailabilityResponseDTO updateAvailabilitySlot(Long doctorId, Long availabilityId, DoctorAvailabilityDTO doctorAvailabilityDTO) {
        DoctorAvailability existingAvailability = doctorAvailabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "availabilityId", availabilityId));

        modelMapper.map(doctorAvailabilityDTO, existingAvailability);

        doctorAvailabilityRepository.save(existingAvailability);

        return modelMapper.map(existingAvailability, DoctorAvailabilityResponseDTO.class);
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
