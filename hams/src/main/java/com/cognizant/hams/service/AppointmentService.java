package com.cognizant.hams.service;

import com.cognizant.hams.dto.request.AppointmentDTO;
import com.cognizant.hams.dto.response.AppointmentResponseDTO;

import java.util.List;

public interface AppointmentService {
    AppointmentResponseDTO confirmAppointment(Long appointmentId);
    AppointmentResponseDTO rejectAppointment(Long appointmentId, String reason);
    AppointmentResponseDTO bookAppointment(AppointmentDTO appointmentDTO);
    AppointmentResponseDTO updateAppointment(Long appointmentId, AppointmentDTO appointmentUpdateDTO);
    AppointmentResponseDTO cancelAppointment(Long appointmentId);
    AppointmentResponseDTO getAppointmentById(Long appointmentId);
    List<AppointmentResponseDTO> getAppointmentsForPatient();
}
