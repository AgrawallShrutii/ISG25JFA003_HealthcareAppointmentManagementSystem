package com.cognizant.hams.service.impl;

import com.cognizant.hams.dto.request.AdminUserRequestDTO;
import com.cognizant.hams.dto.request.DoctorDTO;
import com.cognizant.hams.dto.response.DoctorResponseDTO;
import com.cognizant.hams.entity.Doctor;
import com.cognizant.hams.entity.User;
import com.cognizant.hams.exception.APIException;
import com.cognizant.hams.exception.ResourceNotFoundException;
import com.cognizant.hams.repository.AppointmentRepository;
import com.cognizant.hams.repository.DoctorAvailabilityRepository;
import com.cognizant.hams.repository.DoctorRepository;
import com.cognizant.hams.repository.UserRepository;
import com.cognizant.hams.service.DoctorService;
import com.cognizant.hams.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    private final UserRepository userRepository;

    private final DoctorAvailabilityRepository doctorAvailabilityRepository;

    private final ModelMapper modelMapper;

    private final AppointmentRepository appointmentRepository;

    private final NotificationService notificationService;


    @Override
    public DoctorResponseDTO createDoctor(AdminUserRequestDTO doctorDTO) {
        Doctor doctor = modelMapper.map(doctorDTO,Doctor.class);
        if (doctorRepository.existsByEmailOrContactNumber(doctor.getEmail(), doctor.getContactNumber())) {
            throw new APIException("A doctor with the provided email or contact number already exists.");
        }
        if(doctorRepository.existsByDoctorNameAndSpecialization(doctor.getDoctorName(),doctor.getSpecialization())){
            throw new APIException("Doctor with name " + doctorDTO.getDoctorName() + " and specialization " + doctorDTO.getSpecialization() + " already exist.");
        }
        User user=userRepository.findById("edd31d31-b822-4203-95d8-14557e02a818").get();

        doctor.setUser(user);
        Doctor saveDoctor = doctorRepository.save(doctor);
        return modelMapper.map(saveDoctor,DoctorResponseDTO.class);
    }

    // Get Doctor By I'd:

    @Override
    public DoctorResponseDTO getDoctor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Doctor doctor = (Doctor) doctorRepository.findByUser_Username(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "username", currentUsername));

        return modelMapper.map(doctor, DoctorResponseDTO.class);
    }

    // Get All Doctor

    @Override
    public List<DoctorResponseDTO> getAllDoctor(){
        List<Doctor> doctors = doctorRepository.findAll();
        if(doctors.isEmpty()){
            throw new APIException("No Doctor Available");
        }
        return doctors.stream()
                .map(doctor -> modelMapper.map(doctor,DoctorResponseDTO.class))
                .collect(Collectors.toList());
    }

    // Update Doctor

    @Override
    public DoctorResponseDTO updateDoctor(Long doctorId, DoctorDTO doctorDTO) {
        Doctor existingDoctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "doctorId", doctorId));

        if(doctorDTO.getDoctorName() != null){
            existingDoctor.setDoctorName(doctorDTO.getDoctorName());
        }
        if(doctorDTO.getQualification() != null){
            existingDoctor.setQualification(doctorDTO.getQualification());
        }
        if(doctorDTO.getSpecialization() != null){
            existingDoctor.setSpecialization(doctorDTO.getSpecialization());
        }
        if(doctorDTO.getYearOfExperience() != null){
            existingDoctor.setYearOfExperience(doctorDTO.getYearOfExperience());
        }
        if(doctorDTO.getClinicAddress() != null){
            existingDoctor.setClinicAddress(doctorDTO.getClinicAddress());
        }
        if(doctorDTO.getEmail() != null){
            existingDoctor.setEmail(doctorDTO.getEmail());
        }
        if(doctorDTO.getContactNumber() != null){
            existingDoctor.setContactNumber(doctorDTO.getContactNumber());
        }

        doctorRepository.save(existingDoctor);
        return modelMapper.map(existingDoctor,DoctorResponseDTO.class);
    }

    // Delete Doctor

    @Override
    public DoctorResponseDTO deleteDoctor(Long doctorId){
        Doctor existingDoctor = doctorRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor","doctorId", doctorId));
        doctorRepository.deleteById(doctorId);
        return modelMapper.map(existingDoctor,DoctorResponseDTO.class);
    }

    // Search Doctors By Specialization

    @Override
    public List<DoctorResponseDTO> searchDoctorsBySpecialization(String specialization) {
        List<Doctor> doctorSpecialization = doctorRepository.findBySpecializationContainingIgnoreCase(specialization);
        if(doctorSpecialization == null){
            throw new ResourceNotFoundException("Doctor","Specialization",specialization);
        }

        return doctorSpecialization.stream()
                .map(doctor -> modelMapper.map(doctor,DoctorResponseDTO.class))
                .collect(Collectors.toList());
    }

    // Search Doctors By Name

    @Override
    public List<DoctorResponseDTO> searchDoctorsByName(String name) {
        List<Doctor> doctorName = doctorRepository.findByDoctorNameContainingIgnoreCase(name);
        if(doctorName == null){
            throw new ResourceNotFoundException("Doctor","Name",name);
        }

        return doctorName.stream()
                .map(doctor -> modelMapper.map(doctor,DoctorResponseDTO.class))
                .collect(Collectors.toList());
    }
}