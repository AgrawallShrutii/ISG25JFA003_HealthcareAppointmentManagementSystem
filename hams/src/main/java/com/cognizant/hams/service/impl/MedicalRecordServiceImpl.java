package com.cognizant.hams.service.impl;

import com.cognizant.hams.dto.request.MedicalRecordDTO;
import com.cognizant.hams.dto.response.MedicalRecordResponseDTO;
import com.cognizant.hams.service.MedicalRecordService;

import com.cognizant.hams.entity.Appointment;
import com.cognizant.hams.entity.Doctor;
import com.cognizant.hams.entity.MedicalRecord;
import com.cognizant.hams.entity.Patient;
import com.cognizant.hams.exception.APIException;
import com.cognizant.hams.exception.ResourceNotFoundException;
import com.cognizant.hams.repository.AppointmentRepository;
import com.cognizant.hams.repository.DoctorRepository;
import com.cognizant.hams.repository.MedicalRecordRepository;
import com.cognizant.hams.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;

    @Override
    public MedicalRecordResponseDTO createRecord(MedicalRecordDTO dto) {
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "Id", dto.getAppointmentId()));
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "Id", dto.getPatientId()));
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "Id", dto.getDoctorId()));
        if (!appointment.getPatient().getPatientId().equals(patient.getPatientId()) ||
                !appointment.getDoctor().getDoctorId().equals(doctor.getDoctorId())) {
            throw new APIException("Appointment does not belong to provided patient/doctor");
        }
        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setDoctor(doctor);
        record.setReason(dto.getReason());
        record.setDiagnosis(dto.getDiagnosis());
        record.setNotes(dto.getNotes());
        MedicalRecord saved = medicalRecordRepository.save(record);
        MedicalRecordResponseDTO resp = new MedicalRecordResponseDTO();
        resp.setRecordId(saved.getRecordId());
        resp.setPatientId(patient.getPatientId());
        resp.setDoctorId(doctor.getDoctorId());
        resp.setPatientName(patient.getName());
        resp.setDoctorName(doctor.getDoctorName());
        resp.setReason(saved.getReason());
        resp.setDiagnosis(saved.getDiagnosis());
        resp.setNotes(saved.getNotes());
        resp.setCreatedAt(saved.getCreatedAt());
        return resp;
    }

    @Override
    public List<MedicalRecordResponseDTO> getRecordsForPatient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Patient patient = (Patient) patientRepository.findByUser_Username(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "username", currentUsername));

        List<MedicalRecord> medicalRecords = medicalRecordRepository.findByPatient_PatientIdOrderByCreatedAtDesc(patient.getPatientId());

        return medicalRecords.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public List<MedicalRecordResponseDTO> getRecordsForDoctor(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor", "Id", doctorId);
        }
        return medicalRecordRepository.findByDoctor_DoctorIdOrderByCreatedAtDesc(doctorId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private MedicalRecordResponseDTO toDto(MedicalRecord record) {
        MedicalRecordResponseDTO resp = new MedicalRecordResponseDTO();
        resp.setRecordId(record.getRecordId());
        resp.setPatientId(record.getPatient().getPatientId());
        resp.setDoctorId(record.getDoctor().getDoctorId());
        resp.setPatientName(record.getPatient().getName());
        resp.setDoctorName(record.getDoctor().getDoctorName());
        resp.setReason(record.getReason());
        resp.setDiagnosis(record.getDiagnosis());
        resp.setNotes(record.getNotes());
        resp.setCreatedAt(record.getCreatedAt());
        return resp;
    }
}


