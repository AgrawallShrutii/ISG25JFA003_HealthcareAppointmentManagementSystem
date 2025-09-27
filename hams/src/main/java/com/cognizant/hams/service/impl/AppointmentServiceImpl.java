package com.cognizant.hams.service.impl;

import com.cognizant.hams.dto.request.AppointmentDTO;
import com.cognizant.hams.dto.response.AppointmentResponseDTO;
import com.cognizant.hams.entity.Appointment;
import com.cognizant.hams.entity.AppointmentStatus;
import com.cognizant.hams.entity.Doctor;
import com.cognizant.hams.entity.Patient;
import com.cognizant.hams.exception.APIException;
import com.cognizant.hams.exception.ResourceNotFoundException;
import com.cognizant.hams.repository.AppointmentRepository;
import com.cognizant.hams.repository.DoctorRepository;
import com.cognizant.hams.repository.PatientRepository;
import com.cognizant.hams.service.AppointmentService;
import com.cognizant.hams.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Override
    @Transactional
    public AppointmentResponseDTO rejectAppointment(Long appointmentId, String reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Doctor loggedInDoctor = (Doctor) doctorRepository.findByUser_Username(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "username", currentUsername));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        if (!appointment.getDoctor().getDoctorId().equals(loggedInDoctor.getDoctorId())) {
            throw new AccessDeniedException("Doctor is not authorized to update this appointment.");
        }
        appointment.setStatus(AppointmentStatus.REJECTED);
        Appointment saved = appointmentRepository.save(appointment);
        notificationService.notifyPatientOnAppointmentDecision(saved, false, reason);
        return modelMapper.map(saved, AppointmentResponseDTO.class);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO confirmAppointment(Long appointmentId) {
        // 1. Get the authenticated doctor's username from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Doctor loggedInDoctor = (Doctor) doctorRepository.findByUser_Username(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "username", currentUsername));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        if (!appointment.getDoctor().getDoctorId().equals(loggedInDoctor.getDoctorId())) {
            throw new AccessDeniedException("Doctor is not authorized to update this appointment.");
        }

        // 5. Update appointment status and save
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        Appointment saved = appointmentRepository.save(appointment);

        // 6. Notify the patient
        notificationService.notifyPatientOnAppointmentDecision(saved, true, null);

        return modelMapper.map(saved, AppointmentResponseDTO.class);
    }
    @Override
    public List<AppointmentResponseDTO> getAppointmentsForPatient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Patient patient = (Patient) patientRepository.findByUser_Username(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "username", currentUsername));

        List<Appointment> appointments = appointmentRepository.findByPatient_PatientId(patient.getPatientId());

        return appointments.stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentResponseDTO bookAppointment(AppointmentDTO appointmentDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Patient patient = (Patient) patientRepository.findByUser_Username(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "username", currentUsername));

        Doctor doctor = doctorRepository.findById(appointmentDTO.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "Id", appointmentDTO.getDoctorId()));

        Appointment appointment = modelMapper.map(appointmentDTO, Appointment.class);
        appointment.setAppointmentId(null);
        appointment.setVersion(null);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        notificationService.notifyDoctorOnAppointmentRequest(savedAppointment);

        return modelMapper.map(savedAppointment, AppointmentResponseDTO.class);
    }

    @Override
    public AppointmentResponseDTO updateAppointment(Long appointmentId, AppointmentDTO appointmentUpdateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Appointment existingAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "Id", appointmentId));

        String patientUsername = existingAppointment.getPatient().getUser().getUsername();
        String doctorUsername = existingAppointment.getDoctor().getUser().getUsername();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isPatient = currentUsername.equals(patientUsername);
        boolean isDoctor = currentUsername.equals(doctorUsername);

        if (!isAdmin && !isPatient && !isDoctor) {
            throw new AccessDeniedException("You do not have permission to update this appointment.");
        }

        if (appointmentUpdateDTO.getDoctorId() != null && !existingAppointment.getDoctor().getDoctorId().equals(appointmentUpdateDTO.getDoctorId())) {
            Doctor newDoctor = doctorRepository.findById(appointmentUpdateDTO.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor", "Id", appointmentUpdateDTO.getDoctorId()));
            existingAppointment.setDoctor(newDoctor);
        }


        Appointment updatedAppointment = appointmentRepository.save(existingAppointment);
        return modelMapper.map(updatedAppointment, AppointmentResponseDTO.class);
    }

    @Override
    public AppointmentResponseDTO cancelAppointment(Long appointmentId) {
        Appointment appointmentToCancel = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "Id", appointmentId));

        if (appointmentToCancel.getStatus() == AppointmentStatus.COMPLETED || appointmentToCancel.getStatus() == AppointmentStatus.CANCELED) {
            throw new APIException("Appointment cannot be canceled as it is already " + appointmentToCancel.getStatus());
        }

        appointmentToCancel.setStatus(AppointmentStatus.CANCELED);
        Appointment canceledAppointment = appointmentRepository.save(appointmentToCancel);

        return modelMapper.map(canceledAppointment, AppointmentResponseDTO.class);
    }

    @Override
    public AppointmentResponseDTO getAppointmentById(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "Id", appointmentId));
        return modelMapper.map(appointment, AppointmentResponseDTO.class);
    }
}
