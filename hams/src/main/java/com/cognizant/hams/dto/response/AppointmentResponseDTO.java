package com.cognizant.hams.dto.response;

import com.cognizant.hams.entity.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {

    private Long appointmentId;
    private LocalDate appointmentDate;

    private LocalTime startTime;
    private LocalTime endTime;

    private String reason;
    private AppointmentStatus status;

    private PatientResponseDTO patient;
    private DoctorResponseDTO doctor;
}