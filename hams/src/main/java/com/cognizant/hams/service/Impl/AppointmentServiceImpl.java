package com.cognizant.hams.service.Impl;

import com.cognizant.hams.dto.Request.AppointmentDTO;
import com.cognizant.hams.dto.Response.AppointmentResponseDTO;
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
    public AppointmentResponseDTO rejectAppointment(Long doctorId, Long appointmentId, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));
        if(!appointment.getDoctor().getDoctorId().equals(doctorId)){
            throw new APIException("Doctor is not authorized to update this appointment");
        }
        appointment.setStatus(AppointmentStatus.REJECTED);
        Appointment saved = appointmentRepository.save(appointment);
        notificationService.notifyPatientOnAppointmentDecision(saved, false, reason);
        return modelMapper.map(saved, AppointmentResponseDTO.class);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO confirmAppointment(Long doctorId, Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));
        if(!appointment.getDoctor().getDoctorId().equals(doctorId)){
            throw new APIException("Doctor is not authorized to update this appointment");
        }
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        Appointment saved = appointmentRepository.save(appointment);
        notificationService.notifyPatientOnAppointmentDecision(saved, true, null);
        return modelMapper.map(saved, AppointmentResponseDTO.class);
    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentsForPatient(Long patientId) {

        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient", "Id", patientId);
        }

        List<Appointment> appointments = appointmentRepository.findByPatient_PatientId(patientId);

        return appointments.stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentResponseDTO bookAppointment(Long patientId, AppointmentDTO appointmentDTO) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "Id", patientId));

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
        Appointment existingAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "Id", appointmentId));

        if (appointmentUpdateDTO.getDoctorId() != null && !existingAppointment.getDoctor().getDoctorId().equals(appointmentUpdateDTO.getDoctorId())) {
            Doctor newDoctor = doctorRepository.findById(appointmentUpdateDTO.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor", "Id", appointmentUpdateDTO.getDoctorId()));
            existingAppointment.setDoctor(newDoctor);
        }

        if (appointmentUpdateDTO.getAppointmentDate() != null) {
            existingAppointment.setAppointmentDate(appointmentUpdateDTO.getAppointmentDate());
        }
        if (appointmentUpdateDTO.getStartTime() != null) {
            existingAppointment.setStartTime(appointmentUpdateDTO.getStartTime()); // Updated
        }
        if (appointmentUpdateDTO.getEndTime() != null) {
            existingAppointment.setEndTime(appointmentUpdateDTO.getEndTime());     // Updated
        }
        if (appointmentUpdateDTO.getReason() != null) {
            existingAppointment.setReason(appointmentUpdateDTO.getReason());
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
